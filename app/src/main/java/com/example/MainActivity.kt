package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.TransactionEntity
import com.example.logic.CurrencyFormatter
import com.example.logic.NotificationHelper
import com.example.model.Category
import com.example.model.UPIApp
import com.example.ui.screens.EditTransactionSheet
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.InsightsScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.PasteSmsDialog
import com.example.ui.screens.QuickAddSheet
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.DayLimitTheme
import com.example.ui.theme.SpendAmber
import com.example.viewmodel.DayLimitViewModel
import com.example.viewmodel.UiEvent

enum class ScreenTab {
    HOME, HISTORY, INSIGHTS, SETTINGS
}

class MainActivity : ComponentActivity() {

    private val viewModel: DayLimitViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Create notification channel
        NotificationHelper.createNotificationChannel(this)

        setContent {
            val settings by viewModel.settings.collectAsStateWithLifecycle()
            val isDark = settings.isDarkMode ?: true

            DayLimitTheme(darkTheme = isDark) {
                DayLimitApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun DayLimitApp(viewModel: DayLimitViewModel) {
    val context = LocalContext.current
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val calculation by viewModel.calculationState.collectAsStateWithLifecycle()
    val todayTx by viewModel.todayTransactions.collectAsStateWithLifecycle()
    val dailySummaries by viewModel.dailySummaries.collectAsStateWithLifecycle()
    val insightsData by viewModel.insightsData.collectAsStateWithLifecycle()
    val upiFilter by viewModel.historyUpiFilter.collectAsStateWithLifecycle()
    val currentDate by viewModel.currentDate.collectAsStateWithLifecycle()
    val currentMonthTx by viewModel.currentMonthTransactions.collectAsStateWithLifecycle()

    var currentTab by remember { mutableStateOf(ScreenTab.HOME) }

    // Sheets & Dialog states
    var showQuickAddSheet by remember { mutableStateOf(false) }
    var showPasteSmsSheet by remember { mutableStateOf(false) }
    var editingTransaction by remember { mutableStateOf<TransactionEntity?>(null) }
    var duplicateEvent by remember { mutableStateOf<UiEvent.DuplicateWarning?>(null) }

    // Collect Toast events and duplicate warnings
    LaunchedEffect(viewModel.events) {
        viewModel.events.collect { event ->
            when (event) {
                is UiEvent.ShowToast -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
                is UiEvent.DuplicateWarning -> {
                    duplicateEvent = event
                }
            }
        }
    }

    // If onboarding is not completed, show OnboardingScreen
    if (!settings.isOnboardingDone) {
        OnboardingScreen(
            onComplete = { budget ->
                viewModel.completeOnboarding(budget)
            }
        )
        return
    }

    // Back handling: If on secondary tab, return to Home
    BackHandler(enabled = currentTab != ScreenTab.HOME) {
        currentTab = ScreenTab.HOME
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 20.dp),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(66.dp)
                        .testTag("bottom_nav_bar"),
                    shape = RoundedCornerShape(percent = 50),
                    color = Color(0x99141822),
                    border = BorderStroke(1.dp, Color(0x33FFFFFF)),
                    shadowElevation = 16.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        CapsuleNavItem(
                            selected = currentTab == ScreenTab.HOME,
                            onClick = { currentTab = ScreenTab.HOME },
                            icon = {
                                Icon(
                                    painter = painterResource(
                                        if (currentTab == ScreenTab.HOME) R.drawable.ic_nav_home_filled
                                        else R.drawable.ic_nav_home
                                    ),
                                    contentDescription = "Home",
                                    modifier = Modifier.size(24.dp),
                                    tint = if (currentTab == ScreenTab.HOME) Color.White else Color(0xB3FFFFFF)
                                )
                            },
                            modifier = Modifier.testTag("nav_tab_home")
                        )

                        CapsuleNavItem(
                            selected = currentTab == ScreenTab.HISTORY,
                            onClick = { currentTab = ScreenTab.HISTORY },
                            icon = {
                                Icon(
                                    painter = painterResource(
                                        if (currentTab == ScreenTab.HISTORY) R.drawable.ic_nav_history_filled
                                        else R.drawable.ic_nav_history
                                    ),
                                    contentDescription = "History",
                                    modifier = Modifier.size(24.dp),
                                    tint = if (currentTab == ScreenTab.HISTORY) Color.White else Color(0xB3FFFFFF)
                                )
                            },
                            modifier = Modifier.testTag("nav_tab_history")
                        )

                        CapsuleNavItem(
                            selected = currentTab == ScreenTab.INSIGHTS,
                            onClick = { currentTab = ScreenTab.INSIGHTS },
                            icon = {
                                Icon(
                                    painter = painterResource(
                                        if (currentTab == ScreenTab.INSIGHTS) R.drawable.ic_nav_insights_filled
                                        else R.drawable.ic_nav_insights
                                    ),
                                    contentDescription = "Insights",
                                    modifier = Modifier.size(24.dp),
                                    tint = if (currentTab == ScreenTab.INSIGHTS) Color.White else Color(0xB3FFFFFF)
                                )
                            },
                            modifier = Modifier.testTag("nav_tab_insights")
                        )

                        CapsuleNavItem(
                            selected = currentTab == ScreenTab.SETTINGS,
                            onClick = { currentTab = ScreenTab.SETTINGS },
                            icon = {
                                Icon(
                                    painter = painterResource(
                                        if (currentTab == ScreenTab.SETTINGS) R.drawable.ic_nav_settings_filled
                                        else R.drawable.ic_nav_settings
                                    ),
                                    contentDescription = "Settings",
                                    modifier = Modifier.size(24.dp),
                                    tint = if (currentTab == ScreenTab.SETTINGS) Color.White else Color(0xB3FFFFFF)
                                )
                            },
                            modifier = Modifier.testTag("nav_tab_settings")
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (currentTab) {
                ScreenTab.HOME -> {
                    HomeScreen(
                        calculation = calculation,
                        todayTransactions = todayTx,
                        monthTransactions = currentMonthTx,
                        currentDate = currentDate,
                        userSettings = settings,
                        onOpenQuickAdd = { showQuickAddSheet = true },
                        onOpenPasteSms = { showPasteSmsSheet = true },
                        onSelectTransaction = { tx -> editingTransaction = tx },
                        onDismissRollover = { viewModel.dismissMonthRollover() },
                        onOpenSettings = { currentTab = ScreenTab.SETTINGS },
                        onAddUpiApp = { viewModel.addIntegratedUpiApp(it) },
                        onRemoveUpiApp = { viewModel.removeIntegratedUpiApp(it) },
                        onAutoTrackSpend = { amount, merchant, upiApp, category ->
                            viewModel.forceSaveTransaction(
                                amount = amount,
                                merchant = merchant,
                                upiApp = upiApp,
                                category = category,
                                note = "Auto-tracked via ${upiApp.brandTag}"
                            )
                        }
                    )
                }
                ScreenTab.HISTORY -> {
                    HistoryScreen(
                        dailySummaries = dailySummaries,
                        currentDate = currentDate,
                        calculation = calculation,
                        monthTransactions = currentMonthTx,
                        selectedUpiFilter = upiFilter,
                        onFilterChange = { viewModel.setHistoryUpiFilter(it) },
                        onSelectTransaction = { tx -> editingTransaction = tx }
                    )
                }
                ScreenTab.INSIGHTS -> {
                    InsightsScreen(
                        insightsData = insightsData,
                        calculation = calculation
                    )
                }
                ScreenTab.SETTINGS -> {
                    SettingsScreen(
                        settings = settings,
                        onUpdateBudget = { newBudget, applyFromToday ->
                            viewModel.updateBudget(newBudget, applyFromToday)
                        },
                        onUpdateNotifications = { at50, at80, at100, evening, time ->
                            viewModel.userPrefs.setNotificationToggles(at50, at80, at100, evening, time)
                        },
                        onToggleIndianGrouping = { enabled ->
                            viewModel.userPrefs.setUseIndianGrouping(enabled)
                        },
                        onSetDarkMode = { mode ->
                            viewModel.userPrefs.setDarkMode(mode)
                        },
                        onExportCsv = { viewModel.generateCsv() },
                        onResetMonth = { viewModel.clearCurrentMonthData() },
                        onResetAll = { viewModel.resetAllData() },
                        onAddUpiApp = { viewModel.addIntegratedUpiApp(it) },
                        onRemoveUpiApp = { viewModel.removeIntegratedUpiApp(it) },
                        onUpdateProfile = { name, phone, email ->
                            viewModel.updateProfile(name, phone, email)
                        },
                        onLogout = {
                            viewModel.logout()
                        }
                    )
                }
            }
        }
    }

    // Modal: Add Expenses Sheet
    if (showQuickAddSheet) {
        QuickAddSheet(
            onDismiss = { showQuickAddSheet = false },
            onSave = { amount, merchant, upiApp, category, note ->
                viewModel.attemptAddTransaction(amount, merchant, upiApp, category, note)
            }
        )
    }

    // Modal: Paste SMS Sheet
    if (showPasteSmsSheet) {
        PasteSmsDialog(
            onDismiss = { showPasteSmsSheet = false },
            onSave = { amount, merchant, upiApp, category, note ->
                viewModel.attemptAddTransaction(amount, merchant, upiApp, category, note)
            }
        )
    }

    // Modal: Edit Transaction Sheet
    editingTransaction?.let { tx ->
        EditTransactionSheet(
            transaction = tx,
            onDismiss = { editingTransaction = null },
            onUpdate = { updated -> viewModel.updateTransaction(updated) },
            onDelete = { toDelete -> viewModel.deleteTransaction(toDelete) }
        )
    }

    // Dialog: Duplicate Warning
    duplicateEvent?.let { warning ->
        AlertDialog(
            onDismissRequest = { duplicateEvent = null },
            title = { Text("Duplicate Spend Detected") },
            text = {
                Text(
                    "You just logged ${CurrencyFormatter.format(warning.pendingAmount)} for '${warning.existingTx.merchant}' less than 2 minutes ago.\n\nDo you want to save this transaction anyway?"
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.forceSaveTransaction(
                            warning.pendingAmount,
                            warning.pendingMerchant,
                            warning.pendingUpi,
                            warning.pendingCat,
                            warning.pendingNote
                        )
                        duplicateEvent = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SpendAmber)
                ) {
                    Text("Save Anyway")
                }
            },
            dismissButton = {
                TextButton(onClick = { duplicateEvent = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun RowScope.CapsuleNavItem(
    selected: Boolean,
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .weight(1f)
            .fillMaxHeight()
            .clip(RoundedCornerShape(percent = 50))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (selected) {
            Surface(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.92f),
                shape = RoundedCornerShape(percent = 50),
                color = Color(0x38FFFFFF),
                border = BorderStroke(1.dp, Color(0x55FFFFFF)),
                shadowElevation = 4.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    icon()
                }
            }
        } else {
            icon()
        }
    }
}

