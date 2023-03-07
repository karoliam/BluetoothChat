package com.karoliinamultas.bluetoothchat

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.compose.ui.platform.LocalFocusManager
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.karoliinamultas.bluetoothchat.service.ChatForegroundService
import com.karoliinamultas.bluetoothchat.ui.DrawingPad
import com.karoliinamultas.bluetoothchat.ui.StartScreen
import com.karoliinamultas.bluetoothchat.ui.chat.*
import com.karoliinamultas.bluetoothchat.ui.theme.BluetoothChatTheme
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener

private const val TAG = "MainActivityTAG"
private val REQUEST_CAMERA_PERMISSION = 1
private val REQUEST_FOREGROUND_SERVICE_PERMISSION_CODE = 2
private val REQUEST_IMAGE_CAPTURE = 1
private lateinit var chatForegroundServiceIntent: Intent

class MainActivity : ComponentActivity() {
    var mBluetoothAdapter: BluetoothAdapter? = null
    lateinit var model: MyViewModel
    @RequiresApi(Build.VERSION_CODES.P)
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        mBluetoothAdapter = bluetoothManager.adapter

        val notificationManagerWrapper = NotificationManagerWrapperImpl(this)
        chatForegroundServiceIntent = Intent(this, ChatForegroundService::class.java)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.FOREGROUND_SERVICE) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, ask for permission
            val permission = arrayOf(Manifest.permission.FOREGROUND_SERVICE)
            requestPermissions(permission, REQUEST_FOREGROUND_SERVICE_PERMISSION_CODE)

        }

//        model = MyViewModel(mBluetoothAdapter!!)
        setContent {
        val model: MyViewModel = viewModel(factory = AppViewModelProvider.Factory)
            //Navia
            val navController = rememberNavController()

            BluetoothChatTheme() {
                val result = remember { mutableStateOf<Int?>(100) }
                val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                    result.value = it.resultCode
                }
                LaunchedEffect(key1 = true){
                    Dexter.withContext(this@MainActivity)
                        .withPermissions(
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.BLUETOOTH_ADVERTISE,
                            Manifest.permission.BLUETOOTH_CONNECT,
                            Manifest.permission.BLUETOOTH_SCAN,
                            Manifest.permission.BLUETOOTH,
                            Manifest.permission.BLUETOOTH_ADMIN,
                        )
                        .withListener(object : MultiplePermissionsListener {
                            override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                                val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                                launcher.launch(intent)
                            }
                            override fun onPermissionRationaleShouldBeShown(
                                permissions: List<PermissionRequest?>?,
                                token: PermissionToken?
                            ) {
                            }
                        })
                        .check()
                }

                Surface(
                    modifier = Modifier
                        .fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val context = LocalContext.current
                    NavHost(navController = navController, startDestination = Screen.StartScreen.route) {
                        composable(route = Screen.StartScreen.route){
                            StartScreen(navController = navController, mBluetoothAdapter!!, model)
                        }
                        composable(route = Screen.ShowChats.route) {
                            ShowChats(navController = navController, mBluetoothAdapter!!, model)
                        }
                        composable(route = Screen.ChatWindow.route){
                            ChatWindow(navController = navController, notificationManagerWrapper, mBluetoothAdapter!!, model)
                        }
                        composable(route = Screen.DrawingPad.route){
                            DrawingPad(context, navController = navController, model, mBluetoothAdapter!!)
                        }
                    }
                }
                }
            }

        }

    override fun onPause() {
        super.onPause()
        chatForegroundServiceIntent = Intent(this, ChatForegroundService::class.java)
        startForegroundService(chatForegroundServiceIntent)
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted, launch the camera
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
            } else {
                // Permission is denied, show a message or disable camera-related functionality
                Toast.makeText(this, "Camera permission is required to take photos", Toast.LENGTH_SHORT).show()
            }
        }
        if (requestCode == REQUEST_FOREGROUND_SERVICE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                chatForegroundServiceIntent = Intent(this, ChatForegroundService::class.java)
                startForegroundService(chatForegroundServiceIntent)
            }
        } else {
            // Permission is not granted, show a message to the user
        }
    }

    }






