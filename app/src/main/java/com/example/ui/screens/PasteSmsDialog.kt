package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.logic.CurrencyFormatter
import com.example.logic.ParsedSmsResult
import com.example.logic.SmsParser
import com.example.model.Category
import com.example.model.UPIApp
import com.example.ui.components.CategoryBadge
import com.example.ui.components.UpiBadge
import com.example.ui.theme.SpendGreen
import com.example.ui.theme.SpendRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasteSmsDialog(
    onDismiss: () -> Unit,
    onSave: (amount: Double, merchant: String, upiApp: UPIApp, category: Category, note: String?) -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val clipboardManager = LocalClipboardManager.current

    var smsInput by remember { mutableStateOf("") }
    var parsedResult by remember { mutableStateOf<ParsedSmsResult?>(null) }

    var editableAmount by remember { mutableStateOf("") }
    var editableMerchant by remember { mutableStateOf("") }
    var selectedUpi by remember { mutableStateOf(UPIApp.OTHER) }
    var selectedCategory by remember { mutableStateOf(Category.FOOD) }

    // When text changes, automatically attempt parse
    fun runParse(text: String) {
        val result = SmsParser.parse(text)
        parsedResult = result
        if (result.isValidDebit && result.amount != null) {
            editableAmount = if (result.amount % 1.0 == 0.0) result.amount.toInt().toString() else result.amount.toString()
            editableMerchant = result.merchant ?: "UPI Merchant"
            selectedUpi = result.inferredUpiApp
            selectedCategory = result.inferredCategory
        }
    }

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
                    Icon(
                        imageVector = Icons.Default.Sms,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Paste Debit SMS / Notification",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                IconButton(onClick = onDismiss, modifier = Modifier.testTag("paste_sms_close")) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Sample quick-fill pills
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        val sample = "Sent Rs. 450.00 to swiggy@icici on 28-10-26 Ref 234567. Avail Bal: Rs 15,200.50"
                        smsInput = sample
                        runParse(sample)
                    }
                ) {
                    Text("Sample Swiggy (₹450)")
                }
                OutlinedButton(
                    onClick = {
                        val sample = "A/c ..5678 debited by 320.00 on 28-10-26 transfer to Chai Point via PhonePe UPI"
                        smsInput = sample
                        runParse(sample)
                    }
                ) {
                    Text("Sample PhonePe (₹320)")
                }
                OutlinedButton(
                    onClick = {
                        val sample = "INR 1,299.00 paid towards Uber India via GPay UPI on 28-10-26"
                        smsInput = sample
                        runParse(sample)
                    }
                ) {
                    Text("Sample Uber (₹1,299)")
                }
                OutlinedButton(
                    onClick = {
                        val sample = "INR 500.00 credited to your A/c ... by UPI on 28-10-26"
                        smsInput = sample
                        runParse(sample)
                    }
                ) {
                    Text("Test Credit (Should Reject)")
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Text input box
            OutlinedTextField(
                value = smsInput,
                onValueChange = {
                    smsInput = it
                    runParse(it)
                },
                placeholder = { Text("Paste bank debit SMS here...\n(Only debit transactions are accepted)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .testTag("paste_sms_input"),
                shape = RoundedCornerShape(14.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Paste from clipboard button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                OutlinedButton(
                    onClick = {
                        val clip = clipboardManager.getText()?.text
                        if (!clip.isNullOrBlank()) {
                            smsInput = clip
                            runParse(clip)
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentPaste,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Paste Clipboard")
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Parse Result Status Card
            val result = parsedResult
            if (result != null) {
                if (result.isCreditIgnored) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = SpendRed.copy(alpha = 0.12f)),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.ErrorOutline,
                                contentDescription = null,
                                tint = SpendRed
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = result.statusMessage,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = SpendRed
                            )
                        }
                    }
                } else if (result.isValidDebit) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = SpendGreen
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Debit Transaction Extracted",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = SpendGreen
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Editable Amount & Merchant
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = editableAmount,
                                    onValueChange = { editableAmount = it },
                                    label = { Text("Amount (₹)") },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp)
                                )
                                OutlinedTextField(
                                    value = editableMerchant,
                                    onValueChange = { editableMerchant = it },
                                    label = { Text("Merchant / VPA") },
                                    modifier = Modifier.weight(1.5f),
                                    shape = RoundedCornerShape(10.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // UPI App selector
                            Text(
                                text = "UPI APP:",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                UPIApp.values().forEach { upi ->
                                    UpiBadge(
                                        upiApp = upi,
                                        isSelected = selectedUpi == upi,
                                        onClick = { selectedUpi = upi }
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Category selector
                            Text(
                                text = "CATEGORY:",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
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
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    val finalAmt = editableAmount.toDoubleOrNull() ?: 0.0
                    Button(
                        onClick = {
                            if (finalAmt > 0) {
                                onSave(
                                    finalAmt,
                                    editableMerchant.ifBlank { "UPI Merchant" },
                                    selectedUpi,
                                    selectedCategory,
                                    "SMS: $editableMerchant"
                                )
                                onDismiss()
                            }
                        },
                        enabled = finalAmt > 0,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("paste_sms_confirm_save"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text(
                            text = "Confirm & Save ${CurrencyFormatter.format(finalAmt)}",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                } else {
                    Text(
                        text = result.statusMessage,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
