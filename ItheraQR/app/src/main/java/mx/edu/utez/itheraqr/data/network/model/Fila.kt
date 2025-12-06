package mx.edu.utez.itheraqr.data.network.model

data class Fila(
    val id: Int,
    val nombre: String,
    val categoria: String,
    var estado: EstadoFila,
    var tiempoPromedioAtencion: Long,

    // Contadores para las vistas y la organizacion
    var cantidadEnEspera: Int = 0,
    var cantidadAtendidos: Int = 0,

    val idPropietario: String   //union hacia el creador
)

// Enum para controlar el flujo
enum class EstadoFila {
    ABIERTA, PAUSADA, CERRADA
}