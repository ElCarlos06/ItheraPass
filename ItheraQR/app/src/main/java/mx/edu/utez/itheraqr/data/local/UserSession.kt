package mx.edu.utez.itheraqr.data.local

import android.content.Context
import java.util.UUID

object UserSession {
    private const val PREFS_NAME = "ithera_prefs"
    private const val KEY_USER_ID = "user_id"
    private const val KEY_USER_NAME = "user_name"
    private const val KEY_USER_EMAIL = "user_email"

    // NUEVA CLAVE PARA LA FILA ACTIVA
    private const val KEY_ACTIVE_FILA_ID = "active_fila_id"

    // ... (getUserId, saveUserName, etc. se quedan igual) ...
    fun getUserId(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        var userId = prefs.getString(KEY_USER_ID, null)
        if (userId == null) {
            userId = UUID.randomUUID().toString()
            prefs.edit().putString(KEY_USER_ID, userId).apply()
        }
        return userId
    }

    fun saveUserData(context: Context, nombre: String, correo: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .putString(KEY_USER_NAME, nombre)
            .putString(KEY_USER_EMAIL, correo)
            .apply()
    }

    fun getUserName(context: Context): String =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getString(KEY_USER_NAME, "") ?: ""

    fun getUserEmail(context: Context): String =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getString(KEY_USER_EMAIL, "") ?: ""

    // --- NUEVAS FUNCIONES PARA PERSISTENCIA DE FILA ---

    // 1. Guardar el ID de la fila que acabo de crear
    fun saveActiveFilaId(context: Context, id: Int) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putInt(KEY_ACTIVE_FILA_ID, id).apply()
    }

    // 2. Recuperar el ID al abrir la app
    fun getActiveFilaId(context: Context): Int? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val id = prefs.getInt(KEY_ACTIVE_FILA_ID, -1)
        return if (id != -1) id else null
    }

    // 3. Borrar el ID cuando elimino la fila
    fun clearActiveFilaId(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().remove(KEY_ACTIVE_FILA_ID).apply()
    }
}