package mx.edu.utez.itheraqr.ui.screens.components.manage

import android.graphics.Bitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix

fun generarQr(text: String, width: Int, height: Int): Bitmap {
    val bitMatrix: BitMatrix =
        MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, width, height, null)
    val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    for (x in 0 until width) {
        for (y in 0 until height) {
            bmp.setPixel(x, y, if (bitMatrix[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE)
        }
    }
    return bmp
}