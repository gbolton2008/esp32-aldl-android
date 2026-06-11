package com.example.esp32aldldashboard.parser

data class ALDLFrame(
    val rawBytes: ByteArray,
    val iacPosition: Int,
    val coolantTempC: Float,
    val coolantTempF: Float,
    val vehicleSpeedMPH: Int,
    val mapVolts: Float,
    val mapKpa: Float,
    val engineSpeedRpm: Int,
    val tpsVolts: Float,
    val integrator: Int,
    val o2SensorMv: Float,
    val batteryVolts: Float,
    val blm: Int,
    val richLeanCrosses: Int,
    val sparkAdvance: Float,
    val egrDutyCycle: Float,
    val matC: Float,
    val matF: Float,
    val bpwMs: Float,
    val blmEnable: Boolean,
    val quasiPulse: Boolean,
    val asyncPulse: Boolean,
    val isRich: Boolean,
    val isClosedLoop: Boolean,
    val isAcEnabled: Boolean,
    val isParkNeutral: Boolean,
    val isAcClutchEnabled: Boolean,
    val isTccLocked: Boolean,
    val isPowerSteeringCrampActive: Boolean,
    val activeFaultCodes: List<Int>,
    val timestamp: Long = System.currentTimeMillis()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as ALDLFrame
        return rawBytes.contentEquals(other.rawBytes)
    }

    override fun hashCode(): Int {
        return rawBytes.contentHashCode()
    }
}

object ALDLParser {
    // MAT C interpolation table: key is raw value, value is Temp in C
    private val matTableC = listOf(
        0 to 200.0f,
        12 to 150.0f,
        13 to 145.0f,
        14 to 140.0f,
        16 to 135.0f,
        18 to 130.0f,
        21 to 125.0f,
        23 to 120.0f,
        26 to 115.0f,
        30 to 110.0f,
        34 to 105.0f,
        39 to 100.0f,
        44 to 95.0f,
        50 to 90.0f,
        56 to 85.0f,
        64 to 80.0f,
        72 to 75.0f,
        81 to 70.0f,
        92 to 65.0f,
        102 to 60.0f,
        114 to 55.0f,
        126 to 50.0f,
        139 to 45.0f,
        152 to 40.0f,
        165 to 35.0f,
        177 to 30.0f,
        189 to 25.0f,
        199 to 20.0f,
        209 to 15.0f,
        218 to 10.0f,
        225 to 5.0f,
        231 to 0.0f,
        237 to -5.0f,
        241 to -10.0f,
        245 to -15.0f,
        247 to -20.0f,
        250 to -25.0f,
        251 to -30.0f,
        255 to -40.0f
    )

    // MAT F interpolation table: key is raw value, value is Temp in F
    private val matTableF = listOf(
        0 to 392.0f,
        12 to 302.0f,
        13 to 293.0f,
        14 to 284.0f,
        16 to 275.0f,
        18 to 266.0f,
        21 to 257.0f,
        23 to 248.0f,
        26 to 239.0f,
        30 to 230.0f,
        34 to 221.0f,
        39 to 212.0f,
        44 to 203.0f,
        50 to 194.0f,
        56 to 185.0f,
        64 to 176.0f,
        72 to 167.0f,
        81 to 158.0f,
        92 to 149.0f,
        102 to 140.0f,
        114 to 131.0f,
        126 to 122.0f,
        139 to 113.0f,
        152 to 104.0f,
        165 to 95.0f,
        177 to 86.0f,
        189 to 77.0f,
        199 to 68.0f,
        209 to 59.0f,
        218 to 50.0f,
        225 to 41.0f,
        231 to 32.0f,
        237 to 23.0f,
        241 to 14.0f,
        245 to 5.0f,
        247 to -4.0f,
        250 to -13.0f,
        251 to -22.0f,
        255 to -40.0f
    )

    private fun interpolate(raw: Int, table: List<Pair<Int, Float>>): Float {
        if (raw <= table.first().first) return table.first().second
        if (raw >= table.last().first) return table.last().second
        for (i in 0 until table.size - 1) {
            val current = table[i]
            val next = table[i + 1]
            if (raw >= current.first && raw <= next.first) {
                val span = next.first - current.first
                if (span == 0) return current.second
                val t = (raw - current.first).toFloat() / span
                return current.second + t * (next.second - current.second)
            }
        }
        return 0.0f
    }

