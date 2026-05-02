package com.example.endlessmoney.domain.repository

import com.example.endlessmoney.domain.model.Account
import com.example.endlessmoney.domain.model.Category
import com.example.endlessmoney.domain.model.Transaction
import com.example.endlessmoney.domain.model.TransactionType
import kotlinx.coroutines.flow.Flow

interface FinanceRepository {
    fun getAccount(): Flow<Account?>
    fun getRecentTransactions(): Flow<List<Transaction>>
    fun getAllTransactions(): Flow<List<Transaction>>
    fun getAllCategories(): Flow<List<Category>>

    suspend fun addTransaction(
        amount: Double,
        categoryId: Long,
        userId: String?,
        comment: String,
        type: TransactionType
    )
    suspend fun clearTransactions()
    suspend fun clearSettings()
    suspend fun resetAllData()
}