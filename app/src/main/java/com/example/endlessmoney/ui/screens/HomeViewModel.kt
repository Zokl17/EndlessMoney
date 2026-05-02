package com.example.endlessmoney.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.endlessmoney.domain.model.Account
import com.example.endlessmoney.domain.model.Transaction
import com.example.endlessmoney.domain.model.TransactionType
import com.example.endlessmoney.domain.usecase.AddTransactionUseCase
import com.example.endlessmoney.domain.usecase.GetAccountUseCase
import com.example.endlessmoney.domain.usecase.GetRecentTransactionsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val getAccountUseCase: GetAccountUseCase,
    private val getRecentTransactionsUseCase: GetRecentTransactionsUseCase,
    private val addTransactionUseCase: AddTransactionUseCase
) : ViewModel() {

    private val _account = MutableStateFlow<Account?>(null)
    val account: StateFlow<Account?> = _account.asStateFlow()

    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions.asStateFlow()

    init {
        viewModelScope.launch {
            getAccountUseCase().collect { accountValue ->
                _account.value = accountValue
            }
        }

        viewModelScope.launch {
            getRecentTransactionsUseCase().collect { transactionsValue ->
                _transactions.value = transactionsValue
            }
        }
    }
    fun addTransaction(
        amount: Double,
        categoryId: Long,
        userId: String?,
        comment: String,
        type: TransactionType
    ) {
        viewModelScope.launch {
            addTransactionUseCase(
                amount = amount,
                categoryId = categoryId,
                userId = userId,
                comment = comment,
                type = type
            )
        }
    }

}