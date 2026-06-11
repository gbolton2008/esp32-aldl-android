package com.example.esp32aldldashboard.ui.main

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.esp32aldldashboard.bluetooth.BluetoothService
import com.example.esp32aldldashboard.bluetooth.ConnectionState
import com.example.esp32aldldashboard.parser.ALDLFrame
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MainScreenViewModel(context: Context) : ViewModel() {
    private val bluetoothService = BluetoothService(context.applicationContext)

    val connectionState: StateFlow<ConnectionState> = bluetoothService.connectionState
    val latestFrame: StateFlow<ALDLFrame?> = bluetoothService.latestFrame
    val rawHexLog: StateFlow<List<String>> = bluetoothService.rawHexLog
    val errorMessage: StateFlow<String> = bluetoothService.errorMessage

    private val _isCelsius = MutableStateFlow(false) // Default to Fahrenheit for standard 80s GM telemetry
    val isCelsius: StateFlow<Boolean> = _isCelsius

    fun toggleTemperatureUnit() {
        _isCelsius.value = !_isCelsius.value
    }

    fun connect() {
        bluetoothService.connect()
    }

    fun disconnect() {
        bluetoothService.disconnect()
    }

    fun startSimulation() {
        bluetoothService.startSimulation()
    }

    override fun onCleared() {
        super.onCleared()
        bluetoothService.disconnect()
    }
}
