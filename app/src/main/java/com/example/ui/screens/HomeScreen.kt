package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.TransactionEntity
import com.example.data.UserSettings
import com.example.logic.CurrencyFormatter
import com.example.model.Category
import com.example.model.DayLimitCalculation
import com.example.model.UPIApp
import com.example.ui.components.AutoUpiSyncCard
import com.example.ui.components.CategoryBadge
import com.example.ui.components.CircularProgressRing
import com.example.ui.components.MonthDaysCalendarCard
import com.example.ui.components.UpiBadge
import com.example.ui.theme.ExcessLimitRedStroke
import com.example.ui.theme.GlassBorderGradient
import com.example.ui.theme.GlassCardBackground
import com.example.ui.theme.GlassCardBorder
import com.example.ui.theme.GlassCardBorderSubtle
import com.example.ui.theme.GlassPillBackground
import com.example.ui.theme.SpendAmber
import com.example.ui.theme.SpendOrange
import com.example.ui.theme.SpendRed
import com.example.ui.theme.WithinLimitGreenStroke
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun HomeScreen(
    calculation: DayLimitCalculation,
    todayTransactions: List<TransactionEntity>,
    monthTransactions: List<TransactionEntity> = emptyList(),
    currentDate: LocalDate,
    userSettings: UserSettings,
    onOpenQuickAdd: () -> Unit,
    onOpenPasteSms: () -> Unit,
    onSelectTransaction: (TransactionEntity) -> Unit,
    onDismissRollover: () -> Unit,
    onOpenSettings: () -> Unit,
    onAutoTrackSpend: (Double, String, UPIApp, Category) -> Unit = { _, _, _, _ -> },
    modifier: Modifier = Modifier
) {
    val currentYearMonth = currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM"))
    val isRolloverNeeded = !userSettings.lastConfiguredYearMonth.isNullOrEmpty() &&
            userSettings.lastConfiguredYearMonth != currentYearMonth

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color(0xFF000000),
        floatingActionButton = {
            FloatingActionButton(
                onClick = onOpenQuickAdd,
                containerColor = Color.White,
                contentColor = Color.Black,
                shape = CircleShape,
                modifier = Modifier.testTag("home_fab_quick_add")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Quick Add Spend",
                    modifier = Modifier.size(28.dp),
                    tint = Color.Black
                )
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header: Date & Title in Black & Gray Glassmorphism
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = currentDate.format(DateTimeFormatter.ofPattern("EEEE, d MMMM")),
                            style = MaterialTheme.typography.labelLarge,
                            color = Color(0xFF9CA3AF)
                        )
                        Text(
                            text = "DayLimit",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = (-0.5).sp
                            ),
                            color = Color.White
                        )
                    }

                    // Monthly budget glass pill
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(GlassPillBackground)
                            .border(1.dp, GlassCardBorder, RoundedCornerShape(20.dp))
                            .clickable { onOpenSettings() }
                            .padding(horizontal = 14.dp, vertical = 7.dp)
                    ) {
                        Text(
                            text = "${CurrencyFormatter.format(calculation.monthlyBudget)}/mo",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                    }
                }
            }

            // Month Rollover Prompt if new month has started
            if (isRolloverNeeded) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(containerColor = GlassCardBackground),
                        border = BorderStroke(1.dp, GlassBorderGradient)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CalendarToday,
                                    contentDescription = null,
                                    tint = Color.White
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Welcome to ${currentDate.month.name.lowercase().replaceFirstChar { it.uppercase() }}!",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Start this month with your previous budget of ${CurrencyFormatter.format(userSettings.monthlyBudget)}?",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF9CA3AF)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Button(
                                    onClick = onDismissRollover,
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("Yes, Continue", fontWeight = FontWeight.Bold)
                                }
                                OutlinedButton(
                                    onClick = onOpenSettings,
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                                    border = BorderStroke(1.dp, GlassCardBorder)
                                ) {
                                    Text("Change Budget")
                                }
                            }
                        }
                    }
                }
            }

            // Overspend / Rebalancing Alert Banner
            if (calculation.isOverToday || calculation.isOverMonthBudget || calculation.percentTodayUsed >= 0.8f) {
                item {
                    val isDanger = calculation.isOverToday || calculation.isOverMonthBudget
                    val bannerColor = if (isDanger) ExcessLimitRedStroke else SpendOrange

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(containerColor = GlassCardBackground),
                        border = BorderStroke(1.5.dp, bannerColor.copy(alpha = 0.7f))
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                imageVector = if (isDanger) Icons.Default.Warning else Icons.Default.Info,
                                contentDescription = null,
                                tint = bannerColor,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                val title = when {
                                    calculation.isOverMonthBudget -> "Month Budget Exceeded!"
                                    calculation.isOverToday -> "Daily Limit Crossed!"
                                    calculation.percentTodayUsed >= 1.0f -> "100% of Today's Limit Used"
                                    else -> "80% of Daily Limit Used"
                                }

                                val desc = when {
                                    calculation.isOverMonthBudget ->
                                        "You have exceeded your monthly budget by ${CurrencyFormatter.format(calculation.overMonthBudgetAmount)}. Daily limit is set to ₹0."
                                    calculation.isOverToday -> {
                                        val remainingDays = calculation.daysRemainingIncludingToday - 1
                                        if (remainingDays > 0) {
                                            "You overspent by ${CurrencyFormatter.format(calculation.overspendTodayAmount)}. Tomorrow's daily limit drops to ${CurrencyFormatter.format(calculation.projectedTomorrowLimit)} for the next $remainingDays days."
                                        } else {
                                            "You overspent by ${CurrencyFormatter.format(calculation.overspendTodayAmount)} on the final day of the month."
                                        }
                                    }
                                    calculation.percentTodayUsed >= 1.0f ->
                                        "You've spent your full daily limit (${CurrencyFormatter.format(calculation.dailyLimitToday)}). Any more spend today will reduce tomorrow's limit."
                                    else ->
                                        "Only ${CurrencyFormatter.format(calculation.remainingToday)} remaining for today. Spend mindfully!"
                                }

                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = bannerColor
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = desc,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFFD1D5DB)
                                )
                            }
                        }
                    }
                }
            }

            // Circular Progress Ring Card in Glassmorphism Style
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(26.dp),
                    colors = CardDefaults.cardColors(containerColor = GlassCardBackground),
                    border = BorderStroke(1.dp, GlassBorderGradient),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp, horizontal = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressRing(calculation = calculation)

                        Spacer(modifier = Modifier.height(22.dp))

                        // 3 Quick status stats row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            // Left Today
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = if (calculation.isOverToday) "OVER TODAY" else "LEFT TODAY",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFF9CA3AF)
                                )
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = if (calculation.isOverToday)
                                        "-${CurrencyFormatter.format(calculation.overspendTodayAmount)}"
                                    else
                                        CurrencyFormatter.format(calculation.remainingToday),
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = if (calculation.isOverToday) ExcessLimitRedStroke else WithinLimitGreenStroke
                                )
                            }

                            // Month Left
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "MONTH LEFT",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFF9CA3AF)
                                )
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = CurrencyFormatter.format(calculation.remainingMonthBudget),
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = if (calculation.isOverMonthBudget) ExcessLimitRedStroke else Color.White
                                )
                            }

                            // Days Left
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "DAYS LEFT",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFF9CA3AF)
                                )
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = "${calculation.daysRemainingIncludingToday}",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }

            // Automatic UPI Tracking Hub
            item {
                AutoUpiSyncCard(
                    onSimulateTestPayment = { amount, merchant, upiApp, category ->
                        onAutoTrackSpend(amount, merchant, upiApp, category)
                    }
                )
            }

            // Month Days Calendar: Glassmorphism with Green Stroke (within limit) & Red Stroke (excess limit)
            item {
                MonthDaysCalendarCard(
                    currentDate = currentDate,
                    calculation = calculation,
                    monthTransactions = monthTransactions
                )
            }

            // Quick Actions Bar: Glassmorphic styling
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = onOpenQuickAdd,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("home_quick_add_btn"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Color.Black
                        )
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color.Black)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Quick Add", fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = onOpenPasteSms,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("home_paste_sms_btn"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        border = BorderStroke(1.dp, GlassCardBorder)
                    ) {
                        Icon(imageVector = Icons.Default.ContentPaste, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Paste SMS", fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            // Today's Transactions Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Today's Spends (${todayTransactions.size})",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    Text(
                        text = "Total: ${CurrencyFormatter.format(calculation.spentToday)}",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color(0xFF9CA3AF)
                    )
                }
            }

            // Today's Transactions List in Glassmorphic Cards
            if (todayTransactions.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = GlassCardBackground),
                        border = BorderStroke(1.dp, GlassBorderGradient)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(26.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Savings,
                                contentDescription = null,
                                tint = WithinLimitGreenStroke,
                                modifier = Modifier.size(44.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "No UPI spends logged today!",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "You have ${CurrencyFormatter.format(calculation.dailyLimitToday)} available to spend today.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF9CA3AF)
                            )
                        }
                    }
                }
            } else {
                items(todayTransactions, key = { it.id }) { tx ->
                    TransactionItemCard(
                        transaction = tx,
                        onClick = { onSelectTransaction(tx) }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp)) // Padding for FAB & nav bar
            }
        }
    }
}

@Composable
fun TransactionItemCard(
    transaction: TransactionEntity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val upiApp = transaction.getUpiAppEnum()
    val category = transaction.getCategoryEnum()

    val timeString = java.time.Instant.ofEpochMilli(transaction.timestamp)
        .atZone(java.time.ZoneId.systemDefault())
        .format(DateTimeFormatter.ofPattern("hh:mm a"))

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("tx_item_${transaction.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = GlassCardBackground),
        border = BorderStroke(1.dp, GlassCardBorderSubtle),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // UPI App indicator circle
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(upiApp.primaryColor.copy(alpha = 0.15f))
                    .border(1.dp, upiApp.primaryColor.copy(alpha = 0.35f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = upiApp.brandTag.take(2).uppercase(),
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = upiApp.primaryColor
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Merchant and Category
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.merchant,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = Color.White,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${category.displayName} • $timeString",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF9CA3AF)
                    )
                }
            }

            // Amount
            Text(
                text = CurrencyFormatter.format(transaction.amount),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.White
            )
        }
    }
}
