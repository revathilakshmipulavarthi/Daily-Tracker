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
    OTHER("Other UPI", Color(0xFF546E7A), "UPI");

    companion object {
        fun fromString(name: String?): UPIApp {
            if (name == null) return OTHER
            val lower = name.lowercase()
            return when {
                lower.contains("gpay") || lower.contains("google pay") || lower.contains("googlepay") -> GPAY
                lower.contains("phonepe") || lower.contains("phone pe") -> PHONEPE
                lower.contains("paytm") -> PAYTM
                lower.contains("bhim") || lower.contains("npci") -> BHIM
                else -> OTHER
            }
        }
    }
}
