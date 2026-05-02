package com.example.endlessmoney.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    viewModel: SettingsViewModel = koinViewModel(),
    homeViewModel: HomeViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val account by homeViewModel.account.collectAsStateWithLifecycle()

    var showClearTransactionsDialog by remember { mutableStateOf(false) }
    var showClearSettingsDialog by remember { mutableStateOf(false) }
    var showResetAllDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.refreshRatesIfNeeded()
    }

    val numberFormatter = remember {
        val symbols = DecimalFormatSymbols(Locale.getDefault()).apply {
            groupingSeparator = ' '
            decimalSeparator = '.'
        }
        DecimalFormat("#,##0.00", symbols)
    }

    fun currencySymbol(currency: String): String {
        return when (currency) {
            "RUB" -> "₽"
            "USD" -> "$"
            "EUR" -> "€"
            else -> currency
        }
    }

    fun formatMoney(amount: Double, currency: String): String {
        return "${numberFormatter.format(amount)} ${currencySymbol(currency)}"
    }

    val rawBalance = account?.balance ?: 0.0

    val balanceRub = rawBalance
    val balanceUsd = rawBalance * uiState.usdRate
    val balanceEur = rawBalance * uiState.eurRate

    val mainBalance = when (uiState.selectedCurrency) {
        "USD" -> formatMoney(balanceUsd, "USD")
        "EUR" -> formatMoney(balanceEur, "EUR")
        else -> formatMoney(balanceRub, "RUB")
    }

    val secondaryBalance = when (uiState.selectedCurrency) {
        "USD" -> "(${formatMoney(balanceRub, "RUB")}, ${formatMoney(balanceEur, "EUR")})"
        "EUR" -> "(${formatMoney(balanceRub, "RUB")}, ${formatMoney(balanceUsd, "USD")})"
        else -> "(${formatMoney(balanceUsd, "USD")}, ${formatMoney(balanceEur, "EUR")})"
    }

    val formatter = remember {
        SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
    }

    val lastUpdateText = if (uiState.lastCurrencyUpdate == 0L) {
        "Ещё не обновлялось"
    } else {
        formatter.format(Date(uiState.lastCurrencyUpdate))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "Настройки",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Валюта счета",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("RUB", "USD", "EUR").forEach { currency ->
                        AssistChip(
                            onClick = { viewModel.setCurrency(currency) },
                            label = { Text(currency) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text("Выбрано: ${uiState.selectedCurrency}")

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Ваш баланс: $mainBalance",
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = secondaryBalance,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Скрыть баланс")
                Switch(
                    checked = uiState.hideBalance,
                    onCheckedChange = { viewModel.setHideBalance(it) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Последнее обновление курса",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(lastUpdateText)

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = { viewModel.refreshRates() },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.isLoading
                ) {
                    Text(
                        if (uiState.isLoading) "Обновление..." else "Обновить курсы"

                    )
                }
                if (!uiState.isLoading && uiState.ratesText.isBlank()) {
                    Text("Нет данных по курсам")
                }
                if (uiState.isLoading) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Загрузка курсов...",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))

                if (uiState.ratesText.isNotBlank()) {
                    Text(uiState.ratesText)
                }

                uiState.errorText?.let { errorText ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = errorText,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { showClearTransactionsDialog = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Очистить историю транзакций")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { showClearSettingsDialog = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Сбросить настройки и кэш")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { showResetAllDialog = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Полный сброс")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onBackClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Назад")
        }
    }

    if (showClearTransactionsDialog) {
        AlertDialog(
            onDismissRequest = { showClearTransactionsDialog = false },
            title = { Text("Очистить историю?") },
            text = { Text("Будут удалены все транзакции, а баланс вернётся к начальному значению.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showClearTransactionsDialog = false
                        viewModel.clearTransactions()
                    }
                ) {
                    Text("Да")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showClearTransactionsDialog = false }
                ) {
                    Text("Нет")
                }
            }
        )
    }

    if (showClearSettingsDialog) {
        AlertDialog(
            onDismissRequest = { showClearSettingsDialog = false },
            title = { Text("Сбросить настройки?") },
            text = { Text("Будут сброшены валюта, скрытие баланса и сохранённые курсы.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showClearSettingsDialog = false
                        viewModel.clearSettings()
                    }
                ) {
                    Text("Да")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showClearSettingsDialog = false }
                ) {
                    Text("Нет")
                }
            }
        )
    }

    if (showResetAllDialog) {
        AlertDialog(
            onDismissRequest = { showResetAllDialog = false },
            title = { Text("Полный сброс?") },
            text = { Text("Будут удалены транзакции, сброшены настройки и очищен кэш курсов.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showResetAllDialog = false
                        viewModel.resetAll()
                    }
                ) {
                    Text("Да")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showResetAllDialog = false }
                ) {
                    Text("Нет")
                }
            }
        )
    }
}