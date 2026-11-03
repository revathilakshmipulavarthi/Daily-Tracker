package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.painterResource
import com.example.R
import com.example.model.Category
import com.example.model.UPIApp
import com.example.ui.components.CategoryBadge
import com.example.ui.components.NumericKeypad

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickAddSheet(
    onDismiss: () -> Unit,
    onSave: (amount: Double, merchant: String, upiApp: UPIApp, category: Category, note: String?) -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var amountString by remember { mutableStateOf("") }
    var merchantNote by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(Category.FOOD) }
    var isManualMode by remember { mutableStateOf(false) }
    var selectedUpiApp by remember { mutableStateOf(UPIApp.OTHER) }

    val amountValue = amountString.toDoubleOrNull() ?: 0.0

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val iconTint = selectedUpiApp.primaryColor
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(iconTint.copy(alpha = 0.18f)),
                        contentAlignment = Alignment.Center
                    ) {
                        when (selectedUpiApp) {
                            UPIApp.MANUAL -> {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_cash),
                                    contentDescription = "Cash Spend",
                                    tint = iconTint,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            UPIApp.CARD -> {
                                Icon(
                                    imageVector = Icons.Default.CreditCard,
                                    contentDescription = "Card Spend",
                                    tint = iconTint,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            else -> {
                                Icon(
                                    imageVector = Icons.Default.QrCode,
                                    contentDescription = "UPI Spend",
                                    tint = iconTint,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Add Expenses",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (isManualMode) "Manual Transaction (${selectedUpiApp.displayName})"
                                   else "UPI Transaction (${selectedUpiApp.displayName})",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF9CA3AF)
                        )
                    }
                }
                IconButton(onClick = onDismiss, modifier = Modifier.testTag("quick_add_close")) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Amount Display
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                .padding(vertical = 16.dp, horizontal = 20.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "₹",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 36.sp
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (amountString.isEmpty()) "0" else amountString,
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 42.sp
                        ),
                        color = if (amountString.isEmpty()) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        else MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.testTag("quick_add_amount_text")
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Quick add increment pills (+50, +100, +200, +500)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(50, 100, 200, 500).forEach { inc ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .clickable {
                                val current = amountString.toDoubleOrNull() ?: 0.0
                                val next = (current + inc).toInt().toString()
                                amountString = next
                            }
                            .padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "+₹$inc",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Transaction Method Selector (UPI vs Manual: Cash / Card)
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "TRANSACTION METHOD",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 1.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(6.dp))

                // Primary 2-Way Switcher: UPI vs Manual
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // UPI Tab
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(9.dp))
                            .background(
                                if (!isManualMode) MaterialTheme.colorScheme.primary.copy(alpha = 0.22f)
                                else Color.Transparent
                            )
                            .then(
                                if (!isManualMode) Modifier.border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(9.dp))
                                else Modifier
                            )
                            .clickable {
                                isManualMode = false
                                if (selectedUpiApp.isManual) {
                                    selectedUpiApp = UPIApp.OTHER
                                }
                            }
                            .padding(vertical = 9.dp)
                            .testTag("quick_add_type_upi"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.QrCode,
                                contentDescription = null,
                                tint = if (!isManualMode) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "UPI",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (!isManualMode) FontWeight.Bold else FontWeight.Medium
                                ),
                                color = if (!isManualMode) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Manual Tab (Cash / Card)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(9.dp))
                            .background(
                                if (isManualMode) Color(0xFF10B981).copy(alpha = 0.22f)
                                else Color.Transparent
                            )
                            .then(
                                if (isManualMode) Modifier.border(1.dp, Color(0xFF10B981), RoundedCornerShape(9.dp))
                                else Modifier
                            )
                            .clickable {
                                isManualMode = true
                                if (!selectedUpiApp.isManual) {
                                    selectedUpiApp = UPIApp.MANUAL
                                }
                            }
                            .padding(vertical = 9.dp)
                            .testTag("quick_add_type_manual"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_cash),
                                contentDescription = null,
                                tint = if (isManualMode) Color(0xFF10B981) else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Manual (Cash / Card)",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (isManualMode) FontWeight.Bold else FontWeight.Medium
                                ),
                                color = if (isManualMode) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Specific option chips
                if (!isManualMode) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            UPIApp.OTHER to "UPI (General)",
                            UPIApp.GPAY to "Google Pay",
                            UPIApp.PHONEPE to "PhonePe",
                            UPIApp.PAYTM to "Paytm",
                            UPIApp.BHIM to "BHIM"
                        ).forEach { (app, label) ->
                            val isSelected = selectedUpiApp == app
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(
                                        if (isSelected) app.primaryColor.copy(alpha = 0.22f)
                                        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                                    )
                                    .then(
                                        if (isSelected) Modifier.border(1.dp, app.primaryColor, RoundedCornerShape(14.dp))
                                        else Modifier
                                    )
                                    .clickable { selectedUpiApp = app }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 11.sp
                                    ),
                                    color = if (isSelected) app.primaryColor else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            UPIApp.MANUAL to "💵 Cash Spend",
                            UPIApp.CARD to "💳 Card / Bank"
                        ).forEach { (app, label) ->
                            val isSelected = selectedUpiApp == app
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(
                                        if (isSelected) app.primaryColor.copy(alpha = 0.22f)
                                        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                                    )
                                    .then(
                                        if (isSelected) Modifier.border(1.dp, app.primaryColor, RoundedCornerShape(14.dp))
                                        else Modifier
                                    )
                                    .clickable { selectedUpiApp = app }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 11.sp
                                    ),
                                    color = if (isSelected) app.primaryColor else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Category Selection
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "CATEGORY",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 1.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Category.values().forEach { cat ->
                        CategoryBadge(
                            category = cat,
                            isSelected = selectedCategory == cat,
                            onClick = { selectedCategory = cat }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Merchant / Note (optional)
            OutlinedTextField(
                value = merchantNote,
                onValueChange = { merchantNote = it },
                placeholder = {
                    Text(
                        text = "Merchant or note (optional)",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("quick_add_merchant_input"),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Numeric Keypad
            NumericKeypad(
                onDigitClick = { digit ->
                    if (amountString.length < 8) {
                        if (digit == "." && amountString.contains(".")) return@NumericKeypad
                        amountString += digit
                    }
                },
                onBackspace = {
                    if (amountString.isNotEmpty()) {
                        amountString = amountString.dropLast(1)
                    }
                },
                onClear = {
                    amountString = ""
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Save Button
            Button(
                onClick = {
                    if (amountValue > 0) {
                        val trimmed = merchantNote.trim()
                        val merchantName = if (trimmed.isNotBlank()) {
                            trimmed
                        } else {
                            selectedCategory.displayName
                        }
                        onSave(
                            amountValue,
                            merchantName,
                            selectedUpiApp,
                            selectedCategory,
                            if (trimmed.isNotBlank()) trimmed else null
                        )
                        onDismiss()
                    }
                },
                enabled = amountValue > 0,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("quick_add_save_btn"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = if (amountValue > 0) "Save ₹$amountString Expense" else "Enter Amount",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
