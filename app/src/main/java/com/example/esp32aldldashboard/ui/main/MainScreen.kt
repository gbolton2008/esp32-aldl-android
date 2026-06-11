package com.example.esp32aldldashboard.ui.main

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavKey
import com.example.esp32aldldashboard.bluetooth.ConnectionState
import com.example.esp32aldldashboard.parser.ALDLFrame

// Theme Colors
private val DarkBg = Color(0xFF0F0F12)
private val CardBg = Color(0xFF1B1B22)
private val BorderColor = Color(0xFF2E2E38)
private val NeonCyan = Color(0xFF00E5FF)
private val NeonRed = Color(0xFFFF3D00)
private val NeonGreen = Color(0xFF00E676)
private val NeonOrange = Color(0xFFFF9100)
private val TextWhite = Color(0xFFEEEEEE)
private val TextMuted = Color(0xFF9E9EAF)

@Composable
fun MainScreen(
    onItemClick: (NavKey) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val viewModel: MainScreenViewModel = viewModel { MainScreenViewModel(context) }
    
    val connState by viewModel.connectionState.collectAsStateWithLifecycle()
    val frame by viewModel.latestFrame.collectAsStateWithLifecycle()
    val rawLog by viewModel.rawHexLog.collectAsStateWithLifecycle()
    val errorMsg by viewModel.errorMessage.collectAsStateWithLifecycle()
    val isCelsius by viewModel.isCelsius.collectAsStateWithLifecycle()

    val permissionsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.values.all { it }
        if (allGranted) {
            viewModel.connect()
        } else {
            Toast.makeText(context, "Bluetooth and Location permissions are required to connect", Toast.LENGTH_LONG).show()
        }
    }

    val onConnectClick = {
        val requiredPermissions = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT
            )
        } else {
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        }

        val allGranted = requiredPermissions.all { perm ->
            ContextCompat.checkSelfPermission(context, perm) == PackageManager.PERMISSION_GRANTED
        }

        if (allGranted) {
            viewModel.connect()
        } else {
            permissionsLauncher.launch(requiredPermissions)
        }
    }

    MainScreenContent(
        connState = connState,
        frame = frame,
        rawLog = rawLog,
        errorMsg = errorMsg,
        isCelsius = isCelsius,
        onConnect = onConnectClick,
        onDisconnect = { viewModel.disconnect() },
        onSimulate = { viewModel.startSimulation() },
        onToggleUnit = { viewModel.toggleTemperatureUnit() },
        modifier = modifier
    )
}

