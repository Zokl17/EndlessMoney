package com.example.endlessmoney.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.endlessmoney.data.local.entity.AccountEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountDao {

    @Query("SELECT * FROM accounts LIMIT 1")
    fun getAccount(): Flow<AccountEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(account: AccountEntity)

    @Query("DELETE FROM accounts")
    suspend fun deleteAllAccounts()
}