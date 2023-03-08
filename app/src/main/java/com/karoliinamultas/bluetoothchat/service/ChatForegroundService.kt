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
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.ViewModelFactoryDsl
import androidx.lifecycle.viewmodel.compose.viewModel
import com.karoliinamultas.bluetoothchat.AppViewModelProvider
import com.karoliinamultas.bluetoothchat.MyViewModel
import com.karoliinamultas.bluetoothchat.R
import com.karoliinamultas.bluetoothchat.data.*
import com.karoliinamultas.bluetoothchat.ui.chat.NotificationManagerWrapperImpl
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.nio.charset.Charset
import java.util.*

class ChatForegroundService() : Service() {

    companion object {
        const val CHANNEL_ID = "my_channel_id"
        const val MESSAGE_NOTIFICATION_ID = 2
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

    private val mBluetoothAdapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

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

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val notification = Notification.Builder(this, CHANNEL_ID)
            .setContentTitle("The chat is open")
            .setContentText("Running")
            .setSmallIcon(R.drawable.highlighter_size_4_40px)
            .build()

        startForeground(MESSAGE_NOTIFICATION_ID, notification)
        bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        scanDevices(mBluetoothAdapter.bluetoothLeScanner)

        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

// UUS KOODI
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

    @SuppressLint("MissingPermission", "SuspiciousIndentation")
    fun sendMessage(
        mBluetoothAdapter: BluetoothAdapter,
        bluetoothLeScanner: BluetoothLeScanner,
        message: String,
        uuid: String,
        file: Array<ByteArray> = arrayOf()
    ) {
        var buildMessage: String = ""
        var sendPackage = file
        val leAdvertiser = mBluetoothAdapter.bluetoothLeAdvertiser
        if (message != "") {
            if (uuid.isEmpty()) {
                val uuidl = UUID.randomUUID().toString()
                buildMessage = beaconFilter.value + "/*/" + uuidl +  "/*/0/*/" + message
                uuids += uuidl
                /** maybe */
//                messages.postValue(messages.value?.plus(Message(uuidl,message,beaconFilter.value.toString(),true)))
//                messages.postValue(messages.value?.plus(message))
                GlobalScope.launch {
                    saveMessageToDatabase(
                        uuidl,
                        message,
                        beaconFilter.value.toString(),
                        true
                    )
                }
            } else {
                buildMessage = beaconFilter.value + "/*/" + uuid +  "/*/0/*/" + message
                uuids += uuid
                /** maybe */
//                messages.postValue(messages.value?.plus(Message(uuid,message,beaconFilter.value.toString(), false)))
//                messages.postValue(messages.value?.plus(message))
                GlobalScope.launch {
                    saveMessageToDatabase(
                        uuid,
                        message,
                        beaconFilter.value.toString(),
                        false
                    )
                }

            }
            sendPackage = sendPackage.plus(buildMessage.toByteArray((Charsets.UTF_8)))
        } else {
            val uuidl = UUID.randomUUID().toString()
            var packageIndex: Int = 0
            sendPackage.forEach {
                val messageFirstPart = (beaconFilter.value + "/*/" + uuidl + "/*/"+ (packageIndex + 1) + "/" + sendPackage.size.toString() + "/*/").toByteArray()

                val result = ByteArray(messageFirstPart.size + it.size)
                System.arraycopy(messageFirstPart, 0, result, 0, messageFirstPart.size)
                System.arraycopy(it, 0, result, messageFirstPart.size, it.size)

                Log.d("arrayLength", it.size.toString())
                sendPackage[packageIndex] = result
                packageIndex++
            }
        }

        GlobalScope.launch(Dispatchers.IO) {
            stopScan(bluetoothLeScanner)
            sendPackage.forEach {
                delay(MyViewModel.MESSAGE_PERIOD / 2)
                val data = AdvertiseData.Builder()
                    .setIncludeDeviceName(true)
                    .addServiceData(
                        ParcelUuid(UUID.fromString("cc17cc5a-b1d6-11ed-afa1-0242ac120002")),
                        it
                    )
                    .addServiceUuid(ParcelUuid(UUID.fromString("cc17cc5a-b1d6-11ed-afa1-0242ac120002")))
                    .build()

                mSending.postValue(true)
                leAdvertiser.startAdvertisingSet(
                    parameters.build(),
                    data,
                    null,
                    null,
                    null,
                    callback
                )
                delay(MyViewModel.MESSAGE_PERIOD)
                leAdvertiser.stopAdvertisingSet(callback)
                Log.d("message", String(it, Charset.defaultCharset()))
                mSending.postValue(false)
            }
            scanDevices(bluetoothLeScanner)
        }
    }
    suspend fun saveMessageToDatabase(
        messageUuid: String,
        messageContent: String,
        chatId: String,
        localMessage: Boolean
    ) {
        repository.insertMessage(
            Message(
                messageUuid,
                messageContent,
                chatId,
                localMessage
            )
        )
    }

    @SuppressLint("MissingPermission")
    fun stopScan(bluetoothLeScanner: BluetoothLeScanner) {
        fScanning.postValue(false)
        scanResults.postValue(mResults.values.toList())
        GlobalScope.launch(Dispatchers.IO) {
            bluetoothLeScanner.stopScan(leScanCallback)
        }
    }
    val callback: AdvertisingSetCallback = object : AdvertisingSetCallback() {
        override fun onAdvertisingSetStarted(
            advertisingSet: AdvertisingSet,
            txPower: Int,
            status: Int
        ) {
            Log.i(
                "LOG_TAG", "onAdvertisingSetStarted(): txPower:" + txPower + " , status: "
                        + status
            )
            currentAdvertisingSet = advertisingSet
        }

        override fun onAdvertisingSetStopped(advertisingSet: AdvertisingSet) {
            Log.i("LOG_TAG", "onAdvertisingSetStopped():")
        }
    }

    //UUS KOODI LOPPUU

//
//
//    // callBack is what triggers when scanner found needed service uuid
//    private val leScanCallbackd = object : ScanCallback() {
//        override fun onScanResult(callbackType: Int, result: ScanResult) {
//            super.onScanResult(callbackType, result)
//            val serviceData = result.scanRecord?.getServiceData(ParcelUuid(UUID_APP_SERVICE))
//            val splitMessage = String(serviceData ?: "".toByteArray(Charsets.UTF_8), Charset.defaultCharset()).split("/*/")
//            if (!uuids.contains(splitMessage[0])) {
//                messages.postValue(messages.value?.plus(splitMessage[3]))
//                uuids += splitMessage[0]
//                Log.d("DBG", "message ${splitMessage[3]}")
//
//
//                val notificationManagerWrapper = NotificationManagerWrapperImpl(context)
//                val notif = notificationManagerWrapper.showNotification(
//                    "New message",
//                    "You have received a new message!"
//                )
//
//                startForeground(MESSAGE_NOTIFICATION_ID, notif)
//            }
//        }
//    }

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

