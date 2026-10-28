package com.example.model

import androidx.compose.ui.graphics.Color

enum class Category(
    val displayName: String,
    val iconName: String,
    val accentColor: Color
) {
    FOOD("Food & Dining", "Restaurant", Color(0xFFE65100)),
    GROCERIES("Groceries", "ShoppingCart", Color(0xFF2E7D32)),
    SHOPPING("Shopping", "ShoppingBag", Color(0xFFC2185B)),
    TRAVEL("Travel & Commute", "DirectionsCar", Color(0xFF0288D1)),
    BILLS("Bills & Utilities", "Receipt", Color(0xFF7B1FA2)),
    ENTERTAINMENT("Entertainment", "Movie", Color(0xFFD81B60)),
    MEDICAL("Health & Meds", "LocalHospital", Color(0xFFC62828)),
    OTHER("Other", "MoreHoriz", Color(0xFF607D8B));

    companion object {
        fun inferFromText(text: String): Category {
            val lower = text.lowercase()
            return when {
                lower.contains("swiggy") || lower.contains("zomato") || lower.contains("food") ||
                        lower.contains("restaurant") || lower.contains("cafe") || lower.contains("tea") ||
                        lower.contains("coffee") || lower.contains("burger") || lower.contains("pizza") -> FOOD
                lower.contains("blinkit") || lower.contains("zepto") || lower.contains("instamart") ||
                        lower.contains("grocery") || lower.contains("supermarket") || lower.contains("dmart") ||
                        lower.contains("milk") || lower.contains("vegetable") -> GROCERIES
                lower.contains("amazon") || lower.contains("flipkart") || lower.contains("myntra") ||
                        lower.contains("cloth") || lower.contains("apparel") || lower.contains("store") -> SHOPPING
                lower.contains("uber") || lower.contains("ola") || lower.contains("rapido") ||
                        lower.contains("metro") || lower.contains("fuel") || lower.contains("petrol") ||
                        lower.contains("diesel") || lower.contains("auto") -> TRAVEL
                lower.contains("electricity") || lower.contains("broadband") || lower.contains("wifi") ||
                        lower.contains("recharge") || lower.contains("airtel") || lower.contains("jio") ||
                        lower.contains("bill") || lower.contains("gas") || lower.contains("rent") -> BILLS
                lower.contains("netflix") || lower.contains("spotify") || lower.contains("cinema") ||
                        lower.contains("movie") || lower.contains("game") || lower.contains("pvr") -> ENTERTAINMENT
                lower.contains("pharmacy") || lower.contains("hospital") || lower.contains("doctor") ||
                        lower.contains("chemist") || lower.contains("apollo") || lower.contains("1mg") -> MEDICAL
                else -> OTHER
            }
        }
    }
}
