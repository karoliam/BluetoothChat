package com.karoliinamultas.bluetoothchat

import android.app.Application
import com.karoliinamultas.bluetoothchat.data.AppContainer
import com.karoliinamultas.bluetoothchat.data.AppDataContainer

class BluetoothChatApplication : Application() {

    lateinit var container: AppContainer
    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}