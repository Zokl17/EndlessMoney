package com.example.endlessmoney.domain.usecase

import com.example.endlessmoney.domain.model.TransactionType
import com.example.endlessmoney.domain.repository.FinanceRepository

class AddTransactionUseCase(
    private val repository: FinanceRepository
) {
    suspend operator fun invoke(
        amount: Double,
        categoryId: Long,
        userId: String?,
        comment: String,
        type: TransactionType
    ) {
        repository.addTransaction(
            amount = amount,
            categoryId = categoryId,
            userId = userId,
            comment = comment,
            type = type
        )
    }
}