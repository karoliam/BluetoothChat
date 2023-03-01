package com.karoliinamultas.bluetoothchat.ui.chat
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
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
                elevation = 5.dp,
                backgroundColor = MaterialTheme.colorScheme.surface,
                title = {
                    Text(
                        "Restroom Chat",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontSize = 24.sp,
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

val tekstit = listOf(
        Color(0xFF00FDDC),
        Color(0xFFFF729F),
        Color(0xFF04E762),
        Color(0xFFFDE74C),
        Color(0xFFFF4365))
    val randomTeksti = tekstit.random()

    val backgroundit = listOf(
        Color(0xFF111D4A),
        Color(0xFF43AA8B),
        Color(0xFF8B635C),
        Color(0xFF60594D),
        Color(0xFF93A29B))
    val randomBack = backgroundit.random()

    @SuppressLint("MissingPermission")
    @Composable
    fun ShowDevices(
        navController: NavController,
        /*scanResults: Map<String, BluetoothDevice>,
        onClick: (BluetoothDevice?) -> Unit*/
        mBluetoothAdapter: BluetoothAdapter,
        model : MyViewModel
    ) {
        Button(onClick = {
            model.scanDevices(mBluetoothAdapter.bluetoothLeScanner)
            navController.navigate(Screen.ChatWindow.route)
                         }, Modifier.padding(40.dp)){ Text(text = "Dummy Button")}
        LazyColumn(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth()
        ) {
            /*itemsIndexed(scanResults.keys.toList()) { _, key ->
                Column {
                    Column(
                        modifier = Modifier
                            .clickable {
                                val device: BluetoothDevice? = scanResults.get(key = key)
                                onClick(device)
                            }
                            .background(color = randomBack, shape = RoundedCornerShape(10.dp))
                            .fillMaxWidth()
                            .padding(7.dp)
                    ) {
                        Text(
                            text = scanResults[key]?.name ?: "Unknown Device",
                            color = randomTeksti
                        )
                        Spacer(modifier = Modifier.size(10.dp))
                        Text(
                            text = scanResults[key]?.address ?: "",
                            fontWeight = FontWeight.Light,
                            color = randomTeksti
                        )
                    }
                    Spacer(modifier = Modifier.size(10.dp))
                }
            }*/
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
                            text = "Scanning for devices",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Light
                        )
                    }
                }

            }