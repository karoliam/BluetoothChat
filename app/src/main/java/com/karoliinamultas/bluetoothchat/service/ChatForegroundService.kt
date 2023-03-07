package com.karoliinamultas.bluetoothchat.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.ParcelUuid
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.core.app.NotificationCompat
import androidx.lifecycle.MutableLiveData
import com.karoliinamultas.bluetoothchat.MyViewModel
import com.karoliinamultas.bluetoothchat.R
import com.karoliinamultas.bluetoothchat.ui.chat.NotificationManagerWrapper
import com.karoliinamultas.bluetoothchat.ui.chat.NotificationManagerWrapperImpl
import com.karoliinamultas.bluetoothchat.ui.chat.ShowChat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.internal.notify
import java.nio.charset.Charset
import java.util.*

class ChatForegroundService() : Service() {

    companion object {
        const val CHANNEL_ID = "my_channel_id"
        const val MESSAGE_NOTIFICATION_ID = 2
        val UUID_APP_SERVICE = UUID.fromString("cc17cc5a-b1d6-11ed-afa1-0242ac120002")

    }
    private lateinit var currentAdvertisingSet: AdvertisingSet
    private val messages = MutableLiveData<List<String>>(listOf("message"))
    private var uuids = listOf<String>()
    private val mResults = hashMapOf<String, ScanResult>()
    private val fScanning = MutableLiveData(false)
    private val scanResults = MutableLiveData<List<ScanResult>>(null)
    private val dataToSend = MutableLiveData<ByteArray>("".toByteArray())
    private lateinit var bluetoothManager: BluetoothManager
    private lateinit var mBluetoothAdapter: BluetoothAdapter


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//
//        val channel = NotificationChannel(
//            CHANNEL_ID,
//            "My Channel",
//            NotificationManager.IMPORTANCE_DEFAULT
//        )
//        notificationManager.createNotificationChannel(channel)
//
//        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
//            .setContentTitle("New message received")
//            .setContentText("You have a new message")
//            .setSmallIcon(R.drawable.highlighter_size_4_40px)
//            .setAutoCancel(true)
//            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//
//        notificationManager.notify(MESSAGE_NOTIFICATION_ID.toInt(), builder.build())
        val notificationManagerWrapper = NotificationManagerWrapperImpl(this)
        val notif = notificationManagerWrapper.showNotification(title = "New message", "You received a new dog")
        startForeground(MESSAGE_NOTIFICATION_ID, notif)
        bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        mBluetoothAdapter = bluetoothManager.adapter
        scanDevices(mBluetoothAdapter.bluetoothLeScanner)
        return START_STICKY
    }
    override fun onBind(intent: Intent): IBinder? {
        return null
    }


    // callBack is what triggers when scanner found needed service uuid
    private val leScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            val serviceData = result.scanRecord?.getServiceData(ParcelUuid(UUID_APP_SERVICE))
            val splitMessage = String(serviceData ?: "".toByteArray(Charsets.UTF_8), Charset.defaultCharset()).split("/*/")
            if (!uuids.contains(splitMessage[0])) {
                messages.postValue(messages.value?.plus(splitMessage[1]))
                uuids += splitMessage[0]
                Log.d("DBG", "message ${splitMessage[1]}")


            }
        }
    }

    @SuppressLint("MissingPermission")
    fun scanDevices(bluetoothLeScanner: BluetoothLeScanner) {

        // Scan filter and options to filter for
        fun buildScanFilters(): List<ScanFilter> {
            val builder = ScanFilter.Builder()
            builder.setServiceUuid(ParcelUuid(UUID_APP_SERVICE))
            val filter = builder.build()
            return listOf(filter)
        }

        GlobalScope.launch(Dispatchers.IO) {
            fScanning.postValue(true)

            val settings = ScanSettings.Builder()
                .setLegacy(false)
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
                .setReportDelay(0)
                .build()

            bluetoothLeScanner.startScan(buildScanFilters(), settings, leScanCallback)
        }
    }

}
