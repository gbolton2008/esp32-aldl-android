package com.example.esp32aldldashboard.bluetooth

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.esp32aldldashboard.AldlApplication
import com.example.esp32aldldashboard.MainActivity
import com.example.esp32aldldashboard.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class BluetoothForegroundService : Service() {

    private val CHANNEL_ID = "ALDL_BT_CHANNEL"
    private val NOTIFICATION_ID = 101

    private val serviceScope = CoroutineScope(Dispatchers.Main + Job())
    private lateinit var bluetoothService: BluetoothService

    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
        const val ACTION_DISCONNECT = "ACTION_DISCONNECT"
    }

    override fun onCreate() {
        super.onCreate()
        val app = application as AldlApplication
        bluetoothService = app.bluetoothService
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                startForeground(NOTIFICATION_ID, createNotification("Connected to ESP32-ALDL"))
                observeTelemetry()
            }
            ACTION_DISCONNECT -> {
                bluetoothService.disconnect()
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }
            ACTION_STOP -> {
                bluetoothService.disconnect()
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }
        }
        return START_NOT_STICKY
    }

    private fun observeTelemetry() {
        serviceScope.launch {
            bluetoothService.latestFrame.collectLatest { frame ->
                if (frame != null) {
                    updateNotification("RPM: ${frame.engineSpeedRpm} | Coolant: ${String.format("%.1f", frame.coolantTempC)}°C")
                }
            }
        }
        
        serviceScope.launch {
            bluetoothService.connectionState.collectLatest { state ->
                when (state) {
                    ConnectionState.DISCONNECTED, ConnectionState.ERROR -> {
                        stopForeground(STOP_FOREGROUND_REMOVE)
                        stopSelf()
                    }
                    else -> {}
                }
            }
        }
    }

    private fun createNotification(contentText: String): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val disconnectIntent = Intent(this, BluetoothForegroundService::class.java).apply {
            action = ACTION_DISCONNECT
        }
        val disconnectPendingIntent = PendingIntent.getService(
            this, 1, disconnectIntent, PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("ALDL Telemetry Active")
            .setContentText(contentText)
            .setSmallIcon(R.mipmap.ic_launcher) // Use app icon for now
            .setContentIntent(pendingIntent)
            .addAction(0, "Disconnect", disconnectPendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOnlyAlertOnce(true)
            .build()
    }

    private fun updateNotification(contentText: String) {
        val manager = getSystemService(NotificationManager::class.java)
        manager.notify(NOTIFICATION_ID, createNotification(contentText))
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Bluetooth Connection Service",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.launch { 
            // cancel jobs if needed 
        }
    }
}
