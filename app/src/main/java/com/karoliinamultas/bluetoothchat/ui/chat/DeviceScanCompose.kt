package com.karoliinamultas.bluetoothchat.ui.chat
import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.graphics.drawable.shapes.Shape
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.karoliinamultas.bluetoothchat.bluetooth.ChatServer
import com.karoliinamultas.bluetoothchat.states.DeviceScanViewState
import kotlin.math.round


private const val TAG = "DeviceScanCompose"

object DeviceScanCompose {
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
        scanResults: Map<String, BluetoothDevice>,
        onClick: (BluetoothDevice?) -> Unit
    ) {
        LazyColumn(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth()
        ) {
            itemsIndexed(scanResults.keys.toList()) { _, key ->
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
            }
        }
    }

    @SuppressLint("MissingPermission")
    @Composable
    fun DeviceScan(deviceScanViewState: DeviceScanViewState, onDeviceSelected: () -> Unit) {
        when (deviceScanViewState) {
            is DeviceScanViewState.ActiveScan -> {
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
            is DeviceScanViewState.ScanResults -> {
                ShowDevices(scanResults = deviceScanViewState.scanResults, onClick = {
                    Log.i(TAG, "Device Selected ${it!!.name ?: ""}")
                    ChatServer.setCurrentChatConnection(device = it!!)
                    onDeviceSelected()
                })
            }
            is DeviceScanViewState.Error -> {
                Text(text = deviceScanViewState.message)
            }
            else -> {
                Text(text = "Nothing")
            }
        }
    }

}