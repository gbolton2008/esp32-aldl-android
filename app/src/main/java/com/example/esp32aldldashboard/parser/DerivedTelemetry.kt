package com.example.esp32aldldashboard.parser

// Approximate Engine Load (0.0 to 1.0)
// Very rough approximation: MAP (kPa) / ~100 kPa (atmospheric at sea level)
// At WOT, MAP is near atmospheric (100 kPa), load is near 100%. At idle, MAP is around 30-40 kPa, load is lower.
val ALDLFrame.estimatedEngineLoad: Float
    get() {
        val load = mapKpa / 100.0f
        return load.coerceIn(0.0f, 1.0f)
    }

// Approximate Fuel Flow Rate (lbs/hr)
// Rough formula: RPM * BPW (ms) * Injector Flow Rate Constant
// 2.8L Fiero V6 typically has ~15 lb/hr injectors. There are 6 injectors, firing sequentially or batch? 
// The 1227170 ECM fires batch (3 injectors per driver, twice per cycle).
// Rough estimation: flow = (RPM / 2) * BPW_seconds * 6 * Injector_rate / 60
// We'll provide a simplified unitless flow hint for the UI visualization.
val ALDLFrame.fuelFlowHint: Float
    get() {
        val bpwSeconds = bpwMs / 1000.0f
        return (engineSpeedRpm * bpwSeconds) / 2.0f
    }
