package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.UPIApp

@Composable
fun UpiBadge(
    upiApp: UPIApp,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    val chipModifier = modifier
        .clip(RoundedCornerShape(20.dp))
        .background(
            if (isSelected) upiApp.primaryColor.copy(alpha = 0.15f)
            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
        .then(
            if (isSelected) Modifier.border(1.5.dp, upiApp.primaryColor, RoundedCornerShape(20.dp))
            else Modifier
        )
        .then(
            if (onClick != null) Modifier.clickable { onClick() } else Modifier
        )
        .padding(horizontal = 10.dp, vertical = 6.dp)
        .testTag("upi_badge_${upiApp.name}")

    Row(
        modifier = chipModifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Colored dot
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(upiApp.primaryColor)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = upiApp.brandTag,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                fontSize = 12.sp
            ),
            color = if (isSelected) upiApp.primaryColor else MaterialTheme.colorScheme.onSurface
        )
    }
}
