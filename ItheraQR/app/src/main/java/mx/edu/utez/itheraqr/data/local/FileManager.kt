package mx.edu.utez.itheraqr.data.local

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.OutputStream

class FileManager(
    private val ctx: Context
) {

    suspend fun saveBitmapToGallery(bitmap: Bitmap, filename: String) {
        withContext(Dispatchers.IO) {

            val resolver = ctx.contentResolver

            // Configurar los metadatos de la imagen
            val contentValues = ContentValues().apply {

                put(MediaStore.MediaColumns.DISPLAY_NAME, "$filename.png")
                put(MediaStore.MediaColumns.MIME_TYPE, "image/png")

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                    put(
                        MediaStore.MediaColumns.RELATIVE_PATH,
                        Environment.DIRECTORY_PICTURES + "/MisQRs"
                    )

            }

            // Insertar en la base de datos de medios y obtener la URI
            val imageUri =
                resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

            // Escribir el bitmap en el flujo de salida
            var outputStream: OutputStream? = null

            try {

                imageUri?.let { uri ->

                    outputStream = resolver.openOutputStream(uri)
                    outputStream?.let {
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
                    }
                }

                // Notificar al usuario en el hilo principal
                withContext(Dispatchers.Main) {
                    Toast.makeText(ctx, "QR Guardado en Galería", Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                e.printStackTrace()

                withContext(Dispatchers.Main) {
                    Toast.makeText(ctx, "Error al guardar", Toast.LENGTH_SHORT).show()
                }

            } finally {
                outputStream?.close()
            }
        }
    }

}