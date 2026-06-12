package com.example.esp32aldldashboard.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.esp32aldldashboard.repository.SettingsRepository
import com.example.esp32aldldashboard.repository.TelemetryRepository

class MainScreenViewModelFactory(
    private val telemetryRepository: TelemetryRepository,
    private val settingsRepository: SettingsRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainScreenViewModel::class.java)) {
            return MainScreenViewModel(telemetryRepository, settingsRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
