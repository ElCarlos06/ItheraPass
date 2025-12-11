package mx.edu.utez.itheraqr.data.network.api

import android.content.Context
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import mx.edu.utez.itheraqr.data.network.FllaSingleton
import mx.edu.utez.itheraqr.data.network.model.Turno
import org.json.JSONArray
import org.json.JSONObject

class TurnoAPI(val context: Context) {
    ///telefono fisico aqui debe ser la ip de la computadora
    //virtual es mas facil pq ya estan algo relacionadas
    //val baseURL = "http://10.0.2.2:3000"/// es la virtual
    val baseURL = "http://192.168.107.48:3000" //fisica


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

        val body = JSONObject()
        body.put("idFila", idFila)
        body.put("idUsuario", idUsuario)
        body.put("nombreUsuario", nombre)
        body.put("correoUsuario", correo)

        val request = JsonObjectRequest(Request.Method.POST, url, body,
            { response ->
                if (response.optBoolean("success")) onSuccess(true)
                else onError(response.optString("message", "Error al formarse"))
            },
            { error -> onError(error.message ?: "Error de red") }
        )
        FllaSingleton.getInstance(context).add(request)
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
            onError(error.message.toString())
        }

        val request = JsonObjectRequest(
            metodo, url, body,
            listener, errorListener
        )

        FllaSingleton.getInstance(context).add(request)
    }

    // 3. OBTENER TURNOS DE UNA FILA (Para el Admin/Manage)
    fun getTurnos(
        idFila: Int,
        onSuccess: (List<Turno>) -> Unit,
        onError: (String) -> Unit
    ) {
        val url = "$baseURL/fila/$idFila/turnos"
        val metodo = Request.Method.POST

        val listener = Response.Listener<JSONArray> { response ->
            val lista = mutableListOf<Turno>()
            for (i in 0 until response.length()) {
                val objeto = response.getJSONObject(0)
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
            onError(error.message.toString())
        }


        val request = JsonArrayRequest(
            metodo, url, null,
            listener, errorListener
        )
    }
}