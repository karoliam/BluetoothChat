package com.karoliinamultas.bluetoothchat.ui.chat
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.TopAppBar
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.karoliinamultas.bluetoothchat.MyViewModel
import com.karoliinamultas.bluetoothchat.Screen


private const val TAG = "DeviceScanCompose"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowChats(navController: NavController, mBluetoothAdapter: BluetoothAdapter, model: MyViewModel){
    //Statusbar
    val systemUiController = rememberSystemUiController()
    systemUiController.setStatusBarColor(MaterialTheme.colorScheme.surface)
    // Create a boolean variable
    // to store the display menu state
    var mDisplayMenu by remember { mutableStateOf(false) }

    // fetching local context
    val mContext = LocalContext.current

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
                actions = {
                    // Creating Icon button for dropdown menu
                    IconButton(onClick = { mDisplayMenu = !mDisplayMenu }) {
                        Icon(
                            imageVector = Icons.Filled.Menu,
                            contentDescription = "Menu button"
                        )
                    }

                    DropdownMenu(
                        expanded = mDisplayMenu,
                        onDismissRequest = { mDisplayMenu = false }
                    ) {
                        // Creating dropdown menu item, on click
                        // would create a Toast message
                        androidx.compose.material.DropdownMenuItem(onClick = { Toast.makeText(mContext, "Settings", Toast.LENGTH_SHORT).show() }){
                            Text(text = "Settings")
                        }
                        androidx.compose.material.DropdownMenuItem(onClick = {  }){
                            Text(text = "About")
                        }
                    }
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
        /*scanResults: Map<String, BluetoothDevice>,
        onClick: (BluetoothDevice?) -> Unit*/
        mBluetoothAdapter: BluetoothAdapter,
        model : MyViewModel
    ) {
        val scanResults: Set<String>? by model.beacons.observeAsState()

//        Button(onClick = {
//            model.scanDevices(mBluetoothAdapter.bluetoothLeScanner)
//            navController.navigate(Screen.ChatWindow.route)
//                         }, Modifier.padding(40.dp)){ Text(text = "Dummy Button")}
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
//                                model.messages.postValue(listOf())
                                // delete other chat histories
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
//            itemsIndexed(scanResults.keys.toList()) { _, key ->
//                Column {
//                    Column(
//                        modifier = Modifier
//                            .clickable {
//                                val device: BluetoothDevice? = scanResults.get(key = key)
//                                onClick(device)
//                            }
//                            .background(color = randomBack, shape = RoundedCornerShape(10.dp))
//                            .fillMaxWidth()
//                            .padding(7.dp)
//                    ) {
//                        Text(
//                            text = scanResults[key]?.name ?: "Unknown Device",
//                            color = randomTeksti
//                        )
//                        Spacer(modifier = Modifier.size(10.dp))
//                        Text(
//                            text = scanResults[key]?.address ?: "",
//                            fontWeight = FontWeight.Light,
//                            color = randomTeksti
//                        )
//                    }
//                    Spacer(modifier = Modifier.size(10.dp))
//                }
//            }
//        }
        }
    }
        @SuppressLint("MissingPermission")
        @Composable
        fun DeviceScan() {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {

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
