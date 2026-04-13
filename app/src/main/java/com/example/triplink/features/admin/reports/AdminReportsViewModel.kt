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

    val pendingCount: Int
        get() = repository.pendingReportsCount

    init {
        refreshReports()
    }

    private fun refreshReports() {
        _reportCards.value = repository.reportCases.map { it.toUiModel() }
    }

    fun getReportById(reportId: String): AdminReportUi? = repository.getReportById(reportId)?.toUiModel()

    fun confirmReport(reportId: String) {
        viewModelScope.launch {
            repository.confirmReport(reportId)
            refreshReports()
        }
    }

    fun invalidateReport(reportId: String) {
        viewModelScope.launch {
            repository.invalidateReport(reportId)
            refreshReports()
        }
    }

    private fun AdminReportCase.toUiModel(): AdminReportUi = toUi(appContext)
}
