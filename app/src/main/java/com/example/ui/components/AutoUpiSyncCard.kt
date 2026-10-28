package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
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
import com.example.ui.theme.GlassBorderGradient
import com.example.ui.theme.GlassCardBackground
import com.example.ui.theme.GlassCardBorder
import com.example.ui.theme.GlassSurfaceElevated
import com.example.ui.theme.SpendAmber
import com.example.ui.theme.WithinLimitGreenFill
import com.example.ui.theme.WithinLimitGreenStroke

@Composable
fun AutoUpiSyncCard(
    onSimulateTestPayment: (amount: Double, merchant: String, upiApp: UPIApp, category: Category) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var isListenerEnabled by remember {
        mutableStateOf(checkNotificationListenerEnabled(context))
    }

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
                            imageVector = if (isListenerEnabled) Icons.Default.Bolt else Icons.Default.Payment,
                            contentDescription = null,
                            tint = if (isListenerEnabled) WithinLimitGreenStroke else SpendAmber,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = if (isListenerEnabled) "Automatic UPI Sync Active" else "Automatic UPI Sync",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = (-0.2).sp
                            ),
                            color = Color.White
                        )
                        Text(
                            text = if (isListenerEnabled) "Listening to GPay, PhonePe, Paytm & Bank alerts"
                            else "No manual entry needed",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF9CA3AF)
                        )
                    }
                }

                // Active status frosted pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            if (isListenerEnabled) WithinLimitGreenFill
                            else SpendAmber.copy(alpha = 0.15f)
                        )
                        .border(
                            1.dp,
                            if (isListenerEnabled) WithinLimitGreenStroke.copy(alpha = 0.7f)
                            else SpendAmber.copy(alpha = 0.7f),
                            RoundedCornerShape(20.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = if (isListenerEnabled) "LIVE" else "SETUP",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = if (isListenerEnabled) WithinLimitGreenStroke else SpendAmber
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = if (isListenerEnabled) {
                    "Whenever you pay with any UPI app (GPay, PhonePe, Paytm, BHIM) or receive a bank debit SMS, DayLimit automatically records the spend and rebalances your daily limit in the background."
                } else {
                    "Enable Notification Access in Android Settings so DayLimit can automatically detect payments from Google Pay, PhonePe, Paytm, BHIM and calculate your remaining limits with zero manual input."
                },
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF9CA3AF),
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (!isListenerEnabled) {
                    Button(
                        onClick = {
                            val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS).apply {
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            }
                            context.startActivity(intent)
                        },
                        modifier = Modifier.weight(1.3f).height(46.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Color.Black
                        )
                    ) {
                        Icon(imageVector = Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Enable Auto-Sync", fontWeight = FontWeight.Bold)
                    }
                }

                // Test Auto-Detection Button (simulates incoming UPI transaction to verify automatic tracking)
                OutlinedButton(
                    onClick = {
                        val testPayments = listOf(
                            Triple(450.0, "Swiggy UPI", UPIApp.GPAY),
                            Triple(220.0, "Chai Point", UPIApp.PHONEPE),
                            Triple(120.0, "Metro QR", UPIApp.PAYTM),
                            Triple(850.0, "D-Mart Grocery", UPIApp.BHIM)
                        )
                        val sample = testPayments.random()
                        onSimulateTestPayment(
                            sample.first,
                            sample.second,
                            sample.third,
                            Category.inferFromText(sample.second)
                        )
                    },
                    modifier = Modifier.weight(1f).height(46.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                    border = BorderStroke(1.dp, GlassCardBorder)
                ) {
                    Icon(imageVector = Icons.Default.Bolt, contentDescription = null, modifier = Modifier.size(16.dp), tint = WithinLimitGreenStroke)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Test Auto-Detect", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

private fun checkNotificationListenerEnabled(context: Context): Boolean {
    val packageName = context.packageName
    val enabledPackages = NotificationManagerCompat.getEnabledListenerPackages(context)
    return enabledPackages.contains(packageName)
}
