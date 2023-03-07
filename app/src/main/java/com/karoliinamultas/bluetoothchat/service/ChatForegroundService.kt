package com.karoliinamultas.bluetoothchat.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.*
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.os.ParcelUuid
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.ViewModelFactoryDsl
import androidx.lifecycle.viewmodel.compose.viewModel
import com.karoliinamultas.bluetoothchat.AppViewModelProvider
import com.karoliinamultas.bluetoothchat.MyViewModel
import com.karoliinamultas.bluetoothchat.R
import com.karoliinamultas.bluetoothchat.ui.chat.NotificationManagerWrapperImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.nio.charset.Charset
import java.util.*

class ChatForegroundService() : Service() {

    companion object {
        const val CHANNEL_ID = "my_channel_id"
        const val MESSAGE_NOTIFICATION_ID = 2
        val UUID_APP_SERVICE = UUID.fromString("cc17cc5a-b1d6-11ed-afa1-0242ac120002")

    }
    private lateinit var context: Context

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }
    private lateinit var currentAdvertisingSet: AdvertisingSet
    private val messages = MutableLiveData<List<String>>(listOf("message"))
    private var uuids = listOf<String>()
    private val fScanning = MutableLiveData(false)
    private lateinit var bluetoothManager: BluetoothManager
    private lateinit var mBluetoothAdapter: BluetoothAdapter

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val notification = Notification.Builder(this, CHANNEL_ID)
            .setContentTitle("Foreground Service")
            .setContentText("Running")
            .setSmallIcon(R.drawable.highlighter_size_4_40px)
            .build()

        startForeground(MESSAGE_NOTIFICATION_ID, notification)
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


                val notificationManagerWrapper = NotificationManagerWrapperImpl(context)
                val notif = notificationManagerWrapper.showNotification(
                    "New message",
                    "You have received a new message!"
                )
                startForeground(MESSAGE_NOTIFICATION_ID, notif)
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
