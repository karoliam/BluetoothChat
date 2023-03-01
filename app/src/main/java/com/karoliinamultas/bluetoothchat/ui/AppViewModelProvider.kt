package com.karoliinamultas.bluetoothchat.ui

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.karoliinamultas.bluetoothchat.BluetoothChatApplication

object AppViewModelProvider {

    val Factory = viewModelFactory {

        // TODO : Initilizers for viewmodels like the one below
        /**
        initializer {
            SomeVIewModel(bluetoothChatApplication().container.messagesRepository)
        }
        */
    }
}

fun CreationExtras.bluetoothChatApplication(): BluetoothChatApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as BluetoothChatApplication)