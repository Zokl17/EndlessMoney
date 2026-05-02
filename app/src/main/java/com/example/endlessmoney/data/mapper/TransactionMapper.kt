package com.example.endlessmoney.data.mapper

import com.example.endlessmoney.data.local.entity.TransactionEntity
import com.example.endlessmoney.domain.model.Transaction

fun TransactionEntity.toDomain(): Transaction {
    return Transaction(
        id = id,
        amount = amount,
        categoryId = categoryId,
        userId = userId,
        date = date,
        comment = comment,
        type = type
    )
}