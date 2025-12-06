package mx.edu.utez.itheraqr.data.network.model

data class Turno(
    val id: String,
    val codigoTurno: String,     // Para mostrar en pantalla)
    val idFila: String,          // Union a la fila
    val idUsuario: String,       // Union a la persna

    val fechaCreacion: Long,     // Ordenar por llegada (FIFO)
    var estado: EstadoTurno,     // EN_ESPERA, EN_ATENCION, FINALIZADO, CANCELADO

    var fechaInicioAtencion: Long? = null, // Para calcular estadísticas después
    var fechaFinAtencion: Long? = null
)

enum class EstadoTurno {
    EN_ESPERA,     // Está en la cola
    LLAMANDO,      // Suena su número/notificación
    EN_ATENCION,   // Lo están atendiendo
    FINALIZADO,    // Ya terminó
    CANCELADO      // Se salió de la fila o no llegó
}