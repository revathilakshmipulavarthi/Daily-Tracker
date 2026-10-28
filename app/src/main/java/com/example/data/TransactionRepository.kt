package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlin.math.abs

class TransactionRepository(private val dao: TransactionDao) {

    val allTransactions: Flow<List<TransactionEntity>> = dao.getAllTransactions()

    fun getTransactionsForMonth(yearMonth: String): Flow<List<TransactionEntity>> {
        return dao.getTransactionsForMonth(yearMonth)
    }

    suspend fun insert(transaction: TransactionEntity): Long {
        return dao.insertTransaction(transaction)
    }

    suspend fun update(transaction: TransactionEntity) {
        dao.updateTransaction(transaction)
    }

    suspend fun delete(transaction: TransactionEntity) {
        dao.deleteTransaction(transaction)
    }

    suspend fun deleteById(id: Long) {
        dao.deleteById(id)
    }

    suspend fun checkLikelyDuplicate(amount: Double, withinMillis: Long = 120_000): TransactionEntity? {
        val sinceTime = System.currentTimeMillis() - withinMillis
        val recent = dao.getRecentTransactionsSince(sinceTime)
        return recent.firstOrNull { abs(it.amount - amount) < 0.01 }
    }

    suspend fun clearMonth(yearMonth: String) {
        dao.deleteTransactionsForMonth(yearMonth)
    }

    suspend fun clearAll() {
        dao.deleteAllTransactions()
    }
}
