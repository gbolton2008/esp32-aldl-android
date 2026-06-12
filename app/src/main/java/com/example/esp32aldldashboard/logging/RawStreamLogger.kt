package com.example.esp32aldldashboard.logging

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Logs raw binary ALDL data streams for debugging purposes.
 * Records full 27-byte frames (AA 55 header + 25-byte payload) to Downloads/ALDLLogs/
 */
class RawStreamLogger(private val context: Context) {

    private var currentOutputStream: OutputStream? = null
    private var currentUri: Uri? = null
    private var isRecording = false

    /**
     * Starts a new raw recording session.
     * @return true if recording started successfully, false otherwise
     */
    fun startRecording(): Boolean {
        if (isRecording) return true

        val timeStamp: String = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.US).format(Date())
        val fileName = "raw_$timeStamp.bin"

        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val resolver = context.contentResolver
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "application/octet-stream")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/ALDLLogs")
                }

                val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                if (uri != null) {
                    currentUri = uri
                    currentOutputStream = resolver.openOutputStream(uri)
                    isRecording = true
                    true
                } else {
                    false
                }
            } else {
                // Legacy support not implemented for this debug feature
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Logs a single 27-byte frame (including AA 55 header).
     * @param frame The complete 27-byte frame to log
     */
    fun logFrame(frame: ByteArray) {
        if (!isRecording || frame.size != 27) return

        try {
            currentOutputStream?.write(frame)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Stops the current recording session and closes the file.
     */
    fun stopRecording() {
        if (!isRecording) return

        try {
            currentOutputStream?.flush()
            currentOutputStream?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            currentOutputStream = null
            currentUri = null
            isRecording = false
        }
    }

    /**
     * Returns whether a recording session is currently active.
     */
    fun isRecording(): Boolean = isRecording
}