@Composable
fun MainScreenContent(
    connState: ConnectionState,
    frame: ALDLFrame?,
    rawLog: List<String>,
    errorMsg: String,
    isCelsius: Boolean,
    onConnect: () -> Unit,
    onDisconnect: () -> Unit,
    onSimulate: () -> Unit,
    onToggleUnit: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
            .verticalScroll(rememberScrollState())
    ) {
        // App Title Banner
        Text(
            text = "PONTIAC FIERO ALDL DASHBOARD",
            color = NeonOrange,
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 1.5.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
        )

        // Connection Action Card
        ConnectionCard(
            connState = connState,
            errorMsg = errorMsg,
            isCelsius = isCelsius,
            onConnect = onConnect,
            onDisconnect = onDisconnect,
            onSimulate = onSimulate,
            onToggleUnit = onToggleUnit
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Telemetry Panels
        if (frame != null) {
            // Trouble Codes Card (Flashing if active)
            if (frame.activeFaultCodes.isNotEmpty()) {
                TroubleCodesCard(activeCodes = frame.activeFaultCodes)
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Key Metrics
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricCard(
                    title = "ENGINE SPEED",
                    value = "${frame.engineSpeedRpm}",
                    unit = "RPM",
                    progress = frame.engineSpeedRpm / 6000f,
                    progressColor = NeonCyan,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "VEHICLE SPEED",
                    value = "${frame.vehicleSpeedMPH}",
                    unit = "MPH",
                    progress = frame.vehicleSpeedMPH / 120f,
                    progressColor = NeonGreen,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Grid of Minor Telemetry Parameters
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Coolant and Intake Temp Row
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    val coolantVal = if (isCelsius) frame.coolantTempC else frame.coolantTempF
                    val coolantUnit = if (isCelsius) "°C" else "°F"
                    val coolantProgress = (coolantVal + 40) / 290f // normalized range
                    
                    val matVal = if (isCelsius) frame.matC else frame.matF
                    val matProgress = (matVal + 40) / 290f
                    
                    GridItemCard(
                        title = "COOLANT TEMP",
                        value = String.format("%.1f", coolantVal) + coolantUnit,
                        progress = coolantProgress,
                        progressColor = if (coolantVal > 210) NeonRed else NeonGreen,
                        modifier = Modifier.weight(1f)
                    )
                    GridItemCard(
                        title = "MAT (AIR TEMP)",
                        value = String.format("%.1f", matVal) + coolantUnit,
                        progress = matProgress,
                        progressColor = NeonCyan,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Fuel control row
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    GridItemCard(
                        title = "BPW (INJECTOR)",
                        value = String.format("%.3f ms", frame.bpwMs),
                        progress = frame.bpwMs / 15f,
                        progressColor = NeonCyan,
                        modifier = Modifier.weight(1f)
                    )
                    GridItemCard(
                        title = "O2 SENSOR",
                        value = "${frame.o2SensorMv.toInt()} mV",
                        progress = frame.o2SensorMv / 1000f,
                        progressColor = if (frame.isRich) NeonGreen else NeonOrange,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Air flow & Throttle Position
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    GridItemCard(
                        title = "TPS (THROTTLE)",
                        value = String.format("%.2f V", frame.tpsVolts),
                        progress = frame.tpsVolts / 5.0f,
                        progressColor = NeonGreen,
                        modifier = Modifier.weight(1f)
                    )
                    GridItemCard(
                        title = "MAP (VACUUM)",
                        value = String.format("%.1f kPa", frame.mapKpa),
                        progress = frame.mapKpa / 105f,
                        progressColor = NeonCyan,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Fuel trims (BLM & INT) & Battery
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    GridItemCard(
                        title = "BLM / INT",
                        value = "${frame.blm} / ${frame.integrator}",
                        progress = frame.blm / 256f,
                        progressColor = if (frame.blm in 120..136) NeonGreen else NeonOrange,
                        modifier = Modifier.weight(1f)
                    )
                    GridItemCard(
                        title = "BATTERY VOLTS",
                        value = String.format("%.1f V", frame.batteryVolts),
                        progress = (frame.batteryVolts - 8) / 8f,
                        progressColor = if (frame.batteryVolts < 12.0f) NeonRed else NeonGreen,
                        modifier = Modifier.weight(1f)
                    )
                }

                // IAC, Spark & EGR
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    GridItemCard(
                        title = "IAC POSITION",
                        value = "${frame.iacPosition} Steps",
                        progress = frame.iacPosition / 160f,
                        progressColor = NeonCyan,
                        modifier = Modifier.weight(1f)
                    )
                    GridItemCard(
                        title = "SPARK / EGR",
                        value = String.format("%.1f° / %.0f%%", frame.sparkAdvance, frame.egrDutyCycle),
                        progress = frame.egrDutyCycle / 100f,
                        progressColor = NeonCyan,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Status Badges Section
            StatusFlagsPanel(frame = frame)

            Spacer(modifier = Modifier.height(16.dp))
        } else {
            // Empty / Waiting display
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .padding(horizontal = 16.dp)
                    .background(CardBg, shape = RoundedCornerShape(12.dp))
                    .border(1.dp, BorderColor, shape = RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Waiting for ALDL Stream data...\nSelect Connection or Simulation above.",
                    color = TextMuted,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Live Diagnostic Console log
        DiagnosticConsole(rawLog = rawLog)
        
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun ConnectionCard(
    connState: ConnectionState,
    errorMsg: String,
    isCelsius: Boolean,
    onConnect: () -> Unit,
    onDisconnect: () -> Unit,
    onSimulate: () -> Unit,
    onToggleUnit: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .border(1.dp, BorderColor, shape = RoundedCornerShape(12.dp))
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Status bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "STATUS: ",
                        color = TextMuted,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    val (statusText, statusColor) = when (connState) {
                        ConnectionState.DISCONNECTED -> "DISCONNECTED" to TextMuted
                        ConnectionState.CONNECTING -> "CONNECTING..." to NeonOrange
                        ConnectionState.CONNECTED -> "CONNECTED" to NeonGreen
                        ConnectionState.ERROR -> "ERROR" to NeonRed
                    }
                    Text(
                        text = statusText,
                        color = statusColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                // Temperature Toggle Button
                Button(
                    onClick = onToggleUnit,
                    colors = ButtonDefaults.buttonColors(containerColor = BorderColor),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.height(28.dp)
                ) {
                    Text(
                        text = if (isCelsius) "USE °F" else "USE °C",
                        color = TextWhite,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (errorMsg.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = errorMsg,
                    color = NeonRed,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (connState != ConnectionState.CONNECTED && connState != ConnectionState.CONNECTING) {
                    Button(
                        onClick = onConnect,
                        colors = ButtonDefaults.buttonColors(containerColor = NeonOrange),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("CONNECT BT", fontWeight = FontWeight.Bold)
                    }
                } else {
                    Button(
                        onClick = onDisconnect,
                        colors = ButtonDefaults.buttonColors(containerColor = BorderColor),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("DISCONNECT", fontWeight = FontWeight.Bold, color = TextWhite)
                    }
                }

                Button(
                    onClick = onSimulate,
                    colors = ButtonDefaults.buttonColors(containerColor = BorderColor),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("SIMULATE", fontWeight = FontWeight.Bold, color = NeonCyan)
                }
            }
        }
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    unit: String,
    progress: Float,
    progressColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        modifier = modifier
            .border(1.dp, BorderColor, shape = RoundedCornerShape(12.dp))
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                color = TextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = value,
                    color = TextWhite,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = unit,
                    color = TextMuted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = progress.coerceIn(0f, 1f),
                color = progressColor,
                trackColor = BorderColor,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
            )
        }
    }
}

@Composable
fun GridItemCard(
    title: String,
    value: String,
    progress: Float,
    progressColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        modifier = modifier
            .border(1.dp, BorderColor, shape = RoundedCornerShape(10.dp))
    ) {
        Column(
            modifier = Modifier.padding(10.dp)
        ) {
            Text(
                text = title,
                color = TextMuted,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                color = TextWhite,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(modifier = Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = progress.coerceIn(0f, 1f),
                color = progressColor,
                trackColor = BorderColor,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
            )
        }
    }
}

@Composable
fun TroubleCodesCard(activeCodes: List<Int>) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .border(1.dp, NeonRed, shape = RoundedCornerShape(12.dp))
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "⚠️ ACTIVE ECM FAULT CODES",
                    color = NeonRed,
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                activeCodes.forEach { code ->
                    Box(
                        modifier = Modifier
                            .background(NeonRed, shape = RoundedCornerShape(4.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "CODE $code",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Refer to Fiero shop manual for diagnosis.",
                color = TextMuted,
                fontSize = 11.sp
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StatusFlagsPanel(frame: ALDLFrame) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .border(1.dp, BorderColor, shape = RoundedCornerShape(12.dp))
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Text(
                text = "LOOP STATUS & SYSTEM SWITCHES",
                color = TextMuted,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(10.dp))
            
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                StatusBadge(label = "Closed Loop", active = frame.isClosedLoop, activeColor = NeonGreen)
                StatusBadge(label = "Rich Mixture", active = frame.isRich, activeColor = NeonGreen)
                StatusBadge(label = "BLM Enabled", active = frame.blmEnable, activeColor = NeonGreen)
                StatusBadge(label = "TCC Locked", active = frame.isTccLocked, activeColor = NeonGreen)
                StatusBadge(label = "AC Clutch", active = frame.isAcClutchEnabled, activeColor = NeonGreen)
                StatusBadge(label = "Park/Neutral", active = frame.isParkNeutral, activeColor = NeonCyan)
                StatusBadge(label = "A/C Request", active = frame.isAcEnabled, activeColor = NeonCyan)
                StatusBadge(label = "PS Cramp", active = frame.isPowerSteeringCrampActive, activeColor = NeonOrange)
                StatusBadge(label = "Async Pulse", active = frame.asyncPulse, activeColor = NeonOrange)
                StatusBadge(label = "Quasi Pulse", active = frame.quasiPulse, activeColor = NeonOrange)
            }
        }
    }
}

@Composable
fun StatusBadge(label: String, active: Boolean, activeColor: Color) {
    Box(
        modifier = Modifier
            .background(
                color = if (active) activeColor.copy(alpha = 0.15f) else Color.Transparent,
                shape = RoundedCornerShape(4.dp)
            )
            .border(
                width = 1.dp,
                color = if (active) activeColor else BorderColor,
                shape = RoundedCornerShape(4.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = label,
            color = if (active) activeColor else TextMuted,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun DiagnosticConsole(rawLog: List<String>) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .border(1.dp, BorderColor, shape = RoundedCornerShape(12.dp))
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Text(
                text = "DIAGNOSTIC TELEMETRY STREAM LOG",
                color = TextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(Color.Black, shape = RoundedCornerShape(6.dp))
                    .padding(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    if (rawLog.isEmpty()) {
                        Text(
                            text = "Console Idle. Connect to view hex dump...",
                            color = TextMuted,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp
                        )
                    } else {
                        rawLog.forEach { logLine ->
                            Text(
                                text = logLine,
                                color = NeonGreen,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
