package com.example.kursova.ui.screens.service

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kursova.Graph
import com.example.kursova.domain.model.TariffSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class EditTariffsUiState(
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null,

    val dayPriceInput: String = "",
    val nightPriceInput: String = "",
    val nightStartInput: String = "",
    val nightEndInput: String = ""
)

class EditTariffsViewModel : ViewModel() {

    private val tariffRepo = Graph.tariffRepository

    private var currentSettings: TariffSettings? = null

    private val _uiState = MutableStateFlow(EditTariffsUiState())
    val uiState: StateFlow<EditTariffsUiState> = _uiState

    init {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(
                    isLoading = true,
                    error = null,
                    successMessage = null
                )

                val settings = tariffRepo.getSettings()
                currentSettings = settings

                _uiState.value = EditTariffsUiState(
                    isLoading = false,
                    dayPriceInput = settings.dayPricePerKwh.toString(),
                    nightPriceInput = settings.nightPricePerKwh.toString(),
                    nightStartInput = settings.nightStartHour.toString(),
                    nightEndInput = settings.nightEndHour.toString()
                )
            } catch (e: Exception) {
                _uiState.value = EditTariffsUiState(
                    isLoading = false,
                    error = e.message ?: "Failed to load tariffs"
                )
            }
        }
    }

    fun onDayPriceChange(value: String) {
        _uiState.value = _uiState.value.copy(
            dayPriceInput = value,
            error = null,
            successMessage = null
        )
    }

    fun onNightPriceChange(value: String) {
        _uiState.value = _uiState.value.copy(
            nightPriceInput = value,
            error = null,
            successMessage = null
        )
    }

    fun onNightStartChange(value: String) {
        _uiState.value = _uiState.value.copy(
            nightStartInput = value,
            error = null,
            successMessage = null
        )
    }

    fun onNightEndChange(value: String) {
        _uiState.value = _uiState.value.copy(
            nightEndInput = value,
            error = null,
            successMessage = null
        )
    }

    fun onResetToCurrent() {
        val settings = currentSettings ?: return
        _uiState.value = _uiState.value.copy(
            dayPriceInput = settings.dayPricePerKwh.toString(),
            nightPriceInput = settings.nightPricePerKwh.toString(),
            nightStartInput = settings.nightStartHour.toString(),
            nightEndInput = settings.nightEndHour.toString(),
            error = null,
            successMessage = null
        )
    }

    fun onSave() {
        val state = _uiState.value

        val dayPrice = state.dayPriceInput.toDoubleOrNull()
        val nightPrice = state.nightPriceInput.toDoubleOrNull()
        val nightStart = state.nightStartInput.toIntOrNull()
        val nightEnd = state.nightEndInput.toIntOrNull()

        if (dayPrice == null || nightPrice == null ||
            nightStart == null || nightEnd == null
        ) {
            _uiState.value = state.copy(
                error = "Будь ласка, введи коректні числові значення",
                successMessage = null
            )
            return
        }

        if (nightStart !in 0..23 || nightEnd !in 0..23) {
            _uiState.value = state.copy(
                error = "Години мають бути в діапазоні 0..23",
                successMessage = null
            )
            return
        }

        viewModelScope.launch {
            try {
                _uiState.value = state.copy(
                    isSaving = true,
                    error = null,
                    successMessage = null
                )

                val currentId = currentSettings?.id ?: 1
                val newSettings = TariffSettings(
                    id = currentId,
                    dayPricePerKwh = dayPrice,
                    nightPricePerKwh = nightPrice,
                    nightStartHour = nightStart,
                    nightEndHour = nightEnd
                )

                tariffRepo.updateSettings(newSettings)
                currentSettings = newSettings

                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    successMessage = "Тарифи збережено"
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    error = e.message ?: "Не вдалося зберегти тарифи"
                )
            }
        }
    }
}
