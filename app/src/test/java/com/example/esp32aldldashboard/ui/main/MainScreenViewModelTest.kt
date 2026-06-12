package com.example.esp32aldldashboard.ui.main

import com.example.esp32aldldashboard.bluetooth.ConnectionState
import com.example.esp32aldldashboard.repository.SettingsRepository
import com.example.esp32aldldashboard.repository.TelemetryRepository
import io.mockk.*
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class MainScreenViewModelTest {

    private val telemetryRepository = mockk<TelemetryRepository>(relaxed = true)
    private val settingsRepository = mockk<SettingsRepository>(relaxed = true)
    private val isCelsiusFlow = MutableStateFlow(false)

    @Before
    fun setUp() {
        // Clear mocks
        clearAllMocks()
        
        // Stub telemetry repository flows
        every { telemetryRepository.connectionState } returns MutableStateFlow(ConnectionState.DISCONNECTED)
        every { telemetryRepository.latestFrame } returns MutableStateFlow(null)
        every { telemetryRepository.rawHexLog } returns MutableStateFlow(emptyList())
        every { telemetryRepository.errorMessage } returns MutableStateFlow("")
        every { telemetryRepository.framesReceived } returns MutableStateFlow(0)
        every { telemetryRepository.parseErrors } returns MutableStateFlow(0)
        every { telemetryRepository.currentFrameRate } returns MutableStateFlow(0)
        
        // Stub settings repository
        isCelsiusFlow.value = false
        every { settingsRepository.isCelsiusFlow } returns isCelsiusFlow
        coEvery { settingsRepository.setIsCelsius(any()) } answers {
            isCelsiusFlow.value = firstArg()
        }
    }

    @Test
    fun testInitialStates() = runTest {
        val viewModel = MainScreenViewModel(telemetryRepository, settingsRepository)
        
        assertEquals(ConnectionState.DISCONNECTED, viewModel.connectionState.value)
        assertEquals(null, viewModel.latestFrame.value)
        assertFalse(viewModel.isCelsius.value)
    }

    @Test
    fun testToggleTemperatureUnit() = runTest {
        val viewModel = MainScreenViewModel(telemetryRepository, settingsRepository)
        
        // Wait for stateIn initialization
        runCurrent()
        assertFalse(viewModel.isCelsius.value)
        
        viewModel.toggleTemperatureUnit()
        runCurrent() // Wait for viewmodel scope coroutine to execute settingsRepository.setIsCelsius
        runCurrent() // Wait for settingsRepository update flow to propagate back through stateIn
        
        assertTrue(viewModel.isCelsius.value)
        
        viewModel.toggleTemperatureUnit()
        runCurrent()
        runCurrent()
        
        assertFalse(viewModel.isCelsius.value)
    }
}
