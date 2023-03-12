package com.karoliinamultas.bluetoothchat.ui

import android.bluetooth.BluetoothAdapter
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.karoliinamultas.bluetoothchat.MyViewModel
import com.karoliinamultas.bluetoothchat.R
import com.karoliinamultas.bluetoothchat.Screen


@Composable
    fun StartScreen(navController: NavController, mBluetoothAdapter: BluetoothAdapter, model: MyViewModel){
    //Statusbar
    val systemUiController = rememberSystemUiController()
    systemUiController.setStatusBarColor(Color(0xFFD3D3D3))


        Box(
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
            ) {
            Image(
                painter = painterResource(id = R.drawable.test),
                contentDescription = null,
                Modifier.fillMaxSize(),
                contentScale = ContentScale.FillBounds
            )
            Column(Modifier) {
                Text("Go chat by pressing start",
                    Modifier
                        .align(CenterHorizontally)
                        .padding(0.dp, 480.dp, 0.dp, 80.dp), fontSize = 14.sp, color = Color.Black)
                Text("Start", Modifier.clickable {
                    navController.navigate(Screen.ShowChats.route)
                    model.scanBeacons(mBluetoothAdapter.bluetoothLeScanner)
                }.align(CenterHorizontally), color = Color.Black, fontSize = 20.sp)
            }
        }
    }