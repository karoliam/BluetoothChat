package com.karoliinamultas.bluetoothchat.service

import android.annotation.SuppressLint
import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.*
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.os.ParcelUuid
import androidx.lifecycle.MutableLiveData
import com.karoliinamultas.bluetoothchat.MyViewModel
import com.karoliinamultas.bluetoothchat.data.BluetoothChatDatabase
import com.karoliinamultas.bluetoothchat.data.MessageUuidsListUiState
import com.karoliinamultas.bluetoothchat.data.OfflineMessagesRepository
import com.karoliinamultas.bluetoothchat.ui.chat.NotificationManagerWrapperImpl
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.nio.charset.Charset
import java.util.*

class ChatForegroundService() : Service() {

    private lateinit var context: Context
    private var mBluetoothAdapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    var beaconFilter = MutableLiveData<String>("")
    var uuids: List<String> = listOf("uuids")
    var fScanning = MutableLiveData<Boolean>(false)
    private lateinit var bluetoothManager: BluetoothManager

    companion object {
        const val MESSAGE_NOTIFICATION_ID = 3
        val UUID_APP_SERVICE = UUID.fromString("cc17cc5a-b1d6-11ed-afa1-0242ac120002")

    }
    val repository = OfflineMessagesRepository(BluetoothChatDatabase.getDatabase(this).messageDao())
    val messageUuids: StateFlow<MessageUuidsListUiState> =
        repository.getMessageUuids().map { MessageUuidsListUiState(it) }
            .stateIn(
                scope = GlobalScope,
                started = SharingStarted.WhileSubscribed(5_000L),
                initialValue = MessageUuidsListUiState()
            )

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val notificationManagerWrapperImpl = NotificationManagerWrapperImpl(context)
        val notification = notificationManagerWrapperImpl.showNotification("Restroom Chat", "Running in the background")

        startForeground(MESSAGE_NOTIFICATION_ID, notification)
        bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        scanDevices(mBluetoothAdapter.bluetoothLeScanner)

        return START_STICKY
    }



    private val leScanCallback: ScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            val serviceData = result.scanRecord?.getServiceData(ParcelUuid(MyViewModel.UUID_APP_SERVICE)) ?: byteArrayOf()
            val splitMessage = String(
                serviceData ?: "".toByteArray(),
                Charset.defaultCharset()
            ).split("/*/")

            if(!messageUuids.value.uuidsDatabaseList.contains(splitMessage[1])){
                // notifikaatio
                val notificationManagerWrapper = NotificationManagerWrapperImpl(context)
                val notif = notificationManagerWrapper.showNotification(
                    "New message",
                    "You have received a new message!"
                )
                startForeground(MESSAGE_NOTIFICATION_ID, notif)
                // end of notifikaatio
            }
            if (!uuids?.contains(splitMessage[1])!! && beaconFilter.value.equals(splitMessage[0]) && splitMessage.size > 1) {
                if (splitMessage[2] == "0") {
                    uuids += splitMessage[1]
                }
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

        GlobalScope.launch {
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