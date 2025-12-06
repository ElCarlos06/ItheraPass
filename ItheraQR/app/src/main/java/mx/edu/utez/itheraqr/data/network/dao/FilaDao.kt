package mx.edu.utez.itheraqr.data.network.dao

import mx.edu.utez.itheraqr.data.network.model.Fila

interface FilaDao {
    // Obtener todas las filas
    fun getAll(
        onSuccess: (List<Fila>) -> Unit,
        onError: (String) -> Unit
    )

    // Obtener una sola fila por ID
    fun get(
        id: Int,
        onSuccess: (Fila) -> Unit,
        onError: (String) -> Unit
    )

    // Crear una nueva Fila
    fun create(
        fila: Fila,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    )

    // Borrar fila
    fun delete(
        id: Int,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    )
}