package mx.edu.utez.itheraqr.ui.screens.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import mx.edu.utez.itheraqr.data.local.UserSession
import mx.edu.utez.itheraqr.data.network.model.Fila
import mx.edu.utez.itheraqr.data.network.model.Turno
import mx.edu.utez.itheraqr.data.network.repository.FilaRepository
import mx.edu.utez.itheraqr.data.network.repository.TurnoRepository
import mx.edu.utez.itheraqr.utils.NotificationHelper

class FilaViewModel(application: Application) : AndroidViewModel(application) {

    private val filaRepository = FilaRepository(application.applicationContext)
    private val turnoRepository = TurnoRepository(application.applicationContext)

    val listaFilas = MutableStateFlow<List<Fila>>(emptyList())
    val misFilasActivas = MutableStateFlow<List<Fila>>(emptyList())
    val misFilasCreadas = MutableStateFlow<List<Fila>>(emptyList()) // Filas que el usuario creó

    val errores = MutableStateFlow("")
    val resultadoCrear = MutableStateFlow<Int?>(null)
    val resultadoFormarse = MutableStateFlow(false)
    val listaTurnos = MutableStateFlow<List<Turno>>(emptyList())

    val filaCreada = MutableStateFlow(false)

    // --- BLOQUE INIT: SE EJECUTA AL ABRIR LA APP ---
    init {
        // 1. Intentamos recuperar el ID de la fila guardada en el celular
        val savedId = UserSession.getActiveFilaId(application.applicationContext)
        if (savedId != null) {
            resultadoCrear.value = savedId
            cargarTurnos() // Si existe, cargamos los datos de gestión
        }

        // 2. Cargamos las listas
        cargarFilas()
    }

    fun cargarFilas() {
        filaRepository.getAll(
            onSuccess = { lista -> listaFilas.value = lista },
            onError = { mensaje -> errores.value = mensaje }
        )
    }

    private var ultimoEstadoTurnos = mapOf<Int, String>() // Para detectar cambios
    private var notificacionesEnviadas = mutableSetOf<Int>() // Para evitar notificaciones duplicadas
    private var tiempoInicioAtendiendo = mutableMapOf<Int, Long>() // Para rastrear cuándo empezó a atenderse

    fun cargarMisFilas() {
        val context = getApplication<Application>().applicationContext
        val idUsuario = UserSession.getUserId(context)
        val tiempoActual = System.currentTimeMillis()

        filaRepository.getMisFilas(idUsuario,
            onSuccess = { nuevasFilas ->
                // Verificar si algún turno cambió a ATENDIENDO
                nuevasFilas.forEach { fila ->
                    if (fila.idTurno != null && fila.estadoTurno == "ATENDIENDO") {
                        val estadoAnterior = ultimoEstadoTurnos[fila.idTurno]

                        if (estadoAnterior != "ATENDIENDO") {
                            // El turno acaba de cambiar a ATENDIENDO, registrar el tiempo
                            tiempoInicioAtendiendo[fila.idTurno!!] = tiempoActual
                        } else {
                            // Ya está en ATENDIENDO, verificar si pasaron 5 segundos para activar la llamada
                            val tiempoInicio = tiempoInicioAtendiendo[fila.idTurno!!] ?: tiempoActual
                            val tiempoTranscurrido = tiempoActual - tiempoInicio
                            val yaSeNotifico = notificacionesEnviadas.contains(fila.idTurno)

                            // Después de 5 segundos, reproducir la alarma (solo una vez)
                            if (tiempoTranscurrido >= 5000 && !yaSeNotifico) {
                                NotificationHelper.playCallAlarm(context)
                                notificacionesEnviadas.add(fila.idTurno!!)
                            }
                        }
                    } else if (fila.idTurno != null && fila.estadoTurno != "ATENDIENDO") {
                        // Si el turno ya no está en ATENDIENDO, detener la alarma y limpiar el registro
                        NotificationHelper.stopCallAlarm()
                        notificacionesEnviadas.remove(fila.idTurno)
                        tiempoInicioAtendiendo.remove(fila.idTurno)
                    }
                }
                // Actualizar el estado anterior
                ultimoEstadoTurnos = nuevasFilas
                    .filter { it.idTurno != null }
                    .associate { it.idTurno!! to (it.estadoTurno ?: "EN_ESPERA") }

                misFilasActivas.value = nuevasFilas
            },
            onError = { /* Silencio o log */ }
        )
    }

