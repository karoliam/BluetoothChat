package com.karoliinamultas.bluetoothchat.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.TopCenter
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.karoliinamultas.bluetoothchat.R
import com.karoliinamultas.bluetoothchat.Screen


@Composable
    fun StartScreen(navController: NavController){
    //Statusbar
    val systemUiController = rememberSystemUiController()
    systemUiController.setStatusBarColor(Color.Black)

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
                Text("Go chat by pressing start", Modifier.align(CenterHorizontally).padding(0.dp, 500.dp, 0.dp, 30.dp ), fontSize = 14.sp, color = Color.Black)
                Button(
                    onClick = { navController.navigate(Screen.ShowChats.route)},
                    Modifier.align(CenterHorizontally).padding(30.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black, contentColor = Color.White),
                ) {
                    Text("Start")
                }
            }
        }
    }
