package com.example.esp32aldldashboard.repository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.math.abs

data class BLMCellData(
    val blm: Int = 128,
    val intValue: Int = 128,
    val updateCount: Int = 0,
    val lastUpdateTime: Long = 0
)

class BLMTableRepository {

    // RPM bands as specified by ECM
    val rpmBands = listOf(600, 800, 1000, 1200, 1400, 1600, 2000, 2400, 2800, 3200, 3600, 4000, 4400, 4800)
    
    // MAP bands as specified by ECM
    val mapBands = listOf(20, 25, 30, 35, 40, 45, 50, 55, 60, 65, 70, 75, 80, 85, 90, 95, 100)
    
    private val rowCount = rpmBands.size
    private val colCount = mapBands.size
    
    // 2D array [RPM bands × MAP bands]
    private val _tableData = MutableStateFlow(
        Array(rowCount) { Array(colCount) { BLMCellData() } }
    )
    val tableData: StateFlow<Array<Array<BLMCellData>>> = _tableData
    
    /**
     * Updates the cell corresponding to the nearest RPM and MAP bands.
     * Values are retained until replaced by new updates.
     */
    fun updateCell(rpm: Int, mapKpa: Float, blm: Int, intValue: Int) {
        val rpmIndex = findNearestBandIndex(rpm, rpmBands)
        val mapIndex = findNearestBandIndex(mapKpa.toInt(), mapBands)
        
        if (rpmIndex >= 0 && rpmIndex < rowCount && mapIndex >= 0 && mapIndex < colCount) {
            val currentData = _tableData.value
            val newData = currentData.map { row -> row.map { it }.toTypedArray() }.toTypedArray()
            
            val existing = newData[rpmIndex][mapIndex]
            newData[rpmIndex][mapIndex] = BLMCellData(
                blm = blm,
                intValue = intValue,
                updateCount = existing.updateCount + 1,
                lastUpdateTime = System.currentTimeMillis()
            )
            
            _tableData.value = newData
        }
    }
    
    /**
     * Clears all table data, resetting to default values.
     */
    fun clearTable() {
        _tableData.value = Array(rowCount) { Array(colCount) { BLMCellData() } }
    }
    
    /**
     * Gets the color for a BLM value.
     * Blue at 128 (center), Green at 120 (lean), Red at 150 (rich)
     * Returns ARGB color value
     */
    fun getBLMColor(blm: Int): Long {
        return when {
            blm <= 120 -> {
                // Green (120 and below)
                0xFF00E676
            }
            blm >= 150 -> {
                // Red (150 and above)
                0xFFFF3D00
            }
            blm <= 128 -> {
                // Interpolate between Green (120) and Blue (128)
                val fraction = (blm - 120) / 8f
                interpolateColor(0xFF00E676, 0xFF2196F3, fraction)
            }
            else -> {
                // Interpolate between Blue (128) and Red (150)
                val fraction = (blm - 128) / 22f
                interpolateColor(0xFF2196F3, 0xFFFF3D00, fraction)
            }
        }
    }
    
    private fun interpolateColor(color1: Long, color2: Long, fraction: Float): Long {
        val r1 = (color1 shr 16) and 0xFF
        val g1 = (color1 shr 8) and 0xFF
        val b1 = color1 and 0xFF
        
        val r2 = (color2 shr 16) and 0xFF
        val g2 = (color2 shr 8) and 0xFF
        val b2 = color2 and 0xFF
        
        val r = (r1 + (r2 - r1) * fraction).toInt()
        val g = (g1 + (g2 - g1) * fraction).toInt()
        val b = (b1 + (b2 - b1) * fraction).toInt()
        
        return 0xFF000000 or (r.toLong() shl 16) or (g.toLong() shl 8) or b.toLong()
    }
    
    private fun findNearestBandIndex(value: Int, bands: List<Int>): Int {
        if (bands.isEmpty()) return -1
        
        var nearestIndex = 0
        var minDiff = abs(value - bands[0])
        
        for (i in 1 until bands.size) {
            val diff = abs(value - bands[i])
            if (diff < minDiff) {
                minDiff = diff
                nearestIndex = i
            }
        }
        
        return nearestIndex
    }
}
