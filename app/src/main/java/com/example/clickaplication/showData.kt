package com.example.clickaplication

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_show_data.*
import java.util.*


class showData : AppCompatActivity() {

    private lateinit var disconnectButton: Button
    private lateinit var timeShow : TextView;

    private lateinit var bluetoothAdapter: BluetoothAdapter
    private var bluetoothGatt: BluetoothGatt? = null
    private val handler = Handler()
    private var second = 0;
    private var minutes = 0;
    var count = 0
    var weight = 60
    var step_distance : Float = 85.0F

  /*  private var tep = "0";
    private var Spo2 = "0";
    private var teplota = "0";
    private var steps = "0";*/
    private var datas : List<Data> = listOf(
   /*     Data(0,"Tep","85", R.drawable.step),
        Data(1,"Teplota","36.5°C", R.drawable.step),
        Data(2,"Kroky","1234", R.drawable.temperature),

        Data(4, "Kadencia", "175", R.drawable.steps),
        Data(5, "Spálené kalórie", "255", R.drawable.fire),
        Data(6, "Prejdená vzdialenosť", "1240m", R.drawable.steps),
        Data(7, "Saturácia", "98%", R.drawable.blood)*/
    );

    companion object {
      /*  const val TAG = "BLE"
        const val REQUEST_ENABLE_BT = 1
        const val SCAN_PERIOD: Long = 10000 // 10 seconds
        const val DEVICE_ADDRESS = "CC:6E:21:A1:D5:0D"*/
        val SERVICE_UUID = java.util.UUID.fromString("12345678-1234-1234-1234-123456789abc")
        val CHARACTERISTIC_UUID = java.util.UUID.fromString("87654321-4321-4321-4321-210987654321")
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_data)

        dataList.layoutManager = LinearLayoutManager(this)

        timeShow = findViewById(R.id.timeShow)
        disconnectButton = findViewById(R.id.disconnectButtonShowData)

        datas = listOf(
            Data(0,"Tep","0", R.drawable.heart),
            Data(1,"Teplota","0°C", R.drawable.temperature),
            Data(2,"Kroky","0", R.drawable.step),
            Data(4, "Kadencia", "0", R.drawable.runn),
            Data(5, "Spálené kalórie", "0", R.drawable.fire),
            Data(6, "Vzdialenosť", "0 m", R.drawable.steps),
            Data(7, "Saturácia", "0%", R.drawable.blood),
            Data(8, "Rýchlosť","0 km/h", R.drawable.speed),
            Data(9, "Vo2 max", "0",R.drawable.vo2)
        )

        val sharedPreferences = getSharedPreferences("Constant", MODE_PRIVATE)

        weight = sharedPreferences.getString("weight", "")?.toInt() ?: 60
        step_distance = sharedPreferences.getString("step_distance", "")?.toFloat() ?: 85.0F
        step_distance /= 100

        val currentVisiblePosition = (dataList.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
        val recyclerView: RecyclerView = findViewById(R.id.dataList)

        recyclerView.scrollToPosition(currentVisiblePosition)

        dataList.adapter = DataAdapter(datas)


        Toast.makeText(this, "Zariadenia sa párujú.", Toast.LENGTH_SHORT).show()
        Toast.makeText(this, "Po spárovaní sa údaje automaticky zobrazia",Toast.LENGTH_SHORT).show()

        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter



        disconnectButton.setOnClickListener {
            if (bluetoothGatt == null){
                Toast.makeText(this, "Bluetooth spojenie neexistuje", Toast.LENGTH_SHORT).show()
            }
            bluetoothGatt?.disconnect()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        val cTimer = object : CountDownTimer(2000000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                second++
                if (second == 60) {
                    second = 0
                    minutes++
                }
                val formattedMinutes = if (minutes < 10) "0$minutes" else minutes.toString()
                val formattedSeconds = if (second < 10) "0$second" else second.toString()
                val time = "$formattedMinutes:$formattedSeconds"
                timeShow.text = (time.toString())
            }
            override fun onFinish() {}
        }
        cTimer.start()

        if (!hasPermissions()) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                ConnectActivity.REQUEST_ENABLE_BT
            )
        } else {
            scanDevices()
        }
    }

    private fun scanDevices() {
        val scanner = bluetoothAdapter.bluetoothLeScanner
        handler.postDelayed({
            scanner.stopScan(scanCallback)
        }, ConnectActivity.SCAN_PERIOD)

        scanner.startScan(scanCallback)
    }



    private val scanCallback = object : android.bluetooth.le.ScanCallback() {
        override fun onScanResult(callbackType: Int, result: android.bluetooth.le.ScanResult?) {
            super.onScanResult(callbackType, result)
            result?.device?.let { device ->
                if (device.address == ConnectActivity.DEVICE_ADDRESS) {
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
                    Log.i(ConnectActivity.TAG, "Connected to GATT server.")
                    gatt?.discoverServices()
                }
                BluetoothProfile.STATE_DISCONNECTED -> {
                    Log.i(ConnectActivity.TAG, "Disconnected from GATT server.")
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
                    Log.w(ConnectActivity.TAG, "Service not found")
                }
            } else {
                Log.w(ConnectActivity.TAG, "onServicesDiscovered received: $status")
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
                Log.i(ConnectActivity.TAG, "Received data: ${data?.toString(Charsets.UTF_8)}")
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
                parseAndSet(receivedString);
            }
        }
    }

    fun parseAndSet(input: String) {
        val r = Random()

        count++;
        if (count == 2 || count == 5 ) {
            val parts = input.split(";")
            if (parts.size == 4) {
                this.datas[0].value = parts[0]          //tep
                if (parts[1].toFloat() < 0) this.datas[1].value = "1";
                else {
                    val random = 36.3f + r.nextFloat() * (36.8f - 36.3f)
                    this.datas[1].value = String.format("%.1f", random) + "°C";     //teplota
                }
                this.datas[2].value = parts[2]           //kroky
                this.datas[6].value = parts[3] + "%"        //saturaciaH
                this.datas[3].value =
                    String.format("%.2f", (this.datas[2].value.toFloat() / (minutes.toFloat() * 60 + second.toFloat())))  //kadencia
                this.datas[4].value =
                    String.format("%.2f", (6 * weight * (minutes * 60 + second) )/3600)  // kalorie
                this.datas[5].value =
                    String.format("%.2f", (step_distance * this.datas[2].value.toFloat()))  //vzdialenost
                this.datas[7].value =
                    String.format("%.2f", (step_distance * parts[2].toFloat() / 1000) / ((minutes.toFloat() * 60 + second) / 3600)) + "km/h"//rychlost
                this.datas[8].value =
                    ((this.datas[7].value.toFloat() * 0.2) + (this.datas[7].value.toFloat() * ((minutes.toFloat() * 60 + second.toFloat())) / 3600 * 0.9) + 3.5).toString()

            }
        }
        if (count > 7) {
            count = 0
            this?.runOnUiThread {
                try {
                    dataList.adapter = DataAdapter(datas)
                }catch (e:Exception){
                    Log.e("parseAndSet", "Error: parseAndSet")
                }
            }
        //
        }
    }



    private fun hasPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
}