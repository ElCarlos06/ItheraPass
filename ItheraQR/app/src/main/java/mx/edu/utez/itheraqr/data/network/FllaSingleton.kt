package mx.edu.utez.itheraqr.data.network

import android.content.Context
import com.android.volley.DefaultRetryPolicy
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley

class FllaSingleton {
    companion object {
        @Volatile
        private var INSTANCE: RequestQueue? = null

        fun getInstance(context: Context): RequestQueue {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Volley.newRequestQueue(context.applicationContext)
                    .also { INSTANCE = it }
            }
        }
        
        // Retry policy con timeout de 30 segundos para peticiones largas
        fun getRetryPolicy(): DefaultRetryPolicy {
            return DefaultRetryPolicy(
                30000, // 30 segundos timeout
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )
        }
    }
}