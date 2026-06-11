# ESP32 ALDL Dashboard

An Android application built using Jetpack Compose to display real-time engine telemetry from a **1986 Pontiac Fiero 2.8L V6 (1227170 ECM)**. The app connects to a Bluetooth serial device named **ESP32-ALDL** and decodes a raw binary stream of 27-byte frames containing ECM data packets under the `$24` mask specifications.

---

## Technical Specifications

### Data Stream Frame Structure
The ESP32 reads the raw 160-baud unidirectional ALDL stream from the ECM and encapsulates it in a 27-byte packet transmitted over Bluetooth SPP.

| Byte Index | Length | Value / Field | Description |
| :--- | :--- | :--- | :--- |
| **0 - 1** | 2 bytes | `AA 55` | Frame Sync Header (added by ESP32) |
| **2 - 26** | 25 bytes | Raw Data Payload | 1227170 ECM data stream (0-indexed indices 0 to 24) |

---

## Parameter Offsets & Decoding Formulas

The telemetry parameters are parsed from the `$24` / `$24A` ECM mask definitions (`24-INT10.ads`). In TunerPro's `.ads` file, the byte numbers are 1-indexed (e.g. `btByteNumber = 4` maps to raw payload byte index `3`).

### 1. Primary Sensors & Measurements

| Parameter | Payload Index | Raw Size | Formula / Conversion | Units |
| :--- | :--- | :--- | :--- | :--- |
| **IAC Position** | Index 3 (Byte 4) | 8-bit | $Value = Raw$ | Steps |
| **Coolant Temp** | Index 4 (Byte 5) | 8-bit | $C = (Raw \times 0.75) - 40$<br>$F = (Raw \times 1.35) - 40$ | °C / °F |
| **Vehicle Speed** | Index 5 (Byte 6) | 8-bit | $Value = Raw$ (Operation 3) | MPH |
| **MAP** | Index 6 (Byte 7) | 8-bit | $Volts = Raw \times 0.019608$<br>$kPa = (Raw \times 0.369) + 10.354$ | Volts / kPa |
| **Engine Speed** | Index 7 (Byte 8) | 8-bit | $RPM = Raw \times 25$ | RPM |
| **TPS** | Index 8 (Byte 9) | 8-bit | $Volts = Raw \times 0.019608$ | Volts |
| **Integrator (INT)** | Index 9 (Byte 10) | 8-bit | $Value = Raw$ | — |
| **O2 Sensor** | Index 10 (Byte 11) | 8-bit | $mV = Raw \times 4.44$ | mV |
| **Battery Voltage** | Index 17 (Byte 18) | 8-bit | $Volts = Raw \times 0.1$ | Volts |
| **BLM** | Index 18 (Byte 19) | 8-bit | $Value = Raw$ | — |
| **Rich/Lean Crosses** | Index 19 (Byte 20) | 8-bit | $Value = Raw$ | Crosses |
| **Spark Advance** | Index 20 (Byte 21) | 8-bit | $Degrees = Raw \times 0.351563$ | Degrees |
| **EGR Duty Cycle** | Index 21 (Byte 22) | 8-bit | $Percent = Raw \times 0.392157$ | % |
| **Manifold Air Temp (MAT)** | Index 22 (Byte 23) | 8-bit | **Linear Table Interpolation** (see below) | °C / °F |
| **Base Pulse Width (BPW)** | Index 23-24 (Byte 24-25) | 16-bit | $Raw = (HighByte \ll 8) \vert LowByte$<br>$ms = Raw \times 0.015259$ | Milliseconds (ms) |

---

### 2. MAT Linear Interpolation Tables
The Manifold Air Temperature (MAT) is read from Index 22. It is mapped to degrees C or F using interpolation curves specified in tables 52 and 53:

*   **Celsius (`MAT C` - Table 52):**
    *   `0` -> `200.0`, `12` -> `150.0`, `13` -> `145.0`, `14` -> `140.0`, `16` -> `135.0`, `18` -> `130.0`, `21` -> `125.0`, `23` -> `120.0`, `26` -> `115.0`, `30` -> `110.0`, `34` -> `105.0`, `39` -> `100.0`, `44` -> `95.0`, `50` -> `90.0`, `56` -> `85.0`, `64` -> `80.0`, `72` -> `75.0`, `81` -> `70.0`, `92` -> `65.0`, `102` -> `60.0`, `114` -> `55.0`, `126` -> `50.0`, `139` -> `45.0`, `152` -> `40.0`, `165` -> `35.0`, `177` -> `30.0`, `189` -> `25.0`, `199` -> `20.0`, `209` -> `15.0`, `218` -> `10.0`, `225` -> `5.0`, `231` -> `0.0`, `237` -> `-5.0`, `241` -> `-10.0`, `245` -> `-15.0`, `247` -> `-20.0`, `250` -> `-25.0`, `251` -> `-30.0`, `255` -> `-40.0`
