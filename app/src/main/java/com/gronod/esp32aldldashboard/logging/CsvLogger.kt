package com.gronod.esp32aldldashboard.logging

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.gronod.esp32aldldashboard.parser.ALDLFrame
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CsvLogger(private val context: Context) {

    private var currentOutputStream: OutputStream? = null

    fun startNewSession(isSimulation: Boolean): Boolean {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val fileName = "ALDL_Log_${if(isSimulation) "SIM_" else ""}$timeStamp.csv"

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val resolver = context.contentResolver
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "text/csv")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/ALDLLogs")
                }

                val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                if (uri != null) {
                    currentOutputStream = resolver.openOutputStream(uri)
                }
            } else {
                // For older Android versions, we'd need WRITE_EXTERNAL_STORAGE and write to Environment.getExternalStoragePublicDirectory.
                // We'll skip legacy support for this specific snippet to keep it concise, assuming target is modern Android.
                return false 
            }

            // Write CSV Header
            val header = "Timestamp,Raw_Hex,RPM,Coolant_C,Coolant_F,MAP_kPa,MAP_Volts,TPS_Volts,O2_mV,Battery_V,Spark_Adv,IAC,BPW_ms,Speed_MPH,MAT_C,MAT_F,BLM,INT,EGR_Duty,Rich_Crosses,Closed_Loop,Rich\n"
            currentOutputStream?.write(header.toByteArray())
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    fun logFrame(frame: ALDLFrame) {
        val out = currentOutputStream ?: return
        
        val hexString = frame.rawBytes.joinToString(" ") { String.format("%02X", it) }
        val dateString = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US).format(Date(frame.timestamp))

        val row = String.format(
            Locale.US,
            "%s,%s,%d,%.1f,%.1f,%.1f,%.3f,%.3f,%.1f,%.1f,%.1f,%d,%.2f,%d,%.1f,%.1f,%d,%d,%.1f,%d,%b,%b\n",
            dateString,
            hexString,
            frame.engineSpeedRpm,
            frame.coolantTempC,
            frame.coolantTempF,
            frame.mapKpa,
            frame.mapVolts,
            frame.tpsVolts,
            frame.o2SensorMv,
            frame.batteryVolts,
            frame.sparkAdvance,
            frame.iacPosition,
            frame.bpwMs,
            frame.vehicleSpeedMPH,
            frame.matC,
            frame.matF,
            frame.blm,
            frame.integrator,
            frame.egrDutyCycle,
            frame.richLeanCrosses,
            frame.isClosedLoop,
            frame.isRich
        )

        try {
            out.write(row.toByteArray())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun endSession() {
        try {
            currentOutputStream?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            currentOutputStream = null
        }
    }
}
