package com.gronod.esp32aldldashboard.ui.charts

import androidx.compose.ui.graphics.Color
import com.gronod.esp32aldldashboard.parser.ALDLFrame

enum class ChartParameter(
    val displayName: String,
    val color: Color,
    val maxValue: Float,
    val extractValue: (ALDLFrame) -> Float
) {
    RPM(
        displayName = "RPM",
        color = Color(0xFF00FFCC),
        maxValue = 6000f,
        extractValue = { it.engineSpeedRpm.toFloat() }
    ),
    COOLANT_TEMP(
        displayName = "Coolant Temp",
        color = Color(0xFFFF5722),
        maxValue = 250f,
        extractValue = { it.coolantTempC }
    ),
    MAP(
        displayName = "MAP",
        color = Color(0xFF2196F3),
        maxValue = 105f,
        extractValue = { it.mapKpa }
    ),
    TPS(
        displayName = "TPS",
        color = Color(0xFF9C27B0),
        maxValue = 5.5f,
        extractValue = { it.tpsVolts }
    ),
    O2_SENSOR(
        displayName = "O2 Sensor",
        color = Color(0xFF4CAF50),
        maxValue = 1000f,
        extractValue = { it.o2SensorMv }
    ),
    BATTERY(
        displayName = "Battery",
        color = Color(0xFFFFEB3B),
        maxValue = 16f,
        extractValue = { it.batteryVolts }
    ),
    SPARK_ADVANCE(
        displayName = "Spark Advance",
        color = Color(0xFF00BCD4),
        maxValue = 40f,
        extractValue = { it.sparkAdvance }
    ),
    BPW(
        displayName = "BPW",
        color = Color(0xFFE91E63),
        maxValue = 15f,
        extractValue = { it.bpwMs }
    ),
    MAT(
        displayName = "MAT",
        color = Color(0xFF795548),
        maxValue = 80f,
        extractValue = { it.matC }
    ),
    BLM(
        displayName = "BLM",
        color = Color(0xFF3F51B5),
        maxValue = 160f,
        extractValue = { it.blm.toFloat() }
    ),
    INTEGRATOR(
        displayName = "Integrator",
        color = Color(0xFF607D8B),
        maxValue = 255f,
        extractValue = { it.integrator.toFloat() }
    ),
    IAC(
        displayName = "IAC Position",
        color = Color(0xFFFF9800),
        maxValue = 255f,
        extractValue = { it.iacPosition.toFloat() }
    )
}
