package com.example.esp32aldldashboard

import com.example.esp32aldldashboard.parser.ALDLParser
import org.junit.Assert.*
import org.junit.Test

class ALDLParserTest {

    @Test
    fun testParseSampleFrame() {
        // Sample hex stream from user request:
        // AA 55 20 00 2A 5F 59 00 F4 00 1E 80 65 08 00 00 00 25 18 7D 80 00 00 00 C9 02 62
        // AA 55 is the header, followed by 25 bytes of payload.
        val rawPayload = byteArrayOf(
            0x20.toByte(), 0x00.toByte(), 0x2A.toByte(), 0x5F.toByte(), 0x59.toByte(),
            0x00.toByte(), 0xF4.toByte(), 0x00.toByte(), 0x1E.toByte(), 0x80.toByte(),
            0x65.toByte(), 0x08.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(),
            0x25.toByte(), 0x18.toByte(), 0x7D.toByte(), 0x80.toByte(), 0x00.toByte(),
            0x00.toByte(), 0x00.toByte(), 0xC9.toByte(), 0x02.toByte(), 0x62.toByte()
        )

        val result = ALDLParser.parseFrame(rawPayload)
        assertTrue(result is com.example.esp32aldldashboard.parser.ALDLParseResult.Success)
        val frame = (result as com.example.esp32aldldashboard.parser.ALDLParseResult.Success).frame

        // Assert values based on 24-INT10.ads specifications:
        assertEquals(95, frame.iacPosition) // u[3] (Byte 4)
        
        // coolantTemp = 89 * 0.75 - 40 = 26.75
        assertEquals(26.75f, frame.coolantTempC, 0.001f)
        assertEquals(80.15f, frame.coolantTempF, 0.001f)
        
        assertEquals(0, frame.vehicleSpeedMPH) // u[5] (Byte 6)
        
        // MAP: u[6] = 244
        assertEquals(244 * 0.019608f, frame.mapVolts, 0.001f)
        assertEquals(244 * 0.369f + 10.354f, frame.mapKpa, 0.001f)
        
        assertEquals(0, frame.engineSpeedRpm) // u[7] (Byte 8)
        
        // TPS: u[8] = 30
        assertEquals(30 * 0.019608f, frame.tpsVolts, 0.001f)
        
        assertEquals(128, frame.integrator) // u[9] (Byte 10)
        assertEquals(101 * 4.44f, frame.o2SensorMv, 0.001f) // u[10] (Byte 11)
        
        assertEquals(12.5f, frame.batteryVolts, 0.001f) // u[17] (Byte 18)
        assertEquals(128, frame.blm) // u[18] (Byte 19)
        assertEquals(0, frame.richLeanCrosses) // u[19] (Byte 20)
        assertEquals(0f, frame.sparkAdvance, 0.001f) // u[20] (Byte 21)
        assertEquals(0f, frame.egrDutyCycle, 0.001f) // u[21] (Byte 22)

        // MAT Temp: u[22] = 201 (decimal)
        // Table 52 interpolation:
        // Key 199 -> 20.0 C
        // Key 209 -> 15.0 C
        // value = 20.0 - (201 - 199) / 10.0 * 5.0 = 19.0 C
        assertEquals(19.0f, frame.matC, 0.001f)
        assertEquals(66.2f, frame.matF, 0.001f)

        // BPW: u[23]=0x02, u[24]=0x62 => 0x0262 = 610 decimal
        // bpwMs = 610 * 0.015259 = 9.30799
        assertEquals(610 * 0.015259f, frame.bpwMs, 0.001f)

        // Active fault codes check (0x08 on codesByte1 = Code 21 active)
        assertTrue(frame.activeFaultCodes.contains(21))
        assertEquals(1, frame.activeFaultCodes.size)

        // Misc flags
        assertFalse(frame.isClosedLoop) // bit 7 of u[14] is 0
        assertFalse(frame.isRich)       // bit 6 of u[14] is 0
    }
}
