package com.example.esp32aldldashboard.ui.charts

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.esp32aldldashboard.parser.ALDLFrame
import kotlinx.coroutines.flow.StateFlow

@Composable
fun ChartsScreen(
    latestFrameFlow: StateFlow<ALDLFrame?>,
    modifier: Modifier = Modifier
) {
    val latestFrame by latestFrameFlow.collectAsStateWithLifecycle()
    
    // We maintain a limited rolling history of points (e.g. 100 points)
    val rpmHistory = remember { mutableStateListOf<Float>() }
    val maxHistorySize = 100

    LaunchedEffect(latestFrame) {
        latestFrame?.let {
            rpmHistory.add(it.engineSpeedRpm.toFloat())
            if (rpmHistory.size > maxHistorySize) {
                rpmHistory.removeAt(0)
            }
        }
    }

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Real-Time Telemetry",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(16.dp))
        
        Card(
            modifier = Modifier.fillMaxWidth().height(200.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "RPM", color = Color(0xFF00FFCC))
                Spacer(modifier = Modifier.height(8.dp))
                LineChart(
                    data = rpmHistory,
                    maxValue = 6000f,
                    lineColor = Color(0xFF00FFCC),
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Additional charts can go here (e.g., O2, TPS)
    }
}

@Composable
fun LineChart(
    data: List<Float>,
    maxValue: Float,
    lineColor: Color,
    modifier: Modifier = Modifier
) {
    if (data.isEmpty()) return

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val pointSpacing = if (data.size > 1) width / (data.size - 1) else 0f

        val path = Path()
        data.forEachIndexed { index, value ->
            val x = index * pointSpacing
            // Invert y since Canvas y=0 is at the top
            val y = height - ((value / maxValue) * height).coerceIn(0f, height)
            
            if (index == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }
        }

        drawPath(
            path = path,
            color = lineColor,
            style = Stroke(width = 4.dp.toPx())
        )
    }
}
