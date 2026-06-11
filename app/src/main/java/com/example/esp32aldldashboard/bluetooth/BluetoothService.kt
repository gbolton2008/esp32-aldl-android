package com.example.esp32aldldashboard.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.util.Log
import com.example.esp32aldldashboard.parser.ALDLFrame
import com.example.esp32aldldashboard.parser.ALDLParser
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.IOException
import java.io.InputStream
import java.util.UUID

enum class ConnectionState {
    DISCONNECTED,
    CONNECTING,
    CONNECTED,
    ERROR
}

class BluetoothService(private val context: Context) {

    private val TAG = "ALDLBluetoothService"
    private val SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    private val _connectionState = MutableStateFlow(ConnectionState.DISCONNECTED)
    val connectionState: StateFlow<ConnectionState> = _connectionState

    private val _latestFrame = MutableStateFlow<ALDLFrame?>(null)
    val latestFrame: StateFlow<ALDLFrame?> = _latestFrame

    private val _rawHexLog = MutableStateFlow<List<String>>(emptyList())
    val rawHexLog: StateFlow<List<String>> = _rawHexLog

    private val _framesReceived = MutableStateFlow(0)
    val framesReceived: StateFlow<Int> = _framesReceived

    private val _parseErrors = MutableStateFlow(0)
    val parseErrors: StateFlow<Int> = _parseErrors

    private val _currentFrameRate = MutableStateFlow(0)
    val currentFrameRate: StateFlow<Int> = _currentFrameRate

    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> = _errorMessage

    private var connectionJob: Job? = null
    private var socket: BluetoothSocket? = null
    private var isConnected = false
    private var isSimulating = false

    private val bluetoothAdapter: BluetoothAdapter? by lazy {
        val manager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        manager.adapter
    }

    private fun addRawHexLog(hex: String) {
        val currentList = _rawHexLog.value.toMutableList()
        if (currentList.size >= 100) {
            currentList.removeAt(0)
        }
        currentList.add(hex)
        _rawHexLog.value = currentList
    }

