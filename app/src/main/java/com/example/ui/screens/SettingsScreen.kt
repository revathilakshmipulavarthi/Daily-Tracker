package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.ui.theme.ExcessLimitRedStroke
import com.example.ui.theme.WithinLimitGreenStroke
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
import androidx.compose.material3.OutlinedTextFieldDefaults
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
    onAddUpiApp: (String) -> Unit = {},
    onRemoveUpiApp: (String) -> Unit = {},
    onUpdateProfile: (name: String, phone: String, email: String) -> Unit = { _, _, _ -> },
    onLogout: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    var showEditBudgetDialog by remember { mutableStateOf(false) }
    var showResetMonthDialog by remember { mutableStateOf(false) }
    var showResetAllDialog by remember { mutableStateOf(false) }
    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showManageSubDialog by remember { mutableStateOf(false) }

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

        // ==========================================
        // 1. PROFILE & PERSONAL DETAILS CARD
        // ==========================================
        Text(
            text = "PROFILE & ACCOUNT",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("profile_card"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = GlassCardBackground),
            border = BorderStroke(1.dp, GlassBorderGradient),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Profile Image with ring border
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFF2E2E3E), Color(0xFF14141E))
                            )
                        )
                        .border(
                            2.dp,
                            Brush.linearGradient(
                                colors = listOf(Color.White.copy(alpha = 0.5f), Color(0xFF6B7280))
                            ),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile Photo",
                        tint = Color.White,
                        modifier = Modifier.size(46.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Name
                Text(
                    text = settings.userName.ifBlank { "User" },
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.3).sp
                    ),
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Personal Details Container (Mobile Number & Email ID)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF14141E).copy(alpha = 0.6f))
                        .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(16.dp))
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Mobile Number
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF222230)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Phone,
                                contentDescription = null,
                                tint = Color(0xFF9CA3AF),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Mobile Number",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF9CA3AF)
                            )
                            Text(
                                text = settings.userPhone.ifBlank { "Not provided" },
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = Color.White
                            )
                        }
                    }

                    HorizontalDivider(color = Color.White.copy(alpha = 0.06f))

                    // Email ID
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF222230)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = null,
                                tint = Color(0xFF9CA3AF),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Email ID",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF9CA3AF)
                            )
                            Text(
                                text = settings.userEmail.ifBlank { "Not provided" },
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Action Buttons: Edit Details & Log Out Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = { showEditProfileDialog = true },
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp)
                            .testTag("edit_profile_btn"),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.25f)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                    ) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Edit Details", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    }

                    Button(
                        onClick = { showLogoutDialog = true },
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp)
                            .testTag("logout_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2E1518),
                            contentColor = ExcessLimitRedStroke
                        ),
                        border = BorderStroke(1.dp, ExcessLimitRedStroke.copy(alpha = 0.4f))
                    ) {
                        Icon(imageVector = Icons.Default.ExitToApp, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Log Out", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ==========================================
        // 2. SUBSCRIPTION PLAN CARD (BELOW PERSONAL DETAILS)
        // ==========================================
        Text(
            text = "SUBSCRIPTION PLAN",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("subscription_card"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = GlassCardBackground),
            border = BorderStroke(1.dp, WithinLimitGreenStroke.copy(alpha = 0.45f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF1A2A20)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = WithinLimitGreenStroke,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "DayLimit Pro",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                            Text(
                                text = "Subscription Plan",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF9CA3AF)
                            )
                        }
                    }

                    // Active badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xFF142B1C))
                            .border(1.dp, WithinLimitGreenStroke.copy(alpha = 0.6f), RoundedCornerShape(20.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "ACTIVE",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = WithinLimitGreenStroke
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Price display: Rs 99/- per month
                Row(
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = "₹99",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "/ month",
                        style = MaterialTheme.typography.titleSmall,
                        color = Color(0xFF9CA3AF),
                        modifier = Modifier.padding(bottom = 3.dp)
                    )
                }

                Text(
                    text = "Subscription plan is Rs 99/- per month. Auto-renews on 29 Nov 2026.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF9CA3AF),
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Plan Features
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(
                        "Automatic UPI spend tracking from connected apps",
                        "Dynamic daily rebalancing & spillover calculations",
                        "Visual calendar with overspend alerts",
                        "100% private, on-device financial tracking"
                    ).forEach { feature ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = WithinLimitGreenStroke,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = feature,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFD1D5DB)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { showManageSubDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("manage_subscription_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black
                    )
                ) {
                    Text("Manage Subscription", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

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
            integratedUpiApps = settings.integratedUpiApps,
            onAddUpiApp = onAddUpiApp,
            onRemoveUpiApp = onRemoveUpiApp,
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

    // Dialog: Edit Profile Details
    if (showEditProfileDialog) {
        var editName by remember { mutableStateOf(settings.userName) }
        var editPhone by remember { mutableStateOf(settings.userPhone) }
        var editEmail by remember { mutableStateOf(settings.userEmail) }

        AlertDialog(
            onDismissRequest = { showEditProfileDialog = false },
            containerColor = Color(0xFF14141C),
            shape = RoundedCornerShape(24.dp),
            title = {
                Text(
                    text = "Edit Personal Details",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Update your profile details below.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF9CA3AF)
                    )

                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Name", color = Color(0xFF9CA3AF)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color.White,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.2f)
                        )
                    )

                    OutlinedTextField(
                        value = editPhone,
                        onValueChange = { editPhone = it },
                        label = { Text("Mobile Number", color = Color(0xFF9CA3AF)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color.White,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.2f)
                        )
                    )

                    OutlinedTextField(
                        value = editEmail,
                        onValueChange = { editEmail = it },
                        label = { Text("Email ID", color = Color(0xFF9CA3AF)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color.White,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.2f)
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onUpdateProfile(editName.trim(), editPhone.trim(), editEmail.trim())
                        showEditProfileDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Save Changes", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditProfileDialog = false }) {
                    Text("Cancel", color = Color(0xFF9CA3AF))
                }
            }
        )
    }

    // Dialog: Log Out Confirmation
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            containerColor = Color(0xFF14141C),
            shape = RoundedCornerShape(24.dp),
            title = {
                Text(
                    text = "Log Out of DayLimit?",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to log out? Your local data and UPI configurations will remain safe on your device.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFFD1D5DB)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onLogout()
                        showLogoutDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626), contentColor = Color.White),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Log Out", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel", color = Color(0xFF9CA3AF))
                }
            }
        )
    }

    // Dialog: Manage Subscription Details
    if (showManageSubDialog) {
        AlertDialog(
            onDismissRequest = { showManageSubDialog = false },
            containerColor = Color(0xFF14141C),
            shape = RoundedCornerShape(24.dp),
            title = {
                Text(
                    text = "Subscription Plan Details",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "DayLimit is a subscription-based financial tracker. Your current plan details are below:",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF9CA3AF)
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFF1E2620))
                            .border(1.dp, WithinLimitGreenStroke.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                            .padding(14.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "DayLimit Pro Plan",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                            Text(
                                text = "Price: ₹99 / month (Rs 99/- per month)",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = WithinLimitGreenStroke
                            )
                            Text(
                                text = "Status: Active • Renews on 29 Nov 2026",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFD1D5DB)
                            )
                            Text(
                                text = "Billed via: UPI AutoPay",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF9CA3AF)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showManageSubDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Done", fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}
