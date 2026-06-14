# Privacy Policy for ESP32 ALDL Dashboard

**Android Application**  
**Version**: 0.1.0  
**Last Updated**: June 14, 2026

---

## Overview

The **ESP32 ALDL Dashboard** is a free, open-source Android application designed for vehicle enthusiasts and tuners. It interfaces with GM OBD1 / ALDL-equipped vehicles (such as the 1986 Pontiac Fiero 1227170 ECM) using a custom ESP32 Bluetooth SPP bridge.

**Core Privacy Principle**:  
This app collects **only vehicle engine telemetry data** from your car. All data is processed and stored **locally on your device**. The app makes **no network connections**, sends **no data** to any servers, and contains **no analytics, tracking, or crash reporting**.

---

## Data Collected

### What We Collect
The app receives and decodes the following vehicle diagnostic data via Bluetooth from your paired ESP32-ALDL device:

- Engine speed (RPM)
- Vehicle speed (MPH)
- Coolant temperature (°C / °F)
- Manifold Air Temperature (MAT)
- Manifold Absolute Pressure (MAP)
- Throttle Position Sensor (TPS) voltage
- O2 sensor voltage
- Battery voltage
- Spark advance
- Base Pulse Width (BPW)
- Idle Air Control (IAC) position
- BLM and Integrator fuel trim values
- Status flags (Closed Loop, Rich/Lean, TCC Lockup, A/C Clutch, etc.)
- Active diagnostic trouble codes (DTCs)

**Source**: This data comes exclusively from **your vehicle's Engine Control Module (ECM)** through the ALDL port and the ESP32 Bluetooth bridge you provide and pair.

### What We Do **NOT** Collect
- No personal information (name, email, phone, etc.)
- No device identifiers or advertising IDs
- No location data (location permissions are used only for legacy Bluetooth scanning and are scoped appropriately)
- No user accounts or authentication data
- No photos, contacts, files, or other personal content
- No analytics or usage statistics
- No crash reports sent to third parties

---

## How Data Is Collected

- **Exclusively via Bluetooth Classic SPP** connection to your user-paired ESP32-ALDL device.
- The app never initiates internet connections or uses any network APIs.
- An optional **simulation mode** generates synthetic local data for testing without requiring hardware.

---

## Data Storage and Usage

All data remains **under your full control** on your device:

| Storage Type              | Purpose                              | Location                          | User Control                  |
|---------------------------|--------------------------------------|-----------------------------------|-------------------------------|
| Room Database (SQLite)    | Session history and telemetry points | Private app storage               | Deleted on uninstall          |
| DataStore Preferences     | Settings (units, thresholds, toggles)| Private app storage               | Managed in Settings screen    |
| CSV Log Files             | TunerPro RT compatible logs          | `Downloads/ALDLLogs/` (MediaStore)| User can delete/share         |
| Raw Binary (.bin) Files   | Raw ALDL stream captures             | `Downloads/ALDLLogs/` (MediaStore)| Optional, user-controlled     |

- Logging is **optional** and toggleable (auto-log on connect, raw binary recording).
- You can browse, open, or share your own log files directly inside the app using Android's share sheet.
- No data is ever uploaded, synced to the cloud, or transmitted anywhere.

---

## Data Sharing and Disclosure

**The app does not share any data with anyone.**

- We (the developer) never receive any data from the app.
- No third parties (Google, advertisers, analytics providers, etc.) receive any data.
- You may choose to manually export or share your local log files — this is entirely under your control.

---

## Permissions Explained

The app requests only the minimum permissions required for its functionality:

| Permission                              | Purpose                                      | Notes |
|-----------------------------------------|----------------------------------------------|-------|
| `BLUETOOTH`, `BLUETOOTH_ADMIN`          | Connect to ESP32 device (Android ≤ 11)       | Legacy, maxSdkVersion=30 |
| `BLUETOOTH_SCAN`, `BLUETOOTH_CONNECT`   | Bluetooth scanning and connection (Android 12+) | `neverForLocation` flag used |
| `ACCESS_FINE_LOCATION`, `ACCESS_COARSE_LOCATION` | Required by Android for BT scanning on older versions | Not used for actual location tracking; scoped to maxSdkVersion=30 |
| `FOREGROUND_SERVICE` + `FOREGROUND_SERVICE_CONNECTED_DEVICE` | Keep Bluetooth connection alive in background | Shows persistent notification you can tap to disconnect |
| `POST_NOTIFICATIONS`                    | Show connection status notification          | Required for foreground service on Android 13+ |

No other permissions are requested or used.

---

## Firebase / Crash Reporting Note

Earlier documentation mentioned potential Firebase Crashlytics integration. **This is not present in the current version of the app.** There are no Firebase dependencies, no internet permission, and no crash reporting or analytics of any kind.

---

## Your Rights and Control

- Full transparency — the complete source code is available at:  
  **https://git.i3omb.com/gronod/esp32-aldl-android**
- You control all logging via the Settings screen.
- You can delete individual log files or clear all data by uninstalling the app.
- No accounts means no data to request deletion of from a server.

---

## Changes to This Policy

We will update this policy if the app's data handling changes. The latest version will always be available in the app (Settings → About) and in the project repository.

Because the app has no servers or user accounts, we cannot notify you directly of changes.

---

## Contact

For questions about this privacy policy or the application, please open an issue in the repository:  
https://git.i3omb.com/gronod/esp32-aldl-android

---

## Summary

**ESP32 ALDL Dashboard** is a privacy-first, local-only tool.  
It helps you monitor and log your vehicle's engine data via Bluetooth — and nothing leaves your device unless *you* choose to export it.

**No tracking. No cloud. No surprises.**

---

*This privacy policy is provided in good faith based on a thorough review of the application's source code.*  
*License: MIT*