package com.example.triplink.features.admin.reports.AdminReportDetails

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.triplink.domain.model.admin.AdminReportCase
import com.example.triplink.domain.repository.admin.ReportRepository
import com.example.triplink.features.admin.reports.AdminReportUi
import com.example.triplink.features.admin.reports.toUi
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminReportsDetailsViewModel @Inject constructor(
    @param:ApplicationContext private val appContext: Context,
    private val repository: ReportRepository
) : ViewModel() {

    private val _currentReport = MutableStateFlow<AdminReportUi?>(null)
    val currentReport: StateFlow<AdminReportUi?> = _currentReport.asStateFlow()

    fun loadReportById(reportId: String) {
        viewModelScope.launch {
            _currentReport.value = repository.getReportById(reportId)?.toUiModel()
        }
    }

    fun getReportById(reportId: String): AdminReportUi? {
        return _currentReport.value
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