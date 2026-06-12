package com.example.esp32aldldashboard.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.esp32aldldashboard.repository.SettingsRepository
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    settingsRepository: SettingsRepository,
    modifier: Modifier = Modifier
) {
    val isCelsius by settingsRepository.isCelsiusFlow.collectAsStateWithLifecycle(initialValue = false)
    val autoLogging by settingsRepository.autoLoggingFlow.collectAsStateWithLifecycle(initialValue = false)
    val recordRawData by settingsRepository.recordRawDataFlow.collectAsStateWithLifecycle(initialValue = false)
    val coolantThreshold by settingsRepository.coolantAlertThresholdFlow.collectAsStateWithLifecycle(initialValue = 100f)

    val coroutineScope = rememberCoroutineScope()

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Settings & Alerts",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(24.dp))

        // Temperature Unit Toggle
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = "Temperature Unit", style = MaterialTheme.typography.titleMedium)
                Text(text = if (isCelsius) "Celsius (°C)" else "Fahrenheit (°F)", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Switch(
                checked = isCelsius,
                onCheckedChange = { 
                    coroutineScope.launch { settingsRepository.setIsCelsius(it) } 
                }
            )
        }
        Divider(modifier = Modifier.padding(vertical = 8.dp))

        // Auto Logging Toggle
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = "Auto-Log Sessions", style = MaterialTheme.typography.titleMedium)
                Text(text = "Automatically save CSV and database records.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Switch(
                checked = autoLogging,
                onCheckedChange = { 
                    coroutineScope.launch { settingsRepository.setAutoLogging(it) } 
                }
            )
        }
        Divider(modifier = Modifier.padding(vertical = 8.dp))

        // Raw Data Recording Toggle (Debug)
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = "Record Raw Datastream (Debug)", style = MaterialTheme.typography.titleMedium)
                Text(text = "Save binary .bin files for debugging.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Switch(
                checked = recordRawData,
                onCheckedChange = { 
                    coroutineScope.launch { settingsRepository.setRecordRawData(it) } 
                }
            )
        }
        Divider(modifier = Modifier.padding(vertical = 8.dp))

        // Coolant Alert Threshold
        Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
            Text(text = "Coolant Alert Threshold", style = MaterialTheme.typography.titleMedium)
            Text(text = "Trigger notification when coolant exceeds ${coolantThreshold.toInt()}°C", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Slider(
                value = coolantThreshold,
                onValueChange = { 
                    coroutineScope.launch { settingsRepository.setCoolantAlertThreshold(it) } 
                },
                valueRange = 80f..150f,
                steps = 70
            )
        }
    }
}
