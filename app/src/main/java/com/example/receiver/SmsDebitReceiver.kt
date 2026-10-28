package com.example.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import com.example.data.AppDatabase
import com.example.data.TransactionEntity
import com.example.data.TransactionRepository
import com.example.logic.CurrencyFormatter
import com.example.logic.NotificationHelper
import com.example.logic.SmsParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class SmsDebitReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return

        if (intent.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
            val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent) ?: return

            val fullBody = StringBuilder()
            var sender = ""

            for (sms in messages) {
                fullBody.append(sms.messageBody)
                if (sender.isEmpty()) {
                    sender = sms.originatingAddress ?: ""
                }
            }

            val text = fullBody.toString()
            if (text.isBlank()) return

            val parsed = SmsParser.parse(text)

            if (parsed.isValidDebit && parsed.amount != null && parsed.amount > 0.0) {
                val pendingResult = goAsync()

                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val db = AppDatabase.getDatabase(context)
                        val repository = TransactionRepository(db.transactionDao())

                        val duplicate = repository.checkLikelyDuplicate(parsed.amount, withinMillis = 120_000)
                        if (duplicate == null) {
                            val today = LocalDate.now()
                            val ym = today.format(DateTimeFormatter.ofPattern("yyyy-MM"))
                            val day = today.dayOfMonth

                            val tx = TransactionEntity(
                                amount = parsed.amount,
                                merchant = parsed.merchant ?: (if (sender.isNotBlank()) sender else "Bank Debit"),
                                upiApp = parsed.inferredUpiApp.name,
                                category = parsed.inferredCategory.name,
                                timestamp = System.currentTimeMillis(),
                                note = "Auto-tracked SMS from $sender",
                                yearMonth = ym,
                                dayOfMonth = day
                            )

                            repository.insert(tx)

                            NotificationHelper.sendSpendAlert(
                                context = context,
                                notificationId = (System.currentTimeMillis() % 10000).toInt() + 3000,
                                title = "⚡ Auto-tracked ${CurrencyFormatter.format(parsed.amount)}",
                                message = "Debit from $sender. DayLimit recalculated your daily limit!"
                            )
                        }
                    } finally {
                        pendingResult.finish()
                    }
                }
            }
        }
    }
}
