package com.example.logic

import java.text.DecimalFormat
import java.util.Locale
import kotlin.math.abs

object CurrencyFormatter {

    /**
     * Formats an amount into standard Indian Rupee notation (e.g. ₹ 1,25,000 or ₹ 500)
     * using Indian numbering system (thousands, lakhs, crores).
     */
    fun format(
        amount: Double,
        includeDecimalsIfPresent: Boolean = false,
        useIndianGrouping: Boolean = true
    ): String {
        val isNegative = amount < 0
        val absVal = abs(amount)

        val formattedNumber = if (useIndianGrouping) {
            formatIndianGrouping(absVal, includeDecimalsIfPresent)
        } else {
            val df = DecimalFormat("#,##0.##")
            df.format(absVal)
        }

        return if (isNegative) "-₹$formattedNumber" else "₹$formattedNumber"
    }

    private fun formatIndianGrouping(value: Double, includeDecimals: Boolean): String {
        val longPart = value.toLong()
        val decimalPart = (value - longPart)

        val strLong = longPart.toString()
        val len = strLong.length

        val sb = StringBuilder()
        if (len <= 3) {
            sb.append(strLong)
        } else {
            // Last 3 digits
            val lastThree = strLong.substring(len - 3)
            val rest = strLong.substring(0, len - 3)

            // Group the rest by 2s from right to left
            val restGrouped = StringBuilder()
            var count = 0
            for (i in rest.length - 1 downTo 0) {
                restGrouped.append(rest[i])
                count++
                if (count == 2 && i > 0) {
                    restGrouped.append(",")
                    count = 0
                }
            }
            sb.append(restGrouped.reverse())
            sb.append(",")
            sb.append(lastThree)
        }

        if (includeDecimals && decimalPart > 0.009) {
            val decStr = String.format(Locale.US, "%.2f", decimalPart).substring(1) // e.g. ".50"
            sb.append(decStr)
        }

        return sb.toString()
    }

    /**
     * Compact format for charts or badges (e.g., ₹1.5L, ₹12K, ₹500)
     */
    fun formatCompact(amount: Double): String {
        val absVal = abs(amount)
        val prefix = if (amount < 0) "-₹" else "₹"
        return when {
            absVal >= 10_000_000 -> String.format(Locale.US, "%s%.1fCr", prefix, absVal / 10_000_000)
            absVal >= 100_000 -> String.format(Locale.US, "%s%.1fL", prefix, absVal / 100_000)
            absVal >= 1_000 -> String.format(Locale.US, "%s%.1fK", prefix, absVal / 1_000)
            else -> "$prefix${absVal.toInt()}"
        }
    }
}
