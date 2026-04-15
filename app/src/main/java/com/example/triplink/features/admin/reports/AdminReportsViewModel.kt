package com.example.triplink.features.admin.reports

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.triplink.domain.model.admin.AdminReportCase
import com.example.triplink.domain.repository.admin.ReportRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class AdminReportsViewModel @Inject constructor(
    @param:ApplicationContext private val appContext: Context,
    private val repository: ReportRepository
) : ViewModel() {

    val reportCards: StateFlow<List<AdminReportUi>> = repository.reportCases
        .map { cases -> cases.map { it.toUiModel() } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    val pendingCount: Int
        get() = repository.pendingReportsCount

    fun getReportById(reportId: String): AdminReportUi? = repository.getReportById(reportId)?.toUiModel()

    fun confirmReport(reportId: String) {
        repository.confirmReport(reportId)
    }

    fun invalidateReport(reportId: String) {
        repository.invalidateReport(reportId)
    }

    private fun AdminReportCase.toUiModel(): AdminReportUi = toUi(appContext)
}
