package com.example.core.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import java.io.ByteArrayOutputStream
import java.io.InputStream

object ImageUtils {
    const val MAX_IMAGE_SIZE_KB = 500
    const val MAX_IMAGE_DIMENSION = 800

    fun processImageUri(context: Context, uri: Uri): String? {
        try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            if (inputStream == null) return null
            val originalBitmap = BitmapFactory.decodeStream(inputStream)
            inputStream.close()
            
            if (originalBitmap == null) return null

            // Resize
            val resizedBitmap = resizeBitmap(originalBitmap, MAX_IMAGE_DIMENSION)
            
            // Compress
            var quality = 80
            var byteArray: ByteArray
            do {
                val outputStream = ByteArrayOutputStream()
                resizedBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
                byteArray = outputStream.toByteArray()
                quality -= 10
            } while (byteArray.size > MAX_IMAGE_SIZE_KB * 1024 && quality > 10)

            // Validate final size
            if (byteArray.size > MAX_IMAGE_SIZE_KB * 1024) {
                // If even at lowest quality it's too big, return null (handled by UI)
                throw Exception("Imagen demasiado grande (máximo recomendado: ${MAX_IMAGE_SIZE_KB}KB)")
            }

            return Base64.encodeToString(byteArray, Base64.DEFAULT)
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    private fun resizeBitmap(bitmap: Bitmap, maxSize: Int): Bitmap {
        var width = bitmap.width
        var height = bitmap.height

        if (width <= maxSize && height <= maxSize) return bitmap

        val bitmapRatio = width.toFloat() / height.toFloat()
        if (bitmapRatio > 1) {
            width = maxSize
            height = (width / bitmapRatio).toInt()
        } else {
            height = maxSize
            width = (height * bitmapRatio).toInt()
        }
        return Bitmap.createScaledBitmap(bitmap, width, height, true)
    }

    fun decodeBase64ToBitmap(base64Str: String?): Bitmap? {
        if (base64Str.isNullOrEmpty()) return null
        return try {
            val decodedBytes = Base64.decode(base64Str, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
