package com.example.endlessmoney.data.remote.repository

import com.example.endlessmoney.data.datastore.SettingsDataStore
import com.example.endlessmoney.data.remote.api.CurrencyApi

class CurrencyRepository(
    private val api: CurrencyApi,
    private val settingsDataStore: SettingsDataStore
) {
    suspend fun refreshRates(): Result<Map<String, Double>> {
        return try {
            val response = api.getLatestRates("RUB")

            val usdRate = response.find { it.quote == "USD" }?.rate ?: 1.0
            val eurRate = response.find { it.quote == "EUR" }?.rate ?: 1.0

            settingsDataStore.setUsdRate(usdRate)
            settingsDataStore.setEurRate(eurRate)
            settingsDataStore.setLastCurrencyUpdate(System.currentTimeMillis())

            Result.success(
                mapOf(
                    "USD" to usdRate,
                    "EUR" to eurRate
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}