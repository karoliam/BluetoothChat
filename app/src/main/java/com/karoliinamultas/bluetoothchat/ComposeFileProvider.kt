package com.karoliinamultas.bluetoothchat

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.twotone.Call
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.karoliinamultas.bluetoothchat.ui.DrawingPadViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.net.URL
import java.util.*

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
    context: Context,
    model: ImageViewModel,
    viewModel: DrawingPadViewModel,
    myViewModel: MyViewModel,
    navController: NavController
) {
//    var hasImage by remember {
//        mutableStateOf(false)
//    }
//    var imageUri by remember {
//        mutableStateOf<Uri?>(null)
//    }
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            model.hasImage.value = uri != null
            model.imageUri.value = uri
        }
    )

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            model.hasImage.value = success
        }
    )

    val context = LocalContext.current
    val activity = LocalContext.current as Activity
    Row(modifier = Modifier.fillMaxSize()) {


    if(model.imageUri.value != null && model.hasImage.value) {

//        // 5
//        AsyncImage(
//            model = model.imageUri.value,
//            modifier = Modifier.fillMaxWidth(),
//            contentDescription = "Selected image",
//        )
        LaunchedEffect(key1 = 1) {
            val byteArray = viewModel.bitmapToByteArray(model.bitmapImage())
            Log.d("DBG", "byteArray before compress ${byteArray.size}")
            myViewModel.uploadImage("6d207e02198a847aa98d0a2a901485a5",
                Base64.getEncoder().encodeToString(byteArray), "json", navController)
//            val bitmap = model.bitmapImage()
        }
//
//        Image(
//            bitmap = bitmap.asImageBitmap(),
//            contentDescription = "kuva",
//            modifier = Modifier.fillMaxWidth()
//        )
    }


        IconButton(
            onClick = {
                val cameraPermission = Manifest.permission.CAMERA
                if (ContextCompat.checkSelfPermission(
                        context,
                        cameraPermission
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    // Permission is already granted, launch the camera
                    val uri = ComposeFileProvider.getImageUri(context)
                    model.imageUri.value = uri
                    Log.d("uri", model.imageUri.toString())
                    cameraLauncher.launch(uri)
                } else {
                    // Permission is not granted, request it
                    ActivityCompat.requestPermissions(
                        activity,
                        arrayOf(cameraPermission),
                        REQUEST_CAMERA_PERMISSION
                    )
                }
            },
            modifier = Modifier
                .height(60.dp)
                .width(60.dp)
                .padding(0.dp, 6.dp, 0.dp, 0.dp),
            colors = IconButtonDefaults.iconButtonColors(contentColor = MaterialTheme.colorScheme.background),
            content = {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.photo_camera),
                    contentDescription = "Camera button"
                )
            }
        )
    }
}



@Composable
fun GalleryButton(
    context: Context
) {
    var hasImage by remember {
        mutableStateOf(false)
    }
    var imageUri by remember {
        mutableStateOf<Uri?>(null)
    }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            hasImage = uri != null
            imageUri = uri
        }
    )

    IconButton(
        onClick = {
            imagePicker.launch("picture/*")
        },
        modifier = Modifier
            .height(60.dp)
            .width(60.dp)
            .padding(0.dp, 6.dp, 0.dp, 0.dp),
        colors = IconButtonDefaults.iconButtonColors(contentColor = MaterialTheme.colorScheme.background),
        content = {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.gallery_thumbnail),
                contentDescription = "Camera button"
            )
        }
    )
}