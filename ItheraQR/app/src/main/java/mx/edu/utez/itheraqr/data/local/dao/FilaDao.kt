package mx.edu.utez.itheraqr.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import mx.edu.utez.itheraqr.data.local.model.Fila

@Dao
interface FilaDao {
    @Insert
    suspend fun insertarFila(fila: Fila): Long

    @Update
    suspend fun actualizarFila(fila: Fila)

    @Delete
    suspend fun eliminarFila(fila: Fila)

    @Query("SELECT * FROM fila ORDER BY id ASC")
    fun obtenerFilas(): Flow<List<Fila>>

    @Query("SELECT * FROM fila WHERE id =:id")
    suspend fun obtenerFila(id: Long): Fila
}