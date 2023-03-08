package com.karoliinamultas.bluetoothchat

import android.app.Application
import android.bluetooth.BluetoothAdapter
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.karoliinamultas.bluetoothchat.BluetoothChatApplication
import com.karoliinamultas.bluetoothchat.ui.DrawingPadViewModel

object AppViewModelProvider {

    val Factory = viewModelFactory {

        // TODO : Initilizers for viewmodels like the one below

        initializer {
            MyViewModel(
                bluetoothChatApplication().container.messagesRepository
            )
        }
        initializer {
            DrawingPadViewModel(
                bluetoothChatApplication()
            )
        }
    }
}

fun CreationExtras.bluetoothChatApplication(): BluetoothChatApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as BluetoothChatApplication)