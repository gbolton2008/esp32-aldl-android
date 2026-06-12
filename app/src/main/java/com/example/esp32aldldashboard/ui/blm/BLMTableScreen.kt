package com.example.esp32aldldashboard.ui.blm

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.esp32aldldashboard.repository.BLMTableRepository

@Composable
fun BLMTableScreen(
    viewModel: BLMTableViewModel,
    modifier: Modifier = Modifier
) {
    val tableData by viewModel.tableData.collectAsStateWithLifecycle()
    val rpmBands = viewModel.rpmBands
    val mapBands = viewModel.mapBands

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Header with title and clear button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "BLM/INT Table",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "RPM (vertical) × MAP (horizontal)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Button(
                onClick = { viewModel.clearTable() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Clear")
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Legend
        BLMLegend()
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Table container
        Column {
            // MAP headers (top)
            Row {
                // Empty corner cell
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(Color(0xFF2C2C2C))
                        .border(1.dp, Color(0xFF3C3C3C)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "RPM\\MAP",
                        fontSize = 10.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
                
                // MAP band headers
                mapBands.forEach { map ->
                    Box(
                        modifier = Modifier
                            .width(50.dp)
                            .height(60.dp)
                            .background(Color(0xFF2C2C2C))
                            .border(1.dp, Color(0xFF3C3C3C)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$map",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF00E5FF),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
            
            // Data rows
            rpmBands.forEachIndexed { rowIndex, rpm ->
                Row {
                    // RPM band header (left)
                    Box(
                        modifier = Modifier
                            .width(60.dp)
                            .height(50.dp)
                            .background(Color(0xFF2C2C2C))
                            .border(1.dp, Color(0xFF3C3C3C)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$rpm",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF00E5FF),
                            textAlign = TextAlign.Center
                        )
                    }
                    
                    // Data cells
                    if (rowIndex < tableData.size) {
                        tableData[rowIndex].forEach { cell ->
                            val colorArgb = viewModel.getBLMColor(cell.blm)
                            val cellColor = Color(colorArgb)
                            
                            Box(
                                modifier = Modifier
                                    .width(50.dp)
                                    .height(50.dp)
                                    .background(cellColor.copy(alpha = 0.3f))
                                    .border(1.dp, cellColor),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "${cell.blm}",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (cell.blm > 140 || cell.blm < 116) Color.White else Color.Black
                                    )
                                    Text(
                                        text = "${cell.intValue}",
                                        fontSize = 10.sp,
                                        color = if (cell.blm > 140 || cell.blm < 116) Color.White.copy(alpha = 0.7f) else Color.Black.copy(alpha = 0.7f)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun BLMLegend() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = "Color Legend",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                LegendItem(color = Color(0xFF00E676), label = "≤120 Lean", textColor = Color.White)
                LegendItem(color = Color(0xFF2196F3), label = "128 Ideal", textColor = Color.White)
                LegendItem(color = Color(0xFFFF3D00), label = "≥150 Rich", textColor = Color.White)
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "BLM = Block Learn Multiplier (fuel trim). INT = Integrator (short-term correction). Values show most recent update.",
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun LegendItem(color: Color, label: String, textColor: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .background(color, shape = RoundedCornerShape(2.dp))
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            fontSize = 10.sp,
            color = textColor
        )
    }
}
