package com.example.esp32aldldashboard.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.example.esp32aldldashboard.ui.charts.ChartParameter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.chartPreferencesDataStore: DataStore<Preferences> by preferencesDataStore(name = "chart_preferences")

class ChartPreferencesRepository(private val context: Context) {

    companion object {
        val CHART_VIEW_MODE = stringPreferencesKey("chart_view_mode")
        val SELECTED_PARAMETERS = stringSetPreferencesKey("selected_parameters")
        val SINGLE_CHART_PARAMETER = stringPreferencesKey("single_chart_parameter")
    }

    enum class ViewMode {
        SINGLE, MULTI
    }

    val viewModeFlow: Flow<ViewMode> = context.chartPreferencesDataStore.data
        .map { preferences ->
            val modeString = preferences[CHART_VIEW_MODE] ?: ViewMode.MULTI.name
            try {
                ViewMode.valueOf(modeString)
            } catch (e: IllegalArgumentException) {
                ViewMode.MULTI
            }
        }

    val selectedParametersFlow: Flow<Set<ChartParameter>> = context.chartPreferencesDataStore.data
        .map { preferences ->
            val paramNames = preferences[SELECTED_PARAMETERS] ?: setOf(
                ChartParameter.RPM.name,
                ChartParameter.MAP.name,
                ChartParameter.TPS.name,
                ChartParameter.O2_SENSOR.name
            )
            paramNames.mapNotNull { name ->
                try {
                    ChartParameter.valueOf(name)
                } catch (e: IllegalArgumentException) {
                    null
                }
            }.toSet()
        }

    val singleChartParameterFlow: Flow<ChartParameter> = context.chartPreferencesDataStore.data
        .map { preferences ->
            val paramName = preferences[SINGLE_CHART_PARAMETER] ?: ChartParameter.RPM.name
            try {
                ChartParameter.valueOf(paramName)
            } catch (e: IllegalArgumentException) {
                ChartParameter.RPM
            }
        }

    suspend fun setViewMode(mode: ViewMode) {
        context.chartPreferencesDataStore.edit { preferences ->
            preferences[CHART_VIEW_MODE] = mode.name
        }
    }

    suspend fun setSelectedParameters(parameters: Set<ChartParameter>) {
        context.chartPreferencesDataStore.edit { preferences ->
            preferences[SELECTED_PARAMETERS] = parameters.map { it.name }.toSet()
        }
    }

    suspend fun setSingleChartParameter(parameter: ChartParameter) {
        context.chartPreferencesDataStore.edit { preferences ->
            preferences[SINGLE_CHART_PARAMETER] = parameter.name
        }
    }

    suspend fun toggleParameter(parameter: ChartParameter) {
        context.chartPreferencesDataStore.edit { preferences ->
            val current = preferences[SELECTED_PARAMETERS] ?: setOf(
                ChartParameter.RPM.name,
                ChartParameter.MAP.name,
                ChartParameter.TPS.name,
                ChartParameter.O2_SENSOR.name
            )
            val updated = if (current.contains(parameter.name)) {
                current - parameter.name
            } else {
                current + parameter.name
            }
            preferences[SELECTED_PARAMETERS] = updated
        }
    }
}
