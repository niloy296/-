package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE type LIKE 'WITHDRAW%' ORDER BY timestamp DESC")
    fun getWithdrawalTransactions(): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE type NOT LIKE 'WITHDRAW%' ORDER BY timestamp DESC LIMIT 20")
    fun getRecentEarnings(): Flow<List<Transaction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: Transaction): Long

    @Update
    suspend fun updateTransaction(transaction: Transaction)

    @Query("UPDATE transactions SET status = 'APPROVED' WHERE id = :id")
    suspend fun approveWithdrawal(id: Long)
}
