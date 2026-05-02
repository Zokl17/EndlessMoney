package com.example.endlessmoney.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
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
fun TransactionsScreen(
    onBackClick: () -> Unit,
    viewModel: TransactionsViewModel = koinViewModel(),
    settingsViewModel: SettingsViewModel = koinViewModel()
) {
    val allTransactions by viewModel.transactions.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val selectedCategoryId by viewModel.selectedCategoryId.collectAsStateWithLifecycle()
    val selectedType by viewModel.selectedType.collectAsStateWithLifecycle()
    val selectedDateFilter by viewModel.selectedDateFilter.collectAsStateWithLifecycle()
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

    val now = System.currentTimeMillis()
    val oneDay = 24 * 60 * 60 * 1000L
    val oneWeek = 7 * oneDay
    val oneMonth = 30 * oneDay

    val transactions = allTransactions.filter { transaction ->
        val categoryMatches =
            selectedCategoryId == null || transaction.categoryId == selectedCategoryId

        val typeMatches =
            selectedType == null || transaction.type == selectedType

        val dateMatches = when (selectedDateFilter) {
            DateFilter.ALL -> true
            DateFilter.TODAY -> now - transaction.date <= oneDay
            DateFilter.WEEK -> now - transaction.date <= oneWeek
            DateFilter.MONTH -> now - transaction.date <= oneMonth
        }

        categoryMatches && typeMatches && dateMatches
    }
    val incomeTotalRaw = transactions
        .filter { it.type == TransactionType.INCOME }
        .sumOf { it.amount }

    val expenseTotalRaw = transactions
        .filter { it.type == TransactionType.EXPENSE || it.type == TransactionType.PAYMENT }
        .sumOf { it.amount }

    val totalRaw = incomeTotalRaw - expenseTotalRaw

    fun convertAmount(amount: Double): Double {
        return when (settingsState.selectedCurrency) {
            "USD" -> amount * settingsState.usdRate
            "EUR" -> amount * settingsState.eurRate
            else -> amount
        }
    }
    val incomeTotal = convertAmount(incomeTotalRaw)
    val expenseTotal = convertAmount(expenseTotalRaw)
    val total = convertAmount(totalRaw)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Все транзакции",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Фильтр по типу",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = selectedType == null,
                onClick = { viewModel.selectType(null) },
                label = { Text("Все") }
            )

            FilterChip(
                selected = selectedType == TransactionType.INCOME,
                onClick = { viewModel.selectType(TransactionType.INCOME) },
                label = { Text("Получение") }
            )

            FilterChip(
                selected = selectedType == TransactionType.EXPENSE,
                onClick = { viewModel.selectType(TransactionType.EXPENSE) },
                label = { Text("Перевод") }
            )

            FilterChip(
                selected = selectedType == TransactionType.PAYMENT,
                onClick = { viewModel.selectType(TransactionType.PAYMENT) },
                label = { Text("Оплата") }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Фильтр по категории",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = selectedCategoryId == null,
                onClick = { viewModel.selectCategory(null) },
                label = { Text("Все") }
            )

            categories.forEach { category ->
                FilterChip(
                    selected = selectedCategoryId == category.id,
                    onClick = { viewModel.selectCategory(category.id) },
                    label = { Text("${category.icon} ${category.name}") }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Фильтр по дате",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = selectedDateFilter == DateFilter.ALL,
                onClick = { viewModel.selectDateFilter(DateFilter.ALL) },
                label = { Text("Все") }
            )

            FilterChip(
                selected = selectedDateFilter == DateFilter.TODAY,
                onClick = { viewModel.selectDateFilter(DateFilter.TODAY) },
                label = { Text("Сегодня") }
            )

            FilterChip(
                selected = selectedDateFilter == DateFilter.WEEK,
                onClick = { viewModel.selectDateFilter(DateFilter.WEEK) },
                label = { Text("7 дней") }
            )

            FilterChip(
                selected = selectedDateFilter == DateFilter.MONTH,
                onClick = { viewModel.selectDateFilter(DateFilter.MONTH) },
                label = { Text("30 дней") }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "Сводка",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Доход: ${formatMoney(incomeTotal, settingsState.selectedCurrency)}",
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Расход: ${formatMoney(expenseTotal, settingsState.selectedCurrency)}",
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Итого: ${formatMoney(total, settingsState.selectedCurrency)}",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Найдено: ${transactions.size}",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(transactions, key = { it.id }) { transaction ->
                val title = when (transaction.type) {
                    TransactionType.EXPENSE -> "Перевод пользователю"
                    TransactionType.INCOME -> "Получение перевода"
                    TransactionType.PAYMENT -> "Оплата по категории"
                }

                val convertedAmount = when (settingsState.selectedCurrency) {
                    "USD" -> transaction.amount * settingsState.usdRate
                    "EUR" -> transaction.amount * settingsState.eurRate
                    else -> transaction.amount
                }

                val categoryName = categories.find { it.id == transaction.categoryId }?.name ?: "Без категории"

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.bodyLarge
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Категория: $categoryName",
                            style = MaterialTheme.typography.bodySmall
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
                            text = formatMoney(convertedAmount, settingsState.selectedCurrency),
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

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = onBackClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Назад")
        }
    }
}