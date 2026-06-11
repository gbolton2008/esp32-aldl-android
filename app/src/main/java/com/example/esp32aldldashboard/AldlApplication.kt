package com.example.esp32aldldashboard

import android.app.Application
import com.example.esp32aldldashboard.bluetooth.BluetoothService
import com.example.esp32aldldashboard.repository.SettingsRepository
import com.example.esp32aldldashboard.repository.TelemetryRepository
import com.example.esp32aldldashboard.logging.CsvLogger
import com.example.esp32aldldashboard.data.database.TelemetryDatabase

class AldlApplication : Application() {
    lateinit var bluetoothService: BluetoothService
    lateinit var telemetryRepository: TelemetryRepository
    lateinit var settingsRepository: SettingsRepository

    lateinit var csvLogger: CsvLogger

    override fun onCreate() {
        super.onCreate()
        val database = TelemetryDatabase.getDatabase(this)
        settingsRepository = SettingsRepository(this)
        csvLogger = CsvLogger(this)
        bluetoothService = BluetoothService(this)
        telemetryRepository = TelemetryRepository(
            bluetoothService,
            database.telemetryDao(),
            csvLogger,
            settingsRepository
        )
    }
}
