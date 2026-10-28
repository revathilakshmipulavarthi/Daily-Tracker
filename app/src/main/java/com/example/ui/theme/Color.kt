package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Monochrome Black & Gray Palette with Glassmorphism
val GrayPrimary = Color(0xFFFFFFFF)       // Crisp White / Platinum
val GraySecondary = Color(0xFFA1A1AA)     // Cool Silver Gray
val GrayDark = Color(0xFF18181E)          // Deep Charcoal
val GrayMuted = Color(0xFF71717A)         // Neutral Muted Gray

// Dark Theme (Pitch Black + Frosted Glass Grays)
val PrimaryDark = Color(0xFFFFFFFF)       // Pure Crisp White
val OnPrimaryDark = Color(0xFF000000)     // Pure Black on White
val PrimaryContainerDark = Color(0xFF22222B)
val OnPrimaryContainerDark = Color(0xFFF4F4F5)

val SecondaryDark = Color(0xFFA1A1AA)     // Silver Gray
val OnSecondaryDark = Color(0xFF000000)
val SecondaryContainerDark = Color(0xFF1A1A22)
val OnSecondaryContainerDark = Color(0xFFE4E4E7)

val BackgroundDark = Color(0xFF000000)    // Pure Pitch Black
val OnBackgroundDark = Color(0xFFFFFFFF)  // Crisp White
val SurfaceDark = Color(0xFF121217)       // Deep Frosted Dark Gray
val OnSurfaceDark = Color(0xFFFFFFFF)
val SurfaceVariantDark = Color(0xFF1C1C24)// Elevated Frosted Gray
val OnSurfaceVariantDark = Color(0xFF9CA3AF)
val OutlineDark = Color(0xFF2E2E3A)

// Light Theme (Clean Monochrome Slate)
val PrimaryLight = Color(0xFF18181B)
val OnPrimaryLight = Color(0xFFFFFFFF)
val PrimaryContainerLight = Color(0xFFE4E4E7)
val OnPrimaryContainerLight = Color(0xFF18181B)

val SecondaryLight = Color(0xFF52525B)
val OnSecondaryLight = Color(0xFFFFFFFF)
val SecondaryContainerLight = Color(0xFFF4F4F5)
val OnSecondaryContainerLight = Color(0xFF27272A)

val BackgroundLight = Color(0xFFF8F9FA)
val OnBackgroundLight = Color(0xFF09090B)
val SurfaceLight = Color(0xFFFFFFFF)
val OnSurfaceLight = Color(0xFF09090B)
val SurfaceVariantLight = Color(0xFFF4F4F5)
val OnSurfaceVariantLight = Color(0xFF52525B)
val OutlineLight = Color(0xFFE4E4E7)

// Glassmorphism Design Tokens
val GlassCardBackground = Color(0xFF14141B).copy(alpha = 0.72f)
val GlassCardBorder = Color.White.copy(alpha = 0.15f)
val GlassCardBorderSubtle = Color.White.copy(alpha = 0.08f)
val GlassSurfaceElevated = Color(0xFF22222C).copy(alpha = 0.60f)
val GlassPillBackground = Color(0xFF2A2A36).copy(alpha = 0.65f)

// Glassmorphism Gradient Brushes
val GlassCardGradient = Brush.verticalGradient(
    colors = listOf(
        Color(0xFF22222D).copy(alpha = 0.75f),
        Color(0xFF121218).copy(alpha = 0.85f)
    )
)

val GlassBorderGradient = Brush.verticalGradient(
    colors = listOf(
        Color.White.copy(alpha = 0.28f),
        Color.White.copy(alpha = 0.06f)
    )
)

// Calendar Stroke Highlighting Colors (Within Limit: Green, Excess Limit: Red, Remaining: Dark Gray)
val WithinLimitGreenStroke = Color(0xFF22C55E) // Vivid Emerald Green Stroke
val WithinLimitGreenFill = Color(0xFF22C55E).copy(alpha = 0.14f)

val ExcessLimitRedStroke = Color(0xFFEF4444)   // Vivid Crimson Red Stroke
val ExcessLimitRedFill = Color(0xFFEF4444).copy(alpha = 0.16f)

val RemainingDayGrayStroke = Color(0xFF2A2A34) // Sleek Dark Gray Border for remaining future days
val RemainingDayGrayFill = Color(0xFF14141B).copy(alpha = 0.65f) // Frosted dark gray
val RemainingDayNumberColor = Color(0xFFA1A1AA)// Subtle silver gray
val RemainingDayAmountColor = Color(0xFF71717A)// Muted dark gray limit text

// Status / Spend Meter Colors
val SpendGreen = Color(0xFF22C55E)
val SpendAmber = Color(0xFFF59E0B)
val SpendOrange = Color(0xFFF97316)
val SpendRed = Color(0xFFEF4444)
val SpendPurple = Color(0xFFA855F7)

// Legacy alias for compatibility
val LimePrimary = Color(0xFF22C55E)
val LimeLight = Color(0xFF4ADE80)
