package com.example.esp32aldldashboard.parser

object TroubleCodeDictionary {
    val DTC_DESCRIPTIONS = mapOf(
        12 to "Crank Sensor / System Check - Normal if engine not running.",
        13 to "O2 Sensor Circuit - Open or No Activity.",
        14 to "Coolant Temperature Sensor - High Temperature Indicated.",
        15 to "Coolant Temperature Sensor - Low Temperature Indicated.",
        21 to "Throttle Position Sensor (TPS) - High Voltage.",
        22 to "Throttle Position Sensor (TPS) - Low Voltage.",
        23 to "Manifold Air Temperature (MAT) - Low Temperature Indicated.",
        24 to "Vehicle Speed Sensor (VSS) - Circuit Fault.",
        25 to "Manifold Air Temperature (MAT) - High Temperature Indicated.",
        32 to "Exhaust Gas Recirculation (EGR) - System Fault.",
        33 to "Manifold Absolute Pressure (MAP) - High Pressure Indicated.",
        34 to "Manifold Absolute Pressure (MAP) - Low Pressure Indicated.",
        35 to "Idle Air Control (IAC) - Position Error.",
        42 to "Electronic Spark Timing (EST) - Circuit Fault.",
        43 to "Electronic Spark Control (ESC) - Knock Sensor Fault.",
        44 to "Oxygen Sensor - Lean Exhaust Indicated.",
        45 to "Oxygen Sensor - Rich Exhaust Indicated.",
        51 to "PROM Error - Faulty or Incorrect Memcal.",
        52 to "Cal-Pack Error - Missing or Faulty Cal-Pack.",
        53 to "System Voltage - Battery Over-Voltage.",
        55 to "ADU Error - Internal ECM Fault."
    )

    fun getDescription(code: Int): String {
        return DTC_DESCRIPTIONS[code] ?: "Unknown Trouble Code $code"
    }

    fun isCritical(code: Int): Boolean {
        // Severe codes that might require immediate pull-over
        return code in listOf(14, 43, 51, 52, 53, 55)
    }
}
