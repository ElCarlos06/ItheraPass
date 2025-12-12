package mx.edu.utez.itheraqr.ui.screens.components.create

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import java.util.EnumMap

// Ahora acepta un logo opcional (Bitmap)
fun generarQR(texto: String, logo: Bitmap? = null): Bitmap? {
    return try {
        // 1. CONFIGURAR LA CORRECCIÓN DE ERRORES ALTA (H)
        // Esto permite que el 30% del QR esté cubierto (por el logo) y siga funcionando.
        val hints = EnumMap<EncodeHintType, Any>(EncodeHintType::class.java)
        hints[EncodeHintType.ERROR_CORRECTION] = ErrorCorrectionLevel.H
        hints[EncodeHintType.MARGIN] = 1 // Margen blanco delgado

        val size = 512
        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(texto, BarcodeFormat.QR_CODE, size, size, hints)

        val w = bitMatrix.width
        val h = bitMatrix.height
        val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)

        // 2. DIBUJAR LOS PÍXELES DEL QR
        for (x in 0 until w) {
            for (y in 0 until h) {
                bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
            }
        }

        // 3. DIBUJAR EL LOGO EN EL CENTRO (Si existe)
        if (logo != null) {
            val canvas = Canvas(bitmap)

            // Calculamos el tamaño del logo mas poequeño para que no arruine el qr)
            val logoSize = (size * 0.5f).toInt()

            // Escalamos la imagen original al tamaño calculado
            val scaledLogo = Bitmap.createScaledBitmap(logo, logoSize, logoSize, false)

            // Calculamos la posición para centrarlo
            val x = (w - logoSize) / 2f
            val y = (h - logoSize) / 2f

            // Dibujamos el logo encima del QR
            canvas.drawBitmap(scaledLogo, x, y, null)
        }

        bitmap
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}