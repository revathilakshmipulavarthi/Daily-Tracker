package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.TransactionEntity
import com.example.logic.CurrencyFormatter
import com.example.model.DayLimitCalculation
import com.example.ui.theme.ExcessLimitRedFill
import com.example.ui.theme.ExcessLimitRedStroke
import com.example.ui.theme.GlassBorderGradient
import com.example.ui.theme.GlassCardBackground
import com.example.ui.theme.GlassCardBorder
import com.example.ui.theme.GlassPillBackground
import com.example.ui.theme.GlassSurfaceElevated
import com.example.ui.theme.RemainingDayAmountColor
import com.example.ui.theme.RemainingDayGrayFill
import com.example.ui.theme.RemainingDayGrayStroke
import com.example.ui.theme.RemainingDayNumberColor
import com.example.ui.theme.WithinLimitGreenFill
import com.example.ui.theme.WithinLimitGreenStroke
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class DayCalendarBudgetInfo(
    val day: Int,
    val limitForDay: Double,
    val spentForDay: Double,
    val remainingBalance: Double,
    val isOverLimit: Boolean
)

@Composable
fun MonthDaysCalendarCard(
    currentDate: LocalDate,
    calculation: DayLimitCalculation,
    monthTransactions: List<TransactionEntity>,
    modifier: Modifier = Modifier
) {
    val currentDay = currentDate.dayOfMonth
    val totalDaysInMonth = calculation.totalDaysInMonth
    val firstDayOfMonth = currentDate.withDayOfMonth(1)
    val firstDayOfWeekIndex = (firstDayOfMonth.dayOfWeek.value % 7) // Sunday=0, Monday=1, ...

    val monthName = currentDate.format(DateTimeFormatter.ofPattern("MMMM yyyy"))
    val daysRemaining = calculation.daysRemainingIncludingToday

    var selectedDayDetail by remember { mutableStateOf<Int?>(null) }

    // Dynamically calculate the daily limit and remaining balance for each specific day
    val dailyBudgetMap = remember(monthTransactions, calculation, totalDaysInMonth, currentDay) {
        val map = mutableMapOf<Int, DayCalendarBudgetInfo>()
        val dayTxs = monthTransactions.groupBy { it.dayOfMonth }

        var runningAvailableBudget = calculation.monthlyBudget

        // 1. Sequentially calculate past days (1 until currentDay)
        for (d in 1 until currentDay) {
            val daysRemainingFromThatDay = (totalDaysInMonth - d + 1).coerceAtLeast(1)
            val limitForD = if (runningAvailableBudget <= 0.0) 0.0 else runningAvailableBudget / daysRemainingFromThatDay
            val spentForD = dayTxs[d]?.sumOf { it.amount } ?: 0.0
            val remainingBal = limitForD - spentForD
            val isOver = remainingBal < -0.01

            map[d] = DayCalendarBudgetInfo(
                day = d,
                limitForDay = limitForD,
                spentForDay = spentForD,
                remainingBalance = remainingBal,
                isOverLimit = isOver
            )
            runningAvailableBudget -= spentForD
        }

        // 2. For today (currentDay)
        val todayLimit = calculation.dailyLimitToday
        val todaySpent = calculation.spentToday
        val todayRemaining = calculation.remainingToday
        map[currentDay] = DayCalendarBudgetInfo(
            day = currentDay,
            limitForDay = todayLimit,
            spentForDay = todaySpent,
            remainingBalance = todayRemaining,
            isOverLimit = calculation.isOverToday
        )

        // 3. For future remaining days (currentDay + 1 .. totalDaysInMonth)
        val futureDaysCount = (totalDaysInMonth - currentDay).coerceAtLeast(1)
        val futureDayLimit = if (calculation.remainingMonthBudget <= 0.0) 0.0 else calculation.remainingMonthBudget / futureDaysCount

        for (d in (currentDay + 1)..totalDaysInMonth) {
            val futureSpent = dayTxs[d]?.sumOf { it.amount } ?: 0.0
            val futureRemaining = futureDayLimit - futureSpent
            map[d] = DayCalendarBudgetInfo(
                day = d,
                limitForDay = futureDayLimit,
                spentForDay = futureSpent,
                remainingBalance = futureRemaining,
                isOverLimit = futureRemaining < -0.01
            )
        }

        map
    }

    // Glassmorphic Card Container
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("month_days_calendar_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = GlassCardBackground),
        border = BorderStroke(1.dp, GlassBorderGradient),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(GlassSurfaceElevated)
                            .border(1.dp, GlassCardBorder, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = "Calendar",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = monthName,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = (-0.2).sp
                            ),
                            color = Color.White
                        )
                        Text(
                            text = "Daily Spends & Limit Status",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF9CA3AF)
                        )
                    }
                }

                // Remaining days counter glass pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(GlassPillBackground)
                        .border(1.dp, GlassCardBorder, RoundedCornerShape(20.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "$daysRemaining days left",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Day of Week Headers (S, M, T, W, T, F, S) in cool silver gray
            val dayHeaders = listOf("S", "M", "T", "W", "T", "F", "S")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                dayHeaders.forEach { header ->
                    Text(
                        text = header,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFF9CA3AF)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Calendar Days Grid
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                var dayCounter = 1
                for (row in 0 until 6) {
                    if (dayCounter > totalDaysInMonth) break

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        for (col in 0 until 7) {
                            val cellIndex = row * 7 + col

                            if (cellIndex < firstDayOfWeekIndex || dayCounter > totalDaysInMonth) {
                                // Empty spacer cell
                                Spacer(modifier = Modifier.weight(1f).aspectRatio(0.82f))
                            } else {
                                val thisDay = dayCounter
                                val isCompleted = thisDay < currentDay
                                val isToday = thisDay == currentDay
                                val isRemainingFuture = thisDay > currentDay

                                val dayInfo = dailyBudgetMap[thisDay]
                                val limitForDay = dayInfo?.limitForDay ?: 0.0
                                val remainingBal = dayInfo?.remainingBalance ?: 0.0
                                val isOverLimit = dayInfo?.isOverLimit ?: false

                                val balanceDisplay = when {
                                    isToday -> {
                                        CurrencyFormatter.formatCompact(calculation.spentToday)
                                    }
                                    isCompleted -> {
                                        CurrencyFormatter.formatCompact(dayInfo?.spentForDay ?: 0.0)
                                    }
                                    else -> {
                                        CurrencyFormatter.formatCompact(limitForDay)
                                    }
                                }

                                // User requirement:
                                // "completed days only shows green or red remaining days are in dark gray color in the calendar."
                                val isExcessCompleted = isCompleted && (remainingBal < -0.01)
                                val isExcessToday = isToday && calculation.isOverToday

                                val strokeColor = when {
                                    isToday -> if (isExcessToday) ExcessLimitRedStroke else WithinLimitGreenStroke
                                    isCompleted -> if (isExcessCompleted) ExcessLimitRedStroke else WithinLimitGreenStroke
                                    else -> RemainingDayGrayStroke
                                }

                                val strokeWidth = when {
                                    isToday -> 2.dp
                                    isCompleted -> 1.5.dp
                                    else -> 1.dp
                                }

                                val cellBackground = when {
                                    isToday -> if (isExcessToday) ExcessLimitRedFill else WithinLimitGreenFill
                                    isCompleted -> if (isExcessCompleted) ExcessLimitRedFill else WithinLimitGreenFill
                                    else -> RemainingDayGrayFill
                                }

                                val dayNumberColor = when {
                                    isToday -> Color.White
                                    isCompleted -> Color.White
                                    else -> RemainingDayNumberColor
                                }

                                val amountColor = when {
                                    isToday -> if (isExcessToday) ExcessLimitRedStroke else WithinLimitGreenStroke
                                    isCompleted -> if (isExcessCompleted) ExcessLimitRedStroke else WithinLimitGreenStroke
                                    else -> RemainingDayAmountColor
                                }

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(0.82f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(cellBackground)
                                        .border(strokeWidth, strokeColor, RoundedCornerShape(12.dp))
                                        .clickable { selectedDayDetail = thisDay }
                                        .testTag("cal_day_$thisDay"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center,
                                        modifier = Modifier.padding(vertical = 4.dp, horizontal = 1.dp)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            Text(
                                                text = "$thisDay",
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    fontWeight = if (isToday) FontWeight.ExtraBold else FontWeight.SemiBold,
                                                    fontSize = 12.5.sp
                                                ),
                                                color = dayNumberColor
                                            )
                                            if (isToday) {
                                                Spacer(modifier = Modifier.width(2.dp))
                                                Box(
                                                    modifier = Modifier
                                                        .size(5.dp)
                                                        .clip(CircleShape)
                                                        .background(if (isExcessToday) ExcessLimitRedStroke else WithinLimitGreenStroke)
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(2.dp))

                                        Text(
                                            text = balanceDisplay,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 9.sp
                                            ),
                                            color = amountColor,
                                            maxLines = 1
                                        )
                                    }
                                }

                                dayCounter++
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Tap any date to inspect daily limit & balance breakdown",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF9CA3AF),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
    }

    // Glassmorphism Day Detail Dialog
    selectedDayDetail?.let { day ->
        val isCompleted = day < currentDay
        val isToday = day == currentDay

        val dayInfo = dailyBudgetMap[day]
        val limitForDay = dayInfo?.limitForDay ?: 0.0
        val spentForDay = dayInfo?.spentForDay ?: 0.0
        val remainingBal = dayInfo?.remainingBalance ?: 0.0
        val isExcess = if (isToday) calculation.isOverToday else remainingBal < -0.01

        val formattedDate = currentDate.withDayOfMonth(day).format(DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy"))

        AlertDialog(
            onDismissRequest = { selectedDayDetail = null },
            containerColor = Color(0xFF14141C),
            shape = RoundedCornerShape(24.dp),
            title = {
                Text(
                    text = when {
                        isToday -> "Today's Limit & Balance"
                        isCompleted -> "Day $day Spend Breakdown"
                        else -> "Day $day Spending Limit"
                    },
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = formattedDate,
                        style = MaterialTheme.typography.labelMedium,
                        color = Color(0xFF9CA3AF)
                    )

                    // Daily Limit row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Daily Limit:", style = MaterialTheme.typography.bodyMedium, color = Color(0xFFD1D5DB))
                        Text(
                            CurrencyFormatter.format(limitForDay),
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                    }

                    // Spent row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Amount Spent:", style = MaterialTheme.typography.bodyMedium, color = Color(0xFFD1D5DB))
                        Text(
                            CurrencyFormatter.format(spentForDay),
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                    }

                    // Remaining Balance / Outcome row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = when {
                                isToday -> "Remaining Today:"
                                isCompleted -> if (remainingBal >= 0) "Saved & Rolled Over:" else "Overspent & Rebalanced:"
                                else -> "Available to Spend:"
                            },
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                        Text(
                            text = when {
                                isToday -> CurrencyFormatter.format(remainingBal)
                                isCompleted -> if (remainingBal >= 0) "+${CurrencyFormatter.format(remainingBal)}" else "-${CurrencyFormatter.format(-remainingBal)}"
                                else -> CurrencyFormatter.format(remainingBal)
                            },
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = when {
                                isToday -> if (calculation.isOverToday) ExcessLimitRedStroke else WithinLimitGreenStroke
                                isCompleted -> if (remainingBal < -0.01) ExcessLimitRedStroke else WithinLimitGreenStroke
                                else -> Color(0xFFD1D5DB)
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = when {
                            isToday -> "Dynamic limit recalculates on every new transaction."
                            isCompleted -> if (remainingBal >= 0)
                                "The savings of ${CurrencyFormatter.format(remainingBal)} were equally distributed to boost subsequent daily limits."
                            else
                                "The overspend of ${CurrencyFormatter.format(-remainingBal)} was spread equally across subsequent days."
                            else -> "Projected daily budget based on dynamic rebalancing from previous days."
                        },
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF9CA3AF)
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { selectedDayDetail = null }) {
                    Text("Close", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}
