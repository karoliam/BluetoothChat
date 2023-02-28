package com.karoliinamultas.bluetoothchat

sealed class Screen (val route: String) {
    object ChatWindow : Screen("chat_window")
    object ShowChats : Screen("chat_list")
    object StartScreen : Screen("start_screen")
    object DrawingPad : Screen("draw_screen")
}