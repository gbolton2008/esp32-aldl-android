package com.example.esp32aldldashboard.ui.main

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.esp32aldldashboard.AldlApplication
import com.example.esp32aldldashboard.ui.charts.ChartsScreen
import com.example.esp32aldldashboard.ui.settings.SettingsScreen

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val app = context.applicationContext as AldlApplication
    
    val viewModel: MainScreenViewModel = viewModel(
        factory = MainScreenViewModelFactory(
            telemetryRepository = app.telemetryRepository,
            settingsRepository = app.settingsRepository
        )
    )
    
    val connState by viewModel.connectionState.collectAsStateWithLifecycle()
    val frame by viewModel.latestFrame.collectAsStateWithLifecycle()
    val rawLog by viewModel.rawHexLog.collectAsStateWithLifecycle()
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
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.POST_NOTIFICATIONS
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

    var selectedTab by remember { mutableStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = DarkBg, contentColor = NeonOrange) {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Info, contentDescription = "Dashboard") },
                    label = { Text("Dashboard") },
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = NeonCyan, unselectedIconColor = TextMuted)
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Build, contentDescription = "Charts") },
                    label = { Text("Charts") },
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = NeonCyan, unselectedIconColor = TextMuted)
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                    label = { Text("Settings") },
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = NeonCyan, unselectedIconColor = TextMuted)
                )
            }
        }
    ) { paddingValues ->
        when (selectedTab) {
            0 -> DashboardScreen(
                connState = connState,
                frame = frame,
                isCelsius = isCelsius,
                onConnect = onConnectClick,
                onDisconnect = { viewModel.disconnect() },
                onSimulate = { viewModel.startSimulation() },
                modifier = modifier.padding(paddingValues)
            )
            1 -> ChartsScreen(
                latestFrameFlow = viewModel.latestFrame,
                modifier = modifier.padding(paddingValues)
            )
            2 -> SettingsScreen(
                settingsRepository = app.settingsRepository,
                modifier = modifier.padding(paddingValues)
            )
        }
    }
}
