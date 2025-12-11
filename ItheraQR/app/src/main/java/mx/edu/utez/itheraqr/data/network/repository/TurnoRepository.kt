package mx.edu.utez.itheraqr.data.network.repository

import android.content.Context
import mx.edu.utez.itheraqr.data.network.api.TurnoAPI
import mx.edu.utez.itheraqr.data.network.dao.TurnoDao
import mx.edu.utez.itheraqr.data.network.model.Turno

class TurnoRepository(context: Context) : TurnoDao {

    // Instanciamos el API de Turnos
    private val api = TurnoAPI(context)

    // 1. FORMARSE
    override fun formarse(
        idFila: String,
        idUsuario: String,
        nombre: String,
        correo: String,
        onSuccess: (Boolean) -> Unit,
        onError: (String) -> Unit
    ) {
        api.formarse(idFila, idUsuario, nombre, correo, onSuccess, onError)
    }

    // 2. OBTENER TURNOS DE UNA FILA
    override fun getTurnos(
        idFila: Int,
        onSuccess: (List<Turno>) -> Unit,
        onError: (String) -> Unit
    ) {
        api.getTurnos(idFila, onSuccess, onError)
    }

    // 3. ACTUALIZAR TURNO
    override fun actualizarTurno(
        idTurno: Int,
        estado: String,
        onSuccess: (Boolean) -> Unit,
        onError: (String) -> Unit
    ) {
        api.actualizarTurno(idTurno, estado, onSuccess, onError)
    }
}