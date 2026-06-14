@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
package com.gronod.esp32aldldashboard.ui.main

import com.gronod.esp32aldldashboard.bluetooth.ConnectionState
import com.gronod.esp32aldldashboard.repository.SettingsRepository
import com.gronod.esp32aldldashboard.repository.TelemetryRepository
import io.mockk.*
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

class MainScreenViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val telemetryRepository = mockk<TelemetryRepository>(relaxed = true)
    private val settingsRepository = mockk<SettingsRepository>(relaxed = true)
    private val isCelsiusFlow = MutableStateFlow(false)

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
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

    @After
    fun tearDown() {
        Dispatchers.resetMain()
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
        
        // Start collecting to activate WhileSubscribed stateIn flow
        backgroundScope.launch(testDispatcher) {
            viewModel.isCelsius.collect {}
        }
        
        assertFalse(viewModel.isCelsius.value)
        
        viewModel.toggleTemperatureUnit()
        
        assertTrue(viewModel.isCelsius.value)
        
        viewModel.toggleTemperatureUnit()
        
        assertFalse(viewModel.isCelsius.value)
    }
}
