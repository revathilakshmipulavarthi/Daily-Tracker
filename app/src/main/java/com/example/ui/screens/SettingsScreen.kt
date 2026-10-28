package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.UserSettings
import com.example.logic.CurrencyFormatter
import com.example.ui.components.AutoUpiSyncCard
import com.example.ui.theme.GlassBorderGradient
import com.example.ui.theme.GlassCardBackground
import com.example.ui.theme.SpendRed

@Composable
fun SettingsScreen(
    settings: UserSettings,
    onUpdateBudget: (newBudget: Double, applyFromToday: Boolean) -> Unit,
    onUpdateNotifications: (at50: Boolean, at80: Boolean, at100: Boolean, evening: Boolean, time: String) -> Unit,
    onToggleIndianGrouping: (Boolean) -> Unit,
    onSetDarkMode: (Boolean?) -> Unit,
    onExportCsv: () -> String,
    onResetMonth: () -> Unit,
    onResetAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    var showEditBudgetDialog by remember { mutableStateOf(false) }
    var showResetMonthDialog by remember { mutableStateOf(false) }
    var showResetAllDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Section: Automatic UPI Tracking
        Text(
            text = "AUTOMATIC UPI TRACKING",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        AutoUpiSyncCard(
            onSimulateTestPayment = { _, _, _, _ -> }
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Section: Budget Configuration
        Text(
            text = "BUDGET CONFIGURATION",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AccountBalanceWallet,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Monthly Budget",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                            )
                            Text(
                                text = CurrencyFormatter.format(settings.monthlyBudget),
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            )
                        }
                    }

                    OutlinedButton(
                        onClick = { showEditBudgetDialog = true },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("settings_edit_budget_btn")
                    ) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Edit")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Section: Notification & Alerts
        Text(
            text = "NOTIFICATION THRESHOLDS",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = GlassCardBackground),
            border = BorderStroke(1.dp, GlassBorderGradient),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // 50% Alert
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("50% Limit Used Alert", fontWeight = FontWeight.SemiBold)
                        Text("Gentle heads-up when you reach halfway", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Switch(
                        checked = settings.notify50Percent,
                        onCheckedChange = {
                            onUpdateNotifications(it, settings.notify80Percent, settings.notify100Percent, settings.eveningReminderEnabled, settings.eveningReminderTime)
                        }
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = MaterialTheme.colorScheme.surfaceVariant)

                // 80% Alert
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("80% Limit Warning", fontWeight = FontWeight.SemiBold)
                        Text("Warning before you exhaust today's limit", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Switch(
                        checked = settings.notify80Percent,
                        onCheckedChange = {
                            onUpdateNotifications(settings.notify50Percent, it, settings.notify100Percent, settings.eveningReminderEnabled, settings.eveningReminderTime)
                        }
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = MaterialTheme.colorScheme.surfaceVariant)

                // 100% Alert
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("100% Limit Reached Alert", fontWeight = FontWeight.SemiBold)
                        Text("Strong alert when limit is crossed", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Switch(
                        checked = settings.notify100Percent,
                        onCheckedChange = {
                            onUpdateNotifications(settings.notify50Percent, settings.notify80Percent, it, settings.eveningReminderEnabled, settings.eveningReminderTime)
                        }
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = MaterialTheme.colorScheme.surfaceVariant)

                // Evening Reminder
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Evening Summary Reminder", fontWeight = FontWeight.SemiBold)
                        Text("Summarises the day at ${settings.eveningReminderTime}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Switch(
                        checked = settings.eveningReminderEnabled,
                        onCheckedChange = {
                            onUpdateNotifications(settings.notify50Percent, settings.notify80Percent, settings.notify100Percent, it, settings.eveningReminderTime)
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Section: Currency & Display
        Text(
            text = "CURRENCY & DISPLAY",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = GlassCardBackground),
            border = BorderStroke(1.dp, GlassBorderGradient),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Indian Numbering Grouping Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Indian Digit Grouping", fontWeight = FontWeight.SemiBold)
                        Text("Format as ₹ 1,25,000 (Lakhs/Crores)", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Switch(
                        checked = settings.useIndianGrouping,
                        onCheckedChange = onToggleIndianGrouping
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = MaterialTheme.colorScheme.surfaceVariant)

                // Dark Mode Options
                Text("Appearance Theme", fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        "System" to null,
                        "Light" to false,
                        "Dark" to true
                    ).forEach { (label, mode) ->
                        val isSelected = settings.isDarkMode == mode
                        OutlinedButton(
                            onClick = { onSetDarkMode(mode) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = if (isSelected) ButtonDefaults.outlinedButtonColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                            else ButtonDefaults.outlinedButtonColors()
                        ) {
                            Text(label, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Section: Data & Export
        Text(
            text = "DATA MANAGEMENT",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = GlassCardBackground),
            border = BorderStroke(1.dp, GlassBorderGradient),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Export CSV
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val csvData = onExportCsv()
                            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                putExtra(Intent.EXTRA_TEXT, csvData)
                                type = "text/csv"
                            }
                            val shareIntent = Intent.createChooser(sendIntent, "Export DayLimit CSV")
                            context.startActivity(shareIntent)
                        }
                        .padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Download, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Export Transactions (CSV)", fontWeight = FontWeight.SemiBold)
                        Text("Share or backup all logged spends", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = MaterialTheme.colorScheme.surfaceVariant)

                // Reset Month
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showResetMonthDialog = true }
                        .padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.RestartAlt, contentDescription = null, tint = SpendRed)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Reset Current Month Spends", fontWeight = FontWeight.SemiBold, color = SpendRed)
                        Text("Clears transactions for current month only", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = MaterialTheme.colorScheme.surfaceVariant)

                // Reset All
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showResetAllDialog = true }
                        .padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.DeleteForever, contentDescription = null, tint = SpendRed)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Clear All App Data", fontWeight = FontWeight.SemiBold, color = SpendRed)
                        Text("Factory reset all transactions and preferences", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Privacy Guarantee Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = GlassCardBackground),
            border = BorderStroke(1.dp, GlassBorderGradient),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("DayLimit Privacy Guarantee", fontWeight = FontWeight.Bold)
                    Text(
                        "All data is stored exclusively on this device. No external bank logins, no accounts, and no money is ever transferred.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(80.dp))
    }

    // Dialog: Edit Monthly Budget
    if (showEditBudgetDialog) {
        var newBudgetText by remember { mutableStateOf(settings.monthlyBudget.toInt().toString()) }
        var applyFromTodayOnly by remember { mutableStateOf(true) }

        AlertDialog(
            onDismissRequest = { showEditBudgetDialog = false },
            title = { Text("Edit Monthly Budget") },
            text = {
                Column {
                    OutlinedTextField(
                        value = newBudgetText,
                        onValueChange = { newBudgetText = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Monthly Budget (₹)") },
                        prefix = { Text("₹ ") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text("Rebalancing Strategy:", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { applyFromTodayOnly = true }
                            .padding(vertical = 4.dp)
                    ) {
                        RadioButton(
                            selected = applyFromTodayOnly,
                            onClick = { applyFromTodayOnly = true }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("Apply from today", fontWeight = FontWeight.Medium)
                            Text("Rebalances remaining days only", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { applyFromTodayOnly = false }
                            .padding(vertical = 4.dp)
                    ) {
                        RadioButton(
                            selected = !applyFromTodayOnly,
                            onClick = { applyFromTodayOnly = false }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("Recalculate whole month", fontWeight = FontWeight.Medium)
                            Text("Applies new budget from day 1", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val parsed = newBudgetText.toDoubleOrNull() ?: 0.0
                        if (parsed > 0) {
                            onUpdateBudget(parsed, applyFromTodayOnly)
                            showEditBudgetDialog = false
                        }
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditBudgetDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Dialog: Reset Month
    if (showResetMonthDialog) {
        AlertDialog(
            onDismissRequest = { showResetMonthDialog = false },
            title = { Text("Reset Current Month?") },
            text = { Text("This will permanently delete all transactions for the current month. Your budget will remain intact.") },
            confirmButton = {
                Button(
                    onClick = {
                        onResetMonth()
                        showResetMonthDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SpendRed)
                ) {
                    Text("Reset Month")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetMonthDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Dialog: Reset All Data
    if (showResetAllDialog) {
        AlertDialog(
            onDismissRequest = { showResetAllDialog = false },
            title = { Text("Clear All App Data?") },
            text = { Text("This will permanently erase all transactions, budget preferences, and restart onboarding.") },
            confirmButton = {
                Button(
                    onClick = {
                        onResetAll()
                        showResetAllDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SpendRed)
                ) {
                    Text("Clear Everything")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetAllDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
