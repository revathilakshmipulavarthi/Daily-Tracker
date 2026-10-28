package com.example.model

/**
 * Encapsulates the complete result of the dynamic daily spend and rebalancing calculation.
 */
data class DayLimitCalculation(
    val monthlyBudget: Double,
    val totalDaysInMonth: Int,
    val currentDayOfMonth: Int,
    val daysRemainingIncludingToday: Int,
    val totalSpentBeforeToday: Double,
    val spentToday: Double,
    val dailyLimitToday: Double,
    val remainingToday: Double,
    val isOverToday: Boolean,
    val overspendTodayAmount: Double,
    val totalSpentMonth: Double,
    val remainingMonthBudget: Double,
    val isOverMonthBudget: Boolean,
    val overMonthBudgetAmount: Double,
    val projectedTomorrowLimit: Double,
    val percentTodayUsed: Float,
    val baselineDailyBudget: Double
)
