package com.gronod.esp32aldldashboard.repository

import android.content.Context
import android.content.Intent
import com.gronod.esp32aldldashboard.bluetooth.BluetoothService
import com.gronod.esp32aldldashboard.bluetooth.BluetoothForegroundService
import com.gronod.esp32aldldashboard.bluetooth.ConnectionState
import com.gronod.esp32aldldashboard.parser.ALDLFrame
import com.gronod.esp32aldldashboard.data.database.SessionEntity
import com.gronod.esp32aldldashboard.data.database.TelemetryDao
import com.gronod.esp32aldldashboard.data.database.TelemetryDataPointEntity
import com.gronod.esp32aldldashboard.logging.CsvLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.StateFlow

class TelemetryRepository(
    private val context: Context,
    private val bluetoothService: BluetoothService,
    private val telemetryDao: TelemetryDao,
    private val csvLogger: CsvLogger,
    private val settingsRepository: SettingsRepository,
    private val blmTableRepository: BLMTableRepository
) {
    private val repoScope = CoroutineScope(Dispatchers.IO + Job())
    private var currentSessionId: Long? = null
    private var isRecording = false

    init {
        observeConnectionState()
        observeTelemetry()
    }

    val connectionState: StateFlow<ConnectionState> = bluetoothService.connectionState
    val latestFrame: StateFlow<ALDLFrame?> = bluetoothService.latestFrame
    val rawHexLog: StateFlow<List<String>> = bluetoothService.rawHexLog
    
    val framesReceived: StateFlow<Int> = bluetoothService.framesReceived
    val parseErrors: StateFlow<Int> = bluetoothService.parseErrors
    val currentFrameRate: StateFlow<Int> = bluetoothService.currentFrameRate
    val errorMessage: StateFlow<String> = bluetoothService.errorMessage

    private fun observeConnectionState() {
        repoScope.launch {
            bluetoothService.connectionState.collectLatest { state ->
                when (state) {
                    ConnectionState.CONNECTED -> {
                        val autoLog = settingsRepository.autoLoggingFlow.first()
                        if (autoLog) {
                            startSession(isSimulation = false) // Or true if we knew
                        }
                    }
                    ConnectionState.DISCONNECTED, ConnectionState.ERROR -> {
                        endSession()
                    }
                    else -> {}
                }
            }
        }
    }

    private fun observeTelemetry() {
        repoScope.launch {
            bluetoothService.latestFrame.collectLatest { frame ->
                if (frame != null) {
                    // Update BLM table with every frame (not just when recording)
                    blmTableRepository.updateCell(
                        rpm = frame.engineSpeedRpm,
                        mapKpa = frame.mapKpa,
                        blm = frame.blm,
                        intValue = frame.integrator
                    )
                    
                    if (isRecording) {
                        csvLogger.logFrame(frame)
                        currentSessionId?.let { sid ->
                            val dataPoint = TelemetryDataPointEntity(
                                sessionId = sid,
                                timestamp = frame.timestamp,
                                rawBytes = frame.rawBytes
                            )
                            telemetryDao.insertDataPoints(listOf(dataPoint))
                        }
                    }
                }
            }
        }
    }

    private suspend fun startSession(isSimulation: Boolean) {
        if (isRecording) return
        val session = SessionEntity(
            startTime = System.currentTimeMillis(),
            name = "Session ${System.currentTimeMillis()}",
            isSimulation = isSimulation
        )
        currentSessionId = telemetryDao.insertSession(session)
        csvLogger.startNewSession(isSimulation)
        isRecording = true
    }

    private suspend fun endSession() {
        if (!isRecording) return
        isRecording = false
        csvLogger.endSession()
        currentSessionId?.let { sid ->
            telemetryDao.endSession(sid, System.currentTimeMillis())
        }
        currentSessionId = null
    }

    fun connect() {
        startForegroundService()
        bluetoothService.connect()
    }

    fun disconnect() {
        stopForegroundService()
        bluetoothService.disconnect()
    }

    fun startSimulation() {
        startForegroundService()
        bluetoothService.startSimulation()
    }

    private fun startForegroundService() {
        val intent = Intent(context, BluetoothForegroundService::class.java).apply {
            action = BluetoothForegroundService.ACTION_START
        }
        context.startForegroundService(intent)
    }

    private fun stopForegroundService() {
        val intent = Intent(context, BluetoothForegroundService::class.java).apply {
            action = BluetoothForegroundService.ACTION_STOP
        }
        context.startService(intent)
    }
}
