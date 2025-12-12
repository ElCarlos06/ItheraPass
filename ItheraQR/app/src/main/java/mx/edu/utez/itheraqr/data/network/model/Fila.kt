package mx.edu.utez.itheraqr.data.network.model

data class Fila(
    val id: Int,
    val nombre: String,
    val categoria: String,
    var estado: String,
    var tiempoPromedioAtencion: Long,

    // Contadores para las vistas y la organizacion
    var cantidadEnEspera: Int = 0,
    var cantidadAtendidos: Int = 0,

    val idPropietario: String,   //union hacia el creador
    
    // Campos opcionales para "mis filas" (cuando el usuario está formado)
    val codigoTurno: String? = null,
    val estadoTurno: String? = null,
    val idTurno: Int? = null
)