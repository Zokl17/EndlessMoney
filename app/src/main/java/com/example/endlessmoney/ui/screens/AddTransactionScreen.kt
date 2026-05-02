package com.example.endlessmoney.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.endlessmoney.domain.model.Category
import com.example.endlessmoney.domain.model.TransactionType
import com.example.endlessmoney.domain.usecase.GetAllCategoriesUseCase
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import androidx.compose.runtime.saveable.rememberSaveable
@Composable
fun AddTransactionScreen(
    onBackClick: () -> Unit,
    viewModel: HomeViewModel = koinViewModel(),
    getAllCategoriesUseCase: GetAllCategoriesUseCase = koinInject()
) {
    var userId by rememberSaveable { mutableStateOf("") }
    var amountText by rememberSaveable { mutableStateOf("") }
    var comment by rememberSaveable { mutableStateOf("") }
    var expanded by rememberSaveable { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    var selectedType by rememberSaveable { mutableStateOf(TransactionType.EXPENSE) }
    var errorText by rememberSaveable { mutableStateOf<String?>(null) }

    val categories by getAllCategoriesUseCase().collectAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "Добавление транзакции",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Тип операции",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row {
            RadioButton(
                selected = selectedType == TransactionType.EXPENSE,
                onClick = {
                    selectedType = TransactionType.EXPENSE
                    errorText = null
                }
            )
            Text(
                text = "Перевод",
                modifier = Modifier.padding(top = 12.dp, end = 16.dp)
            )

            RadioButton(
                selected = selectedType == TransactionType.INCOME,
                onClick = {
                    selectedType = TransactionType.INCOME
                    errorText = null
                }
            )
            Text(
                text = "Получение",
                modifier = Modifier.padding(top = 12.dp, end = 16.dp)
            )

            RadioButton(
                selected = selectedType == TransactionType.PAYMENT,
                onClick = {
                    selectedType = TransactionType.PAYMENT
                    errorText = null
                }
            )
            Text(
                text = "Оплата",
                modifier = Modifier.padding(top = 12.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (selectedType != TransactionType.PAYMENT) {
            OutlinedTextField(
                value = userId,
                onValueChange = {
                    userId = it
                    errorText = null
                },
                label = { Text("ID пользователя") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = errorText != null && userId.isBlank()
            )

            Spacer(modifier = Modifier.height(12.dp))
        }

        OutlinedTextField(
            value = amountText,
            onValueChange = {
                amountText = it
                errorText = null
            },
            label = { Text("Сумма") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = errorText != null && amountText.isBlank()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = selectedCategory?.let { "${it.icon} ${it.name}" } ?: "Выбрать категорию"
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            categories.forEach { category ->
                DropdownMenuItem(
                    text = { Text("${category.icon} ${category.name}") },
                    onClick = {
                        selectedCategory = category
                        expanded = false
                        errorText = null
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = comment,
            onValueChange = {
                comment = it
                errorText = null
            },
            label = { Text("Комментарий") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2,
            maxLines = 4
        )

        if (errorText != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = errorText!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
        val isFormValid =
            amountText.isNotBlank() &&
                    amountText.toDoubleOrNull() != null &&
                    selectedCategory != null &&
                    (selectedType == TransactionType.PAYMENT || userId.isNotBlank())
        Button(
            onClick = {
                val amountValue = amountText.toDoubleOrNull()

                when {
                    amountText.isBlank() -> {
                        errorText = "Введите сумму."
                        return@Button
                    }

                    amountValue == null || amountValue <= 0.0 -> {
                        errorText = "Сумма должна быть числом больше нуля."
                        return@Button
                    }

                    selectedCategory == null -> {
                        errorText = "Выберите категорию."
                        return@Button
                    }

                    selectedType != TransactionType.PAYMENT && userId.isBlank() -> {
                        errorText = "Введите ID пользователя."
                        return@Button
                    }
                }

                val finalUserId = if (selectedType == TransactionType.PAYMENT) {
                    null
                } else {
                    userId.ifBlank { null }
                }

                val finalComment = if (comment.isBlank()) {
                    when (selectedType) {
                        TransactionType.EXPENSE -> "Перевод пользователю"
                        TransactionType.INCOME -> "Получение перевода"
                        TransactionType.PAYMENT -> "Оплата по категории ${selectedCategory?.name ?: ""}"
                    }
                } else {
                    comment
                }

                viewModel.addTransaction(
                    amount = amountValue,
                    categoryId = selectedCategory!!.id,
                    userId = finalUserId,
                    comment = finalComment,
                    type = selectedType
                )

                userId = ""
                amountText = ""
                comment = ""
                selectedCategory = null
                selectedType = TransactionType.EXPENSE
                errorText = null

                onBackClick()
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = isFormValid
        ) {
            Text("Сохранить")
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