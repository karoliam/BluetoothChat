package com.karoliinamultas.bluetoothchat.ui.chat
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.TopAppBar
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.karoliinamultas.bluetoothchat.MyViewModel
import com.karoliinamultas.bluetoothchat.Screen



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowChats(navController: NavController, mBluetoothAdapter: BluetoothAdapter, model: MyViewModel){
    //Statusbar
    val systemUiController = rememberSystemUiController()
    systemUiController.setStatusBarColor(MaterialTheme.colorScheme.surface)

    Scaffold(
        topBar = {
            TopAppBar(
                elevation = 8.dp,
                backgroundColor = MaterialTheme.colorScheme.surface,
                title = {
                    Text(
                        "Restroom Chat",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontSize = 20.sp,
                    )
                },
            )
        },
        content = { innerPadding ->
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding)) {
                ShowDevices(navController, mBluetoothAdapter, model)
                DeviceScan()
            }
        }
    )
}

    @SuppressLint("MissingPermission")
    @Composable
    fun ShowDevices(
        navController: NavController,
        mBluetoothAdapter: BluetoothAdapter,
        model : MyViewModel
    ) {
        val scanResults: Set<String>? by model.beacons.observeAsState()

        LazyColumn(
            modifier = Modifier

                .fillMaxWidth()
        ) {
            items(scanResults?.size ?: 0) { index ->
                Column {
                    Column(
                        modifier = Modifier
                            .clickable {
                                model.beaconFilter.postValue(scanResults?.elementAt(index))
                                model.scanDevices(mBluetoothAdapter.bluetoothLeScanner)
                                model.stopScanBeacons(mBluetoothAdapter.bluetoothLeScanner)
                                model.chatRoomOnJoinDatabaseChanges(scanResults?.elementAt(index).toString())
                                navController.navigate(Screen.ChatWindow.route)
                            }
                            .background(
                                color = MaterialTheme.colorScheme.surface,
                                shape = RectangleShape
                            )
                            .border(
                                Dp.Hairline,
                                MaterialTheme.colorScheme.background,
                                RectangleShape
                            )
                            .fillMaxWidth(1f)
                            .height(70.dp)
                            .padding(24.dp)
                    ) {
                        Text(
                            text = scanResults?.elementAt(index) ?: "Unknown Device",
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.align(CenterHorizontally)
                        )
                    }
                }
            }
        }
    }
        @SuppressLint("MissingPermission")
        @Composable
        fun DeviceScan() {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Column(horizontalAlignment = CenterHorizontally) {

                    CircularProgressIndicator()

                    Spacer(modifier = Modifier.height(15.dp))
                    Text(
                        text = "Scanning for chats",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Light
                    )
                }
            }

        }
