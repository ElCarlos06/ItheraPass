package mx.edu.utez.itheraqr.data.network.model

data class Usuario(
    val id: String,
    val nombre: String,
    val correo: String,
    val fcmToken: String? = null, //
    val rol: RolUsuario           // CLIENTE, ADMIN_FILA
)

enum class RolUsuario {
    CLIENTE, ADMIN
}