package com.example.endlessmoney.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "settings")

class SettingsDataStore(
    private val context: Context
) {
    private object Keys {
        val SELECTED_CURRENCY = stringPreferencesKey("selected_currency")
        val HIDE_BALANCE = booleanPreferencesKey("hide_balance")
        val LAST_CURRENCY_UPDATE = longPreferencesKey("last_currency_update")
        val USD_RATE = doublePreferencesKey("usd_rate")
        val EUR_RATE = doublePreferencesKey("eur_rate")
    }

    val selectedCurrencyFlow: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[Keys.SELECTED_CURRENCY] ?: "RUB"
    }

    val hideBalanceFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[Keys.HIDE_BALANCE] ?: false
    }

    val lastCurrencyUpdateFlow: Flow<Long> = context.dataStore.data.map { prefs ->
        prefs[Keys.LAST_CURRENCY_UPDATE] ?: 0L
    }

    val usdRateFlow: Flow<Double> = context.dataStore.data.map { prefs ->
        prefs[Keys.USD_RATE] ?: 1.0
    }

    val eurRateFlow: Flow<Double> = context.dataStore.data.map { prefs ->
        prefs[Keys.EUR_RATE] ?: 1.0
    }

    suspend fun setSelectedCurrency(currency: String) {
        context.dataStore.edit { prefs ->
            prefs[Keys.SELECTED_CURRENCY] = currency
        }
    }

    suspend fun setHideBalance(hide: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[Keys.HIDE_BALANCE] = hide
        }
    }

    suspend fun setLastCurrencyUpdate(timestamp: Long) {
        context.dataStore.edit { prefs ->
            prefs[Keys.LAST_CURRENCY_UPDATE] = timestamp
        }
    }

    suspend fun setUsdRate(rate: Double) {
        context.dataStore.edit { prefs ->
            prefs[Keys.USD_RATE] = rate
        }
    }

    suspend fun setEurRate(rate: Double) {
        context.dataStore.edit { prefs ->
            prefs[Keys.EUR_RATE] = rate
        }
    }

    suspend fun clearAll() {
        context.dataStore.edit {
            it.clear()
        }
    }
}