    fun startSimulation() {
        if (_connectionState.value == ConnectionState.CONNECTED) {
            disconnect()
        }
        isSimulating = true
        _connectionState.value = ConnectionState.CONNECTED
        _errorMessage.value = ""

        connectionJob = CoroutineScope(Dispatchers.Default).launch {
            var simStep = 0
            val basePayload = byteArrayOf(
                0x20.toByte(), 0x00.toByte(), 0x2A.toByte(), 0x5F.toByte(), 0x59.toByte(),
                0x00.toByte(), 0xF4.toByte(), 0x00.toByte(), 0x1E.toByte(), 0x80.toByte(),
                0x65.toByte(), 0x08.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(),
                0x25.toByte(), 0x18.toByte(), 0x7D.toByte(), 0x80.toByte(), 0x00.toByte(),
                0x00.toByte(), 0x00.toByte(), 0xC9.toByte(), 0x02.toByte(), 0x62.toByte()
            )

            while (isActive && isSimulating) {
                // Generate dynamic simulation data to show moving values on UI
                val payload = basePayload.clone()
                simStep++

                // Simulate Engine Speed (Index 7), Coolant Temp (Index 4), Speed (Index 5), TPS (Index 8)
                val rpmRaw: Int
                val coolantRaw: Int
                val speedRaw: Int
                val tpsRaw: Int
                val bpwHighRaw: Int
                val bpwLowRaw: Int
                val mapRaw: Int
                val o2MvRaw: Int
                val codesByte1: Int
                val miscByte1: Int

                when (simStep % 4) {
                    0 -> { // Key On Engine Off (Prompt data)
                        rpmRaw = 0
                        coolantRaw = 89 // ~80F / 26C
                        speedRaw = 0
                        tpsRaw = 30 // ~0.58V
                        mapRaw = 244 // ~100 kPa (Atmospheric)
                        o2MvRaw = 101 // ~448 mV
                        bpwHighRaw = 0x02
                        bpwLowRaw = 0x62 // 610 dec = 9.3ms
                        codesByte1 = 0x08 // Code 21 Active (TPS High)
                        miscByte1 = 0x00 // Open loop
                    }
                    1 -> { // Cranking
                        rpmRaw = 8 // 200 RPM
                        coolantRaw = 90
                        speedRaw = 0
                        tpsRaw = 35 // ~0.68V
                        mapRaw = 220 // ~91 kPa
                        o2MvRaw = 110 // ~488 mV
                        bpwHighRaw = 0x04
                        bpwLowRaw = 0x10 // 1040 dec = 15.8ms
                        codesByte1 = 0x00
                        miscByte1 = 0x00
                    }
                    2 -> { // Idle (Warmup)
                        rpmRaw = 30 // 750 RPM
                        coolantRaw = 140 // ~149F / 65C
                        speedRaw = 0
                        tpsRaw = 28 // ~0.54V
                        mapRaw = 80 // ~39 kPa (Vacuum)
                        o2MvRaw = (120 + 80 * Math.sin(simStep.toDouble())).toInt() // oscillating O2
                        bpwHighRaw = 0x00
                        bpwLowRaw = 0xD0 // 208 dec = 3.1ms
                        codesByte1 = 0x00
                        miscByte1 = 0x82 // Closed Loop, BLM Enable
                    }
                    else -> { // Cruising
                        rpmRaw = 88 // 2200 RPM
                        coolantRaw = 180 // ~203F / 95C
                        speedRaw = 45 // 45 MPH
                        tpsRaw = 62 // ~1.2V
                        mapRaw = 140 // ~62 kPa
                        o2MvRaw = (120 + 100 * Math.sin(simStep.toDouble())).toInt()
                        bpwHighRaw = 0x01
                        bpwLowRaw = 0x20 // 288 dec = 4.4ms
                        codesByte1 = 0x00
                        miscByte1 = 0xC2 // Closed Loop, BLM Enable, Rich
                    }
                }

                payload[4] = coolantRaw.toByte()
                payload[5] = speedRaw.toByte()
                payload[6] = mapRaw.toByte()
                payload[7] = rpmRaw.toByte()
                payload[8] = tpsRaw.toByte()
                payload[10] = o2MvRaw.toByte()
                payload[11] = codesByte1.toByte()
                payload[14] = miscByte1.toByte()
                payload[23] = bpwHighRaw.toByte()
                payload[24] = bpwLowRaw.toByte()

                val parsed = ALDLParser.parseFrame(payload)
                if (parsed is com.example.esp32aldldashboard.parser.ALDLParseResult.Success) {
                    _latestFrame.value = parsed.frame
                    val hexString = payload.joinToString(" ") { String.format("%02X", it) }
                    addRawHexLog("AA 55 $hexString (SIMULATED)")
                }

                delay(1000)
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun connect() {
        if (isSimulating) {
            isSimulating = false
            connectionJob?.cancel()
        }

        if (_connectionState.value == ConnectionState.CONNECTED || _connectionState.value == ConnectionState.CONNECTING) {
            return
        }

        val adapter = bluetoothAdapter
        if (adapter == null) {
            _connectionState.value = ConnectionState.ERROR
            _errorMessage.value = "Bluetooth is not supported on this device"
            return
        }

        if (!adapter.isEnabled) {
            _connectionState.value = ConnectionState.ERROR
            _errorMessage.value = "Bluetooth is turned off"
            return
        }

        _connectionState.value = ConnectionState.CONNECTING
        _errorMessage.value = ""

        connectionJob = CoroutineScope(Dispatchers.IO).launch {
            try {
                val pairedDevices = adapter.bondedDevices
                val targetDevice: BluetoothDevice? = pairedDevices.find { it.name == "ESP32-ALDL" }

                if (targetDevice == null) {
                    withContext(Dispatchers.Main) {
                        _connectionState.value = ConnectionState.ERROR
                        _errorMessage.value = "Device named 'ESP32-ALDL' is not paired. Please pair in system settings first."
                    }
                    return@launch
                }

                socket = targetDevice.createRfcommSocketToServiceRecord(SPP_UUID)
                adapter.cancelDiscovery() // cancel discovery to speed up connection

                socket?.connect()
                isConnected = true

                withContext(Dispatchers.Main) {
                    _connectionState.value = ConnectionState.CONNECTED
                }

                readDataStream(socket!!.inputStream)

            } catch (e: Exception) {
                Log.e(TAG, "Connection failed: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    _connectionState.value = ConnectionState.ERROR
                    _errorMessage.value = e.message ?: "Failed to connect"
                }
                disconnect()
            }
        }
    }

    private suspend fun readDataStream(inputStream: InputStream) {
        val readBuffer = ByteArray(128)
        val syncBuffer = ArrayDeque<Byte>(128)
        var lastFrameTime = System.currentTimeMillis()
        var framesInCurrentSecond = 0

        while (currentCoroutineContext().isActive && isConnected) {
            try {
                val bytesRead = withContext(Dispatchers.IO) {
                    inputStream.read(readBuffer)
                }
                if (bytesRead <= 0) {
                    break // Stream closed
                }

                for (j in 0 until bytesRead) {
                    syncBuffer.addLast(readBuffer[j])
                }

                // Check for frame matches in buffer
                while (syncBuffer.size >= 27) {
                    var foundHeader = false
                    val iterator = syncBuffer.iterator()
                    var idx = 0
                    var headerIdx = -1
                    
                    // Find header AA 55
                    var prev = iterator.next().toInt() and 0xFF
                    while (iterator.hasNext()) {
                        val curr = iterator.next().toInt() and 0xFF
                        if (prev == 0xAA && curr == 0x55) {
                            headerIdx = idx
                            foundHeader = true
                            break
                        }
                        prev = curr
                        idx++
                    }

                    if (foundHeader) {
                        // Discard garbage preceding header
                        if (headerIdx > 0) {
                            _parseErrors.value += 1
                            for (i in 0 until headerIdx) {
                                syncBuffer.removeFirst()
                            }
                        }

                        if (syncBuffer.size >= 27) {
                            syncBuffer.removeFirst() // AA
                            syncBuffer.removeFirst() // 55

                            val payload = ByteArray(25)
                            for (p in 0 until 25) {
                                payload[p] = syncBuffer.removeFirst()
                            }

                            val parsed = ALDLParser.parseFrame(payload)
                            when (parsed) {
                                is com.example.esp32aldldashboard.parser.ALDLParseResult.Success -> {
                                    _framesReceived.value += 1
                                    framesInCurrentSecond++
                                    
                                    val now = System.currentTimeMillis()
                                    if (now - lastFrameTime >= 1000) {
                                        _currentFrameRate.value = framesInCurrentSecond
                                        framesInCurrentSecond = 0
                                        lastFrameTime = now
                                    }

                                    withContext(Dispatchers.Main) {
                                        _latestFrame.value = parsed.frame
                                        val hexString = payload.joinToString(" ") { String.format("%02X", it) }
                                        addRawHexLog("AA 55 $hexString")
                                    }
                                }
                                is com.example.esp32aldldashboard.parser.ALDLParseResult.InvalidData -> {
                                    _parseErrors.value += 1
                                    Log.w(TAG, "Invalid frame: ${parsed.reason}")
                                }
                                com.example.esp32aldldashboard.parser.ALDLParseResult.Incomplete -> {
                                    // Handled by size check
                                }
                            }
                        } else {
                            // Header found, but waiting for full 27-byte frame
                            break
                        }
                    } else {
                        // Header sequence not found, purge all but last byte if it is part of a potential header
                        val lastByte = syncBuffer.last()
                        syncBuffer.clear()
                        if ((lastByte.toInt() and 0xFF) == 0xAA) {
                            syncBuffer.addLast(lastByte)
                        } else {
                            _parseErrors.value += 1
                        }
                        break
                    }
                }

            } catch (e: IOException) {
                Log.e(TAG, "Read stream error: ${e.message}")
                break
            }
        }

        withContext(Dispatchers.Main) {
            _connectionState.value = ConnectionState.ERROR
            _errorMessage.value = "Connection lost"
        }
        disconnect()
    }

    fun disconnect() {
        isConnected = false
        isSimulating = false
        connectionJob?.cancel()
        connectionJob = null

        try {
            socket?.close()
        } catch (e: IOException) {
            Log.e(TAG, "Error closing socket: ${e.message}")
        }
        socket = null

        _connectionState.value = ConnectionState.DISCONNECTED
    }
}
