package com.gronod.esp32aldldashboard

import android.app.Application
import com.gronod.esp32aldldashboard.bluetooth.BluetoothService
import com.gronod.esp32aldldashboard.repository.BLMTableRepository
import com.gronod.esp32aldldashboard.repository.ChartPreferencesRepository
import com.gronod.esp32aldldashboard.repository.SettingsRepository
import com.gronod.esp32aldldashboard.repository.TelemetryRepository
import com.gronod.esp32aldldashboard.logging.CsvLogger
import com.gronod.esp32aldldashboard.logging.RawStreamLogger
import com.gronod.esp32aldldashboard.data.database.TelemetryDatabase

class AldlApplication : Application() {
    lateinit var bluetoothService: BluetoothService
    lateinit var telemetryRepository: TelemetryRepository
    lateinit var settingsRepository: SettingsRepository

    lateinit var csvLogger: CsvLogger
    lateinit var rawStreamLogger: RawStreamLogger
    lateinit var chartPreferencesRepository: ChartPreferencesRepository
    lateinit var blmTableRepository: BLMTableRepository

    override fun onCreate() {
        super.onCreate()
        val database = TelemetryDatabase.getDatabase(this)
        settingsRepository = SettingsRepository(this)
        chartPreferencesRepository = ChartPreferencesRepository(this)
        blmTableRepository = BLMTableRepository()
        csvLogger = CsvLogger(this)
        rawStreamLogger = RawStreamLogger(this)
        bluetoothService = BluetoothService(this, rawStreamLogger, settingsRepository)
        telemetryRepository = TelemetryRepository(
            this,
            bluetoothService,
            database.telemetryDao(),
            csvLogger,
            settingsRepository,
            blmTableRepository
        )
    }
}
