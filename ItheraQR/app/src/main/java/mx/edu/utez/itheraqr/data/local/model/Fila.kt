package mx.edu.utez.itheraqr.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "fila")
data class Fila (
    @PrimaryKey(autoGenerate = true) var id: Long=0,
    ///val id: Long,
    var nombre: String,
    var categoria: Int,
    var capacidad: Int,
    var formados: Int,
    var atendidos: Int,

    )