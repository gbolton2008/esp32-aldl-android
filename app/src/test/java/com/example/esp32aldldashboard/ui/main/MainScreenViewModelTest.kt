package com.example.esp32aldldashboard.ui.main

import android.content.Context
import android.content.ContextWrapper
import com.example.esp32aldldashboard.bluetooth.ConnectionState
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Test

class MainScreenViewModelTest {

    private class FakeContext : ContextWrapper(null) {
        override fun getApplicationContext(): Context {
            return this
        }
        override fun getSystemService(name: String): Any? {
            return null
        }
    }

    @Test
    fun testInitialStates() = runTest {
        val context = FakeContext()
        val viewModel = MainScreenViewModel(context)
        
        assertEquals(ConnectionState.DISCONNECTED, viewModel.connectionState.value)
        assertEquals(null, viewModel.latestFrame.value)
        assertFalse(viewModel.isCelsius.value)
    }

    @Test
    fun testToggleTemperatureUnit() = runTest {
        val context = FakeContext()
        val viewModel = MainScreenViewModel(context)
        
        assertFalse(viewModel.isCelsius.value)
        viewModel.toggleTemperatureUnit()
        assertTrue(viewModel.isCelsius.value)
        viewModel.toggleTemperatureUnit()
        assertFalse(viewModel.isCelsius.value)
    }
}
