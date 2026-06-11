package com.example.esp32aldldashboard.parser

import org.junit.Assert.*
import org.junit.Test

class RingBufferTest {

    @Test
    fun testALDLParseResultSafety() {
        val validPayload = ByteArray(25) { 0 }
        // Coolant = 0 (0 * 0.75 - 40 = -40 C) -> Valid
        // Battery = 120 (120 * 0.1 = 12 V) -> Valid
        validPayload[17] = 120.toByte()
        
        val result = ALDLParser.parseFrame(validPayload)
        assertTrue("Expected valid parsing", result is ALDLParseResult.Success)

        val invalidBatteryPayload = validPayload.clone()
        invalidBatteryPayload[17] = 250.toByte() // 250 * 0.1 = 25 V -> Invalid (> 20V)
        val resultInvalid = ALDLParser.parseFrame(invalidBatteryPayload)
        assertTrue("Expected invalid parsing", resultInvalid is ALDLParseResult.InvalidData)

        val incompletePayload = ByteArray(10)
        val resultIncomplete = ALDLParser.parseFrame(incompletePayload)
        assertTrue("Expected incomplete parsing", resultIncomplete is ALDLParseResult.Incomplete)
    }
}
