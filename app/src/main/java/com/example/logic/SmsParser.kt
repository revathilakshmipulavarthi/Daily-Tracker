package com.example.logic

import com.example.model.Category
import com.example.model.UPIApp
import java.util.regex.Pattern

data class ParsedSmsResult(
    val isValidDebit: Boolean,
    val isCreditIgnored: Boolean,
    val amount: Double?,
    val merchant: String?,
    val inferredUpiApp: UPIApp,
    val inferredCategory: Category,
    val dateText: String?,
    val rawText: String,
    val statusMessage: String
)

object SmsParser {

    private val CREDIT_KEYWORDS = listOf("credited", "credit", "received", "refunded", "cashback", "deposited")
    private val DEBIT_KEYWORDS = listOf("debited", "debit", "paid", "sent", "transferred", "spent", "deducted", "dr.")

    // Regex for amount: e.g. Rs. 450.00, INR 1,299, ₹500, 350.50 debited
    private val AMOUNT_PATTERNS = listOf(
        Pattern.compile("""(?:Rs\.?|INR|₹)\s*([0-9,]+(?:\.[0-9]{1,2})?)""", Pattern.CASE_INSENSITIVE),
        Pattern.compile("""debited\s*(?:by|for)?\s*(?:Rs\.?|INR|₹)?\s*([0-9,]+(?:\.[0-9]{1,2})?)""", Pattern.CASE_INSENSITIVE),
        Pattern.compile("""paid\s*(?:Rs\.?|INR|₹)?\s*([0-9,]+(?:\.[0-9]{1,2})?)""", Pattern.CASE_INSENSITIVE),
        Pattern.compile("""sent\s*(?:Rs\.?|INR|₹)?\s*([0-9,]+(?:\.[0-9]{1,2})?)""", Pattern.CASE_INSENSITIVE)
    )

    // Regex for merchant or VPA
    private val MERCHANT_PATTERNS = listOf(
        Pattern.compile("""(?:to\s+VPA|to)\s+([A-Za-z0-9@_.\s\-&]{2,35}?)(?=\s+(?:on|via|ref|upi|avail|bal|\.|\,))""", Pattern.CASE_INSENSITIVE),
        Pattern.compile("""paid\s+towards\s+([A-Za-z0-9@_.\s\-&]{2,35}?)(?=\s+(?:on|via|ref|upi|avail|bal|\.|\,))""", Pattern.CASE_INSENSITIVE),
        Pattern.compile("""transfer\s+to\s+([A-Za-z0-9@_.\s\-&]{2,35}?)(?=\s+(?:on|via|ref|upi|avail|bal|\.|\,))""", Pattern.CASE_INSENSITIVE),
        Pattern.compile("""(?:at|info:)\s+([A-Za-z0-9@_.\s\-&]{2,30}?)(?=\s+(?:on|via|ref|upi|avail|bal|\.|\,))""", Pattern.CASE_INSENSITIVE)
    )

    // Regex for date: e.g. 28-Oct-26, 28/10/2026, 28-10-26
    private val DATE_PATTERN = Pattern.compile(
        """\b(\d{1,2}[-/\.](?:Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec|\d{1,2})[-/\.]\d{2,4})\b""",
        Pattern.CASE_INSENSITIVE
    )

    fun parse(smsText: String): ParsedSmsResult {
        val trimmed = smsText.trim()
        if (trimmed.isEmpty()) {
            return ParsedSmsResult(
                isValidDebit = false,
                isCreditIgnored = false,
                amount = null,
                merchant = null,
                inferredUpiApp = UPIApp.OTHER,
                inferredCategory = Category.OTHER,
                dateText = null,
                rawText = trimmed,
                statusMessage = "Please paste an SMS or notification text."
            )
        }

        val lower = trimmed.lowercase()

        // Check for credit (we strictly reject credits per requirement)
        val hasCreditWord = CREDIT_KEYWORDS.any { lower.contains(it) }
        val hasDebitWord = DEBIT_KEYWORDS.any { lower.contains(it) }

        if (hasCreditWord && !hasDebitWord) {
            return ParsedSmsResult(
                isValidDebit = false,
                isCreditIgnored = true,
                amount = null,
                merchant = null,
                inferredUpiApp = UPIApp.OTHER,
                inferredCategory = Category.OTHER,
                dateText = null,
                rawText = trimmed,
                statusMessage = "Ignored: This message appears to be a credit/refund (DayLimit only tracks debits)."
            )
        }

        if (!hasDebitWord && !lower.contains("upi") && !lower.contains("vpa")) {
            return ParsedSmsResult(
                isValidDebit = false,
                isCreditIgnored = false,
                amount = null,
                merchant = null,
                inferredUpiApp = UPIApp.OTHER,
                inferredCategory = Category.OTHER,
                dateText = null,
                rawText = trimmed,
                statusMessage = "Could not identify a debit transaction in the pasted text."
            )
        }

        // 1. Extract Amount
        var parsedAmount: Double? = null
        for (pattern in AMOUNT_PATTERNS) {
            val matcher = pattern.matcher(trimmed)
            if (matcher.find()) {
                val rawAmt = matcher.group(1)?.replace(",", "")
                val amt = rawAmt?.toDoubleOrNull()
                if (amt != null && amt > 0.0) {
                    parsedAmount = amt
                    break
                }
            }
        }

        // 2. Extract Merchant / VPA
        var parsedMerchant: String? = null
        for (pattern in MERCHANT_PATTERNS) {
            val matcher = pattern.matcher(trimmed)
            if (matcher.find()) {
                val candidate = matcher.group(1)?.trim()
                if (!candidate.isNullOrBlank() && candidate.length > 1) {
                    // Filter out false positive single words like "A/c"
                    if (!candidate.equals("a/c", ignoreCase = true) && !candidate.equals("account", ignoreCase = true)) {
                        parsedMerchant = cleanMerchantName(candidate)
                        break
                    }
                }
            }
        }

        // Fallback: look for @upi address if merchant still null
        if (parsedMerchant == null) {
            val vpaPattern = Pattern.compile("""([A-Za-z0-9._-]+@[A-Za-z]{2,15})""")
            val vpaMatcher = vpaPattern.matcher(trimmed)
            if (vpaMatcher.find()) {
                parsedMerchant = vpaMatcher.group(1)
            }
        }

        // 3. Extract Date if present
        var dateText: String? = null
        val dateMatcher = DATE_PATTERN.matcher(trimmed)
        if (dateMatcher.find()) {
            dateText = dateMatcher.group(1)
        }

        // 4. Infer UPI App
        val inferredUpi = UPIApp.fromString(trimmed)

        // 5. Infer Category from merchant name
        val finalMerchant = parsedMerchant ?: "UPI Payment"
        val inferredCategory = Category.inferFromText("$finalMerchant $trimmed")

        val isValid = parsedAmount != null && parsedAmount > 0.0

        return ParsedSmsResult(
            isValidDebit = isValid,
            isCreditIgnored = false,
            amount = parsedAmount,
            merchant = finalMerchant,
            inferredUpiApp = inferredUpi,
            inferredCategory = inferredCategory,
            dateText = dateText,
            rawText = trimmed,
            statusMessage = if (isValid) "Successfully extracted debit transaction!" else "Could not clearly detect the amount."
        )
    }

    private fun cleanMerchantName(raw: String): String {
        return raw.replace(Regex("""(?i)\b(upi|ref|no|on|a/c|via|vpa)\b.*"""), "")
            .replace(Regex("""[,\.]+$"""), "")
            .trim()
    }
}
