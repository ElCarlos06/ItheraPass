package mx.edu.utez.itheraqr.data.network

import android.content.Context
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley


class FllaSingleton {
    companion object {
        @Volatile
        private var INSTANCE: RequestQueue? = null

        fun getInstance(context: Context): RequestQueue {
            return INSTANCE ?: synchronized(this) { INSTANCE ?: Volley.newRequestQueue(context.applicationContext)
                .also { INSTANCE = it } } }
    }
}