*   **Fahrenheit (`MAT F` - Table 53):**
    *   `0` -> `392.0`, `12` -> `302.0`, `13` -> `293.0`, `14` -> `284.0`, `16` -> `275.0`, `18` -> `266.0`, `21` -> `257.0`, `23` -> `248.0`, `26` -> `239.0`, `30` -> `230.0`, `34` -> `221.0`, `39` -> `212.0`, `44` -> `203.0`, `50` -> `194.0`, `56` -> `185.0`, `64` -> `176.0`, `72` -> `167.0`, `81` -> `158.0`, `92` -> `149.0`, `102` -> `140.0`, `114` -> `131.0`, `126` -> `122.0`, `139` -> `113.0`, `152` -> `104.0`, `165` -> `95.0`, `177` -> `86.0`, `189` -> `77.0`, `199` -> `68.0`, `209` -> `59.0`, `218` -> `50.0`, `225` -> `41.0`, `231` -> `32.0`, `237` -> `23.0`, `241` -> `14.0`, `245` -> `5.0`, `247` -> `-4.0`, `250` -> `-13.0`, `251` -> `-22.0`, `255` -> `-40.0`

---

### 3. Stored Fault Trouble Codes

Malfunction Indicator Codes are mapped as bit flags across three specific status bytes:

#### Codes Byte 1 (Payload Index 11 / Byte 12)
*   **Bit 7:** Code 12 - Crank Sensor / System Check
*   **Bit 6:** Code 13 - O2 Sensor
*   **Bit 5:** Code 14 - Coolant High Temp
*   **Bit 4:** Code 15 - Coolant Low Temp
*   **Bit 3:** Code 21 - TPS Voltage High
*   **Bit 2:** Code 22 - TPS Voltage Low
*   **Bit 1:** Code 23 - MAT Voltage Low
*   **Bit 0:** Code 24 - Vehicle Speed Sensor (VSS)

#### Codes Byte 2 (Payload Index 12 / Byte 13)
*   **Bit 7:** Code 25 - MAT Voltage High
*   **Bit 5:** Code 32 - EGR System
*   **Bit 4:** Code 33 - MAP Sensor High
*   **Bit 3:** Code 34 - MAP Sensor Low
*   **Bit 2:** Code 35 - IAC Position
*   **Bit 0:** Code 42 - Electronic Spark Timing (EST)

#### Codes Byte 3 (Payload Index 13 / Byte 14)
*   **Bit 7:** Code 43 - Electronic Spark Control (Knock Sensor)
*   **Bit 6:** Code 44 - O2 Sensor Lean Exhaust
*   **Bit 5:** Code 45 - O2 Sensor Rich Exhaust
*   **Bit 4:** Code 51 - PROM Error
*   **Bit 3:** Code 52 - Cal-Pack Error
*   **Bit 2:** Code 53 - System Battery Over-Voltage
*   **Bit 0:** Code 55 - ADU Error

---

### 4. Engine Status & Bit Flags

#### Misc Byte 1 (Payload Index 14 / Byte 15)
*   **Bit 1:** BLM Enabled (1 = Yes)
*   **Bit 3:** Quasi Pulse Mode (1 = Yes)
*   **Bit 4:** Async Pulse Mode (1 = Yes)
*   **Bit 6:** Rich/Lean Status (1 = Rich, 0 = Lean)
*   **Bit 7:** Loop Status (1 = Closed, 0 = Open)

#### Misc Byte 2 (Payload Index 15 / Byte 16)
*   **Bit 5:** A/C Status (0 = Enabled, 1 = Disabled/Idle)
*   **Bit 7:** Park/Neutral Switch (1 = Park/Neutral, 0 = In Gear)

#### Misc Byte 3 (Payload Index 16 / Byte 17)
*   **Bit 0:** A/C Clutch Command (1 = Enabled)
*   **Bit 2:** Torque Converter Clutch (TCC) (1 = Locked)
*   **Bit 5:** Power Steering Cramp Switch (1 = Active)

---

## App Features & Architecture

1.  **Bluetooth Thread Management:**
    *   Queries for paired devices and connects directly to `"ESP32-ALDL"` over standard SPP RFCOMM sockets.
    *   Robust frame synchronization: buffers incoming streams and aligns on the double-byte header `0xAA 0x55`.
2.  **State Management:**
    *   State flows from the Bluetooth thread through Kotlin `StateFlow` to Compose UI.
3.  **Modern Dash Dashboard UI:**
    *   Dynamic animations for RPM and TPS sweeps.
    *   Metrics displayed in responsive tiles with custom indicator colors (e.g. engine loop states, fuel trims, battery health).
    *   Live trouble code alert panel.
    *   Diagnostic pane displaying real-time raw hex buffers for physical signal verification.