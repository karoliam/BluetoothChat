package com.karoliinamultas.bluetoothchat.ui

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import com.karoliinamultas.bluetoothchat.BluetoothChatApplication
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.format
import id.zelory.compressor.constraint.quality
import id.zelory.compressor.constraint.resolution
import id.zelory.compressor.constraint.size
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.nio.file.Files


class DrawingPadViewModel(application: Application): AndroidViewModel(application) {

    val context = getApplication<BluetoothChatApplication>()
    suspend fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
        val outputStream = ByteArrayOutputStream()
        val actualFile = File(context.cacheDir, "drawing")
        withContext(Dispatchers.IO) {
            actualFile.createNewFile()
        }
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        val byteArray = outputStream.toByteArray()
        withContext(Dispatchers.IO) {
            val fos = FileOutputStream(actualFile)
            fos.write(byteArray)
            fos.flush()
            fos.close()
        }



        val compressedImageFile = Compressor.compress(context, actualFile) {
            resolution(200, 400)
            quality(70)
            format(Bitmap.CompressFormat.JPEG)
            size(3_000_00) // 1 MB
        }

        return withContext(Dispatchers.IO) {
            Files.readAllBytes(compressedImageFile.toPath())
        }
    }
}