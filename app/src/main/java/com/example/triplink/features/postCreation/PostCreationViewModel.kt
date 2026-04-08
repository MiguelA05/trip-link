package com.example.triplink.features.postCreation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.triplink.core.utils.ValidatedField
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PostCreationViewModel @Inject constructor() : ViewModel() {

    var placeName = ValidatedField("") { value ->
        if (value.isBlank()) "El nombre del lugar es obligatorio" else null
    }

    var description by mutableStateOf("")

    var selectedCategory = ValidatedField("") { value ->
        if (value.isBlank()) "La categoría es obligatoria" else null
    }

    var isOpenEveryDay by mutableStateOf(false)

    var daySchedules by mutableStateOf(
        listOf("Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom").map {
            DayScheduleData(it)
        }
    )

    var selectedPriceRange by mutableStateOf("Gratuito")
    var showSuccessModal by mutableStateOf(false)

    val categories = listOf("Gastronomía", "Cultura", "Naturaleza", "Entretenimiento", "Historia")

    val isFormValid: Boolean
        get() {
            val areMandatoryFieldsValid = placeName.isValid && 
                                        placeName.value.isNotBlank() && 
                                        selectedCategory.isValid && 
                                        selectedCategory.value.isNotBlank()
            
            val areSchedulesValid = daySchedules.all { schedule ->
                if (schedule.isEnabled) {
                    schedule.openTime.isNotBlank() && 
                    schedule.closeTime.isNotBlank() && 
                    isTimeOrderValid(schedule.openTime, schedule.closeTime)
                } else {
                    true
                }
            }
            
            return areMandatoryFieldsValid && areSchedulesValid
        }

    private fun isTimeOrderValid(open: String, close: String): Boolean {
        return try {
            val openParts = open.split(":").map { it.toInt() }
            val closeParts = close.split(":").map { it.toInt() }
            val openMinutes = openParts[0] * 60 + openParts[1]
            val closeMinutes = closeParts[0] * 60 + closeParts[1]
            openMinutes < closeMinutes
        } catch (e: Exception) {
            false
        }
    }

    fun onOpenEveryDayChange(checked: Boolean) {
        isOpenEveryDay = checked
        daySchedules = daySchedules.map {
            it.copy(
                isEnabled = checked,
                openTime = if (checked) it.openTime else "",
                closeTime = if (checked) it.closeTime else ""
            )
        }
    }

    fun onDayToggle(index: Int, checked: Boolean) {
        daySchedules = daySchedules.toMutableList().apply {
            this[index] = this[index].copy(
                isEnabled = checked,
                openTime = if (checked) this[index].openTime else "",
                closeTime = if (checked) this[index].closeTime else ""
            )
        }
        
        if (!checked) {
            isOpenEveryDay = false
        } else if (daySchedules.all { it.isEnabled }) {
            isOpenEveryDay = true
        }
    }

    fun onOpenTimeChange(index: Int, time: String) {
        daySchedules = daySchedules.toMutableList().apply {
            this[index] = this[index].copy(openTime = time)
        }
    }

    fun onCloseTimeChange(index: Int, time: String) {
        daySchedules = daySchedules.toMutableList().apply {
            this[index] = this[index].copy(closeTime = time)
        }
    }
}
