package com.gronod.esp32aldldashboard.parser

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

sealed class ALDLParseResult {
    data class Success(val frame: ALDLFrame) : ALDLParseResult()
    data class InvalidData(val reason: String) : ALDLParseResult()
    object Incomplete : ALDLParseResult()
}

object ALDLConstants {
    const val PAYLOAD_SIZE = 25
    
    // Scale Factors
    const val COOLANT_SCALE_C = 0.75f
    const val COOLANT_OFFSET_C = -40.0f
    const val COOLANT_SCALE_F = 1.35f
    const val COOLANT_OFFSET_F = -40.0f
    
    const val MAP_VOLTS_SCALE = 0.019608f
    const val MAP_KPA_SCALE = 0.369f
    const val MAP_KPA_OFFSET = 10.354f
    
    const val RPM_SCALE = 25
    const val TPS_VOLTS_SCALE = 0.019608f
    const val O2_MV_SCALE = 4.44f
    
    const val BATTERY_VOLTS_SCALE = 0.1f
    const val SPARK_ADVANCE_SCALE = 0.351563f
    const val EGR_DUTY_CYCLE_SCALE = 0.392157f
    const val BPW_MS_SCALE = 0.015259f

    // Plausibility Limits
    const val MAX_RPM = 8000
    const val MIN_TEMP_C = -45.0f
    const val MAX_TEMP_C = 220.0f
    const val MIN_BATTERY_V = 5.0f
    const val MAX_BATTERY_V = 20.0f
    const val MAX_TPS_V = 5.1f

    // Bitmasks
    const val BIT_0 = 0x01
    const val BIT_1 = 0x02
    const val BIT_2 = 0x04
    const val BIT_3 = 0x08
    const val BIT_4 = 0x10
    const val BIT_5 = 0x20
    const val BIT_6 = 0x40
    const val BIT_7 = 0x80

    // MAT C interpolation table
    val MAT_TABLE_C = listOf(
        0 to 200.0f, 12 to 150.0f, 13 to 145.0f, 14 to 140.0f, 16 to 135.0f,
        18 to 130.0f, 21 to 125.0f, 23 to 120.0f, 26 to 115.0f, 30 to 110.0f,
        34 to 105.0f, 39 to 100.0f, 44 to 95.0f, 50 to 90.0f, 56 to 85.0f,
        64 to 80.0f, 72 to 75.0f, 81 to 70.0f, 92 to 65.0f, 102 to 60.0f,
        114 to 55.0f, 126 to 50.0f, 139 to 45.0f, 152 to 40.0f, 165 to 35.0f,
        177 to 30.0f, 189 to 25.0f, 199 to 20.0f, 209 to 15.0f, 218 to 10.0f,
        225 to 5.0f, 231 to 0.0f, 237 to -5.0f, 241 to -10.0f, 245 to -15.0f,
        247 to -20.0f, 250 to -25.0f, 251 to -30.0f, 255 to -40.0f
    )

    // MAT F interpolation table
    val MAT_TABLE_F = listOf(
        0 to 392.0f, 12 to 302.0f, 13 to 293.0f, 14 to 284.0f, 16 to 275.0f,
        18 to 266.0f, 21 to 257.0f, 23 to 248.0f, 26 to 239.0f, 30 to 230.0f,
        34 to 221.0f, 39 to 212.0f, 44 to 203.0f, 50 to 194.0f, 56 to 185.0f,
        64 to 176.0f, 72 to 167.0f, 81 to 158.0f, 92 to 149.0f, 102 to 140.0f,
        114 to 131.0f, 126 to 122.0f, 139 to 113.0f, 152 to 104.0f, 165 to 95.0f,
        177 to 86.0f, 189 to 77.0f, 199 to 68.0f, 209 to 59.0f, 218 to 50.0f,
        225 to 41.0f, 231 to 32.0f, 237 to 23.0f, 241 to 14.0f, 245 to 5.0f,
        247 to -4.0f, 250 to -13.0f, 251 to -22.0f, 255 to -40.0f
    )
}

