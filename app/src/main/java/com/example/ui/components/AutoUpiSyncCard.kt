package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.model.Category
import com.example.model.UPIApp
import com.example.ui.theme.ExcessLimitRedStroke
import com.example.ui.theme.GlassBorderGradient
import com.example.ui.theme.GlassCardBackground
import com.example.ui.theme.GlassCardBorder
import com.example.ui.theme.GlassPillBackground
import com.example.ui.theme.GlassSurfaceElevated
import com.example.ui.theme.SpendAmber
import com.example.ui.theme.WithinLimitGreenFill
import com.example.ui.theme.WithinLimitGreenStroke

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AutoUpiSyncCard(
    integratedUpiApps: Set<String>,
    onAddUpiApp: (String) -> Unit,
    onRemoveUpiApp: (String) -> Unit,
    onSimulateTestPayment: (amount: Double, merchant: String, upiApp: UPIApp, category: Category) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var isListenerEnabled by remember {
        mutableStateOf(checkNotificationListenerEnabled(context))
    }

    var showAddDialog by remember { mutableStateOf(false) }

    // Re-check permission when returning to app
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                isListenerEnabled = checkNotificationListenerEnabled(context)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val isTrackingActive = isListenerEnabled && integratedUpiApps.isNotEmpty()

    // Glassmorphic Card Container
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("auto_upi_sync_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = GlassCardBackground),
        border = BorderStroke(1.dp, GlassBorderGradient),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(GlassSurfaceElevated)
                            .border(1.dp, GlassCardBorder, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isTrackingActive) Icons.Default.Bolt else Icons.Default.Payment,
                            contentDescription = null,
                            tint = if (isTrackingActive) WithinLimitGreenStroke else Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "UPI Integration",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = (-0.2).sp
                            ),
                            color = Color.White
                        )
                        Text(
                            text = if (isTrackingActive) "${integratedUpiApps.size} apps connected & tracking"
                            else if (integratedUpiApps.isEmpty()) "No apps connected yet"
                            else "Ready to connect",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF9CA3AF)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Connect your UPI apps to automatically track spends and rebalance your daily limit. Only transactions from connected apps are tracked.",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF9CA3AF),
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Connected UPI Apps Section
            Text(
                text = "CONNECTED APPS (${integratedUpiApps.size})",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF9CA3AF),
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (integratedUpiApps.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF14141B).copy(alpha = 0.5f))
                        .border(1.dp, GlassCardBorder, RoundedCornerShape(14.dp))
                        .padding(14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No UPI apps connected yet. Tap 'Connect UPI' below to connect your apps.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF8E8E9B)
                    )
                }
            } else {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    integratedUpiApps.forEach { appKey ->
                        val matchedEnum = runCatching { UPIApp.valueOf(appKey) }.getOrNull()
                        val displayName = matchedEnum?.displayName ?: appKey
                        val color = matchedEnum?.primaryColor ?: Color(0xFF8B5CF6)

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(GlassSurfaceElevated)
                                .border(1.dp, color.copy(alpha = 0.45f), RoundedCornerShape(14.dp))
                                .padding(start = 12.dp, end = 6.dp, top = 6.dp, bottom = 6.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .clip(CircleShape)
                                        .background(WithinLimitGreenStroke)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = displayName,
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                IconButton(
                                    onClick = { onRemoveUpiApp(appKey) },
                                    modifier = Modifier.size(22.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Disconnect $displayName",
                                        tint = ExcessLimitRedStroke,
                                        modifier = Modifier.size(13.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Prominent "Connect UPI" button (test track and settings icon removed)
            Button(
                onClick = { showAddDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("connect_upi_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black
                )
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Connect UPI", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
    }

    // Dialog: Connect UPI Apps
    if (showAddDialog) {
        val availableDefaults = listOf(
            UPIApp.GPAY to "Google Pay (GPay)",
            UPIApp.PHONEPE to "PhonePe",
            UPIApp.PAYTM to "Paytm",
            UPIApp.BHIM to "BHIM UPI",
            UPIApp.OTHER to "Amazon Pay"
        )

        var customAppName by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            containerColor = Color(0xFF14141C),
            shape = RoundedCornerShape(24.dp),
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Connect UPI",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    IconButton(
                        onClick = { showAddDialog = false },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color(0xFF9CA3AF),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Connect your UPI apps to automatically track spending. Connected apps are tracked in real-time.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF9CA3AF)
                    )

                    availableDefaults.forEach { (upi, label) ->
                        val isConnected = integratedUpiApps.contains(upi.name)

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isConnected) Color(0xFF19241E) else Color(0xFF181822))
                                .border(
                                    1.dp,
                                    if (isConnected) WithinLimitGreenStroke.copy(alpha = 0.5f) else Color.White.copy(alpha = 0.08f),
                                    RoundedCornerShape(12.dp)
                                )
                                .padding(horizontal = 14.dp, vertical = 10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(if (isConnected) WithinLimitGreenStroke else Color(0xFF6B7280))
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = label,
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                            color = Color.White
                                        )
                                        if (isConnected) {
                                            Text(
                                                text = "Connected & Tracking",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = WithinLimitGreenStroke
                                            )
                                        }
                                    }
                                }

                                if (isConnected) {
                                    Button(
                                        onClick = { onRemoveUpiApp(upi.name) },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFF2A1515),
                                            contentColor = ExcessLimitRedStroke
                                        ),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.height(34.dp)
                                    ) {
                                        Text("Disconnect", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                } else {
                                    Button(
                                        onClick = { onAddUpiApp(upi.name) },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color.White,
                                            contentColor = Color.Black
                                        ),
                                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.height(34.dp)
                                    ) {
                                        Text("Connect", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Or connect another UPI app (e.g. CRED, Jupiter):",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF9CA3AF)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = customAppName,
                            onValueChange = { customAppName = it },
                            placeholder = { Text("App Name", color = Color(0xFF71717A), fontSize = 13.sp) },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color.White,
                                unfocusedBorderColor = Color.White.copy(alpha = 0.2f)
                            )
                        )
                        Button(
                            onClick = {
                                if (customAppName.isNotBlank()) {
                                    onAddUpiApp(customAppName.trim().uppercase())
                                    customAppName = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black),
                            shape = RoundedCornerShape(10.dp),
                            enabled = customAppName.isNotBlank(),
                            modifier = Modifier.height(48.dp)
                        ) {
                            Text("Connect", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showAddDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Done", fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

private fun checkNotificationListenerEnabled(context: Context): Boolean {
    val packageName = context.packageName
    val enabledPackages = NotificationManagerCompat.getEnabledListenerPackages(context)
    return enabledPackages.contains(packageName)
}
