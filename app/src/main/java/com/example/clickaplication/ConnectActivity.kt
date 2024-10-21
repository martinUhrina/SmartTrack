package com.example.clickaplication

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.connect_activity.*
import java.util.*
import android.Manifest
import android.bluetooth.BluetoothGattDescriptor
import android.content.Intent
import android.os.Handler

class ConnectActivity : AppCompatActivity() {

    private lateinit var connectButton : Button
    private lateinit var diconnectButton : Button

    private lateinit var bluetoothAdapter: BluetoothAdapter
    private var bluetoothGatt: BluetoothGatt? = null
    private val handler = Handler()

    companion object {
        const val TAG = "BLE"
        const val REQUEST_ENABLE_BT = 1
        const val SCAN_PERIOD: Long = 10000 // 10 seconds
        const val DEVICE_ADDRESS = "CC:6E:21:A1:D5:0D"
        // const val DEVICE_ADDRESS = "4C:67:2C:CA:DA:1D" // MAC adresa NIE sense verzia
        val SERVICE_UUID = java.util.UUID.fromString("12345678-1234-1234-1234-123456789abc")
        val CHARACTERISTIC_UUID = java.util.UUID.fromString("87654321-4321-4321-4321-210987654321")
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.connect_activity)

        connectButton = findViewById(R.id.connectButton)
        diconnectButton = findViewById(R.id.disconnectButton)

        disconnectButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
//        val address = "4C:67:2C:CA:DA:1D"

        connectButton.setOnClickListener {
            val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
            bluetoothAdapter = bluetoothManager.adapter

            if (bluetoothAdapter == null) {
                Log.e(TAG, "Bluetooth is not supported")
                finish()
                return@setOnClickListener
            }
            if (!hasPermissions()) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_ENABLE_BT
                )
            } else {
                // Bluetooth permissions are granted, start scanning
                scanDevices()
            }
        }


    }
    private fun hasPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
    private fun scanDevices() {
        val scanner = bluetoothAdapter.bluetoothLeScanner
        handler.postDelayed({
            scanner.stopScan(scanCallback)
        }, SCAN_PERIOD)

        scanner.startScan(scanCallback)
    }

    private val scanCallback = object : android.bluetooth.le.ScanCallback() {
        override fun onScanResult(callbackType: Int, result: android.bluetooth.le.ScanResult?) {
            super.onScanResult(callbackType, result)
            result?.device?.let { device ->
                if (device.address == DEVICE_ADDRESS) {
                    connectToDevice(device)
                }
            }
        }
    }

    private fun connectToDevice(device: BluetoothDevice) {
        bluetoothGatt = device.connectGatt(this, false, gattCallback)
    }

    private val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)
            when (newState) {
                BluetoothProfile.STATE_CONNECTED -> {
                    Log.i(TAG, "Connected to GATT server.")
                    gatt?.discoverServices()
                }
                BluetoothProfile.STATE_DISCONNECTED -> {
                    Log.i(TAG, "Disconnected from GATT server.")
                    val intentLogin = Intent(this@ConnectActivity, LoginActivity::class.java)
                    startActivity(intentLogin)
                }
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            super.onServicesDiscovered(gatt, status)
            if (status == BluetoothGatt.GATT_SUCCESS) {
                val service = gatt?.getService(SERVICE_UUID)
                if (service != null) {
                    val characteristic = service.getCharacteristic(CHARACTERISTIC_UUID)
                    gatt.setCharacteristicNotification(characteristic, true)

                    val descriptor = characteristic.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")).apply {
                        value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                    }
                    gatt.writeDescriptor(descriptor)
                } else {
                    Log.w(TAG, "Service not found")
                }
            } else {
                Log.w(TAG, "onServicesDiscovered received: $status")
            }
        }


        override fun onCharacteristicRead(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?,
            status: Int
        ) {
            super.onCharacteristicRead(gatt, characteristic, status)
            if (status == BluetoothGatt.GATT_SUCCESS) {
                val data = characteristic?.value
                // Handle received data here
                Log.i(TAG, "Received data: ${data?.toString(Charsets.UTF_8)}")
            }
        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?
        ) {
            super.onCharacteristicChanged(gatt, characteristic)
            val data = characteristic?.value
            if (data != null) {
                val receivedString = data.toString(Charsets.UTF_8)
                Toast.makeText(baseContext, receivedString, Toast.LENGTH_SHORT).show()

            }
        }
    }
}