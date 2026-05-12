package com.example.triplink.features.admin.reports

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.triplink.domain.model.admin.AdminReportCase
import com.example.triplink.domain.repository.admin.ReportRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminReportsViewModel @Inject constructor(
    @param:ApplicationContext private val appContext: Context,
    private val repository: ReportRepository
) : ViewModel() {

    private val _reportCards = MutableStateFlow<List<AdminReportUi>>(emptyList())
    val reportCards: StateFlow<List<AdminReportUi>> = _reportCards.asStateFlow()

    init {
        viewModelScope.launch {
            repository.reportCases.collect { cases ->
                _reportCards.value = cases.map { it.toUiModel() }
            }
        }
    }

    val pendingCount: Int
        get() = repository.pendingReportsCount

    fun getReportById(reportId: String, callback: (AdminReportUi?) -> Unit) {
        viewModelScope.launch {
            val report = repository.getReportById(reportId)?.toUiModel()
            callback(report)
        }
    }

    fun confirmReport(reportId: String) {
        viewModelScope.launch {
            repository.confirmReport(reportId)
        }
    }

    fun invalidateReport(reportId: String) {
        viewModelScope.launch {
            repository.invalidateReport(reportId)
        }
    }

    private fun AdminReportCase.toUiModel(): AdminReportUi = toUi(appContext)
}
