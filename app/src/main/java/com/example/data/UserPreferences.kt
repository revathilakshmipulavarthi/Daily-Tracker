package com.example.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class UserSettings(
    val monthlyBudget: Double = 15000.0,
    val lastConfiguredYearMonth: String = "",
    val isOnboardingDone: Boolean = false,
    val isLoggedIn: Boolean = false,
    val isSignedUp: Boolean = false,
    val notify50Percent: Boolean = true,
    val notify80Percent: Boolean = true,
    val notify100Percent: Boolean = true,
    val eveningReminderEnabled: Boolean = true,
    val eveningReminderTime: String = "21:00", // 9:00 PM
    val useIndianGrouping: Boolean = true,
    val isDarkMode: Boolean? = true,
    val integratedUpiApps: Set<String> = emptySet(),
    val userName: String = "Revathi Lakshmi",
    val userPhone: String = "+91 98765 43210",
    val userEmail: String = "revathilakshmipulavarthi@gmail.com",
    val subscriptionPlan: String = "Free Edition",
    val subscriptionPrice: String = "Free",
    val isSubscribed: Boolean = true,
    val subscriptionRenewal: String = "Lifetime Free"
)

class UserPreferences(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("daylimit_prefs", Context.MODE_PRIVATE)

    private val _settings = MutableStateFlow(loadSettings())
    val settings: StateFlow<UserSettings> = _settings.asStateFlow()

    private fun loadSettings(): UserSettings {
        val hasDarkMode = prefs.contains(KEY_DARK_MODE)
        val darkModeVal = if (hasDarkMode) prefs.getBoolean(KEY_DARK_MODE, true) else true
        val integratedApps = prefs.getStringSet(KEY_INTEGRATED_UPI_APPS, emptySet()) ?: emptySet()

        return UserSettings(
            monthlyBudget = prefs.getFloat(KEY_MONTHLY_BUDGET, 15000f).toDouble(),
            lastConfiguredYearMonth = prefs.getString(KEY_LAST_MONTH, "") ?: "",
            isOnboardingDone = prefs.getBoolean(KEY_ONBOARDING_DONE, false),
            isLoggedIn = prefs.getBoolean(KEY_IS_LOGGED_IN, false),
            isSignedUp = prefs.getBoolean(KEY_IS_SIGNED_UP, false),
            notify50Percent = prefs.getBoolean(KEY_NOTIFY_50, true),
            notify80Percent = prefs.getBoolean(KEY_NOTIFY_80, true),
            notify100Percent = prefs.getBoolean(KEY_NOTIFY_100, true),
            eveningReminderEnabled = prefs.getBoolean(KEY_EVENING_REMINDER, true),
            eveningReminderTime = prefs.getString(KEY_EVENING_TIME, "21:00") ?: "21:00",
            useIndianGrouping = prefs.getBoolean(KEY_INDIAN_GROUPING, true),
            isDarkMode = darkModeVal,
            integratedUpiApps = integratedApps,
            userName = prefs.getString(KEY_USER_NAME, "Revathi Lakshmi") ?: "Revathi Lakshmi",
            userPhone = prefs.getString(KEY_USER_PHONE, "+91 98765 43210") ?: "+91 98765 43210",
            userEmail = prefs.getString(KEY_USER_EMAIL, "revathilakshmipulavarthi@gmail.com") ?: "revathilakshmipulavarthi@gmail.com",
            subscriptionPlan = prefs.getString(KEY_SUB_PLAN, "Free Edition") ?: "Free Edition",
            subscriptionPrice = prefs.getString(KEY_SUB_PRICE, "Free") ?: "Free",
            isSubscribed = prefs.getBoolean(KEY_IS_SUBSCRIBED, true),
            subscriptionRenewal = prefs.getString(KEY_SUB_RENEWAL, "Lifetime Free") ?: "Lifetime Free"
        )
    }

    fun registerUser(name: String, phone: String, email: String) {
        prefs.edit()
            .putBoolean(KEY_IS_LOGGED_IN, true)
            .putBoolean(KEY_IS_SIGNED_UP, true)
            .putString(KEY_USER_NAME, name)
            .putString(KEY_USER_PHONE, phone)
            .putString(KEY_USER_EMAIL, email)
            .apply()
        _settings.value = _settings.value.copy(
            isLoggedIn = true,
            isSignedUp = true,
            userName = name,
            userPhone = phone,
            userEmail = email
        )
    }

    fun loginUser(phoneOrEmail: String, isEmail: Boolean) {
        val editor = prefs.edit().putBoolean(KEY_IS_LOGGED_IN, true)
        var updatedName = _settings.value.userName
        var updatedEmail = _settings.value.userEmail
        var updatedPhone = _settings.value.userPhone

        if (isEmail) {
            editor.putString(KEY_USER_EMAIL, phoneOrEmail)
            updatedEmail = phoneOrEmail
            if (updatedName.isBlank() || updatedName == "Guest User") {
                updatedName = phoneOrEmail.substringBefore("@").replace(".", " ")
                    .split(" ").joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } }
                editor.putString(KEY_USER_NAME, updatedName)
            }
        } else {
            editor.putString(KEY_USER_PHONE, phoneOrEmail)
            updatedPhone = phoneOrEmail
            if (updatedName.isBlank() || updatedName == "Guest User") {
                updatedName = "User ${phoneOrEmail.takeLast(4)}"
                editor.putString(KEY_USER_NAME, updatedName)
            }
        }
        editor.apply()
        _settings.value = _settings.value.copy(
            isLoggedIn = true,
            userName = updatedName,
            userEmail = updatedEmail,
            userPhone = updatedPhone
        )
    }

    fun updateProfile(name: String, phone: String, email: String) {
        prefs.edit()
            .putString(KEY_USER_NAME, name)
            .putString(KEY_USER_PHONE, phone)
            .putString(KEY_USER_EMAIL, email)
            .apply()
        _settings.value = _settings.value.copy(
            userName = name,
            userPhone = phone,
            userEmail = email
        )
    }

    fun logout() {
        prefs.edit()
            .putBoolean(KEY_IS_LOGGED_IN, false)
            .apply()
        _settings.value = _settings.value.copy(
            isLoggedIn = false
        )
    }

    fun updateMonthlyBudget(newBudget: Double, currentYearMonth: String) {
        prefs.edit()
            .putFloat(KEY_MONTHLY_BUDGET, newBudget.toFloat())
            .putString(KEY_LAST_MONTH, currentYearMonth)
            .apply()
        _settings.value = _settings.value.copy(
            monthlyBudget = newBudget,
            lastConfiguredYearMonth = currentYearMonth
        )
    }

    fun setOnboardingDone(budget: Double, currentYearMonth: String) {
        prefs.edit()
            .putBoolean(KEY_ONBOARDING_DONE, true)
            .putFloat(KEY_MONTHLY_BUDGET, budget.toFloat())
            .putString(KEY_LAST_MONTH, currentYearMonth)
            .apply()
        _settings.value = _settings.value.copy(
            isOnboardingDone = true,
            monthlyBudget = budget,
            lastConfiguredYearMonth = currentYearMonth
        )
    }

    fun setNotificationToggles(at50: Boolean, at80: Boolean, at100: Boolean, evening: Boolean, time: String) {
        prefs.edit()
            .putBoolean(KEY_NOTIFY_50, at50)
            .putBoolean(KEY_NOTIFY_80, at80)
            .putBoolean(KEY_NOTIFY_100, at100)
            .putBoolean(KEY_EVENING_REMINDER, evening)
            .putString(KEY_EVENING_TIME, time)
            .apply()
        _settings.value = _settings.value.copy(
            notify50Percent = at50,
            notify80Percent = at80,
            notify100Percent = at100,
            eveningReminderEnabled = evening,
            eveningReminderTime = time
        )
    }

    fun setUseIndianGrouping(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_INDIAN_GROUPING, enabled).apply()
        _settings.value = _settings.value.copy(useIndianGrouping = enabled)
    }

    fun setDarkMode(darkMode: Boolean?) {
        val editor = prefs.edit()
        if (darkMode == null) {
            editor.remove(KEY_DARK_MODE)
        } else {
            editor.putBoolean(KEY_DARK_MODE, darkMode)
        }
        editor.apply()
        _settings.value = _settings.value.copy(isDarkMode = darkMode)
    }

    fun addIntegratedUpiApp(appTag: String) {
        val updated = _settings.value.integratedUpiApps + appTag.uppercase()
        prefs.edit().putStringSet(KEY_INTEGRATED_UPI_APPS, updated).apply()
        _settings.value = _settings.value.copy(integratedUpiApps = updated)
    }

    fun removeIntegratedUpiApp(appTag: String) {
        val updated = _settings.value.integratedUpiApps - appTag.uppercase()
        prefs.edit().putStringSet(KEY_INTEGRATED_UPI_APPS, updated).apply()
        _settings.value = _settings.value.copy(integratedUpiApps = updated)
    }

    fun acknowledgeMonthRollover(currentYearMonth: String) {
        prefs.edit().putString(KEY_LAST_MONTH, currentYearMonth).apply()
        _settings.value = _settings.value.copy(lastConfiguredYearMonth = currentYearMonth)
    }

    fun resetAll() {
        prefs.edit().clear().apply()
        _settings.value = UserSettings()
    }

    companion object {
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_IS_SIGNED_UP = "is_signed_up"
        private const val KEY_MONTHLY_BUDGET = "monthly_budget"
        private const val KEY_LAST_MONTH = "last_month"
        private const val KEY_ONBOARDING_DONE = "onboarding_done"
        private const val KEY_NOTIFY_50 = "notify_50"
        private const val KEY_NOTIFY_80 = "notify_80"
        private const val KEY_NOTIFY_100 = "notify_100"
        private const val KEY_EVENING_REMINDER = "evening_reminder"
        private const val KEY_EVENING_TIME = "evening_time"
        private const val KEY_INDIAN_GROUPING = "indian_grouping"
        private const val KEY_DARK_MODE = "dark_mode"
        private const val KEY_INTEGRATED_UPI_APPS = "integrated_upi_apps"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_PHONE = "user_phone"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_SUB_PLAN = "sub_plan"
        private const val KEY_SUB_PRICE = "sub_price"
        private const val KEY_IS_SUBSCRIBED = "is_subscribed"
        private const val KEY_SUB_RENEWAL = "sub_renewal"
    }
}
