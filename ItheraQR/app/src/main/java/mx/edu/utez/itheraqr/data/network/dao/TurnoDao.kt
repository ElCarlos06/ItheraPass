package mx.edu.utez.itheraqr.data.network.dao

import mx.edu.utez.itheraqr.data.network.model.Turno

interface TurnoDao {

    // Formarse en una fila
    fun formarse(
        idFila: String,
        idUsuario: String,
        nombre: String,
        correo: String,
        onSuccess: (Boolean) -> Unit,
        onError: (String) -> Unit
    )

    // Obtener lista de turnos de una fila
    fun getTurnos(
        idFila: Int,
        onSuccess: (List<Turno>) -> Unit,
        onError: (String) -> Unit
    )

    // Actualizar estado (Llamar/Finalizar)
    fun actualizarTurno(
        idTurno: Int,
        estado: String,
        onSuccess: (Boolean) -> Unit,
        onError: (String) -> Unit
    )
}