package com.karoliinamultas.bluetoothchat.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.*
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.os.ParcelUuid
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.lifecycle.MutableLiveData
import com.karoliinamultas.bluetoothchat.MyViewModel
import com.karoliinamultas.bluetoothchat.R
import com.karoliinamultas.bluetoothchat.data.BluetoothChatDatabase
import com.karoliinamultas.bluetoothchat.data.Message
import com.karoliinamultas.bluetoothchat.data.MessageUuidsListUiState
import com.karoliinamultas.bluetoothchat.data.OfflineMessagesRepository
import com.karoliinamultas.bluetoothchat.ui.chat.NotificationManagerWrapperImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.nio.charset.Charset
import java.util.*

class ChatForegroundService() : Service() {

    companion object {
        const val CHANNEL_ID = "foreground_channel_id"
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
    val parameters = AdvertisingSetParameters.Builder()
        .setLegacyMode(false)
        .setInterval(AdvertisingSetParameters.INTERVAL_LOW)
        .setTxPowerLevel(AdvertisingSetParameters.TX_POWER_MAX)
        .setPrimaryPhy(BluetoothDevice.PHY_LE_1M)
        .setSecondaryPhy(BluetoothDevice.PHY_LE_2M)



    private lateinit var context: Context

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }

    private var mBluetoothAdapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

    lateinit var currentAdvertisingSet: AdvertisingSet

    var beacons = MutableLiveData<Set<String>>(setOf("DEBUGGING 1", "DEBUGGING 2"))
    var beaconFilter = MutableLiveData<String>("")

    var uuids: List<String> = listOf("uuids")
    private val mResults = java.util.HashMap<String, ScanResult>()
    var fScanning = MutableLiveData<Boolean>(false)
    var mSending = MutableLiveData<Boolean>(false)
    var scanResults = MutableLiveData<List<ScanResult>>(null)
    var dataToSend = MutableLiveData<ByteArray>("".toByteArray())
    var compressedBitmap = MutableLiveData<ByteArray>()
    // file recieving and sending stuff
    var fRecieving = MutableLiveData<Boolean>(false)
    var recievedPackages: Array<String> = arrayOf()
    var packageUUID: String = ""
    var fileInParts: Array<ByteArray> = arrayOf()
    private lateinit var bluetoothManager: BluetoothManager

    override fun onBind(intent: Intent): IBinder? {
        return null
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
            Log.d(
                "KAROLIINAKAROLININAINIFISK",
                "byteArray ${splitMessage[0]} the thing 1 ${splitMessage[1]} the thing 2 ${splitMessage[2]}}"
            )


            /** maybe */
            val splitMessageToMessageObject: Message = Message(splitMessage[1],splitMessage[3],splitMessage[0],false)
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
                Log.d("message content", splitMessage.size.toString())
                if (splitMessage[2] == "0") {

                    uuids += splitMessage[1]
                    Log.d(
                        "hei",
                        String(
                            serviceData ?: "t".toByteArray(Charsets.UTF_8),
                            Charset.defaultCharset()
                        )
                    )


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
