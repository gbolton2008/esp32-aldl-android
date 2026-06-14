package com.gronod.esp32aldldashboard.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RpmGauge(
    rpm: Int,
    maxRpm: Int = 6000,
    modifier: Modifier = Modifier
) {
    val animatedRpm by animateFloatAsState(
        targetValue = rpm.toFloat(),
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "rpmAnimation"
    )

    Box(modifier = modifier.aspectRatio(1f), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            val strokeWidth = 24.dp.toPx()
            val startAngle = 135f
            val sweepAngle = 270f
            
            // Background arc
            drawArc(
                color = Color.DarkGray.copy(alpha = 0.5f),
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                size = Size(size.width, size.height)
            )

            // Foreground arc
            val progress = (animatedRpm / maxRpm).coerceIn(0f, 1f)
            val color = if (progress > 0.85f) Color.Red else Color(0xFF00FFCC) // Neon Cyan
            
            drawArc(
                color = color,
                startAngle = startAngle,
                sweepAngle = sweepAngle * progress,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                size = Size(size.width, size.height)
            )
        }
        
        Text(
            text = rpm.toString(),
            color = Color.White,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "RPM",
            color = Color.LightGray,
            fontSize = 14.sp,
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 24.dp)
        )
    }
}

@Composable
fun TpsBar(
    tpsVolts: Float,
    modifier: Modifier = Modifier
) {
    val animatedTps by animateFloatAsState(
        targetValue = tpsVolts,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "tpsAnimation"
    )

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = size.height
            val maxVolts = 5.0f
            val progress = (animatedTps / maxVolts).coerceIn(0f, 1f)
            
            // Background bar
            drawLine(
                color = Color.DarkGray.copy(alpha = 0.5f),
                start = Offset(0f, size.height / 2),
                end = Offset(size.width, size.height / 2),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )

            // Foreground bar
            drawLine(
                color = Color(0xFFFF9900), // Neon Orange
                start = Offset(0f, size.height / 2),
                end = Offset(size.width * progress, size.height / 2),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )
        }
        Text(
            text = String.format("%.2f V", tpsVolts),
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
    }
}
