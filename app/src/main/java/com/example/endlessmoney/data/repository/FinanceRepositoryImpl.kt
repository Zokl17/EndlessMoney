package com.example.endlessmoney.data.repository

import com.example.endlessmoney.data.local.dao.AccountDao
import com.example.endlessmoney.data.local.dao.CategoryDao
import com.example.endlessmoney.data.local.dao.TransactionDao
import com.example.endlessmoney.data.local.entity.AccountEntity
import com.example.endlessmoney.data.local.entity.TransactionEntity
import com.example.endlessmoney.data.mapper.toDomain
import com.example.endlessmoney.domain.model.Account
import com.example.endlessmoney.domain.model.Category
import com.example.endlessmoney.domain.model.Transaction
import com.example.endlessmoney.domain.model.TransactionType
import com.example.endlessmoney.domain.repository.FinanceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import com.example.endlessmoney.data.datastore.SettingsDataStore

class FinanceRepositoryImpl(
    private val accountDao: AccountDao,
    private val transactionDao: TransactionDao,
    private val categoryDao: CategoryDao,
    private val settingsDataStore: SettingsDataStore
) : FinanceRepository {

    override fun getAccount(): Flow<Account?> {
        return accountDao.getAccount().map { it?.toDomain() }
    }

    override fun getRecentTransactions(): Flow<List<Transaction>> {
        return transactionDao.getRecentTransactions().map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun getAllCategories(): Flow<List<Category>> {
        return categoryDao.getAllCategories().map { list ->
            list.map { it.toDomain() }
        }
    }
    override fun getAllTransactions(): Flow<List<Transaction>> {
        return transactionDao.getAllTransactions().map { list ->
            list.map { it.toDomain() }
        }
    }
    override suspend fun clearTransactions() {
        transactionDao.deleteAllTransactions()

        accountDao.insert(
            AccountEntity(
                id = 1L,
                name = "Основной",
                balance = 10000.0
            )
        )
    }

    override suspend fun clearSettings() {
        settingsDataStore.clearAll()
    }

    override suspend fun resetAllData() {
        transactionDao.deleteAllTransactions()
        accountDao.deleteAllAccounts()

        settingsDataStore.clearAll()

        accountDao.insert(
            AccountEntity(
                id = 1L,
                name = "Основной",
                balance = 10000.0
            )
        )
    }
    override suspend fun addTransaction(
        amount: Double,
        categoryId: Long,
        userId: String?,
        comment: String,
        type: TransactionType
    ) {
        val account = accountDao.getAccount().first()

        val newBalance = when (type) {
            TransactionType.INCOME -> (account?.balance ?: 0.0) + amount
            TransactionType.EXPENSE, TransactionType.PAYMENT -> (account?.balance ?: 0.0) - amount
        }

        accountDao.insert(
            AccountEntity(
                id = account?.id ?: 1L,
                name = account?.name ?: "Основной",
                balance = newBalance
            )
        )

        transactionDao.insert(
            TransactionEntity(
                amount = amount,
                categoryId = categoryId,
                userId = userId,
                date = System.currentTimeMillis(),
                comment = comment,
                type = type
            )
        )
    }
}