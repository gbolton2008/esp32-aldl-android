package com.example.esp32aldldashboard.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsRepository(private val context: Context) {

    companion object {
        val IS_CELSIUS = booleanPreferencesKey("is_celsius")
        val COOLANT_ALERT_THRESHOLD = floatPreferencesKey("coolant_alert_threshold")
        val BATTERY_LOW_THRESHOLD = floatPreferencesKey("battery_low_threshold")
        val AUTO_LOGGING = booleanPreferencesKey("auto_logging")
    }

    val isCelsiusFlow: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[IS_CELSIUS] ?: false // Default to Fahrenheit
        }

    val coolantAlertThresholdFlow: Flow<Float> = context.dataStore.data
        .map { preferences ->
            preferences[COOLANT_ALERT_THRESHOLD] ?: 100f // Default 100C / 212F
        }

    val batteryLowThresholdFlow: Flow<Float> = context.dataStore.data
        .map { preferences ->
            preferences[BATTERY_LOW_THRESHOLD] ?: 11.5f // Default 11.5V
        }
        
    val autoLoggingFlow: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[AUTO_LOGGING] ?: false
        }

    suspend fun setIsCelsius(isCelsius: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[IS_CELSIUS] = isCelsius
        }
    }

    suspend fun setCoolantAlertThreshold(threshold: Float) {
        context.dataStore.edit { preferences ->
            preferences[COOLANT_ALERT_THRESHOLD] = threshold
        }
    }

    suspend fun setBatteryLowThreshold(threshold: Float) {
        context.dataStore.edit { preferences ->
            preferences[BATTERY_LOW_THRESHOLD] = threshold
        }
    }

    suspend fun setAutoLogging(autoLog: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[AUTO_LOGGING] = autoLog
        }
    }
}
