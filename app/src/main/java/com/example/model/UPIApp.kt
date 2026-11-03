package com.example.model

import androidx.compose.ui.graphics.Color

enum class UPIApp(
    val displayName: String,
    val primaryColor: Color,
    val brandTag: String
) {
    GPAY("Google Pay", Color(0xFF1A73E8), "GPay"),
    PHONEPE("PhonePe", Color(0xFF5F259F), "PhonePe"),
    PAYTM("Paytm", Color(0xFF002E6E), "Paytm"),
    BHIM("BHIM UPI", Color(0xFF00796B), "BHIM"),
    OTHER("UPI", Color(0xFF38BDF8), "UPI"),
    MANUAL("Cash", Color(0xFF10B981), "Cash"),
    CARD("Card", Color(0xFFA855F7), "Card");

    val isManual: Boolean
        get() = this == MANUAL || this == CARD

    companion object {
        fun fromString(name: String?): UPIApp {
            if (name == null) return OTHER
            val lower = name.lowercase()
            return when {
                lower.contains("card") -> CARD
                lower.contains("manual") || lower.contains("cash") -> MANUAL
                lower.contains("gpay") || lower.contains("google pay") || lower.contains("googlepay") -> GPAY
                lower.contains("phonepe") || lower.contains("phone pe") -> PHONEPE
                lower.contains("paytm") -> PAYTM
                lower.contains("bhim") || lower.contains("npci") -> BHIM
                else -> OTHER
            }
        }
    }
}
