package com.gronod.esp32aldldashboard.ui.blm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.gronod.esp32aldldashboard.repository.BLMCellData
import com.gronod.esp32aldldashboard.repository.BLMTableRepository
import kotlinx.coroutines.flow.StateFlow

class BLMTableViewModel(private val repository: BLMTableRepository) : ViewModel() {

    val tableData: StateFlow<Array<Array<BLMCellData>>> = repository.tableData
    val rpmBands: List<Int> = repository.rpmBands
    val mapBands: List<Int> = repository.mapBands

    fun clearTable() {
        repository.clearTable()
    }

    fun getBLMColor(blm: Int): Long {
        return repository.getBLMColor(blm)
    }
}

class BLMTableViewModelFactory(private val repository: BLMTableRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BLMTableViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BLMTableViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
