package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.logic.CurrencyFormatter
import com.example.model.DayLimitCalculation
import com.example.ui.theme.SpendAmber
import com.example.ui.theme.SpendGreen
import com.example.ui.theme.SpendOrange
import com.example.ui.theme.SpendRed

@Composable
fun CircularProgressRing(
    calculation: DayLimitCalculation,
    modifier: Modifier = Modifier,
    size: Dp = 230.dp,
    strokeWidth: Dp = 16.dp
) {
    val percentUsed = calculation.percentTodayUsed.coerceIn(0f, 1.25f)

    val animatedSweep by animateFloatAsState(
        targetValue = (percentUsed.coerceAtMost(1f) * 360f),
        animationSpec = tween(durationMillis = 700, easing = FastOutSlowInEasing),
        label = "progressSweep"
    )

    // Dynamic ring color depending on threshold
    val targetColor = when {
        calculation.isOverToday -> SpendRed
        percentUsed >= 1.0f -> SpendRed
        percentUsed >= 0.8f -> SpendOrange
        percentUsed >= 0.5f -> SpendAmber
        else -> SpendGreen
    }

    val animatedColor by animateColorAsState(
        targetValue = targetColor,
        animationSpec = tween(durationMillis = 400),
        label = "colorAnim"
    )

    val trackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)

    Box(
        modifier = modifier
            .size(size)
            .testTag("circular_progress_ring"),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize().padding(strokeWidth / 2)) {
            val canvasSize = this.size.minDimension
            val strokePx = strokeWidth.toPx()
            val arcSize = Size(canvasSize, canvasSize)
            val topLeft = Offset(
                (this.size.width - canvasSize) / 2f,
                (this.size.height - canvasSize) / 2f
            )

            // Background Track
            drawArc(
                color = trackColor,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokePx, cap = StrokeCap.Round)
            )

            // Animated Active Arc
            if (animatedSweep > 0f) {
                drawArc(
                    brush = Brush.sweepGradient(
                        0.0f to animatedColor.copy(alpha = 0.85f),
                        0.5f to animatedColor,
                        1.0f to animatedColor,
                        center = Offset(this.size.width / 2f, this.size.height / 2f)
                    ),
                    startAngle = -90f,
                    sweepAngle = animatedSweep,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokePx, cap = StrokeCap.Round)
                )
            }
        }

        // Inner Content Text
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "TODAY'S SPENT",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 1.2.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = CurrencyFormatter.format(calculation.spentToday),
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp
                ),
                color = if (calculation.isOverToday) SpendRed else MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "of ${CurrencyFormatter.format(calculation.dailyLimitToday)} limit",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Percentage pill badge
            val displayPct = (calculation.percentTodayUsed * 100).toInt()
            val statusLabel = when {
                calculation.isOverToday -> "Over by ${CurrencyFormatter.format(calculation.overspendTodayAmount)}"
                displayPct >= 100 -> "Limit reached"
                else -> "$displayPct% used"
            }

            Text(
                text = statusLabel,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                color = animatedColor
            )
        }
    }
}
