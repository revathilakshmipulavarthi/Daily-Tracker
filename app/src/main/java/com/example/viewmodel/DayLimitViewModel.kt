package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.TransactionEntity
import com.example.data.TransactionRepository
import com.example.data.UserPreferences
import com.example.data.UserSettings
import com.example.logic.BudgetCalculator
import com.example.logic.CurrencyFormatter
import com.example.logic.NotificationHelper
import com.example.logic.ParsedSmsResult
import com.example.logic.SmsParser
import com.example.model.Category
import com.example.model.DayLimitCalculation
import com.example.model.UPIApp
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

sealed interface UiEvent {
    data class ShowToast(val message: String) : UiEvent
    data class DuplicateWarning(
        val existingTx: TransactionEntity,
        val pendingAmount: Double,
        val pendingMerchant: String,
        val pendingUpi: UPIApp,
        val pendingCat: Category,
        val pendingNote: String?
    ) : UiEvent
}

data class DaySpendSummary(
    val dayOfMonth: Int,
    val dateString: String,
    val totalSpent: Double,
    val limitAtThatTime: Double,
    val isOverLimit: Boolean,
    val difference: Double,
    val transactions: List<TransactionEntity>
)

data class InsightsData(
    val weeklySpending: List<Pair<String, Double>>,
    val upiBreakdown: List<Pair<UPIApp, Double>>,
    val categoryBreakdown: List<Pair<Category, Double>>,
    val averageDailySpend: Double,
    val bestDay: Pair<Int, Double>?,  // day with lowest spend > 0
    val worstDay: Pair<Int, Double>?, // day with highest spend
    val totalMonthTransactions: Int,
    val projectedSavings: Double
)

class DayLimitViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repository = TransactionRepository(db.transactionDao())
    val userPrefs = UserPreferences(application)

    private val _events = MutableSharedFlow<UiEvent>()
    val events: SharedFlow<UiEvent> = _events.asSharedFlow()

    // Current date state (can be refreshed at midnight)
    private val _currentDate = MutableStateFlow(LocalDate.now())
    val currentDate: StateFlow<LocalDate> = _currentDate.asStateFlow()

    // History filter by UPI App
    private val _historyUpiFilter = MutableStateFlow<UPIApp?>(null)
    val historyUpiFilter: StateFlow<UPIApp?> = _historyUpiFilter.asStateFlow()

    val settings: StateFlow<UserSettings> = userPrefs.settings

    // All transactions for the current month
    val currentMonthTransactions: StateFlow<List<TransactionEntity>> = combine(
        repository.allTransactions,
        _currentDate
    ) { allTx, date ->
        val currentYearMonth = date.format(DateTimeFormatter.ofPattern("yyyy-MM"))
        allTx.filter { it.yearMonth == currentYearMonth }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Dynamic DayLimit Calculation State
    val calculationState: StateFlow<DayLimitCalculation> = combine(
        settings,
        currentMonthTransactions,
        _currentDate
    ) { currentSettings, monthTx, date ->
        val currentDay = date.dayOfMonth
        val totalDaysInMonth = BudgetCalculator.getDaysInMonth(date.year, date.monthValue)

        val beforeToday = monthTx.filter { it.dayOfMonth < currentDay }
        val todayTx = monthTx.filter { it.dayOfMonth == currentDay }

        val spentBeforeToday = beforeToday.sumOf { it.amount }
        val spentToday = todayTx.sumOf { it.amount }

        val calc = BudgetCalculator.calculate(
            monthlyBudget = currentSettings.monthlyBudget,
            totalSpentBeforeToday = spentBeforeToday,
            spentToday = spentToday,
            currentDayOfMonth = currentDay,
            totalDaysInMonth = totalDaysInMonth
        )

        calc
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = BudgetCalculator.calculate(15000.0, 0.0, 0.0, 1, 30)
    )

    // Today's transactions
    val todayTransactions: StateFlow<List<TransactionEntity>> = combine(
        currentMonthTransactions,
        _currentDate
    ) { monthTx, date ->
        monthTx.filter { it.dayOfMonth == date.dayOfMonth }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // History: Daily summaries for the month
    val dailySummaries: StateFlow<List<DaySpendSummary>> = combine(
        currentMonthTransactions,
        _historyUpiFilter,
        _currentDate,
        settings
    ) { monthTx, upiFilter, date, currentSettings ->
        val daysInMonth = BudgetCalculator.getDaysInMonth(date.year, date.monthValue)
        val filteredTx = if (upiFilter != null) {
            monthTx.filter { it.getUpiAppEnum() == upiFilter }
        } else {
            monthTx
        }

        val baselineLimit = if (daysInMonth > 0) currentSettings.monthlyBudget / daysInMonth else 0.0

        val list = mutableListOf<DaySpendSummary>()
        val groupedByDay = filteredTx.groupBy { it.dayOfMonth }

        for (day in date.dayOfMonth downTo 1) {
            val txForDay = groupedByDay[day] ?: emptyList()
            val totalSpentDay = txForDay.sumOf { it.amount }
            val isOver = totalSpentDay > baselineLimit
            val diff = totalSpentDay - baselineLimit

            val dateObj = date.withDayOfMonth(day)
            val dateStr = dateObj.format(DateTimeFormatter.ofPattern("EEE, dd MMM"))

            list.add(
                DaySpendSummary(
                    dayOfMonth = day,
                    dateString = dateStr,
                    totalSpent = totalSpentDay,
                    limitAtThatTime = baselineLimit,
                    isOverLimit = isOver,
                    difference = diff,
                    transactions = txForDay
                )
            )
        }
        list
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Insights state
    val insightsData: StateFlow<InsightsData> = combine(
        currentMonthTransactions,
        calculationState,
        _currentDate
    ) { monthTx, calc, date ->
        val currentDay = date.dayOfMonth

        // 1. Weekly spending (last 7 days)
        val weekly = mutableListOf<Pair<String, Double>>()
        val grouped = monthTx.groupBy { it.dayOfMonth }
        for (i in 6 downTo 0) {
            val targetDay = currentDay - i
            if (targetDay in 1..calc.totalDaysInMonth) {
                val d = date.withDayOfMonth(targetDay)
                val label = d.format(DateTimeFormatter.ofPattern("E d"))
                val amt = grouped[targetDay]?.sumOf { it.amount } ?: 0.0
                weekly.add(label to amt)
            }
        }

        // 2. UPI app breakdown
        val upiMap = mutableMapOf<UPIApp, Double>()
        UPIApp.values().forEach { upiMap[it] = 0.0 }
        monthTx.forEach { tx ->
            val app = tx.getUpiAppEnum()
            upiMap[app] = (upiMap[app] ?: 0.0) + tx.amount
        }
        val upiList = upiMap.toList().filter { it.second > 0.0 }.sortedByDescending { it.second }

        // 3. Category breakdown
        val catMap = mutableMapOf<Category, Double>()
        monthTx.forEach { tx ->
            val cat = tx.getCategoryEnum()
            catMap[cat] = (catMap[cat] ?: 0.0) + tx.amount
        }
        val catList = catMap.toList().sortedByDescending { it.second }

        // 4. Days with spending
        val daysWithSpending = grouped.filter { it.key <= currentDay && it.value.isNotEmpty() }
        val avgDaily = if (currentDay > 0) calc.totalSpentMonth / currentDay else 0.0

        val best = daysWithSpending.minByOrNull { entry -> entry.value.sumOf { it.amount } }
        val worst = daysWithSpending.maxByOrNull { entry -> entry.value.sumOf { it.amount } }

        val bestPair = best?.let { it.key to it.value.sumOf { tx -> tx.amount } }
        val worstPair = worst?.let { it.key to it.value.sumOf { tx -> tx.amount } }

        val projectedSavings = (calc.monthlyBudget - (avgDaily * calc.totalDaysInMonth)).coerceAtLeast(0.0)

        InsightsData(
            weeklySpending = weekly,
            upiBreakdown = upiList,
            categoryBreakdown = catList,
            averageDailySpend = avgDaily,
            bestDay = bestPair,
            worstDay = worstPair,
            totalMonthTransactions = monthTx.size,
            projectedSavings = projectedSavings
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = InsightsData(
            weeklySpending = emptyList(),
            upiBreakdown = emptyList(),
            categoryBreakdown = emptyList(),
            averageDailySpend = 0.0,
            bestDay = null,
            worstDay = null,
            totalMonthTransactions = 0,
            projectedSavings = 0.0
        )
    )

    fun refreshDate() {
        _currentDate.value = LocalDate.now()
    }

    fun setHistoryUpiFilter(upiApp: UPIApp?) {
        _historyUpiFilter.value = upiApp
    }

    /**
     * Attempts to add a transaction. Detects duplicates within 2 minutes and prompts user.
     */
    fun attemptAddTransaction(
        amount: Double,
        merchant: String,
        upiApp: UPIApp,
        category: Category,
        note: String? = null
    ) {
        if (amount <= 0.0) {
            emitToast("Please enter a valid amount greater than ₹0")
            return
        }
        viewModelScope.launch {
            val duplicate = repository.checkLikelyDuplicate(amount, withinMillis = 120_000)
            if (duplicate != null) {
                _events.emit(
                    UiEvent.DuplicateWarning(
                        existingTx = duplicate,
                        pendingAmount = amount,
                        pendingMerchant = merchant.ifBlank { "UPI Merchant" },
                        pendingUpi = upiApp,
                        pendingCat = category,
                        pendingNote = note
                    )
                )
            } else {
                saveTransaction(amount, merchant, upiApp, category, note)
            }
        }
    }

    fun forceSaveTransaction(
        amount: Double,
        merchant: String,
        upiApp: UPIApp,
        category: Category,
        note: String?
    ) {
        viewModelScope.launch {
            saveTransaction(amount, merchant, upiApp, category, note)
        }
    }

    private suspend fun saveTransaction(
        amount: Double,
        merchant: String,
        upiApp: UPIApp,
        category: Category,
        note: String?
    ) {
        val date = _currentDate.value
        val ym = date.format(DateTimeFormatter.ofPattern("yyyy-MM"))
        val day = date.dayOfMonth

        val tx = TransactionEntity(
            amount = amount,
            merchant = merchant.ifBlank { "UPI Merchant" },
            upiApp = upiApp.name,
            category = category.name,
            timestamp = System.currentTimeMillis(),
            note = note,
            yearMonth = ym,
            dayOfMonth = day
        )
        repository.insert(tx)
        emitToast("Added ${CurrencyFormatter.format(amount)} to today's spends")

        // Trigger notification check
        checkAndSendSpendAlerts(amount)
    }

    private fun checkAndSendSpendAlerts(recentAmount: Double) {
        val currentCalc = calculationState.value
        val currentSettings = settings.value
        val percent = currentCalc.percentTodayUsed

        val context = getApplication<Application>().applicationContext

        when {
            currentCalc.isOverToday -> {
                val overAmt = CurrencyFormatter.format(currentCalc.overspendTodayAmount)
                val newLimit = CurrencyFormatter.format(currentCalc.projectedTomorrowLimit)
                val daysLeft = currentCalc.daysRemainingIncludingToday - 1
                NotificationHelper.sendSpendAlert(
                    context = context,
                    notificationId = 1004,
                    title = "⚠️ Limit Crossed!",
                    message = "You overspent by $overAmt. Your daily limit for the next $daysLeft days is now $newLimit/day."
                )
            }
            percent >= 1.0f && currentSettings.notify100Percent -> {
                NotificationHelper.sendSpendAlert(
                    context = context,
                    notificationId = 1003,
                    title = "🚨 100% Daily Limit Reached",
                    message = "You have used your full daily limit of ${CurrencyFormatter.format(currentCalc.dailyLimitToday)}. Further spending will reduce tomorrow's limit."
                )
            }
            percent >= 0.8f && currentSettings.notify80Percent -> {
                val left = CurrencyFormatter.format(currentCalc.remainingToday)
                NotificationHelper.sendSpendAlert(
                    context = context,
                    notificationId = 1002,
                    title = "⚠️ 80% Daily Limit Used",
                    message = "You have used 80% of today's limit. Only $left remaining for today."
                )
            }
            percent >= 0.5f && currentSettings.notify50Percent -> {
                val left = CurrencyFormatter.format(currentCalc.remainingToday)
                NotificationHelper.sendSpendAlert(
                    context = context,
                    notificationId = 1001,
                    title = "💡 50% Daily Limit Used",
                    message = "You're halfway through today's limit ($left left)."
                )
            }
        }
    }

    fun updateTransaction(tx: TransactionEntity) {
        viewModelScope.launch {
            repository.update(tx)
            emitToast("Transaction updated")
        }
    }

    fun deleteTransaction(tx: TransactionEntity) {
        viewModelScope.launch {
            repository.delete(tx)
            emitToast("Transaction deleted")
        }
    }

    fun parseSms(text: String): ParsedSmsResult {
        return SmsParser.parse(text)
    }

    fun completeOnboarding(monthlyBudget: Double) {
        val ym = _currentDate.value.format(DateTimeFormatter.ofPattern("yyyy-MM"))
        userPrefs.setOnboardingDone(monthlyBudget, ym)
    }

    fun updateBudget(newBudget: Double, applyFromTodayOnly: Boolean) {
        val date = _currentDate.value
        val ym = date.format(DateTimeFormatter.ofPattern("yyyy-MM"))
        if (applyFromTodayOnly) {
            val calc = calculationState.value
            val rebalanced = BudgetCalculator.recalculateWithNewBudget(
                newMonthlyBudget = newBudget,
                applyFromTodayOnly = true,
                totalSpentBeforeToday = calc.totalSpentBeforeToday,
                spentToday = calc.spentToday,
                currentDayOfMonth = calc.currentDayOfMonth,
                totalDaysInMonth = calc.totalDaysInMonth
            )
            userPrefs.updateMonthlyBudget(rebalanced.monthlyBudget, ym)
        } else {
            userPrefs.updateMonthlyBudget(newBudget, ym)
        }
        emitToast("Monthly budget updated to ${CurrencyFormatter.format(newBudget)}")
    }

    fun addIntegratedUpiApp(appTag: String) {
        userPrefs.addIntegratedUpiApp(appTag)
        emitToast("Integrated $appTag for automatic tracking")
    }

    fun removeIntegratedUpiApp(appTag: String) {
        userPrefs.removeIntegratedUpiApp(appTag)
        emitToast("Removed $appTag from auto-tracking")
    }

    fun dismissMonthRollover() {
        val ym = _currentDate.value.format(DateTimeFormatter.ofPattern("yyyy-MM"))
        userPrefs.acknowledgeMonthRollover(ym)
    }

    fun clearCurrentMonthData() {
        viewModelScope.launch {
            val ym = _currentDate.value.format(DateTimeFormatter.ofPattern("yyyy-MM"))
            repository.clearMonth(ym)
            emitToast("Current month transactions reset")
        }
    }

    fun resetAllData() {
        viewModelScope.launch {
            repository.clearAll()
            userPrefs.resetAll()
            emitToast("All data cleared")
        }
    }

    fun generateCsv(): String {
        val txs = currentMonthTransactions.value
        val sb = StringBuilder()
        sb.append("ID,Date,Time,Amount,Merchant,UPI App,Category,Note\n")
        val dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd,HH:mm:ss")
        txs.forEach { tx ->
            val dateInstant = java.time.Instant.ofEpochMilli(tx.timestamp)
                .atZone(java.time.ZoneId.systemDefault())
                .format(dtf)
            val cleanMerchant = tx.merchant.replace("\"", "\"\"")
            val cleanNote = (tx.note ?: "").replace("\"", "\"\"")
            sb.append("${tx.id},$dateInstant,${tx.amount},\"$cleanMerchant\",${tx.upiApp},${tx.category},\"$cleanNote\"\n")
        }
        return sb.toString()
    }

    fun updateProfile(name: String, phone: String, email: String) {
        userPrefs.updateProfile(name, phone, email)
        emitToast("Profile details updated")
    }

    fun logout() {
        userPrefs.logout()
        emitToast("Logged out successfully")
    }

    private fun emitToast(msg: String) {
        viewModelScope.launch {
            _events.emit(UiEvent.ShowToast(msg))
        }
    }
}
