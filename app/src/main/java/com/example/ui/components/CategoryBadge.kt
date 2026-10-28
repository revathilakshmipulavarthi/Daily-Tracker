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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Category

@Composable
fun CategoryBadge(
    category: Category,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    val icon: ImageVector = when (category) {
        Category.FOOD -> Icons.Default.Restaurant
        Category.GROCERIES -> Icons.Default.ShoppingCart
        Category.SHOPPING -> Icons.Default.ShoppingBag
        Category.TRAVEL -> Icons.Default.DirectionsCar
        Category.BILLS -> Icons.Default.Receipt
        Category.ENTERTAINMENT -> Icons.Default.Movie
        Category.MEDICAL -> Icons.Default.LocalHospital
        Category.OTHER -> Icons.Default.MoreHoriz
    }

    val chipModifier = modifier
        .clip(RoundedCornerShape(20.dp))
        .background(
            if (isSelected) category.accentColor.copy(alpha = 0.15f)
            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
        .then(
            if (isSelected) Modifier.border(1.5.dp, category.accentColor, RoundedCornerShape(20.dp))
            else Modifier
        )
        .then(
            if (onClick != null) Modifier.clickable { onClick() } else Modifier
        )
        .padding(horizontal = 10.dp, vertical = 6.dp)
        .testTag("category_badge_${category.name}")

    Row(
        modifier = chipModifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = category.displayName,
            tint = if (isSelected) category.accentColor else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = category.displayName,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                fontSize = 12.sp
            ),
            color = if (isSelected) category.accentColor else MaterialTheme.colorScheme.onSurface
        )
    }
}
