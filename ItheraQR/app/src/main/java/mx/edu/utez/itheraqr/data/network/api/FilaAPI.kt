package mx.edu.utez.itheraqr.data.network.api

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import mx.edu.utez.itheraqr.data.network.FllaSingleton
import mx.edu.utez.itheraqr.data.network.model.Fila
import mx.edu.utez.itheraqr.data.network.model.Turno
import org.json.JSONArray
import org.json.JSONObject
import java.nio.charset.Charset

class FilaAPI(val context: Context) {
    ///telefono fisico aqui debe ser la ip de la computadora
    //virtual es mas facil pq ya estan algo relacionadas
    //val baseURL = "http://10.0.2.2:3000" // es la virtual (solo emulador)
    val baseURL = "http://192.168.0.83:3000" //ip carlos
    //val baseURL = "http://192.168.100.18:3000" //ip tony (funciona para emulador Y celular físico)


    //  OBTENER TODAS LAS FILAS (GET)
    fun getAll(
        onSuccess: (List<Fila>) -> Unit,
        onError: (String) -> Unit
    ) {
        val url = "$baseURL/fila"
        val metodo = Request.Method.GET
        
        Log.d("FilaAPI", "Intentando conectar a: $url")

        val listener = Response.Listener<JSONArray> { response ->
            Log.d("FilaAPI", "Respuesta exitosa: ${response.length()} filas")
            val lista = mutableListOf<Fila>()
            for (i in 0 until response.length()) {
                val objeto = response.getJSONObject(i)
                lista.add(parseFila(objeto))
            }
            onSuccess(lista)
        }

        val errorListener = Response.ErrorListener { error ->
            val errorMsg = getErrorMessage(error)
            Log.e("FilaAPI", "Error al obtener filas: $errorMsg", error)
            Log.e("FilaAPI", "URL intentada: $url")
            Log.e("FilaAPI", "Código de error: ${error.networkResponse?.statusCode}")
            onError(errorMsg)
        }

        val request = JsonArrayRequest(metodo, url, null, listener, errorListener).apply {
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

    fun get(
        id: Int,
        onSuccess: (Fila) -> Unit,
        onError: (String) -> Unit
    ) {
        val url = "$baseURL/fila/$id"
        val metodo = Request.Method.GET

        //  JsonObjectRequest porque UN solo registro
        val listener = Response.Listener<JSONObject> { response ->
            // Reutilizamos parsing
            val filaEncontrada = parseFila(response)
            onSuccess(filaEncontrada)

        }

        val errorListener = Response.ErrorListener { error ->
            onError(error.message.toString())
        }

        val request = JsonObjectRequest(metodo, url, null, listener, errorListener).apply {
            retryPolicy = FllaSingleton.getRetryPolicy()
        }

        // FllaSingleton
        FllaSingleton.getInstance(context).add(request)

    }

    fun create(
        fila: Fila,
        onSuccess: (Int) -> Unit,
        onError: (String) -> Unit
    ) {
        val url = "$baseURL/fila"
        val metodo = Request.Method.POST

        val body = JSONObject()
        body.put("nombre", fila.nombre)
        body.put("categoria", fila.categoria)
        body.put("tiempoPromedioAtencion", fila.tiempoPromedioAtencion)
        body.put("idPropietario", fila.idPropietario)
        body.put("estado", fila.estado)


        val listener = Response.Listener<JSONObject> { response ->
            // El backend devuelve { "success": true, "id": 15 }
            val nuevoId = response.optInt("id", -1)
            onSuccess(nuevoId)
        }

        val errorListener = Response.ErrorListener { error ->
            onError(error.message.toString())
        }

        val request = JsonObjectRequest(
            metodo, url, body,
            listener, errorListener
        ).apply {
            retryPolicy = FllaSingleton.getRetryPolicy()
        }

        FllaSingleton.getInstance(context).add(request)
    }

    fun delete(
        id: Int,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val url = "$baseURL/fila/$id"
        val metodo = Request.Method.DELETE

        val listener = Response.Listener<JSONObject> { response ->
            if (response.getInt("affectedRows") == 1) {
                onSuccess()
            }
        }

        val errorListener = Response.ErrorListener { error ->
            onError(error.message.toString())
        }

        val request = JsonObjectRequest(
            metodo, url, null,
            listener, errorListener
        ).apply {
            retryPolicy = FllaSingleton.getRetryPolicy()
        }

        FllaSingleton.getInstance(context).add(request)
    }

    fun getMisFilas(
        idUsuario: String,
        onSuccess: (List<Fila>) -> Unit,
        onError: (String) -> Unit
    ) {
        val url = "$baseURL/mis-filas/$idUsuario"
        val metodo = Request.Method.GET

        val listener = Response.Listener<JSONArray> { response ->
            val lista = mutableListOf<Fila>()
            try {
                for (i in 0 until response.length()) {
                    lista.add(parseFila(response.getJSONObject(i)))
                }
                onSuccess(lista)
            } catch (e: Exception) {
                onError(e.message ?: "Error procesando mis filas")
            }
        }

        val errorListener = Response.ErrorListener { error ->
            onError(error.message.toString())
        }

        val request = JsonArrayRequest(
            metodo, url, null,
            listener, errorListener
        ).apply {
            retryPolicy = FllaSingleton.getRetryPolicy()
        }

        FllaSingleton.getInstance(context).add(request)
    }

    //para no estar repitiendo xd
    private fun parseFila(json: JSONObject): Fila {
        val estadoString = json.optString("estado", "ABIERTA")

        return Fila(
            id = json.optInt("id", 0),
            nombre = json.getString("nombre"),
            categoria = json.getString("categoria"),
            estado = estadoString,
            tiempoPromedioAtencion = json.optLong("tiempoPromedioAtencion", 5),
            cantidadEnEspera = json.optInt("cantidadEnEspera", json.optInt("formados", 0)),
            cantidadAtendidos = json.optInt("cantidadAtendidos", json.optInt("atendidos", 0)),
            idPropietario = json.optString("idPropietario", ""),
            // Campos opcionales para mis-filas
            codigoTurno = json.optString("codigoTurno", null)?.let { codigo ->
                // Limpiar el código (quitar "A-" si existe para compatibilidad)
                if (codigo.startsWith("A-")) {
                    codigo.substringAfter("-")
                } else {
                    codigo
                }
            }?.takeIf { !it.isNullOrEmpty() },
            estadoTurno = json.optString("estadoTurno", null).takeIf { !it.isNullOrEmpty() },
            idTurno = if (json.has("idTurno") && !json.isNull("idTurno")) json.optInt("idTurno", 0) else null
        )
    }

}