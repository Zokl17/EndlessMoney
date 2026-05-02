package com.example.endlessmoney.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.endlessmoney.data.local.dao.AccountDao
import com.example.endlessmoney.data.local.dao.CategoryDao
import com.example.endlessmoney.data.local.dao.TransactionDao
import com.example.endlessmoney.data.local.entity.AccountEntity
import com.example.endlessmoney.data.local.entity.CategoryEntity
import com.example.endlessmoney.data.local.entity.TransactionEntity

@Database(
    entities = [
        TransactionEntity::class,
        CategoryEntity::class,
        AccountEntity::class
    ],
    version = 7,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun categoryDao(): CategoryDao
    abstract fun accountDao(): AccountDao
}