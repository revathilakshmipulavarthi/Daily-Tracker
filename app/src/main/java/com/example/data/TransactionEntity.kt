package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.model.Category
import com.example.model.UPIApp

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val amount: Double,
    val merchant: String,
    val upiApp: String = UPIApp.OTHER.name,
    val category: String = Category.OTHER.name,
    val timestamp: Long = System.currentTimeMillis(),
    val note: String? = null,
    val yearMonth: String, // e.g. "2026-10"
    val dayOfMonth: Int    // 1..31
) {
    fun getUpiAppEnum(): UPIApp = try {
        UPIApp.valueOf(upiApp)
    } catch (_: Exception) {
        UPIApp.OTHER
    }

    fun getCategoryEnum(): Category = try {
        Category.valueOf(category)
    } catch (_: Exception) {
        Category.OTHER
    }
}
