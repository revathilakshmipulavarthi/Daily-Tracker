package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE yearMonth = :yearMonth ORDER BY timestamp DESC")
    fun getTransactionsForMonth(yearMonth: String): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE yearMonth = :yearMonth AND dayOfMonth = :dayOfMonth ORDER BY timestamp DESC")
    fun getTransactionsForDay(yearMonth: String, dayOfMonth: Int): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE timestamp >= :sinceTimestamp ORDER BY timestamp DESC")
    suspend fun getRecentTransactionsSince(sinceTimestamp: Long): List<TransactionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity): Long

    @Update
    suspend fun updateTransaction(transaction: TransactionEntity)

    @Delete
    suspend fun deleteTransaction(transaction: TransactionEntity)

    @Query("DELETE FROM transactions WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM transactions WHERE yearMonth = :yearMonth")
    suspend fun deleteTransactionsForMonth(yearMonth: String)

    @Query("DELETE FROM transactions")
    suspend fun deleteAllTransactions()
}
