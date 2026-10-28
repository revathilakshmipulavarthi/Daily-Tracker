package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.TransactionEntity
import com.example.logic.CurrencyFormatter
import com.example.model.DayLimitCalculation
import com.example.model.UPIApp
import com.example.ui.components.MonthDaysCalendarCard
import com.example.ui.components.UpiBadge
import com.example.ui.theme.GlassBorderGradient
import com.example.ui.theme.GlassCardBackground
import com.example.ui.theme.SpendGreen
import com.example.ui.theme.SpendRed
import com.example.viewmodel.DaySpendSummary
import java.time.LocalDate

@Composable
fun HistoryScreen(
    dailySummaries: List<DaySpendSummary>,
    currentDate: LocalDate,
    calculation: DayLimitCalculation,
    monthTransactions: List<TransactionEntity> = emptyList(),
    selectedUpiFilter: UPIApp?,
    onFilterChange: (UPIApp?) -> Unit,
    onSelectTransaction: (TransactionEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val expandedStates = remember { mutableStateMapOf<Int, Boolean>() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Title
        Text(
            text = "Spend History",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(10.dp))

        // UPI App Filters row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // "All" filter chip
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        if (selectedUpiFilter == null) Color.White.copy(alpha = 0.92f)
                        else Color(0xFF1E1E28).copy(alpha = 0.85f)
                    )
                    .clickable { onFilterChange(null) }
                    .padding(horizontal = 14.dp, vertical = 7.dp)
                    .testTag("filter_all")
            ) {
                Text(
                    text = "All Apps",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = if (selectedUpiFilter == null) Color.Black
                    else Color(0xFFB0B0BD)
                )
            }

            UPIApp.values().forEach { upi ->
                UpiBadge(
                    upiApp = upi,
                    isSelected = selectedUpiFilter == upi,
                    onClick = {
                        if (selectedUpiFilter == upi) onFilterChange(null)
                        else onFilterChange(upi)
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                MonthDaysCalendarCard(
                    currentDate = currentDate,
                    calculation = calculation,
                    monthTransactions = monthTransactions
                )
            }

            if (dailySummaries.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = GlassCardBackground),
                        border = BorderStroke(1.dp, GlassBorderGradient),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(28.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = null,
                                tint = Color(0xFF8E8E9B),
                                modifier = Modifier.size(44.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "No spend history recorded yet",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color(0xFFB8B8C6)
                            )
                        }
                    }
                }
            } else {
                items(dailySummaries, key = { it.dayOfMonth }) { summary ->
                    val isExpanded = expandedStates[summary.dayOfMonth] ?: false

                    DaySummaryCard(
                        summary = summary,
                        isExpanded = isExpanded,
                        onToggleExpand = {
                            expandedStates[summary.dayOfMonth] = !isExpanded
                        },
                        onSelectTransaction = onSelectTransaction
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
}

@Composable
fun DaySummaryCard(
    summary: DaySpendSummary,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    onSelectTransaction: (TransactionEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val statusColor = if (summary.isOverLimit) SpendRed else SpendGreen
    val statusLabel = if (summary.isOverLimit) {
        "Over Limit (+${CurrencyFormatter.format(summary.difference)})"
    } else {
        "Under Limit (Saved ${CurrencyFormatter.format(-summary.difference)})"
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onToggleExpand() }
            .testTag("day_summary_${summary.dayOfMonth}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = GlassCardBackground),
        border = BorderStroke(1.dp, GlassBorderGradient),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = summary.dateString,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = "Daily limit: ${CurrencyFormatter.format(summary.limitAtThatTime)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF9E9EA8)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = CurrencyFormatter.format(summary.totalSpent),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (summary.isOverLimit) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                                contentDescription = null,
                                tint = statusColor,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = statusLabel,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                color = statusColor
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = "Expand",
                        tint = Color(0xFF9E9EA8)
                    )
                }
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    HorizontalDivider(color = Color.White.copy(alpha = 0.08f))
                    Spacer(modifier = Modifier.height(10.dp))

                    if (summary.transactions.isEmpty()) {
                        Text(
                            text = "No transactions logged for this day.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF9E9EA8),
                            modifier = Modifier.padding(vertical = 6.dp)
                        )
                    } else {
                        for (tx in summary.transactions) {
                            TransactionItemCard(
                                transaction = tx,
                                onClick = { onSelectTransaction(tx) },
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
