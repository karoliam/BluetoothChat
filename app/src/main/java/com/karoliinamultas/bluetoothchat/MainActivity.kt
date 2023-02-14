package com.karoliinamultas.bluetoothchat

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.karoliinamultas.bluetoothchat.bluetooth.ChatServer
import com.karoliinamultas.bluetoothchat.states.DeviceConnectionState
import com.karoliinamultas.bluetoothchat.ui.chat.ChatCompose
import com.karoliinamultas.bluetoothchat.ui.chat.DeviceScanCompose
import com.karoliinamultas.bluetoothchat.ui.chat.DeviceScanViewModel
import com.karoliinamultas.bluetoothchat.ui.theme.BluetoothChatTheme
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener

private const val TAG = "MainActivityTAG"
private val REQUEST_CAMERA_PERMISSION = 1
private val REQUEST_IMAGE_CAPTURE = 1

class MainActivity : ComponentActivity() {

    private val viewModel: DeviceScanViewModel by viewModels()

    override fun onStop() {
        super.onStop()
        ChatServer.stopServer()
    }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
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

                LaunchedEffect(key1 = result.value){
                    if(result.value == RESULT_OK){
                        ChatServer.startServer(application)
                        viewModel.startScan()
                    }
                }
                    Surface(
                        modifier = Modifier
                            .fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        //Statusbar
                        val systemUiController = rememberSystemUiController()
                        systemUiController.setStatusBarColor(MaterialTheme.colorScheme.background)

                        val deviceScanningState by viewModel.viewState.observeAsState()

                        val deviceConnectionState by ChatServer.deviceConnection.observeAsState()

                        var isChatOpen by remember {mutableStateOf(false)}
                        Scaffold(
                            topBar = {
                                CenterAlignedTopAppBar(
                                    colors = TopAppBarDefaults.smallTopAppBarColors(
                                        containerColor = MaterialTheme.colorScheme.background,
                                        titleContentColor = MaterialTheme.colorScheme.onBackground,
                                        navigationIconContentColor = MaterialTheme.colorScheme.onBackground,
                                        actionIconContentColor = MaterialTheme.colorScheme.onBackground
                                    ),
                                    title = {
                                        Text(
                                            "Restroom Chat",
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    },
                                            navigationIcon = {
                                        IconButton(onClick = { isChatOpen = false }) {
                                            Icon(
                                                imageVector = Icons.Filled.ArrowBack,
                                                contentDescription = "Localized description"
                                            )
                                        }
                                    },
                                    actions = {
                                        IconButton(onClick = { /* doSomething() */ }) {
                                            Icon(
                                                imageVector = Icons.Filled.Menu,
                                                contentDescription = "Localized description"
                                            )
                                        }
                                    },
                                )
                            },
                            content = { innerPadding ->
                                Box(
                                    contentAlignment = Alignment.TopCenter,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(innerPadding)
                                ) {
                                    if (deviceScanningState != null && !isChatOpen || deviceConnectionState == DeviceConnectionState.Disconnected) {
                                        Column (){
                                            Text(
                                                text = "Choose a chat to join",
                                                modifier = Modifier
                                                    .padding(30.dp)
                                                    .align(alignment = CenterHorizontally),
                                                fontSize = 20.sp,
                                                fontWeight = FontWeight.Bold,
                                            )
                                            Spacer(modifier = Modifier.height(10.dp))
                                            DeviceScanCompose.DeviceScan(deviceScanViewState = deviceScanningState!!) {
                                                isChatOpen = true
                                            }
                                        }

                                    } else if (deviceScanningState != null && deviceConnectionState is DeviceConnectionState.Connected) {
                                        ChatCompose.Chats((deviceConnectionState as DeviceConnectionState.Connected).device.name)
                                    } else {
                                        Text(text = "Nothing")
                                    }
                                }
                            }
                        )
                    }
                }
            }
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
    }

    }
