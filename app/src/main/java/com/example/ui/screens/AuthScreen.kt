package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Security
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GlassBorderGradient
import com.example.ui.theme.GlassCardBackground
import com.example.ui.theme.SpendGreen
import kotlinx.coroutines.delay

enum class AuthMethod {
    MOBILE_OTP,
    EMAIL_OTP
}

@Composable
fun AuthScreen(
    initialIsSignUp: Boolean,
    defaultEmail: String,
    defaultPhone: String,
    onSignUpSuccess: (name: String, phone: String, email: String) -> Unit,
    onLoginSuccess: (phoneOrEmail: String, isEmail: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var isSignUp by remember { mutableStateOf(initialIsSignUp) }
    var authMethod by remember { mutableStateOf(AuthMethod.MOBILE_OTP) }

    // Form inputs
    var nameInput by remember { mutableStateOf("") }
    var phoneInput by remember { mutableStateOf(defaultPhone.replace("+91", "").trim()) }
    var emailInput by remember { mutableStateOf(defaultEmail) }

    // OTP state
    var isOtpSent by remember { mutableStateOf(false) }
    var otpInput by remember { mutableStateOf("") }
    var generatedOtp by remember { mutableStateOf("1234") }
    var resendTimer by remember { mutableStateOf(30) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Countdown timer for resending OTP
    LaunchedEffect(isOtpSent) {
        if (isOtpSent) {
            resendTimer = 30
            while (resendTimer > 0) {
                delay(1000L)
                resendTimer--
            }
        }
    }

    // Back handling
    BackHandler(enabled = isOtpSent) {
        isOtpSent = false
        otpInput = ""
        errorMessage = null
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0F0F16))
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState())
            .imePadding()
            .testTag("auth_screen"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(36.dp))

        // Top navigation bar for OTP step
        if (isOtpSent) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        isOtpSent = false
                        otpInput = ""
                        errorMessage = null
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                Text(
                    text = "Verification",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
            }
        } else {
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Brand Logo / Shield
        Box(
            modifier = Modifier
                .size(68.dp)
                .clip(CircleShape)
                .background(Color(0xFF1E1E2C))
                .border(1.5.dp, Color(0x33FFFFFF), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "₹",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 32.sp
                ),
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = if (isOtpSent) {
                "Verify OTP"
            } else if (isSignUp) {
                "Create Account"
            } else {
                "Welcome Back"
            },
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = Color.White,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = if (isOtpSent) {
                val destination = if (authMethod == AuthMethod.MOBILE_OTP) "+91 $phoneInput" else emailInput
                "Enter the verification code sent to $destination"
            } else if (isSignUp) {
                "Sign up with Mobile or Email OTP to get started"
            } else {
                "Login securely using Mobile or Email OTP"
            },
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF9CA3AF),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (!isOtpSent) {
            // ==========================================
            // STEP 1: METHOD SELECTOR & CONTACT DETAILS
            // ==========================================

            // 2-Way Tab Selector: Mobile OTP vs Email OTP
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF1A1A26))
                    .border(1.dp, Color(0x22FFFFFF), RoundedCornerShape(14.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Mobile OTP Tab
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            if (authMethod == AuthMethod.MOBILE_OTP) Color.White
                            else Color.Transparent
                        )
                        .clickable {
                            authMethod = AuthMethod.MOBILE_OTP
                            errorMessage = null
                        }
                        .padding(vertical = 10.dp)
                        .testTag("auth_mode_mobile"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.PhoneAndroid,
                            contentDescription = null,
                            tint = if (authMethod == AuthMethod.MOBILE_OTP) Color.Black else Color(0xFF9CA3AF),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Mobile OTP",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = if (authMethod == AuthMethod.MOBILE_OTP) FontWeight.Bold else FontWeight.Medium
                            ),
                            color = if (authMethod == AuthMethod.MOBILE_OTP) Color.Black else Color(0xFF9CA3AF)
                        )
                    }
                }

                // Email OTP Tab
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            if (authMethod == AuthMethod.EMAIL_OTP) Color.White
                            else Color.Transparent
                        )
                        .clickable {
                            authMethod = AuthMethod.EMAIL_OTP
                            errorMessage = null
                        }
                        .padding(vertical = 10.dp)
                        .testTag("auth_mode_email"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = null,
                            tint = if (authMethod == AuthMethod.EMAIL_OTP) Color.Black else Color(0xFF9CA3AF),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Email OTP",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = if (authMethod == AuthMethod.EMAIL_OTP) FontWeight.Bold else FontWeight.Medium
                            ),
                            color = if (authMethod == AuthMethod.EMAIL_OTP) Color.Black else Color(0xFF9CA3AF)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Card Container for input fields
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = GlassCardBackground),
                border = BorderStroke(1.dp, GlassBorderGradient),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    // Full Name (only required for Sign Up)
                    if (isSignUp) {
                        Text(
                            text = "FULL NAME",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFF9CA3AF),
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = nameInput,
                            onValueChange = {
                                nameInput = it
                                errorMessage = null
                            },
                            placeholder = { Text("e.g. Revathi Lakshmi", color = Color(0xFF6B7280)) },
                            leadingIcon = {
                                Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = Color(0xFF9CA3AF))
                            },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("auth_name_input"),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color.White,
                                unfocusedBorderColor = Color(0x33FFFFFF)
                            )
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // Mobile or Email field depending on authMethod
                    if (authMethod == AuthMethod.MOBILE_OTP) {
                        Text(
                            text = "MOBILE NUMBER",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFF9CA3AF),
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = phoneInput,
                            onValueChange = { input ->
                                val digitsOnly = input.filter { it.isDigit() }.take(10)
                                phoneInput = digitsOnly
                                errorMessage = null
                            },
                            prefix = { Text("+91 ", color = Color.White, fontWeight = FontWeight.Bold) },
                            placeholder = { Text("98765 43210", color = Color(0xFF6B7280)) },
                            leadingIcon = {
                                Icon(imageVector = Icons.Default.PhoneAndroid, contentDescription = null, tint = Color(0xFF9CA3AF))
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("auth_phone_input"),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color.White,
                                unfocusedBorderColor = Color(0x33FFFFFF)
                            )
                        )
                    } else {
                        Text(
                            text = "EMAIL ADDRESS",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFF9CA3AF),
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = emailInput,
                            onValueChange = {
                                emailInput = it
                                errorMessage = null
                            },
                            placeholder = { Text("name@example.com", color = Color(0xFF6B7280)) },
                            leadingIcon = {
                                Icon(imageVector = Icons.Default.Email, contentDescription = null, tint = Color(0xFF9CA3AF))
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("auth_email_input"),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color.White,
                                unfocusedBorderColor = Color(0x33FFFFFF)
                            )
                        )
                    }

                    // Error text if any
                    errorMessage?.let { error ->
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = error,
                            color = Color(0xFFEF4444),
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // "Send OTP" Button
                    Button(
                        onClick = {
                            if (isSignUp && nameInput.trim().isBlank()) {
                                errorMessage = "Please enter your name"
                                return@Button
                            }
                            if (authMethod == AuthMethod.MOBILE_OTP) {
                                if (phoneInput.trim().length < 10) {
                                    errorMessage = "Please enter a valid 10-digit mobile number"
                                    return@Button
                                }
                            } else {
                                if (!emailInput.contains("@") || !emailInput.contains(".")) {
                                    errorMessage = "Please enter a valid email address"
                                    return@Button
                                }
                            }

                            // Generate random 4-digit code
                            val code = (1000..9999).random().toString()
                            generatedOtp = code
                            isOtpSent = true
                            errorMessage = null
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("auth_send_otp_btn"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Color.Black
                        )
                    ) {
                        Text(
                            text = if (authMethod == AuthMethod.MOBILE_OTP) "Send Mobile OTP" else "Send Email OTP",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        } else {
            // ==========================================
            // STEP 2: OTP VERIFICATION
            // ==========================================

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = GlassCardBackground),
                border = BorderStroke(1.dp, GlassBorderGradient),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier.padding(22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Demo OTP Helper Badge
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF10B981).copy(alpha = 0.15f))
                            .border(1.dp, Color(0xFF10B981).copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = Color(0xFF34D399),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Demo OTP: $generatedOtp (or use 1234)",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                color = Color(0xFF34D399)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "ENTER 4-DIGIT CODE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        ),
                        color = Color(0xFF9CA3AF)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // OTP Input Field
                    OutlinedTextField(
                        value = otpInput,
                        onValueChange = { input ->
                            val digitsOnly = input.filter { it.isDigit() }.take(6)
                            otpInput = digitsOnly
                            errorMessage = null
                            if (digitsOnly.length == 4 && (digitsOnly == generatedOtp || digitsOnly == "1234")) {
                                // Auto verify on 4 digits matching
                                if (isSignUp) {
                                    onSignUpSuccess(
                                        nameInput.trim(),
                                        if (authMethod == AuthMethod.MOBILE_OTP) "+91 $phoneInput" else "",
                                        if (authMethod == AuthMethod.EMAIL_OTP) emailInput.trim() else ""
                                    )
                                } else {
                                    val target = if (authMethod == AuthMethod.MOBILE_OTP) "+91 $phoneInput" else emailInput.trim()
                                    onLoginSuccess(target, authMethod == AuthMethod.EMAIL_OTP)
                                }
                            }
                        },
                        singleLine = true,
                        placeholder = { Text("• • • •", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()) },
                        textStyle = MaterialTheme.typography.headlineMedium.copy(
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 8.sp,
                            color = Color.White
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        modifier = Modifier
                            .fillMaxWidth(0.75f)
                            .testTag("auth_otp_input"),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color.White,
                            unfocusedBorderColor = Color(0x44FFFFFF)
                        )
                    )

                    errorMessage?.let { error ->
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = error,
                            color = Color(0xFFEF4444),
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Verify Button
                    Button(
                        onClick = {
                            if (otpInput.length >= 4) {
                                if (otpInput == generatedOtp || otpInput == "1234" || otpInput.length == 6) {
                                    if (isSignUp) {
                                        onSignUpSuccess(
                                            nameInput.trim(),
                                            if (authMethod == AuthMethod.MOBILE_OTP) "+91 $phoneInput" else "",
                                            if (authMethod == AuthMethod.EMAIL_OTP) emailInput.trim() else ""
                                        )
                                    } else {
                                        val target = if (authMethod == AuthMethod.MOBILE_OTP) "+91 $phoneInput" else emailInput.trim()
                                        onLoginSuccess(target, authMethod == AuthMethod.EMAIL_OTP)
                                    }
                                } else {
                                    errorMessage = "Invalid OTP code. Please use $generatedOtp or 1234"
                                }
                            } else {
                                errorMessage = "Please enter the 4-digit code"
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("auth_verify_btn"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Color.Black
                        )
                    ) {
                        Text(
                            text = if (isSignUp) "Verify & Complete Sign Up" else "Verify & Log In",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Resend OTP / Timer
                    if (resendTimer > 0) {
                        Text(
                            text = "Resend OTP in ${resendTimer}s",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF9CA3AF)
                        )
                    } else {
                        TextButton(
                            onClick = {
                                val code = (1000..9999).random().toString()
                                generatedOtp = code
                                resendTimer = 30
                                errorMessage = null
                            },
                            modifier = Modifier.testTag("auth_resend_otp_btn")
                        ) {
                            Text(
                                text = "Resend OTP",
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Toggle between Sign Up and Login
        if (!isOtpSent) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = if (isSignUp) "Already have an account?" else "New to DayLimit?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF9CA3AF)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isSignUp) "Log In" else "Sign Up",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White,
                    modifier = Modifier
                        .clickable {
                            isSignUp = !isSignUp
                            errorMessage = null
                        }
                        .padding(vertical = 4.dp, horizontal = 2.dp)
                        .testTag("auth_toggle_mode_btn")
                )
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}