    /**
     * Parses a 25-byte raw data payload.
     */
    fun parseFrame(data: ByteArray): ALDLFrame? {
        if (data.size != 25) return null

        val u = IntArray(25) { data[it].toInt() and 0xFF }

        // Mappings based on 1-indexed btByteNumber in 24-INT10.ads (index = byteNumber - 1)
        val iacPosition = u[3] // Byte 4
        val coolantTempC = u[4] * 0.75f - 40.0f // Byte 5
        val coolantTempF = u[4] * 1.35f - 40.0f // Byte 5
        val vehicleSpeedMPH = u[5] // Byte 6
        val mapVolts = u[6] * 0.019608f // Byte 7
        val mapKpa = u[6] * 0.369f + 10.354f // Byte 7
        val engineSpeedRpm = u[7] * 25 // Byte 8
        val tpsVolts = u[8] * 0.019608f // Byte 9
        val integrator = u[9] // Byte 10
        val o2SensorMv = u[10] * 4.44f // Byte 11

        val codesByte1 = u[11] // Byte 12
        val codesByte2 = u[12] // Byte 13
        val codesByte3 = u[13] // Byte 14
        val miscByte1 = u[14] // Byte 15
        val miscByte2 = u[15] // Byte 16
        val miscByte3 = u[16] // Byte 17

        val batteryVolts = u[17] * 0.1f // Byte 18
        val blm = u[18] // Byte 19
        val richLeanCrosses = u[19] // Byte 20
        val sparkAdvance = u[20] * 0.351563f // Byte 21
        val egrDutyCycle = u[21] * 0.392157f // Byte 22
        
        // MAT (Air Temp) Interpolation
        val matC = interpolate(u[22], matTableC) // Byte 23
        val matF = interpolate(u[22], matTableF) // Byte 23

        // BPW (Base Pulse Width) 16-bit
        val rawBpw = (u[23] shl 8) or u[24] // Byte 24 (High), Byte 25 (Low)
        val bpwMs = rawBpw * 0.015259f

        // Status Flags Decoding
        // Misc Byte 1 (Byte 15)
        val blmEnable = (miscByte1 and 0x02) != 0     // bit 1
        val quasiPulse = (miscByte1 and 0x08) != 0    // bit 3
        val asyncPulse = (miscByte1 and 0x10) != 0    // bit 4
        val isRich = (miscByte1 and 0x40) != 0        // bit 6 (1=RICH, 0=LEAN)
        val isClosedLoop = (miscByte1 and 0x80) != 0  // bit 7 (1=CLOSED, 0=OPEN)

        // Misc Byte 2 (Byte 16)
        val isAcEnabled = (miscByte2 and 0x20) == 0   // bit 5 (0=ENABLED, 1=DISABLED/IDLE)
        val isParkNeutral = (miscByte2 and 0x80) != 0 // bit 7 (1=PARK/NEUTRAL, 0=IN GEAR)

        // Misc Byte 3 (Byte 17)
        val isAcClutchEnabled = (miscByte3 and 0x01) != 0 // bit 0 (1=ENABLED)
        val isTccLocked = (miscByte3 and 0x04) != 0       // bit 2 (1=LOCKED)
        val isPowerSteeringCrampActive = (miscByte3 and 0x20) != 0 // bit 5 (1=ACTIVE)

        // Active Fault Codes list based on code bits
        val activeCodes = mutableListOf<Int>()
        // Byte 12
        if ((codesByte1 and 0x80) != 0) activeCodes.add(12) // bit 7
        if ((codesByte1 and 0x40) != 0) activeCodes.add(13) // bit 6
        if ((codesByte1 and 0x20) != 0) activeCodes.add(14) // bit 5
        if ((codesByte1 and 0x10) != 0) activeCodes.add(15) // bit 4
        if ((codesByte1 and 0x08) != 0) activeCodes.add(21) // bit 3
        if ((codesByte1 and 0x04) != 0) activeCodes.add(22) // bit 2
        if ((codesByte1 and 0x02) != 0) activeCodes.add(23) // bit 1
        if ((codesByte1 and 0x01) != 0) activeCodes.add(24) // bit 0
        // Byte 13
        if ((codesByte2 and 0x80) != 0) activeCodes.add(25) // bit 7
        if ((codesByte2 and 0x20) != 0) activeCodes.add(32) // bit 5
        if ((codesByte2 and 0x10) != 0) activeCodes.add(33) // bit 4
        if ((codesByte2 and 0x08) != 0) activeCodes.add(34) // bit 3
        if ((codesByte2 and 0x04) != 0) activeCodes.add(35) // bit 2
        if ((codesByte2 and 0x01) != 0) activeCodes.add(42) // bit 0
        // Byte 14
        if ((codesByte3 and 0x80) != 0) activeCodes.add(43) // bit 7
        if ((codesByte3 and 0x40) != 0) activeCodes.add(44) // bit 6
        if ((codesByte3 and 0x20) != 0) activeCodes.add(45) // bit 5
        if ((codesByte3 and 0x10) != 0) activeCodes.add(51) // bit 4
        if ((codesByte3 and 0x08) != 0) activeCodes.add(52) // bit 3
        if ((codesByte3 and 0x04) != 0) activeCodes.add(53) // bit 2
        if ((codesByte3 and 0x01) != 0) activeCodes.add(55) // bit 0

        return ALDLFrame(
            rawBytes = data,
            iacPosition = iacPosition,
            coolantTempC = coolantTempC,
            coolantTempF = coolantTempF,
            vehicleSpeedMPH = vehicleSpeedMPH,
            mapVolts = mapVolts,
            mapKpa = mapKpa,
            engineSpeedRpm = engineSpeedRpm,
            tpsVolts = tpsVolts,
            integrator = integrator,
            o2SensorMv = o2SensorMv,
            batteryVolts = batteryVolts,
            blm = blm,
            richLeanCrosses = richLeanCrosses,
            sparkAdvance = sparkAdvance,
            egrDutyCycle = egrDutyCycle,
            matC = matC,
            matF = matF,
            bpwMs = bpwMs,
            blmEnable = blmEnable,
            quasiPulse = quasiPulse,
            asyncPulse = asyncPulse,
            isRich = isRich,
            isClosedLoop = isClosedLoop,
            isAcEnabled = isAcEnabled,
            isParkNeutral = isParkNeutral,
            isAcClutchEnabled = isAcClutchEnabled,
            isTccLocked = isTccLocked,
            isPowerSteeringCrampActive = isPowerSteeringCrampActive,
            activeFaultCodes = activeCodes
        )
    }
}
