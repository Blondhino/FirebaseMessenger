package com.blondi.firebasemessenger.utils

import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import java.io.ByteArrayOutputStream
import kotlin.math.roundToInt

class ImageResizerCompresser (){
    fun resizeCompress(imageUri: Uri, QualtiyPercentage: Int, maxImageSizeInPx :Int):ByteArray {
        var bmp = MediaStore.Images.Media.getBitmap(FirebaseMessengerApp.getAppContext().contentResolver, imageUri)
        val baos = ByteArrayOutputStream()
        bmp=scaleDownBitmap(bmp,maxImageSizeInPx.toFloat(),true)
        bmp.compress(Bitmap.CompressFormat.JPEG, QualtiyPercentage, baos)
        return baos.toByteArray()
    }
    private fun scaleDownBitmap(realImage : Bitmap, maxImageSize : Float, filter: Boolean): Bitmap {
        val ratio = (maxImageSize / realImage.width).coerceAtMost(maxImageSize / realImage.height)
        val width = (ratio * realImage.width).roundToInt()
        val height = (ratio * realImage.height).roundToInt()
        val newBitmap = Bitmap.createScaledBitmap(realImage, width,
            height, filter)
        return newBitmap
    }
}