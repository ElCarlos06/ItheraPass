package mx.edu.utez.itheraqr.utils

import android.content.Context
import android.media.MediaPlayer
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.PowerManager

object NotificationHelper {

    // Variable global para mantener la referencia al ringtone/mediaplayer
    private var currentRingtone: Ringtone? = null
    private var currentMediaPlayer: MediaPlayer? = null
    private var wakeLock: PowerManager.WakeLock? = null

    /**
     * Reproduce el sonido de llamada directamente como alarma (sin notificación)
     */
    fun playCallAlarm(context: Context) {
        try {
            // Detener cualquier sonido anterior
            stopCallAlarm()

            // Adquirir WakeLock para asegurar que suene incluso con pantalla apagada
            val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
            wakeLock = powerManager.newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK,
                "ItheraPass::AlarmWakeLock"
            ).apply {
                acquire(60000) // Mantener despierto por 60 segundos máximo
            }

            // Intentar obtener el recurso raw primero
            val resourceId = context.resources.getIdentifier("llamada", "raw", context.packageName)
            
            if (resourceId != 0) {
                // Usar MediaPlayer para reproducir el archivo raw
                val mediaPlayer = MediaPlayer.create(context, resourceId)
                if (mediaPlayer != null) {
                    currentMediaPlayer = mediaPlayer.apply {
                        isLooping = true // Repetir hasta que se detenga
                        setWakeMode(context, PowerManager.PARTIAL_WAKE_LOCK) // Mantener despierto durante reproducción
                        setOnCompletionListener {
                            it?.release()
                            currentMediaPlayer = null
                            releaseWakeLock()
                        }
                        setOnErrorListener { mp, what, extra ->
                            android.util.Log.e("NotificationHelper", "Error en MediaPlayer: what=$what, extra=$extra")
                            mp?.release()
                            currentMediaPlayer = null
                            releaseWakeLock()
                            true
                        }
                        start()
                    }
                    android.util.Log.d("NotificationHelper", "Reproduciendo alarma desde raw/llamada (resourceId=$resourceId)")
                } else {
                    android.util.Log.e("NotificationHelper", "No se pudo crear MediaPlayer para resourceId=$resourceId")
                    releaseWakeLock()
                }
            } else {
                // Si no existe el recurso, usar el ringtone del sistema
                val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
                currentRingtone = RingtoneManager.getRingtone(context, soundUri)?.apply {
                    play()
                }
                android.util.Log.d("NotificationHelper", "Reproduciendo ringtone del sistema")
            }
        } catch (e: Exception) {
            android.util.Log.e("NotificationHelper", "Error al reproducir alarma: ${e.message}")
            releaseWakeLock()
        }
    }
    
    private fun releaseWakeLock() {
        try {
            wakeLock?.let {
                if (it.isHeld) {
                    it.release()
                }
                wakeLock = null
            }
        } catch (e: Exception) {
            android.util.Log.e("NotificationHelper", "Error al liberar WakeLock: ${e.message}")
        }
    }

    /**
     * Detiene el sonido de llamada
     */
    fun stopCallAlarm() {
        try {
            currentMediaPlayer?.let {
                if (it.isPlaying) {
                    it.stop()
                }
                it.release()
                currentMediaPlayer = null
            }
            currentRingtone?.let {
                if (it.isPlaying) {
                    it.stop()
                }
                currentRingtone = null
            }
            releaseWakeLock()
        } catch (e: Exception) {
            android.util.Log.e("NotificationHelper", "Error al detener alarma: ${e.message}")
        }
    }
}

