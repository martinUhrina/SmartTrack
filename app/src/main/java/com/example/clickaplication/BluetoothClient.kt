package com.example.clickaplication

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.ContentValues.TAG
import android.util.Log
import android.widget.Toast
import java.io.IOException
import java.util.*

class BluetoothClient(private val device: BluetoothDevice) {        //Trieda na komunikáciu s bluetooth, treba zapracovať
    private var socket: BluetoothSocket? = null

    fun connect() {
        try {
            socket = device.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"))
            socket?.connect()
            Log.d(TAG, "Connected to device: ${device.name}")
        } catch (e: IOException) {
            Log.e(TAG, "Connection failed", e)
        }
    }

    fun disconnect() {
        try {
            socket?.close()
            Log.d(TAG, "Disconnected from device: ${device.name}")
        } catch (e: IOException) {
            Log.e(TAG, "Disconnection failed", e)
        }
    }

    fun send(message: String) {
        try {
            socket?.outputStream?.write(message.toByteArray())
            Log.d(TAG, "Sent message: $message")
        } catch (e: IOException) {
            Log.e(TAG, "Sending failed", e)
        }
    }

}