object ALDLParser {
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
     * Parses a 25-byte raw data payload, validating bounds and checking for errors.
     */
    fun parseFrame(data: ByteArray): ALDLParseResult {
        if (data.size != ALDLConstants.PAYLOAD_SIZE) {
            return ALDLParseResult.Incomplete
        }

        val u = IntArray(ALDLConstants.PAYLOAD_SIZE) { data[it].toInt() and 0xFF }

        val engineSpeedRpm = u[7] * ALDLConstants.RPM_SCALE
        if (engineSpeedRpm > ALDLConstants.MAX_RPM) {
            return ALDLParseResult.InvalidData("RPM ($engineSpeedRpm) exceeds plausibility limit (${ALDLConstants.MAX_RPM})")
        }

        val coolantTempC = u[4] * ALDLConstants.COOLANT_SCALE_C + ALDLConstants.COOLANT_OFFSET_C
        val coolantTempF = u[4] * ALDLConstants.COOLANT_SCALE_F + ALDLConstants.COOLANT_OFFSET_F
        if (coolantTempC < ALDLConstants.MIN_TEMP_C || coolantTempC > ALDLConstants.MAX_TEMP_C) {
            return ALDLParseResult.InvalidData("Coolant Temp ($coolantTempC C) outside bounds")
        }

        val batteryVolts = u[17] * ALDLConstants.BATTERY_VOLTS_SCALE
        if (batteryVolts < ALDLConstants.MIN_BATTERY_V || batteryVolts > ALDLConstants.MAX_BATTERY_V) {
            return ALDLParseResult.InvalidData("Battery Voltage ($batteryVolts V) outside bounds")
        }

        val tpsVolts = u[8] * ALDLConstants.TPS_VOLTS_SCALE
        if (tpsVolts > ALDLConstants.MAX_TPS_V) {
            return ALDLParseResult.InvalidData("TPS Voltage ($tpsVolts V) outside bounds")
        }

        val iacPosition = u[3]
        val vehicleSpeedMPH = u[5]
        val mapVolts = u[6] * ALDLConstants.MAP_VOLTS_SCALE
        val mapKpa = u[6] * ALDLConstants.MAP_KPA_SCALE + ALDLConstants.MAP_KPA_OFFSET
        val integrator = u[9]
        val o2SensorMv = u[10] * ALDLConstants.O2_MV_SCALE

        val codesByte1 = u[11]
        val codesByte2 = u[12]
        val codesByte3 = u[13]
        val miscByte1 = u[14]
        val miscByte2 = u[15]
        val miscByte3 = u[16]

        val blm = u[18]
        val richLeanCrosses = u[19]
        val sparkAdvance = u[20] * ALDLConstants.SPARK_ADVANCE_SCALE
        val egrDutyCycle = u[21] * ALDLConstants.EGR_DUTY_CYCLE_SCALE
        
        val matC = interpolate(u[22], ALDLConstants.MAT_TABLE_C)
        val matF = interpolate(u[22], ALDLConstants.MAT_TABLE_F)

        val rawBpw = (u[23] shl 8) or u[24]
        val bpwMs = rawBpw * ALDLConstants.BPW_MS_SCALE

        val blmEnable = (miscByte1 and ALDLConstants.BIT_1) != 0
        val quasiPulse = (miscByte1 and ALDLConstants.BIT_3) != 0
        val asyncPulse = (miscByte1 and ALDLConstants.BIT_4) != 0
        val isRich = (miscByte1 and ALDLConstants.BIT_6) != 0
        val isClosedLoop = (miscByte1 and ALDLConstants.BIT_7) != 0

        val isAcEnabled = (miscByte2 and ALDLConstants.BIT_5) == 0
        val isParkNeutral = (miscByte2 and ALDLConstants.BIT_7) != 0

        val isAcClutchEnabled = (miscByte3 and ALDLConstants.BIT_0) != 0
        val isTccLocked = (miscByte3 and ALDLConstants.BIT_2) != 0
        val isPowerSteeringCrampActive = (miscByte3 and ALDLConstants.BIT_5) != 0

        val activeCodes = mutableListOf<Int>()
        if ((codesByte1 and ALDLConstants.BIT_7) != 0) activeCodes.add(12)
        if ((codesByte1 and ALDLConstants.BIT_6) != 0) activeCodes.add(13)
        if ((codesByte1 and ALDLConstants.BIT_5) != 0) activeCodes.add(14)
        if ((codesByte1 and ALDLConstants.BIT_4) != 0) activeCodes.add(15)
        if ((codesByte1 and ALDLConstants.BIT_3) != 0) activeCodes.add(21)
        if ((codesByte1 and ALDLConstants.BIT_2) != 0) activeCodes.add(22)
        if ((codesByte1 and ALDLConstants.BIT_1) != 0) activeCodes.add(23)
        if ((codesByte1 and ALDLConstants.BIT_0) != 0) activeCodes.add(24)

        if ((codesByte2 and ALDLConstants.BIT_7) != 0) activeCodes.add(25)
        if ((codesByte2 and ALDLConstants.BIT_5) != 0) activeCodes.add(32)
        if ((codesByte2 and ALDLConstants.BIT_4) != 0) activeCodes.add(33)
        if ((codesByte2 and ALDLConstants.BIT_3) != 0) activeCodes.add(34)
        if ((codesByte2 and ALDLConstants.BIT_2) != 0) activeCodes.add(35)
        if ((codesByte2 and ALDLConstants.BIT_0) != 0) activeCodes.add(42)

        if ((codesByte3 and ALDLConstants.BIT_7) != 0) activeCodes.add(43)
        if ((codesByte3 and ALDLConstants.BIT_6) != 0) activeCodes.add(44)
        if ((codesByte3 and ALDLConstants.BIT_5) != 0) activeCodes.add(45)
        if ((codesByte3 and ALDLConstants.BIT_4) != 0) activeCodes.add(51)
        if ((codesByte3 and ALDLConstants.BIT_3) != 0) activeCodes.add(52)
        if ((codesByte3 and ALDLConstants.BIT_2) != 0) activeCodes.add(53)
        if ((codesByte3 and ALDLConstants.BIT_0) != 0) activeCodes.add(55)

        val frame = ALDLFrame(
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
        return ALDLParseResult.Success(frame)
    }
}
