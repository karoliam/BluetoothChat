package com.karoliinamultas.bluetoothchat


import android.annotation.SuppressLint
import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.*
import android.content.Context
import android.os.Bundle
import android.os.ParcelUuid
import android.util.Log
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.karoliinamultas.bluetoothchat.data.Message
import com.karoliinamultas.bluetoothchat.data.MessagesListUiState
import com.karoliinamultas.bluetoothchat.data.MessagesRepository
import com.karoliinamultas.bluetoothchat.network.ImageApi
import com.karoliinamultas.bluetoothchat.ui.chat.NotificationManagerWrapperImpl
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.nio.charset.Charset
import java.util.*
import kotlin.math.log

private const val TAG = "MyViewModelTAG"
class ImageRepository() {
    private val call = ImageApi.service
    suspend fun getPress(param1:String,param2:String,param3:String, ) = call.postData(param1, param2, param3)
}
class MyViewModel(private val messagesRepository: MessagesRepository) : ViewModel() {
    private val repository: ImageRepository = ImageRepository()
    var imageUrl: String by mutableStateOf("")
        private set

    private val mBluetoothAdapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

    lateinit var currentAdvertisingSet: AdvertisingSet

    var beacons = MutableLiveData<Set<String>>(setOf("DEBUGGING 1", "DEBUGGING 2"))
    var beaconFilter = MutableLiveData<String>("")
    val messages: StateFlow<MessagesListUiState> =
        messagesRepository.getChatMessages().map { MessagesListUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000L),
                initialValue = MessagesListUiState()
            )
    var uuids: List<String> = listOf("uuids")
    private val mResults = java.util.HashMap<String, ScanResult>()
    var fScanning = MutableLiveData<Boolean>(false)
    var mSending = MutableLiveData<Boolean>(false)
    var scanResults = MutableLiveData<List<ScanResult>>(null)
    var dataToSend = MutableLiveData<ByteArray>("".toByteArray())
    var compressedBitmap = MutableLiveData<ByteArray>()
    var uploadingImage by mutableStateOf(true)
    // file recieving and sending stuff
    var fRecieving = MutableLiveData<Boolean>(false)
    var recievedPackages: Array<String> = arrayOf()
    var packageUUID: String = ""
    var fileInParts: Array<ByteArray> = arrayOf()

    // Create an AdvertiseData object to include data in the advertisement

    val parameters = AdvertisingSetParameters.Builder()
        .setLegacyMode(false)
        .setInterval(AdvertisingSetParameters.INTERVAL_LOW)
        .setTxPowerLevel(AdvertisingSetParameters.TX_POWER_MAX)
        .setPrimaryPhy(BluetoothDevice.PHY_LE_1M)
        .setSecondaryPhy(BluetoothDevice.PHY_LE_2M)
    val coroutineExceptionHandler = CoroutineExceptionHandler{_, throwable ->
        throwable.printStackTrace()
    }
    //    callBack is what triggers when scanner found needed service uuid

    fun uploadImage(param1: String, param2: String, param3:String){
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler){
            val responce = repository.getPress(param1, param2, param3)
            imageUrl = responce.image.url
            Log.d("url", imageUrl.toString())
            sendMessage(mBluetoothAdapter, mBluetoothAdapter.bluetoothLeScanner, imageUrl, "", "1")
            uploadingImage=false
        }


    }
    private val leScanCallback: ScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            val serviceData = result.scanRecord?.getServiceData(ParcelUuid(UUID_APP_SERVICE))
            val splitMessage = String(
                serviceData ?: "".toByteArray(),
                Charset.defaultCharset()
            ).split("/*/")
                Log.d(
                    "package",
                    "byteArray ${splitMessage[0]} the thing 1 ${splitMessage[1]} the thing 2 ${splitMessage[2]}}"
                )

            /** maybe */
            val splitMessageToMessageObject: Message = Message(splitMessage[1],splitMessage[2],splitMessage[0],false)

            if (!uuids?.contains(splitMessage[1])!! && beaconFilter.value.equals(splitMessage[0]) && splitMessage.size > 1) {
                Log.d("message content", splitMessage.size.toString())
//                    messages.postValue(messages.value?.plus(splitMessage[3]))
                    uuids += splitMessage[1]
                    Log.d(
                        "hei",
                        String(
                            serviceData ?: "t".toByteArray(Charsets.UTF_8),
                            Charset.defaultCharset()
                        )
                    )
                    if (!fRecieving.value!!) {
                        sendMessage(
                            mBluetoothAdapter,
                            mBluetoothAdapter.bluetoothLeScanner,
                            splitMessage[3],
                            splitMessage[1],
                            splitMessage[2]
                        )

                    }


            }
            }

        }


    private val leScanCallbackBeacons: ScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            if (result.scanRecord?.deviceName?.contains("btchat") ?: false) {
                beacons.postValue(
                    beacons.value?.plus(
                        result.scanRecord?.deviceName?.split("//")?.get(0) ?: "no beacons"
                    )
                )
                Log.d("beacon", "beacon found ${result.scanRecord?.deviceName}")
            }
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

    @SuppressLint("SuspiciousIndentation")
    fun glueFileTogether(parts: Array<ByteArray>) {
        var byteLength: Int = 0
        parts.forEach {
            byteLength += it.size
        }

        Log.d("glued", byteLength.toString())
    }

    @SuppressLint("MissingPermission")
    fun scanBeacons(bluetoothLeScanner: BluetoothLeScanner) {
        var filterList: List<ScanFilter> = listOf()

        //        Scan filter and options to filter for
        @SuppressLint("SuspiciousIndentation")
        fun buildScanFilters(): List<ScanFilter> {
            val builder = ScanFilter.Builder()
//            builder.setServiceUuid(ParcelUuid(UUID_APP_SERVICE))
//            builder.setServiceData(ParcelUuid(UUID.fromString("cc17cc5a-b1d6-11ed-afa1-0242ac120002")))
//            builder.setDeviceName("Nova1")
            val filter = builder.build()
            return listOf(filter)
        }
        if (filterList.isEmpty()) {
            filterList = buildScanFilters()
        }
        viewModelScope.launch(Dispatchers.IO) {

            fScanning.postValue(true)

            val settings = ScanSettings.Builder()
                .setLegacy(false)
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
                .setReportDelay(0)
                .build()

            bluetoothLeScanner.startScan(null, settings, leScanCallbackBeacons)

        }
    }

    @SuppressLint("MissingPermission")
    fun stopScanBeacons(mBluetoothLeScanner: BluetoothLeScanner) {
        mBluetoothLeScanner.stopScan(leScanCallbackBeacons)
    }




    fun divideArray(source: ByteArray, chunksize: Int): Array<ByteArray>? {
        val ret = Array(Math.ceil(source.size / chunksize.toDouble()).toInt()) {
            ByteArray(
                chunksize
            )
        }

        var start = 0
        var parts = 0
        for (i in ret.indices) {
            if (start + chunksize > source.size) {
                System.arraycopy(source, start, ret[i], 0, source.size - start)
            } else {
                System.arraycopy(source, start, ret[i], 0, chunksize)
            }
            start += chunksize
            parts++
        }

        return ret

    }

    @SuppressLint("MissingPermission", "SuspiciousIndentation")
    fun sendMessage(
        mBluetoothAdapter: BluetoothAdapter,
        bluetoothLeScanner: BluetoothLeScanner,
        message: String,
        uuid: String,
        isUrl:String
    ) {
        var buildMessage: String = ""
        val leAdvertiser = mBluetoothAdapter.bluetoothLeAdvertiser
            if (uuid.isEmpty()) {
                val uuidl = UUID.randomUUID().toString()
                buildMessage = beaconFilter.value + "/*/" + uuidl +  "/*/"+isUrl+"/*/" + message
                uuids += uuidl
                /** maybe */
//                messages.postValue(messages.value?.plus(Message(uuidl,message,beaconFilter.value.toString(),true)))
//                messages.postValue(messages.value?.plus(message))
                viewModelScope.launch {
                    saveMessageToDatabase(
                        uuidl,
                        message,
                        beaconFilter.value.toString(),
                        true
                    )
                }
            } else {
                buildMessage = beaconFilter.value + "/*/" + uuid +  "/*/"+isUrl+"/*/" + message
                uuids += uuid
                /** maybe */
//                messages.postValue(messages.value?.plus(Message(uuid,message,beaconFilter.value.toString(), false)))
//                messages.postValue(messages.value?.plus(message))
                viewModelScope.launch {
                    saveMessageToDatabase(
                        uuid,
                        message,
                        beaconFilter.value.toString(),
                        false
                    )
                }
            }



        viewModelScope.launch(Dispatchers.IO) {
                stopScan(bluetoothLeScanner)
                val data = AdvertiseData.Builder()
                    .setIncludeDeviceName(true)
                    .addServiceData(
                        ParcelUuid(UUID.fromString("cc17cc5a-b1d6-11ed-afa1-0242ac120002")),
                        buildMessage.toByteArray(Charsets.UTF_8)
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
                delay(MESSAGE_PERIOD)
                leAdvertiser.stopAdvertisingSet(callback)
                Log.d("message", buildMessage)
                mSending.postValue(false)

            scanDevices(bluetoothLeScanner)
        }
    }


    //    Scanner with settings to follow specifis service uuid
    @SuppressLint("MissingPermission")
    fun scanDevices(bluetoothLeScanner: BluetoothLeScanner) {

        var filterList: List<ScanFilter> = listOf()

        //        Scan filter and options to filter for
        @SuppressLint("SuspiciousIndentation")
        fun buildScanFilters(): List<ScanFilter> {
            val builder = ScanFilter.Builder()
            builder.setServiceUuid(ParcelUuid(UUID_APP_SERVICE))
//            builder.setServiceData(ParcelUuid(UUID.fromString("cc17cc5a-b1d6-11ed-afa1-0242ac120002")))
//            builder.setDeviceName("PAVEL")
            val filter = builder.build()
            return listOf(filter)
        }
        if (filterList.isEmpty()) {
            filterList = buildScanFilters()
        }
        viewModelScope.launch(Dispatchers.IO) {

            fScanning.postValue(true)

            val settings = ScanSettings.Builder()
                .setLegacy(false)
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
                .setReportDelay(0)
                .build()

            bluetoothLeScanner.startScan(filterList, settings, leScanCallback)
            Log.d("start", "Scanning")
        }
    }

    @SuppressLint("MissingPermission")
    fun stopScan(bluetoothLeScanner: BluetoothLeScanner) {
        fScanning.postValue(false)
        scanResults.postValue(mResults.values.toList())
        viewModelScope.launch(Dispatchers.IO) {
            bluetoothLeScanner.stopScan(leScanCallback)
        }
    }

    companion object GattAttributes {
        const val SCAN_PERIOD: Long = 10000
        const val MESSAGE_PERIOD: Long = 1000
        const val STATE_CONNECTING = 1
        const val STATE_CONNECTED = 2
        val UUID_APP_SERVICE = UUID.fromString("cc17cc5a-b1d6-11ed-afa1-0242ac120002")
        val UUID_APP_DATA = UUID.fromString("cc17cc5a-b1d6-11ed-afa1-0242ac120002")
    }

    suspend fun saveMessageToDatabase(
        messageUuid: String,
        messageContent: String,
        chatId: String,
        localMessage: Boolean
    ) {
        messagesRepository.insertMessage(
            Message(
                messageUuid,
                messageContent,
                chatId,
                localMessage
            )
        )
    }

    fun chatRoomOnJoinDatabaseChanges(chatId: String) {
        viewModelScope.launch {
            deleteOtherMessagesFromDatabase(chatId)
        }
    }

    suspend fun deleteOtherMessagesFromDatabase(chatId: String) {
        messagesRepository.deleteOtherChatMessages(chatId)
    }
}
