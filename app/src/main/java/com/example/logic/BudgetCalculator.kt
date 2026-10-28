package com.example.logic

import com.example.model.DayLimitCalculation
import java.time.YearMonth

/**
 * Pure functions for calculating dynamic daily spend limits and dynamic rebalancing.
 * Never performs I/O or mutates state.
 */
object BudgetCalculator {

    /**
     * Returns the exact number of days in the given year and month (1-12), correctly
     * handling leap years for February.
     */
    fun getDaysInMonth(year: Int, month: Int): Int {
        require(month in 1..12) { "Month must be between 1 and 12" }
        return YearMonth.of(year, month).lengthOfMonth()
    }

    /**
     * Checks if a given year is a leap year.
     */
    fun isLeapYear(year: Int): Boolean {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)
    }

    /**
     * Calculates the dynamic daily limit and month tracking metrics.
     *
     * @param monthlyBudget The allocated budget for the month in ₹.
     * @param totalSpentBeforeToday Sum of all transactions from day 1 up to yesterday.
     * @param spentToday Sum of all transactions recorded today.
     * @param currentDayOfMonth Current day index in month (1..totalDaysInMonth).
     * @param totalDaysInMonth Total days in current month (e.g., 28, 29, 30, 31).
     */
    fun calculate(
        monthlyBudget: Double,
        totalSpentBeforeToday: Double,
        spentToday: Double,
        currentDayOfMonth: Int,
        totalDaysInMonth: Int
    ): DayLimitCalculation {
        val safeDay = currentDayOfMonth.coerceIn(1, totalDaysInMonth)
        val daysRemainingIncludingToday = (totalDaysInMonth - safeDay + 1).coerceAtLeast(1)

        val baselineDailyBudget = if (totalDaysInMonth > 0) {
            monthlyBudget / totalDaysInMonth
        } else {
            0.0
        }

        // Available budget at the start of today
        val budgetAvailableAtStartOfToday = monthlyBudget - totalSpentBeforeToday

        // Dynamic daily limit today:
        // daily_limit_today = (monthly_budget - total_spent_before_today) / (days_remaining_in_month including today)
        // If remaining balance is <= 0, limit is ₹0
        val dailyLimitToday = if (budgetAvailableAtStartOfToday <= 0.0) {
            0.0
        } else {
            budgetAvailableAtStartOfToday / daysRemainingIncludingToday
        }

        // Remaining limit for today
        val remainingToday = dailyLimitToday - spentToday
        val isOverToday = remainingToday < 0.0
        val overspendTodayAmount = if (isOverToday) -remainingToday else 0.0

        // Total spent in month
        val totalSpentMonth = totalSpentBeforeToday + spentToday
        val remainingMonthBudget = monthlyBudget - totalSpentMonth
        val isOverMonthBudget = remainingMonthBudget < 0.0
        val overMonthBudgetAmount = if (isOverMonthBudget) -remainingMonthBudget else 0.0

        // Tomorrow's projected limit if no more money is spent today:
        // Shortfalls or savings are automatically rebalanced across the remaining (daysRemaining - 1) days.
        val daysRemainingTomorrow = daysRemainingIncludingToday - 1
        val projectedTomorrowLimit = when {
            daysRemainingTomorrow <= 0 -> 0.0
            remainingMonthBudget <= 0.0 -> 0.0
            else -> remainingMonthBudget / daysRemainingTomorrow
        }

        val percentTodayUsed = when {
            dailyLimitToday <= 0.0 -> if (spentToday > 0.0) 1.0f else 0.0f
            else -> (spentToday / dailyLimitToday).toFloat()
        }

        return DayLimitCalculation(
            monthlyBudget = monthlyBudget,
            totalDaysInMonth = totalDaysInMonth,
            currentDayOfMonth = safeDay,
            daysRemainingIncludingToday = daysRemainingIncludingToday,
            totalSpentBeforeToday = totalSpentBeforeToday,
            spentToday = spentToday,
            dailyLimitToday = dailyLimitToday,
            remainingToday = remainingToday,
            isOverToday = isOverToday,
            overspendTodayAmount = overspendTodayAmount,
            totalSpentMonth = totalSpentMonth,
            remainingMonthBudget = remainingMonthBudget,
            isOverMonthBudget = isOverMonthBudget,
            overMonthBudgetAmount = overMonthBudgetAmount,
            projectedTomorrowLimit = projectedTomorrowLimit,
            percentTodayUsed = percentTodayUsed,
            baselineDailyBudget = baselineDailyBudget
        )
    }

    /**
     * Rebalance calculation when user changes budget mid-month:
     * - "Apply from today": keeps previous spending intact, divides remaining new budget across remaining days.
     * - "Recalculate whole month": updates monthly total budget retroactively.
     */
    fun recalculateWithNewBudget(
        newMonthlyBudget: Double,
        applyFromTodayOnly: Boolean,
        totalSpentBeforeToday: Double,
        spentToday: Double,
        currentDayOfMonth: Int,
        totalDaysInMonth: Int
    ): DayLimitCalculation {
        return if (applyFromTodayOnly) {
            // New budget is treated as remaining budget for remaining days
            val daysRemaining = (totalDaysInMonth - currentDayOfMonth + 1).coerceAtLeast(1)
            val effectiveMonthlyBudget = totalSpentBeforeToday + newMonthlyBudget
            calculate(
                monthlyBudget = effectiveMonthlyBudget,
                totalSpentBeforeToday = totalSpentBeforeToday,
                spentToday = spentToday,
                currentDayOfMonth = currentDayOfMonth,
                totalDaysInMonth = totalDaysInMonth
            )
        } else {
            calculate(
                monthlyBudget = newMonthlyBudget,
                totalSpentBeforeToday = totalSpentBeforeToday,
                spentToday = spentToday,
                currentDayOfMonth = currentDayOfMonth,
                totalDaysInMonth = totalDaysInMonth
            )
        }
    }
}
