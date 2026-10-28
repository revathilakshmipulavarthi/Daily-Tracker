package com.example

import com.example.logic.BudgetCalculator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BudgetCalculatorTest {

    private val delta = 0.001

    @Test
    fun testNormalDay() {
        // ₹15,000 monthly budget, 30 days month, day 10.
        // Days remaining including day 10 = 30 - 10 + 1 = 21.
        // Spent before today = ₹4,500.
        // Budget available start of today = 15,000 - 4,500 = ₹10,500.
        // Daily limit today = 10,500 / 21 = ₹500.
        // Spent today = ₹300.
        // Remaining today = 500 - 300 = ₹200.
        val result = BudgetCalculator.calculate(
            monthlyBudget = 15000.0,
            totalSpentBeforeToday = 4500.0,
            spentToday = 300.0,
            currentDayOfMonth = 10,
            totalDaysInMonth = 30
        )

        assertEquals(500.0, result.dailyLimitToday, delta)
        assertEquals(200.0, result.remainingToday, delta)
        assertFalse(result.isOverToday)
        assertEquals(0.0, result.overspendTodayAmount, delta)
        assertEquals(4800.0, result.totalSpentMonth, delta)
        assertEquals(10200.0, result.remainingMonthBudget, delta)
        assertFalse(result.isOverMonthBudget)
        // Tomorrow remaining days = 20. Tomorrow projected limit = 10200 / 20 = 510.0
        assertEquals(510.0, result.projectedTomorrowLimit, delta)
    }

    @Test
    fun testOverspendDay() {
        // Daily limit is ₹500. User spends ₹800 (overspent by ₹300).
        // Tomorrow's limit must drop accordingly!
        val result = BudgetCalculator.calculate(
            monthlyBudget = 15000.0,
            totalSpentBeforeToday = 4500.0,
            spentToday = 800.0,
            currentDayOfMonth = 10,
            totalDaysInMonth = 30
        )

        assertEquals(500.0, result.dailyLimitToday, delta)
        assertEquals(-300.0, result.remainingToday, delta)
        assertTrue(result.isOverToday)
        assertEquals(300.0, result.overspendTodayAmount, delta)
        // Total spent month = 4500 + 800 = 5300.
        // Remaining in month = 15000 - 5300 = 9700.
        // 20 days remaining tomorrow -> 9700 / 20 = 485.0 (limit dropped from 500 to 485!)
        assertEquals(485.0, result.projectedTomorrowLimit, delta)
    }

    @Test
    fun testUnderspendDay() {
        // Daily limit is ₹500. User spends only ₹100 (saved ₹400).
        // Tomorrow's limit must rise!
        val result = BudgetCalculator.calculate(
            monthlyBudget = 15000.0,
            totalSpentBeforeToday = 4500.0,
            spentToday = 100.0,
            currentDayOfMonth = 10,
            totalDaysInMonth = 30
        )

        assertEquals(500.0, result.dailyLimitToday, delta)
        assertEquals(400.0, result.remainingToday, delta)
        assertFalse(result.isOverToday)
        // Total spent month = 4500 + 100 = 4600.
        // Remaining month = 15000 - 4600 = 10400.
        // 20 days remaining tomorrow -> 10400 / 20 = 520.0 (limit increased from 500 to 520!)
        assertEquals(520.0, result.projectedTomorrowLimit, delta)
    }

    @Test
    fun testLastDayOfMonth() {
        // Day 31 of 31.
        // Days remaining = 1.
        // Monthly budget = ₹31,000. Spent before today = ₹29,500.
        // Remaining for today = 31,000 - 29,500 = ₹1,500.
        val result = BudgetCalculator.calculate(
            monthlyBudget = 31000.0,
            totalSpentBeforeToday = 29500.0,
            spentToday = 1200.0,
            currentDayOfMonth = 31,
            totalDaysInMonth = 31
        )

        assertEquals(1, result.daysRemainingIncludingToday)
        assertEquals(1500.0, result.dailyLimitToday, delta)
        assertEquals(300.0, result.remainingToday, delta)
        assertFalse(result.isOverToday)
        assertEquals(0.0, result.projectedTomorrowLimit, delta) // No days left tomorrow
    }

    @Test
    fun testZeroRemainingBudget() {
        // Entire monthly budget has already been exhausted before today.
        // Daily limit must be ₹0, and today's spend must show over budget.
        val result = BudgetCalculator.calculate(
            monthlyBudget = 10000.0,
            totalSpentBeforeToday = 10200.0, // over budget by ₹200
            spentToday = 150.0,
            currentDayOfMonth = 15,
            totalDaysInMonth = 30
        )

        assertEquals(0.0, result.dailyLimitToday, delta)
        assertTrue(result.isOverToday)
        assertEquals(150.0, result.overspendTodayAmount, delta)
        assertTrue(result.isOverMonthBudget)
        assertEquals(350.0, result.overMonthBudgetAmount, delta) // 10350 - 10000
        assertEquals(0.0, result.projectedTomorrowLimit, delta)
    }

    @Test
    fun testLeapYearFebruary() {
        // 2024 is a leap year (29 days)
        val leapDays2024 = BudgetCalculator.getDaysInMonth(2024, 2)
        assertEquals(29, leapDays2024)
        assertTrue(BudgetCalculator.isLeapYear(2024))

        // 2025 is not a leap year (28 days)
        val normalDays2025 = BudgetCalculator.getDaysInMonth(2025, 2)
        assertEquals(28, normalDays2025)
        assertFalse(BudgetCalculator.isLeapYear(2025))

        // 2028 is a leap year (29 days)
        val leapDays2028 = BudgetCalculator.getDaysInMonth(2028, 2)
        assertEquals(29, leapDays2028)
        assertTrue(BudgetCalculator.isLeapYear(2028))

        // On day 1 of Feb in leap year with ₹29,000 budget:
        val leapResult = BudgetCalculator.calculate(
            monthlyBudget = 29000.0,
            totalSpentBeforeToday = 0.0,
            spentToday = 0.0,
            currentDayOfMonth = 1,
            totalDaysInMonth = leapDays2024
        )
        assertEquals(1000.0, leapResult.dailyLimitToday, delta)
        assertEquals(29, leapResult.daysRemainingIncludingToday)
    }
}
