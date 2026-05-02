package com.example.endlessmoney.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.endlessmoney.domain.model.Category
import com.example.endlessmoney.domain.model.Transaction
import com.example.endlessmoney.domain.model.TransactionType
import com.example.endlessmoney.domain.usecase.GetAllCategoriesUseCase
import com.example.endlessmoney.domain.usecase.GetAllTransactionsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class DateFilter {
    ALL,
    TODAY,
    WEEK,
    MONTH
}

class TransactionsViewModel(
    private val getAllTransactionsUseCase: GetAllTransactionsUseCase,
    private val getAllCategoriesUseCase: GetAllCategoriesUseCase
) : ViewModel() {

    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions.asStateFlow()

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    private val _selectedCategoryId = MutableStateFlow<Long?>(null)
    val selectedCategoryId: StateFlow<Long?> = _selectedCategoryId.asStateFlow()

    private val _selectedType = MutableStateFlow<TransactionType?>(null)
    val selectedType: StateFlow<TransactionType?> = _selectedType.asStateFlow()

    private val _selectedDateFilter = MutableStateFlow(DateFilter.ALL)
    val selectedDateFilter: StateFlow<DateFilter> = _selectedDateFilter.asStateFlow()

    init {
        viewModelScope.launch {
            getAllTransactionsUseCase().collect { list ->
                _transactions.value = list
            }
        }

        viewModelScope.launch {
            getAllCategoriesUseCase().collect { list ->
                _categories.value = list
            }
        }
    }

    fun selectCategory(categoryId: Long?) {
        _selectedCategoryId.value = categoryId
    }

    fun selectType(type: TransactionType?) {
        _selectedType.value = type
    }

    fun selectDateFilter(filter: DateFilter) {
        _selectedDateFilter.value = filter
    }

    fun filteredTransactions(): List<Transaction> {
        val now = System.currentTimeMillis()
        val oneDay = 24 * 60 * 60 * 1000L
        val oneWeek = 7 * oneDay
        val oneMonth = 30 * oneDay

        return _transactions.value.filter { transaction ->
            val categoryMatches =
                _selectedCategoryId.value == null || transaction.categoryId == _selectedCategoryId.value

            val typeMatches =
                _selectedType.value == null || transaction.type == _selectedType.value

            val dateMatches = when (_selectedDateFilter.value) {
                DateFilter.ALL -> true
                DateFilter.TODAY -> now - transaction.date <= oneDay
                DateFilter.WEEK -> now - transaction.date <= oneWeek
                DateFilter.MONTH -> now - transaction.date <= oneMonth
            }

            categoryMatches && typeMatches && dateMatches
        }
    }
}