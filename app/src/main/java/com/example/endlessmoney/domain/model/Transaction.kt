package com.example.endlessmoney.domain.model

data class Transaction(
    val id: Long,
    val amount: Double,
    val categoryId: Long,
    val userId: String?,
    val date: Long,
    val comment: String,
    val type: TransactionType
)