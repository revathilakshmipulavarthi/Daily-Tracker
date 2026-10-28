package com.example.service

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.example.data.AppDatabase
import com.example.data.TransactionEntity
import com.example.data.TransactionRepository
import com.example.logic.CurrencyFormatter
import com.example.logic.NotificationHelper
import com.example.logic.SmsParser
import com.example.model.UPIApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class UpiNotificationListenerService : NotificationListenerService() {

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    // Known UPI and banking package identifiers
    private val upiPackages = setOf(
        "com.google.android.apps.nbu.paisa.user", // Google Pay
        "com.phonepe.app",                      // PhonePe
        "net.one97.paytm",                      // Paytm
        "in.org.npci.upiapp",                   // BHIM
        "com.dreamplug.androidapp",             // CRED
        "in.amazon.mShop.android.shopping",      // Amazon Pay
        "com.msf.kbank.mobile",                 // Kotak
        "com.snapwork.hdfc",                    // HDFC
        "com.csam.icici.bank.imobile",          // ICICI
        "com.sbi.lotusintouch",                 // SBI Yono
        "com.axis.mobile"                       // Axis Bank
    )

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        if (sbn == null) return

        val pkgName = sbn.packageName?.lowercase() ?: ""
        val extras = sbn.notification?.extras ?: return

        val title = extras.getString(Notification.EXTRA_TITLE) ?: ""
        val text = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString() ?: ""
        val bigText = extras.getCharSequence(Notification.EXTRA_BIG_TEXT)?.toString() ?: ""

        val combinedContent = "$title $text $bigText".trim()
        if (combinedContent.isBlank()) return

        // Check if package is a known UPI/bank app, or if the text explicitly contains UPI debit markers
        val isTargetApp = upiPackages.any { pkgName.contains(it) } ||
                pkgName.contains("bank") ||
                pkgName.contains("pay") ||
                pkgName.contains("upi") ||
                pkgName.contains("messaging")

        val lower = combinedContent.lowercase()
        val hasDebitKeywords = lower.contains("debited") || lower.contains("paid") ||
                lower.contains("sent") || lower.contains("transfer to") || lower.contains("transferred")

        if (!isTargetApp && !hasDebitKeywords) return

        // Parse with SmsParser
        val parsed = SmsParser.parse(combinedContent)

        if (parsed.isValidDebit && parsed.amount != null && parsed.amount > 0.0) {
            val inferredUpi = if (parsed.inferredUpiApp != UPIApp.OTHER) {
                parsed.inferredUpiApp
            } else {
                when {
                    pkgName.contains("paisa") -> UPIApp.GPAY
                    pkgName.contains("phonepe") -> UPIApp.PHONEPE
                    pkgName.contains("paytm") -> UPIApp.PAYTM
                    pkgName.contains("bhim") || pkgName.contains("npci") -> UPIApp.BHIM
                    else -> UPIApp.OTHER
                }
            }

            processAutoDetectedDebit(
                amount = parsed.amount,
                merchant = parsed.merchant ?: "UPI Payment",
                upiApp = inferredUpi,
                note = "Auto-tracked via ${inferredUpi.displayName}"
            )
        }
    }

    private fun processAutoDetectedDebit(
        amount: Double,
        merchant: String,
        upiApp: UPIApp,
        note: String
    ) {
        serviceScope.launch {
            val db = AppDatabase.getDatabase(applicationContext)
            val repository = TransactionRepository(db.transactionDao())

            // Prevent duplicate entries if both SMS and UPI push notification fire for the same payment
            val duplicate = repository.checkLikelyDuplicate(amount, withinMillis = 120_000)
            if (duplicate != null) {
                return@launch
            }

            val today = LocalDate.now()
            val ym = today.format(DateTimeFormatter.ofPattern("yyyy-MM"))
            val day = today.dayOfMonth

            val tx = TransactionEntity(
                amount = amount,
                merchant = merchant,
                upiApp = upiApp.name,
                category = com.example.model.Category.inferFromText(merchant).name,
                timestamp = System.currentTimeMillis(),
                note = note,
                yearMonth = ym,
                dayOfMonth = day
            )

            repository.insert(tx)

            // Post confirmation alert
            NotificationHelper.sendSpendAlert(
                context = applicationContext,
                notificationId = (System.currentTimeMillis() % 10000).toInt() + 2000,
                title = "⚡ Auto-tracked ${CurrencyFormatter.format(amount)} via ${upiApp.brandTag}",
                message = "Paid to $merchant. DayLimit has automatically updated your daily spend limit!"
            )
        }
    }
}
