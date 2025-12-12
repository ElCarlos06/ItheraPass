package mx.edu.utez.itheraqr.data.network.api

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import mx.edu.utez.itheraqr.data.network.FllaSingleton
import mx.edu.utez.itheraqr.data.network.model.Turno
import org.json.JSONArray
import org.json.JSONObject

class TurnoAPI(val context: Context) {
    ///telefono fisico aqui debe ser la ip de la computadora
    //virtual es mas facil pq ya estan algo relacionadas
    //val baseURL = "http://10.0.2.2:3000" // es la virtual (solo emulador)
    val baseURL = "http://192.168.107.48:3000" //ip carlos
    //val baseURL = "http://192.168.100.18:3000" //ip tony (funciona para emulador Y celular físico)


    // 1. FORMARSE (CREAR TURNO)
    fun formarse(
        idFila: String,
        idUsuario: String,
        nombre: String,
        correo: String,
        onSuccess: (Boolean) -> Unit,
        onError: (String) -> Unit
    ) {
        val url = "$baseURL/turno"
        val metodo = Request.Method.POST
        
        Log.d("TurnoAPI", "Intentando formarse en fila: $idFila")
        Log.d("TurnoAPI", "URL: $url")

        val body = JSONObject()
        body.put("idFila", idFila)
        body.put("idUsuario", idUsuario)
        body.put("nombreUsuario", nombre)
        body.put("correoUsuario", correo)

        val request = JsonObjectRequest(Request.Method.POST, url, body,
            { response ->
                Log.d("TurnoAPI", "Respuesta del servidor: $response")
                if (response.optBoolean("success")) {
                    Log.d("TurnoAPI", "Formarse exitoso")
                    onSuccess(true)
                } else {
                    val msg = response.optString("message", "Error al formarse")
                    Log.e("TurnoAPI", "Error en respuesta: $msg")
                    onError(msg)
                }
            },
            { error -> 
                val errorMsg = getErrorMessage(error)
                Log.e("TurnoAPI", "Error al formarse: $errorMsg", error)
                Log.e("TurnoAPI", "URL intentada: $url")
                Log.e("TurnoAPI", "Código de error: ${error.networkResponse?.statusCode}")
                onError(errorMsg)
            }
        ).apply {
            retryPolicy = FllaSingleton.getRetryPolicy()
        }
        FllaSingleton.getInstance(context).add(request)
    }
    
    private fun getErrorMessage(error: VolleyError): String {
        return when {
            error.networkResponse == null -> {
                "Sin conexión al servidor. Verifica que el servidor esté corriendo en $baseURL"
            }
            error.networkResponse.statusCode == 500 -> {
                "Error del servidor (500). Revisa los logs del backend."
            }
            error.networkResponse.statusCode == 404 -> {
                "Endpoint no encontrado (404). Verifica la URL."
            }
            error.message != null -> {
                error.message!!
            }
            else -> {
                "Error de conexión: ${error.networkResponse.statusCode}"
            }
        }
    }

    // ACTUALIZAR ESTADO DEL TURNO (Llamar/Finalizar)
    fun actualizarTurno(
        idTurno: Int,
        estado: String,
        onSuccess: (Boolean) -> Unit,
        onError: (String) -> Unit
    ) {
        val url = "$baseURL/turno/$idTurno"
        val metodo = Request.Method.PUT

        val body = JSONObject().apply {
            put("estado", estado)
        }

        val listener = Response.Listener<JSONObject> { response ->
            // Si el servidor responde, asumimos éxito
            onSuccess(true)
        }

        val errorListener = Response.ErrorListener { error ->
            val errorMsg = getErrorMessage(error)
            Log.e("TurnoAPI", "Error al actualizar turno: $errorMsg", error)
            onError(errorMsg)
        }

        val request = JsonObjectRequest(
            metodo, url, body,
            listener, errorListener
        ).apply {
            retryPolicy = FllaSingleton.getRetryPolicy()
        }

        FllaSingleton.getInstance(context).add(request)
    }

    // 3. OBTENER TURNOS DE UNA FILA (Para el Admin/Manage)
    fun getTurnos(
        idFila: Int,
        onSuccess: (List<Turno>) -> Unit,
        onError: (String) -> Unit
    ) {
        val url = "$baseURL/fila/$idFila/turnos"
        val metodo = Request.Method.GET
        
        Log.d("TurnoAPI", "Obteniendo turnos de fila: $idFila")
        Log.d("TurnoAPI", "URL: $url")

        val listener = Response.Listener<JSONArray> { response ->
            Log.d("TurnoAPI", "Turnos recibidos: ${response.length()}")
            val lista = mutableListOf<Turno>()
            for (i in 0 until response.length()) {
                val objeto = response.getJSONObject(i)
                lista.add(
                    Turno(
                        id = objeto.optInt("id", 0),
                        codigoTurno = objeto.optString("codigoTurno", "A-00"),
                        idFila = objeto.optInt("idFila", idFila),
                        idUsuario = objeto.optString("idUsuario", ""),
                        nombreUsuario = objeto.optString("nombreUsuario", "Anónimo"),
                        correoUsuario = objeto.optString("correoUsuario", ""),
                        fechaCreacion = objeto.optString("fecha", ""),
                        estado = objeto.optString("estado", "EN_ESPERA")
                    )
                )
            }
            onSuccess(lista)
        }
        val errorListener = Response.ErrorListener { error ->
            val errorMsg = getErrorMessage(error)
            Log.e("TurnoAPI", "Error al obtener turnos: $errorMsg", error)
            onError(errorMsg)
        }

        val request = JsonArrayRequest(
            metodo, url, null,
            listener, errorListener
        ).apply {
            retryPolicy = FllaSingleton.getRetryPolicy()
        }
        FllaSingleton.getInstance(context).add(request)
    }
}