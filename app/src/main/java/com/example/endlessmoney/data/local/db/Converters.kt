package com.example.endlessmoney.data.local.db

import androidx.room.TypeConverter
import com.example.endlessmoney.domain.model.TransactionType

class Converters {

    @TypeConverter
    fun fromTransactionType(type: TransactionType): String {
        return type.name
    }

    @TypeConverter
    fun toTransactionType(value: String): TransactionType {
        return TransactionType.valueOf(value)
    }
}