    //crear fila
    fun crearFila(nombre: String, categoria: String, tiempo: Long) {
        val context = getApplication<Application>().applicationContext
        val idPropietario = UserSession.getUserId(context)
        // Usamos "ABIERTA" directo (String)
        val nueva = Fila(0, nombre, categoria, "ABIERTA", tiempo, 0, 0, idPropietario)

        filaRepository.create(nueva,
            onSuccess = { id ->
                resultadoCrear.value = id
                filaCreada.value = true // Actualizamos tu variable original

                // PERSISTENCIA: Guardamos el ID para que no se pierda al cerrar
                UserSession.saveActiveFilaId(context, id)

                cargarFilas()
            },
            onError = { errores.value = it }
        )
    }

    // Función para formarse
    fun formarse(idFila: String, nombre: String, correo: String) {
        val context = getApplication<Application>().applicationContext
        val idUsuario = UserSession.getUserId(context)
        // Guardamos datos de usuario para autocompletar después
        UserSession.saveUserData(context, nombre, correo)

        turnoRepository.formarse(idFila, idUsuario, nombre, correo,
            onSuccess = {
                resultadoFormarse.value = it
                cargarMisFilas()
            },
            onError = { errores.value = it }
        )
    }


    // Usada en Manage (antes cargarTurnosDeMiFila)
    fun cargarTurnos() {
        val id = resultadoCrear.value
        if (id != null) {
            turnoRepository.getTurnos(id,
                onSuccess = { listaTurnos.value = it },
                onError = { errores.value = it }
            )
        }
    }
    
    // Cargar turnos por ID de fila (para la pantalla de detalles)
    fun cargarTurnosPorFila(idFila: Int) {
        turnoRepository.getTurnos(idFila,
            onSuccess = { turnos ->
                listaTurnos.value = turnos },
            onError = { message ->
                errores.value = message }
        )
    }

    fun llamarSiguiente() {
        val siguiente = listaTurnos.value.firstOrNull { it.estado == "EN_ESPERA" }
        if (siguiente != null) {
            turnoRepository.actualizarTurno(siguiente.id, "ATENDIENDO",
                onSuccess = { cargarTurnos() },
                onError = { errores.value = it }
            )
        } else {
            errores.value = "No hay nadie en espera"
        }
    }

    fun atendido() {
        val actual = listaTurnos.value.firstOrNull { it.estado == "ATENDIENDO" }
        if (actual != null) {
            val nombreUsuario = actual.nombreUsuario
            turnoRepository.actualizarTurno(actual.id, "ATENDIDO",
                onSuccess = {
                    cargarTurnos()
                    cargarFilas()
                    errores.value = "Turno de $nombreUsuario atendido"
                },
                onError = { errores.value = it }
            )
        } else {
            errores.value = "No estás atendiendo a nadie"
        }
    }

    fun eliminarFila() {
        val id = resultadoCrear.value
        if (id != null) {
            // Obtenemos el contexto tal como lo tenías, es correcto
            val context = getApplication<Application>().applicationContext

            filaRepository.delete(id,
                onSuccess = {
                    resultadoCrear.value = null
                    listaTurnos.value = emptyList()

                    // PERSISTENCIA: Borramos el ID de la memoria del celular
                    UserSession.clearActiveFilaId(context)

                    cargarFilas()
                },
                onError = { errores.value = it }
            )
        }
    }
}