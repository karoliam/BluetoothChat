package com.karoliinamultas.bluetoothchat

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel

class ImageViewModel(application: Application): AndroidViewModel(application) {
    var hasImage = mutableStateOf(false)
    var imageUri = mutableStateOf<Uri?>(null)
    val bitmap = mutableStateOf<Bitmap>(Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888))

    // This returns camera image bitmap
    fun bitmapImage() : Bitmap {
        val context = getApplication<BluetoothChatApplication>()
        if (hasImage.value && imageUri.value != null) {
            val inputStream = context.contentResolver.openInputStream(imageUri.value!!)
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeStream(inputStream, null, options)
            inputStream?.close()

            val maxSize = 1024 // Set your max size here
            var scale = 1
            while (options.outWidth / scale / 2 >= maxSize && options.outHeight / scale / 2 >= maxSize) {
                scale *= 2
            }

            val options2 = BitmapFactory.Options()
            options2.inSampleSize = scale
            val inputStream2 = context.contentResolver.openInputStream(imageUri.value!!)
            bitmap.value = BitmapFactory.decodeStream(inputStream2, null, options2)!!
            inputStream2?.close()
        }
        return bitmap.value
    }
}