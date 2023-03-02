package com.karoliinamultas.bluetoothchat.ui

import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
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
import java.util.zip.Deflater
import java.util.zip.Inflater


class DrawingPadViewModel(context: Context): ViewModel() {

    fun compressByteArray(byteArray: ByteArray): ByteArray {
        val input = byteArray
        val output = ByteArray(input.size*4)
        val compressor = Deflater().apply {
            setInput(input)
            finish()
        }
        val compressedDataLength = compressor.deflate(output)
        Log.d("DBG", "compress $compressedDataLength")
        Log.d("DBG", "output copy ${output.copyOfRange(0, compressedDataLength).size}")
        return output.copyOfRange(0, compressedDataLength)
    }

    fun decompressByteArray(byteArray: ByteArray): ByteArray {
        val input = byteArray
        val output = ByteArray(input.size*4)
        val inflater = Inflater().apply {
            setInput(input)
        }
        val decompressedDataLength = inflater.inflate(output)
        Log.d("DBG", "decompress $decompressedDataLength")
        Log.d("DBG", "output copy ${output.copyOfRange(0, decompressedDataLength).size}")
        return output.copyOfRange(0, decompressedDataLength)
    }

//    fun decompressByteArray(byteArray: ByteArray): ByteArray {
//        val inflater = Inflater()
//        val outputStream = ByteArrayOutputStream()
//
//        return outputStream.use {
//            val buffer = ByteArray(1024)
//
//            inflater.setInput(byteArray)
//            var count = -1
//            while (count != 0) {
//                count = inflater.inflate(buffer)
//                outputStream.write(buffer, 0, count)
//            }
//            inflater.end()
//            outputStream.toByteArray()
//        }
//    }

//    fun bitmapToByteArray(bitmap: Bitmap, format: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG, quality: Int = 90): ByteArray {
//        val outputStream = ByteArrayOutputStream()
//        bitmap.compress(format, quality, outputStream)
//        val byteArray = outputStream.toByteArray()
//        compressByteArray(byteArray)
//        Log.d("DBG", "${byteArray.size}")
//        return byteArray
//    }
//    fun decompressByteArray

    val context = context
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



//
//        val outputStream = ByteArrayOutputStream()
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
//        var quality = 90
//        while (outputStream.toByteArray().size / 1024 > 1 && quality > 0) {
//            outputStream.reset()
//            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
//            quality -= 10
//        }
//        return outputStream.toByteArray()
//    }

