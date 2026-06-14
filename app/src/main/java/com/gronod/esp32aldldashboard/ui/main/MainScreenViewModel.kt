package com.gronod.esp32aldldashboard.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gronod.esp32aldldashboard.bluetooth.ConnectionState
import com.gronod.esp32aldldashboard.parser.ALDLFrame
import com.gronod.esp32aldldashboard.repository.SettingsRepository
import com.gronod.esp32aldldashboard.repository.TelemetryRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainScreenViewModel(
    private val telemetryRepository: TelemetryRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val connectionState: StateFlow<ConnectionState> = telemetryRepository.connectionState
    val latestFrame: StateFlow<ALDLFrame?> = telemetryRepository.latestFrame
    val rawHexLog: StateFlow<List<String>> = telemetryRepository.rawHexLog
    val errorMessage: StateFlow<String> = telemetryRepository.errorMessage

    val framesReceived: StateFlow<Int> = telemetryRepository.framesReceived
    val parseErrors: StateFlow<Int> = telemetryRepository.parseErrors
    val currentFrameRate: StateFlow<Int> = telemetryRepository.currentFrameRate

    val isCelsius: StateFlow<Boolean> = settingsRepository.isCelsiusFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun toggleTemperatureUnit() {
        viewModelScope.launch {
            val currentValue = isCelsius.value
            settingsRepository.setIsCelsius(!currentValue)
        }
    }

    fun connect() {
        telemetryRepository.connect()
    }

    fun disconnect() {
        telemetryRepository.disconnect()
    }

    fun startSimulation() {
        telemetryRepository.startSimulation()
    }

    override fun onCleared() {
        super.onCleared()
        telemetryRepository.disconnect()
    }
}
