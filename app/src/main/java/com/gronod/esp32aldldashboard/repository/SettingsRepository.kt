package com.gronod.esp32aldldashboard.repository

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsRepository(private val context: Context) {

    companion object {
        val IS_CELSIUS = booleanPreferencesKey("is_celsius")
        val COOLANT_ALERT_THRESHOLD = floatPreferencesKey("coolant_alert_threshold")
        val BATTERY_LOW_THRESHOLD = floatPreferencesKey("battery_low_threshold")
        val AUTO_LOGGING = booleanPreferencesKey("auto_logging")
        val RECORD_RAW_DATA = booleanPreferencesKey("record_raw_data")
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

    val recordRawDataFlow: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[RECORD_RAW_DATA] ?: false
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

    suspend fun setRecordRawData(recordRaw: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[RECORD_RAW_DATA] = recordRaw
        }
    }

    /**
     * Queries MediaStore for logged files (CSV and .bin) in Downloads/ALDLLogs
     */
    suspend fun getLoggedFiles(): List<LoggedFile> = withContext(Dispatchers.IO) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            return@withContext emptyList() // Legacy not supported for this feature
        }

        val files = mutableListOf<LoggedFile>()
        val resolver = context.contentResolver

        val projection = arrayOf(
            MediaStore.MediaColumns._ID,
            MediaStore.MediaColumns.DISPLAY_NAME,
            MediaStore.MediaColumns.SIZE,
            MediaStore.MediaColumns.DATE_MODIFIED,
            MediaStore.MediaColumns.MIME_TYPE
        )

        // Query for CSV files
        val csvSelection = "${MediaStore.MediaColumns.RELATIVE_PATH} LIKE ? AND ${MediaStore.MediaColumns.DISPLAY_NAME} LIKE ?"
        val csvSelectionArgs = arrayOf("%${Environment.DIRECTORY_DOWNLOADS}/ALDLLogs%", "%.csv")

        resolver.query(
            MediaStore.Downloads.EXTERNAL_CONTENT_URI,
            projection,
            csvSelection,
            csvSelectionArgs,
            "${MediaStore.MediaColumns.DATE_MODIFIED} DESC"
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID)
            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME)
            val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.SIZE)
            val dateColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_MODIFIED)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val name = cursor.getString(nameColumn)
                val size = cursor.getLong(sizeColumn)
                val dateModified = cursor.getLong(dateColumn)
                val uri = ContentUris.withAppendedId(MediaStore.Downloads.EXTERNAL_CONTENT_URI, id)

                files.add(
                    LoggedFile(
                        uri = uri,
                        name = name,
                        size = size,
                        lastModified = dateModified * 1000, // Convert to milliseconds
                        type = FileType.CSV
                    )
                )
            }
        }

        // Query for binary files (.bin)
        val binSelection = "${MediaStore.MediaColumns.RELATIVE_PATH} LIKE ? AND ${MediaStore.MediaColumns.DISPLAY_NAME} LIKE ?"
        val binSelectionArgs = arrayOf("%${Environment.DIRECTORY_DOWNLOADS}/ALDLLogs%", "%.bin")

        resolver.query(
            MediaStore.Downloads.EXTERNAL_CONTENT_URI,
            projection,
            binSelection,
            binSelectionArgs,
            "${MediaStore.MediaColumns.DATE_MODIFIED} DESC"
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID)
            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME)
            val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.SIZE)
            val dateColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_MODIFIED)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val name = cursor.getString(nameColumn)
                val size = cursor.getLong(sizeColumn)
                val dateModified = cursor.getLong(dateColumn)
                val uri = ContentUris.withAppendedId(MediaStore.Downloads.EXTERNAL_CONTENT_URI, id)

                files.add(
                    LoggedFile(
                        uri = uri,
                        name = name,
                        size = size,
                        lastModified = dateModified * 1000,
                        type = FileType.BINARY
                    )
                )
            }
        }

        // Sort by date descending
        files.sortByDescending { it.lastModified }
        files
    }
}

data class LoggedFile(
    val uri: Uri,
    val name: String,
    val size: Long,
    val lastModified: Long,
    val type: FileType
) {
    fun getFormattedSize(): String {
        return when {
            size < 1024 -> "$size B"
            size < 1024 * 1024 -> "${size / 1024} KB"
            else -> "${size / (1024 * 1024)} MB"
        }
    }

    fun getFormattedDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        return sdf.format(Date(lastModified))
    }
}

enum class FileType {
    CSV, BINARY
}
