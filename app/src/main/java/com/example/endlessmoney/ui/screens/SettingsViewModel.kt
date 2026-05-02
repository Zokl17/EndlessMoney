package com.example.endlessmoney.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.endlessmoney.data.datastore.SettingsDataStore
import com.example.endlessmoney.data.remote.repository.CurrencyRepository
import com.example.endlessmoney.domain.usecase.ClearSettingsUseCase
import com.example.endlessmoney.domain.usecase.ClearTransactionsUseCase
import com.example.endlessmoney.domain.usecase.ResetAllDataUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class SettingsUiState(
    val selectedCurrency: String = "RUB",
    val hideBalance: Boolean = false,
    val lastCurrencyUpdate: Long = 0L,
    val usdRate: Double = 1.0,
    val eurRate: Double = 1.0,
    val ratesText: String = "",
    val errorText: String? = null,
    val isLoading: Boolean = false
)

class SettingsViewModel(
    private val settingsDataStore: SettingsDataStore,
    private val currencyRepository: CurrencyRepository,
    private val clearTransactionsUseCase: ClearTransactionsUseCase,
    private val clearSettingsUseCase: ClearSettingsUseCase,
    private val resetAllDataUseCase: ResetAllDataUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            settingsDataStore.selectedCurrencyFlow.collect { currency: String ->
                android.util.Log.d("SETTINGS_VM", "currency = $currency")
                _uiState.value = _uiState.value.copy(selectedCurrency = currency)
            }
        }

        viewModelScope.launch {
            settingsDataStore.hideBalanceFlow.collect { hide: Boolean ->
                android.util.Log.d("SETTINGS_VM", "currency = $hide")
                _uiState.value = _uiState.value.copy(hideBalance = hide)
            }
        }

        viewModelScope.launch {
            settingsDataStore.lastCurrencyUpdateFlow.collect { timestamp: Long ->
                _uiState.value = _uiState.value.copy(lastCurrencyUpdate = timestamp)
            }
        }

        viewModelScope.launch {
            settingsDataStore.usdRateFlow.collect { rate: Double ->
                _uiState.value = _uiState.value.copy(usdRate = rate)
            }
        }

        viewModelScope.launch {
            settingsDataStore.eurRateFlow.collect { rate: Double ->
                _uiState.value = _uiState.value.copy(eurRate = rate)
            }
        }
    }

    fun setCurrency(currency: String) {
        viewModelScope.launch {
            settingsDataStore.setSelectedCurrency(currency)
        }
    }

    fun setHideBalance(hide: Boolean) {
        viewModelScope.launch {
            settingsDataStore.setHideBalance(hide)
        }
    }

    fun clearTransactions() {
        viewModelScope.launch {
            clearTransactionsUseCase()
        }
    }

    fun clearSettings() {
        viewModelScope.launch {
            clearSettingsUseCase()
        }
    }

    fun resetAll() {
        viewModelScope.launch {
            resetAllDataUseCase()
        }
    }

    fun refreshRatesIfNeeded() {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val lastUpdate = _uiState.value.lastCurrencyUpdate
            val oneDayMillis = 24 * 60 * 60 * 1000L

            if (lastUpdate == 0L || now - lastUpdate >= oneDayMillis) {
                refreshRates()
            }
        }
    }

    fun refreshRates() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorText = null
            )

            val result = currencyRepository.refreshRates()

            result.onSuccess {
                val usd = settingsDataStore.usdRateFlow.first()
                val eur = settingsDataStore.eurRateFlow.first()
                val lastUpdate = settingsDataStore.lastCurrencyUpdateFlow.first()

                _uiState.value = _uiState.value.copy(
                    usdRate = usd,
                    eurRate = eur,
                    lastCurrencyUpdate = lastUpdate,
                    ratesText = "USD: $usd\nEUR: $eur",
                    errorText = null,
                    isLoading = false
                )
            }.onFailure {
                _uiState.value = _uiState.value.copy(
                    errorText = "Не удалось обновить курсы. Используй сохранённые данные.",
                    isLoading = false
                )
            }
        }
    }
}