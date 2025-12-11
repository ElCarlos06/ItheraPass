package mx.edu.utez.itheraqr.data.network.model

data class Turno(
    val id: Int,
    val codigoTurno: String,     // Para mostrar en pantalla)

    val idFila: Int,          // Union a la fila
    val idUsuario: String,       // Union a la persna
    val nombreUsuario: String,
    val correoUsuario: String,

    val fechaCreacion: String,     // Ordenar por llegada (FIFO)
    var estado: String,     // EN_ESPERA, EN_ATENCION, FINALIZADO, CANCELADO


)