package com.karoliinamultas.bluetoothchat

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.net.URL

private val REQUEST_CAMERA_PERMISSION = 1
class ComposeFileProvider : FileProvider(
    R.xml.filepaths
) {
    companion object {
        fun getImageUri(context: Context): Uri {
            val directory = File(context.cacheDir, "images")
            directory.mkdirs()
            val file = File.createTempFile(
                "selected_image_",
                ".jpg",
                directory,
            )
            val authority = context.packageName + ".fileprovider"
            return getUriForFile(
                context,
                authority,
                file,
            )
        }
    }

}

@Composable
fun ShowImage(urlText: URL) {
    var savedBitmap by remember { mutableStateOf(Bitmap.createBitmap(1, 1, Bitmap.Config.ALPHA_8)) }
    LaunchedEffect(urlText) {
        savedBitmap = getImage(urlText)
    }
    Image(
        bitmap = savedBitmap.asImageBitmap(),
        contentDescription = "image",
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .padding(start = 16.dp, top = 16.dp, bottom = 16.dp)
            .clip(RoundedCornerShape(8.dp))
            .size(56.dp)
    )
}
private suspend fun getImage(url: URL): Bitmap =
    withContext(Dispatchers.IO) {
        val myConn = url.openStream()
        return@withContext BitmapFactory.decodeStream(myConn)
    }


@Composable
fun CameraButton(
    context: Context
) {
    var hasImage by remember {
        mutableStateOf(false)
    }
    var imageUri by remember {
        mutableStateOf<Uri?>(null)
    }

//    val imagePicker = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.GetContent(),
//        onResult = { uri ->
//            hasImage = uri != null
//            imageUri = uri
//        }
//    )

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            hasImage = success
        }
    )

    val context = LocalContext.current
    val activity = LocalContext.current as Activity
    IconButton(
        onClick = {
            val cameraPermission = Manifest.permission.CAMERA
            if (ContextCompat.checkSelfPermission(context, cameraPermission) == PackageManager.PERMISSION_GRANTED) {
                // Permission is already granted, launch the camera
                val uri = ComposeFileProvider.getImageUri(context)
                imageUri = uri
                cameraLauncher.launch(uri)
            } else {
                // Permission is not granted, request it
                ActivityCompat.requestPermissions(activity, arrayOf(cameraPermission), REQUEST_CAMERA_PERMISSION)
            }
        },
        modifier = Modifier
            .height(60.dp)
            .width(60.dp)
            .padding(0.dp, 6.dp, 0.dp, 0.dp),
        colors = IconButtonDefaults.iconButtonColors(contentColor = MaterialTheme.colorScheme.background),
        content = {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = "Camera button"
            )
        }
    )
}