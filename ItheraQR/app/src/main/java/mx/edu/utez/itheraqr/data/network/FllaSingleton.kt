package mx.edu.utez.itheraqr.data.network

import android.content.Context
import com.android.volley.DefaultRetryPolicy
import com.android.volley.RequestQueue
import com.android.volley.toolbox.BasicNetwork
import com.android.volley.toolbox.DiskBasedCache
import com.android.volley.toolbox.HurlStack
import com.android.volley.toolbox.Volley
import java.io.File


class FllaSingleton {
    companion object {
        @Volatile
        private var INSTANCE: RequestQueue? = null

        fun getInstance(context: Context): RequestQueue {
            return INSTANCE ?: synchronized(this) {
                if (INSTANCE == null) {
                    // Configuración personalizada con timeouts más largos
                    val cache = DiskBasedCache(File(context.cacheDir, "volley"), 1024 * 1024) // 1MB cache
                    val network = BasicNetwork(HurlStack())
                    
                    INSTANCE = RequestQueue(cache, network).apply {
                        start()
                    }
                }
                INSTANCE!!
            }
        }
        
        // Función helper para configurar retry policy con timeouts largos
        fun getRetryPolicy(): DefaultRetryPolicy {
            // timeout: 30 segundos, maxRetries: 2, backoffMultiplier: 2.0
            return DefaultRetryPolicy(
                30000, // 30 segundos timeout
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )
        }
    }
}