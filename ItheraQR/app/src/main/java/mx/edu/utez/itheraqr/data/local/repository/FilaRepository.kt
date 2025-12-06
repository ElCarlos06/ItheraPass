package mx.edu.utez.itheraqr.data.local.repository

import kotlinx.coroutines.flow.Flow
import mx.edu.utez.itheraqr.data.local.dao.FilaDao
import mx.edu.utez.itheraqr.data.local.model.Fila

class FilaRepository(private val dao: FilaDao) {
    val filas: Flow<List<Fila>> = dao.obtenerFilas()

    suspend fun insertar(fila: Fila) = dao.insertarFila(fila)
    suspend fun actualizar(fila: Fila) = dao.actualizarFila(fila)
    suspend fun eliminar(fila: Fila) = dao.eliminarFila(fila)
    suspend fun obtenerPorId(id: Long) = dao.obtenerFila(id)
}