package mx.edu.utez.itheraqr.ui.screens.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import mx.edu.utez.itheraqr.data.local.UserSession
import mx.edu.utez.itheraqr.data.network.model.Fila
import mx.edu.utez.itheraqr.data.network.model.Turno
import mx.edu.utez.itheraqr.data.network.repository.FilaRepository
import mx.edu.utez.itheraqr.data.network.repository.TurnoRepository

class FilaViewModel(application: Application) : AndroidViewModel(application) {

    private val filaRepository = FilaRepository(application.applicationContext)
    private val turnoRepository = TurnoRepository(application.applicationContext)
    val listaFilas = MutableStateFlow<List<Fila>>(emptyList())
    val misFilasActivas = MutableStateFlow<List<Fila>>(emptyList())

    val errores = MutableStateFlow<String>("")
    val resultadoCrear = MutableStateFlow<Int?>(null)
    val resultadoFormarse = MutableStateFlow<Boolean>(false)
    val listaTurnos = MutableStateFlow<List<Turno>>(emptyList())


    fun cargarFilas() {
        filaRepository.getAll(
            onSuccess = { lista ->
                listaFilas.value = lista
            },
            onError = { mensaje ->
                errores.value = mensaje
            }
        )
    }

    fun cargarMisFilas() {
        val context = getApplication<Application>().applicationContext
        val idUsuario = UserSession.getUserId(context)

        filaRepository.getMisFilas(idUsuario,
            onSuccess = { misFilasActivas.value = it },
            onError = { /* Silencio o log */ }
        )
    }

    //crear fila
    fun crearFila(nombre: String, categoria: String, tiempo: Long) {
        val context = getApplication<Application>().applicationContext
        val idPropietario = UserSession.getUserId(context)
        val nueva = Fila(
            0,
            nombre,
            categoria,
            "ABIERTA",
            tiempo,
            0,
            0,
            idPropietario
        )

        filaRepository.create(nueva,
            onSuccess = { id ->
                resultadoCrear.value = id
                cargarFilas() // Actualizamos la lista general
            },
            onError = { errores.value = it }
        )
    }

    // Función para formarse
    // NUEVO: Ahora recibe el nombre
    fun formarse(idFila: String, nombre: String, correo: String) {
        val context = getApplication<Application>().applicationContext

        // 1. Obtenemos ID y Guardamos Nombre (Lógica de UserSession)
        val idUsuario = UserSession.getUserId(context)
        UserSession.saveUserName(context, nombre, correo)

        // 2. Llamamos al server
        turnoRepository.formarse(idFila, idUsuario, nombre, correo,
            onSuccess = { turno ->
                resultadoFormarse.value = turno
                cargarMisFilas()
            },
            onError = { message ->
                errores.value = message
            }
        )
    }

    // NUEVO: Función para refrescar la lista de gente
    fun cargarTurnos() {
        val id = resultadoCrear.value
        if (id != null) {
            turnoRepository.getTurnos(id,
                onSuccess = { turnos ->
                    listaTurnos.value = turnos },
                onError = {message ->
                    errores.value = message }
            )
        }
    }

    fun llamarSiguiente() {
        // Buscamos al primero en espera
        val siguiente = listaTurnos.value.firstOrNull { it.estado == "EN_ESPERA" }

        if (siguiente != null) {
            turnoRepository.actualizarTurno(siguiente.id, "ATENDIENDO",
                onSuccess = { cargarTurnos() },
                onError = {message ->
                    errores.value = message }
            )
        } else {
            errores.value = "No hay nadie en espera"
        }
    }

    fun atendido() {
        // Buscamos a quien estamos atendiendo
        val actual = listaTurnos.value.firstOrNull { it.estado == "ATENDIENDO" }

        if (actual != null) {
            // "FINALIZADO" es el estado correcto para que el backend lo archive
            turnoRepository.actualizarTurno(actual.id, "FINALIZADO",
                onSuccess = { cargarTurnos() },
                onError = { message ->
                    errores.value = message }
            )
        } else {
            errores.value = "No estás atendiendo a nadie"
        }
    }

    fun eliminarFila() {
        val id = resultadoCrear.value
        if (id != null) {
            filaRepository.delete(id,
                onSuccess = {
                    resultadoCrear.value = null
                    listaTurnos.value = emptyList()
                    cargarFilas()
                },
                onError = { message ->
                    errores.value = message }
            )
        }
    }
}