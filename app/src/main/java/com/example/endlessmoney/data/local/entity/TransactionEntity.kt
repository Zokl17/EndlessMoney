package com.example.endlessmoney.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.endlessmoney.domain.model.TransactionType

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val amount: Double,
    val categoryId: Long,
    val userId: String?,
    val date: Long,
    val comment: String,
    val type: TransactionType
)