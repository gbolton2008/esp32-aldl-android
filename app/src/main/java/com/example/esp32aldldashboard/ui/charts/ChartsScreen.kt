package com.example.esp32aldldashboard.ui.charts

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.esp32aldldashboard.parser.ALDLFrame
import com.example.esp32aldldashboard.repository.ChartPreferencesRepository
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@Composable
fun ChartsScreen(
    latestFrameFlow: StateFlow<ALDLFrame?>,
    chartPreferencesRepository: ChartPreferencesRepository,
    modifier: Modifier = Modifier
) {
    val latestFrame by latestFrameFlow.collectAsStateWithLifecycle()
    val viewMode by chartPreferencesRepository.viewModeFlow.collectAsStateWithLifecycle(
        initialValue = ChartPreferencesRepository.ViewMode.MULTI
    )
    val selectedParameters by chartPreferencesRepository.selectedParametersFlow.collectAsStateWithLifecycle(
        initialValue = setOf(ChartParameter.RPM, ChartParameter.MAP, ChartParameter.TPS, ChartParameter.O2_SENSOR)
    )
    val singleChartParameter by chartPreferencesRepository.singleChartParameterFlow.collectAsStateWithLifecycle(
        initialValue = ChartParameter.RPM
    )

    // History storage for all parameters
    val maxHistorySize = 100
    val histories = remember {
        mutableStateMapOf<ChartParameter, MutableList<Float>>().apply {
            ChartParameter.values().forEach { put(it, mutableStateListOf()) }
        }
    }

    LaunchedEffect(latestFrame) {
        latestFrame?.let { frame ->
            ChartParameter.values().forEach { param ->
                val history = histories.getOrPut(param) { mutableStateListOf() }
                history.add(param.extractValue(frame))
                if (history.size > maxHistorySize) {
                    history.removeAt(0)
                }
            }
        }
    }

    val coroutineScope = rememberCoroutineScope()

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        // Header with view mode toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Real-Time Telemetry",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            
            // View mode toggle
            Row {
                FilterChip(
                    selected = viewMode == ChartPreferencesRepository.ViewMode.SINGLE,
                    onClick = {
                        coroutineScope.launch {
                            chartPreferencesRepository.setViewMode(
                                if (viewMode == ChartPreferencesRepository.ViewMode.SINGLE) 
                                    ChartPreferencesRepository.ViewMode.MULTI 
                                else 
                                    ChartPreferencesRepository.ViewMode.SINGLE
                            )
                        }
                    },
                    label = { Text(if (viewMode == ChartPreferencesRepository.ViewMode.SINGLE) "Single" else "Multi") }
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))

        when (viewMode) {
            ChartPreferencesRepository.ViewMode.SINGLE -> {
                // Single chart mode
                SingleChartView(
                    selectedParameter = singleChartParameter,
                    history = histories[singleChartParameter] ?: emptyList(),
                    onParameterChange = { param ->
                        coroutineScope.launch {
                            chartPreferencesRepository.setSingleChartParameter(param)
                        }
                    }
                )
            }
            ChartPreferencesRepository.ViewMode.MULTI -> {
                // Multi chart mode
                MultiChartView(
                    selectedParameters = selectedParameters,
                    histories = histories,
                    onToggleParameter = { param ->
                        coroutineScope.launch {
                            chartPreferencesRepository.toggleParameter(param)
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun SingleChartView(
    selectedParameter: ChartParameter,
    history: List<Float>,
    onParameterChange: (ChartParameter) -> Unit
) {
    Column {
        // Parameter selector dropdown
        var expanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it }
        ) {
            OutlinedTextField(
                value = selectedParameter.displayName,
                onValueChange = {},
                readOnly = true,
                label = { Text("Parameter") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                ChartParameter.values().forEach { param ->
                    DropdownMenuItem(
                        text = { Text(param.displayName) },
                        onClick = {
                            onParameterChange(param)
                            expanded = false }
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Large single chart
        Card(
            modifier = Modifier.fillMaxWidth().height(300.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = selectedParameter.displayName,
                        color = selectedParameter.color,
                        style = MaterialTheme.typography.titleMedium
                    )
                    if (history.isNotEmpty()) {
                        Text(
                            text = String.format("%.1f", history.last()),
                            color = selectedParameter.color,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                LineChart(
                    data = history,
                    maxValue = selectedParameter.maxValue,
                    lineColor = selectedParameter.color,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
private fun MultiChartView(
    selectedParameters: Set<ChartParameter>,
    histories: Map<ChartParameter, List<Float>>,
    onToggleParameter: (ChartParameter) -> Unit
) {
    Column {
        // Parameter toggle chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(ChartParameter.values().toList()) { param ->
                val isSelected = selectedParameters.contains(param)
                FilterChip(
                    selected = isSelected,
                    onClick = { onToggleParameter(param) },
                    label = { Text(param.displayName) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = param.color.copy(alpha = 0.3f),
                        selectedLabelColor = param.color
                    )
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 2x2 grid of charts (up to 4)
        val activeParams = selectedParameters.take(4)
        
        if (activeParams.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxWidth().height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Select parameters above to display charts",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                activeParams.chunked(2).forEach { rowParams ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        rowParams.forEach { param ->
                            val history = histories[param] ?: emptyList()
                            ChartCard(
                                parameter = param,
                                history = history,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        // Fill remaining space if odd number
                        if (rowParams.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ChartCard(
    parameter: ChartParameter,
    history: List<Float>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(180.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = parameter.displayName,
                    color = parameter.color,
                    style = MaterialTheme.typography.bodyMedium
                )
                if (history.isNotEmpty()) {
                    Text(
                        text = String.format("%.1f", history.last()),
                        color = parameter.color,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            LineChart(
                data = history,
                maxValue = parameter.maxValue,
                lineColor = parameter.color,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
private fun LineChart(
    data: List<Float>,
    maxValue: Float,
    lineColor: Color,
    modifier: Modifier = Modifier
) {
    if (data.isEmpty()) {
        Box(modifier = modifier, contentAlignment = Alignment.Center) {
            Text(
                text = "Waiting for data...",
                color = Color.Gray,
                style = MaterialTheme.typography.bodySmall
            )
        }
        return
    }

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
            style = Stroke(width = 3.dp.toPx())
        )
    }
}
