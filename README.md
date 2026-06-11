# ESP32 ALDL Dashboard Android Application

This Android app connects via Bluetooth SPP to an ESP32 microcontroller that interfaces with a 1986 Pontiac Fiero 1227170 ECM using the 160-baud ALDL (Assembly Line Diagnostic Link) datastream. It decodes the telemetry stream in real-time and displays it on a modern Jetpack Compose dashboard.

## Features

*   **Real-time Dashboard:** View essential engine metrics including Engine Speed (RPM), Vehicle Speed (MPH), Coolant Temperature, Manifold Air Temperature (MAT), Manifold Absolute Pressure (MAP), Throttle Position (TPS), O2 Sensor Voltage, and Battery Voltage.
*   **Custom UI Components:** Beautiful animated Canvas-based radial RPM gauge and TPS bar graph.
*   **Trouble Code Alerts:** Automatically decodes ECM active fault codes into human-readable alerts (e.g., Code 14 - Coolant Temperature Sensor High).
*   **Status Flags:** Real-time visibility into Closed Loop, Rich Mixture, TCC Lockup, A/C Clutch requests, and more.
*   **Derived Metrics:** Calculates estimated Engine Load and Fuel Flow Hint based on core telemetry data.
*   **Real-Time Line Charts:** Built-in rolling graphs of RPM vs. Time, perfect for diagnostic troubleshooting.
*   **Background Telemetry Logging:** Uses an Android Foreground Service to maintain the Bluetooth connection and record data seamlessly even when the app is minimized.
*   **Local Persistence:** Uses Jetpack Room database to save full sessions with timestamped raw payloads.
*   **CSV Data Export:** Automatically generates TunerPro RT compatible `.csv` log files in the `Downloads/ALDLLogs` folder using Android MediaStore APIs.
*   **Settings & Configuration:** Persistent DataStore preferences for Unit Toggle (°C/°F), Auto-Logging toggles, and Coolant Alert Thresholds.

## Architecture Highlights

*   **MVVM & StateFlow:** Strict MVVM separation of concerns. The UI reactivity is entirely driven by `StateFlow` streams.
*   **Circular Ring Buffer Packet Parsing:** The Bluetooth ingest layer uses `ArrayDeque<Byte>` circular buffering. It constantly seeks the `AA 55` header, preventing misalignment desyncs over noisy lines.
*   **Robust Parsers:** The `ALDLParser` encapsulates all scaling constants and interpolation arrays (for MAT) required by the TunerPro `24-INT10.ads` definition.

## Requirements

*   Android Device running Android 8.0+ (Tested extensively against Android 13+)
*   Bluetooth Permissions (Nearby Devices on Android 12+)
*   ESP32 hardware module programmed with the accompanying ALDL datastream code.

## Setup Instructions

1.  Pair the ESP32 (named `ESP32-ALDL`) in your Android Bluetooth settings.
2.  Grant the requested Bluetooth permissions inside the app.
3.  Tap "Connect BT" to begin the telemetry stream.
4.  Navigate to the **Settings** tab to toggle Auto-Logging or customize unit formats.
5.  View historical `.csv` logs in your device's `Downloads/ALDLLogs` folder.

## Build Information
Developed using Android Studio and Gradle. Built with Jetpack Compose, Room (with KSP Annotation Processor), and DataStore.