package com.karoliinamultas.bluetoothchat.service
//
//import android.annotation.SuppressLint
//import android.app.Service
//import android.bluetooth.BluetoothAdapter
//import android.bluetooth.BluetoothManager
//import android.bluetooth.BluetoothSocket
//import android.bluetooth.le.*
//import android.content.Context
//import android.content.Intent
//import android.os.IBinder
//import android.os.ParcelUuid
//import android.util.Log
//import androidx.compose.ui.platform.LocalContext
//import androidx.lifecycle.*
//import com.karoliinamultas.bluetoothchat.MainActivity
//import com.karoliinamultas.bluetoothchat.MyViewModel
//import com.karoliinamultas.bluetoothchat.MyViewModel.GattAttributes.UUID_APP_SERVICE
//import com.karoliinamultas.bluetoothchat.ui.chat.NotificationManagerWrapperImpl
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.GlobalScope
//import kotlinx.coroutines.coroutineScope
//import kotlinx.coroutines.launch
//import java.io.InputStream
//import java.io.OutputStream
//import java.nio.charset.Charset
//import java.util.*
//
//class ChatBackgroundService() : Service() {
//
//    private lateinit var currentAdvertisingSet: AdvertisingSet
//    private val messages = MutableLiveData<List<String>>(listOf("message"))
//    private var uuids = listOf<String>()
//    private val mResults = hashMapOf<String, ScanResult>()
//    private val fScanning = MutableLiveData(false)
//    private val scanResults = MutableLiveData<List<ScanResult>>(null)
//    private val dataToSend = MutableLiveData<ByteArray>("".toByteArray())
//    private lateinit var bluetoothManager: BluetoothManager
//    private lateinit var mBluetoothAdapter: BluetoothAdapter
//
//    override fun onBind(intent: Intent?): IBinder? {
//        return null
//    }
//
//    override fun onCreate() {
//        super.onCreate()
//        bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
//        mBluetoothAdapter = bluetoothManager.adapter
//        scanDevices(mBluetoothAdapter.bluetoothLeScanner)
//    }
//
//
//
//    // callBack is what triggers when scanner found needed service uuid
//    private val leScanCallback = object : ScanCallback() {
//        override fun onScanResult(callbackType: Int, result: ScanResult) {
//            super.onScanResult(callbackType, result)
//            val serviceData = result.scanRecord?.getServiceData(ParcelUuid(UUID_APP_SERVICE))
//            val splitMessage = String(serviceData ?: "".toByteArray(Charsets.UTF_8), Charset.defaultCharset()).split("//")
//            if (!uuids.contains(splitMessage[0])) {
//                messages.postValue(messages.value?.plus(splitMessage[1]))
//                uuids += splitMessage[0]
//                Log.d("DBG", "message ${splitMessage[1]}")
//
//            }
//        }
//    }
//    @SuppressLint("MissingPermission")
//    fun scanDevices(bluetoothLeScanner: BluetoothLeScanner) {
//
//        // Scan filter and options to filter for
//        fun buildScanFilters(): List<ScanFilter> {
//            val builder = ScanFilter.Builder()
//            builder.setServiceUuid(ParcelUuid(UUID_APP_SERVICE))
//            val filter = builder.build()
//            return listOf(filter)
//        }
//
//        GlobalScope.launch(Dispatchers.IO) {
//            fScanning.postValue(true)
//
//            val settings = ScanSettings.Builder()
//                .setLegacy(false)
//                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
//                .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
//                .setReportDelay(0)
//                .build()
//
//            bluetoothLeScanner.startScan(buildScanFilters(), settings, leScanCallback)
//        }
//    }
//
//
//    companion object GattAttributes {
//        const val SCAN_PERIOD: Long = 10000
//        const val MESSAGE_PERIOD: Long= 700
//        const val STATE_CONNECTING = 1
//        const val STATE_CONNECTED = 2
//        val UUID_APP_SERVICE = UUID.fromString("cc17cc5a-b1d6-11ed-afa1-0242ac120002")
//        val UUID_APP_DATA = UUID.fromString("cc17cc5a-b1d6-11ed-afa1-0242ac120002")
//
//    }
//}
