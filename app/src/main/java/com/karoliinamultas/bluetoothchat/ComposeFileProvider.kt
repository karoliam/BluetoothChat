package com.karoliinamultas.bluetoothchat

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.karoliinamultas.bluetoothchat.bluetooth.ChatServer
import java.io.File

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
            .width(80.dp)
            .padding(0.dp, 6.dp, 0.dp, 0.dp),
        content = {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = "Camera button"
            )
        }
    )
}