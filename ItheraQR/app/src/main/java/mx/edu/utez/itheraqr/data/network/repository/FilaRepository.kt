package mx.edu.utez.itheraqr.data.network.repository

import android.content.Context
// CORRECCIÓN: Cambiamos 'local.model' por 'network.model' para que coincida con el API y el DAO
import mx.edu.utez.itheraqr.data.network.model.Fila
import mx.edu.utez.itheraqr.data.network.api.FilaAPI
import mx.edu.utez.itheraqr.data.network.dao.FilaDao

class FilaRepository(context: Context) : FilaDao {

    private val api = FilaAPI(context)

    // 1. TRAER TODAS LAS FILAS
    override fun getAll(
        onSuccess: (List<Fila>) -> Unit,
        onError: (String) -> Unit
    ) {
        api.getAll(onSuccess, onError)
    }

    // 2. TRAER UNA SOLA FILA
    override fun get(
        id: Int,
        onSuccess: (Fila) -> Unit,
        onError: (String) -> Unit
    ) {
        api.get(id, onSuccess, onError)
    }

    // 3. CREAR UNA NUEVA FILA
    override fun create(
        fila: Fila,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        api.create(fila, onSuccess, onError)
    }

    // 4. ELIMINAR UNA FILA
    override fun delete(
        id: Int,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        api.delete(id, onSuccess, onError)
    }


    //deep links
    fun formarse(
        idFila: String,
        idUsuario: String,
        onSuccess: (Boolean) -> Unit,
        onError: (String) -> Unit
    ) {
        api.formarse(idFila, idUsuario, onSuccess, onError)
    }
}