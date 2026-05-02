package com.example.endlessmoney.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.endlessmoney.domain.model.TransactionType
import org.koin.androidx.compose.koinViewModel
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    onAddExpenseClick: () -> Unit,
    onOpenTransactionsClick: () -> Unit,
    onOpenSettingsClick: () -> Unit,
    viewModel: HomeViewModel = koinViewModel(),
    settingsViewModel: SettingsViewModel = koinViewModel()
) {
    val account by viewModel.account.collectAsStateWithLifecycle()
    val transactions by viewModel.transactions.collectAsStateWithLifecycle()
    val settingsState by settingsViewModel.uiState.collectAsStateWithLifecycle()

    val dateFormatter = remember {
        SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
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

    fun convertAmount(amount: Double): Double {
        return when (settingsState.selectedCurrency) {
            "USD" -> amount * settingsState.usdRate
            "EUR" -> amount * settingsState.eurRate
            else -> amount
        }
    }

    val rawBalance = account?.balance ?: 0.0

    val convertedBalance = convertAmount(rawBalance)

    val secondaryBalanceText = when (settingsState.selectedCurrency) {
        "RUB" -> {
            val usd = rawBalance * settingsState.usdRate
            val eur = rawBalance * settingsState.eurRate
            "~ ${formatMoney(usd, "USD")} / ${formatMoney(eur, "EUR")}"
        }
        "USD", "EUR" -> {
            "~ ${formatMoney(rawBalance, "RUB")}"
        }
        else -> ""
    }

    val incomeTotalRaw = transactions
        .filter { it.type == TransactionType.INCOME }
        .sumOf { it.amount }

    val expenseTotalRaw = transactions
        .filter { it.type == TransactionType.EXPENSE || it.type == TransactionType.PAYMENT }
        .sumOf { it.amount }

    val incomeTotal = convertAmount(incomeTotalRaw)
    val expenseTotal = convertAmount(expenseTotalRaw)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Общий баланс",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = if (settingsState.hideBalance) {
                        "••••••"
                    } else {
                        formatMoney(convertedBalance, settingsState.selectedCurrency)
                    },
                    style = MaterialTheme.typography.headlineMedium
                )

                if (!settingsState.hideBalance) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = secondaryBalanceText,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Card(
                modifier = Modifier.weight(1f),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "Доходы",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (settingsState.hideBalance) "••••••" else formatMoney(
                            incomeTotal,
                            settingsState.selectedCurrency
                        ),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            Card(
                modifier = Modifier.weight(1f),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "Расходы",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (settingsState.hideBalance) "••••••" else formatMoney(
                            expenseTotal,
                            settingsState.selectedCurrency
                        ),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = onAddExpenseClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Добавить транзакцию")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onOpenTransactionsClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Все транзакции")
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = onOpenSettingsClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Настройки")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Последние транзакции",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (transactions.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Пока нет транзакций",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Добавь первую операцию, чтобы увидеть историю.",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(transactions, key = { it.id }) { transaction ->
                    val title = when (transaction.type) {
                        TransactionType.EXPENSE -> "Перевод пользователю"
                        TransactionType.INCOME -> "Получение перевода"
                        TransactionType.PAYMENT -> "Оплата по категории"
                    }

                    val convertedTransactionAmount = convertAmount(transaction.amount)

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.bodyLarge
                            )

                            if (!transaction.userId.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "ID пользователя: ${transaction.userId}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = formatMoney(
                                    convertedTransactionAmount,
                                    settingsState.selectedCurrency
                                ),
                                style = MaterialTheme.typography.titleMedium
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = dateFormatter.format(Date(transaction.date)),
                                style = MaterialTheme.typography.bodySmall
                            )

                            if (transaction.comment.isNotBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = transaction.comment,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}