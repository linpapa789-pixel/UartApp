package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Usb
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.GlassCard
import com.example.ui.viewmodel.MainViewModel
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.AppBackground
import com.example.ui.theme.CardBackground
import com.example.ui.theme.CardBorder
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextMutedColor
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarningYellow
import com.example.ui.theme.ThemeState
import androidx.compose.material.icons.filled.DarkMode

@Composable
fun SettingsScreen(
    viewModel: MainViewModel,
    onNavigateToAbout: () -> Unit = {},
    onNavigateToGuide: () -> Unit = {}
) {
    val currentLang by viewModel.currentLanguage.collectAsState()
    val showTimestamps by viewModel.showTimestamps.collectAsState()
    val connectionState by viewModel.usbManager.connectionState.collectAsState()
    val baudRate = connectionState.baudRate
    val isDark by viewModel.isDarkTheme.collectAsState()

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .padding(14.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Top Header
        GlassCard(borderColor = AccentCyan) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Settings, contentDescription = null, tint = AccentCyan)
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "UART System Settings",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Hardware Baud, Drivers, Language & System Preferences",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }
            }
        }

        // Theme Toggle
        GlassCard {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.DarkMode, contentDescription = null, tint = AccentCyan)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Dark Theme", fontSize = 14.sp, color = TextPrimary, fontWeight = FontWeight.Bold)
                    }
                    Switch(
                        checked = isDark,
                        onCheckedChange = { viewModel.toggleTheme() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = CardBackground,
                            checkedTrackColor = AccentCyan
                        )
                    )
                }
            }
        }

        // Gemini API Key Input Card
        var apiKeyInput by remember { mutableStateOf(viewModel.aiService.getApiKey()) }
        var isApiKeySaved by remember { mutableStateOf(viewModel.aiService.customApiKey.isNotBlank()) }

        GlassCard(borderColor = SuccessGreen) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Key, contentDescription = null, tint = SuccessGreen)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Google Gemini Live API Key", fontSize = 14.sp, color = TextPrimary, fontWeight = FontWeight.Bold)
                }
                Text(
                    text = "Google AI Studio မှ အခမဲ့ ရရှိနိုင်သော API Key ကို ထည့်သွင်းပါက Gemini AI Live Chat & Online Diagnostic ကို အသုံးပြုနိုင်ပါမည်။",
                    fontSize = 11.sp,
                    color = TextSecondary
                )

                OutlinedTextField(
                    value = apiKeyInput,
                    onValueChange = {
                        apiKeyInput = it
                        isApiKeySaved = false
                    },
                    placeholder = { Text("Paste AIzaSy... API Key here", fontSize = 11.sp, color = TextMutedColor) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SuccessGreen,
                        unfocusedBorderColor = CardBorder,
                        focusedContainerColor = CardBackground,
                        unfocusedContainerColor = CardBackground,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isApiKeySaved) "✓ API Key Saved & Connected!" else if (apiKeyInput.isNotBlank()) "Key Entered (Press Save)" else "Offline Engine Active",
                        fontSize = 11.sp,
                        color = if (isApiKeySaved) SuccessGreen else TextSecondary,
                        fontWeight = FontWeight.Bold
                    )

                    Button(
                        onClick = {
                            viewModel.aiService.customApiKey = apiKeyInput.trim()
                            isApiKeySaved = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Save Key", color = AppBackground, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
            }
        }

        // Language Selection Bento Box
        GlassCard {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Language, contentDescription = null, tint = AccentCyan)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("App Language / ဘာသာစကား", fontSize = 14.sp, color = TextPrimary, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { viewModel.setLanguage("Myanmar") },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (currentLang == "Myanmar") AccentCyan else CardBackground
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("🇲🇲 Myanmar", color = if (currentLang == "Myanmar") AppBackground else TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { viewModel.setLanguage("English") },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (currentLang == "English") AccentCyan else CardBackground
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("🇬🇧 English", color = if (currentLang == "English") AppBackground else TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        var customBaudRate by remember { mutableStateOf("") }

        // UART Hardware Baud Rate Configuration
        GlassCard(borderColor = WarningYellow) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Speed, contentDescription = null, tint = WarningYellow)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Default Hardware Baud Rate", fontSize = 14.sp, color = TextPrimary, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text("Current: $baudRate bps (8N1 Standard Serial)", fontSize = 11.sp, color = TextSecondary)
                Spacer(modifier = Modifier.height(10.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf(115200, 921600, 3000000).forEach { rate ->
                        val isSelected = baudRate == rate
                        Button(
                            onClick = { viewModel.usbManager.setBaudRate(rate) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSelected) WarningYellow else CardBackground
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "$rate",
                                color = if (isSelected) AppBackground else TextPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(10.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = customBaudRate,
                        onValueChange = { customBaudRate = it.filter { char -> char.isDigit() } },
                        placeholder = { Text("Custom Baud Rate (e.g. 1500000)", fontSize = 11.sp, color = TextMutedColor) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = WarningYellow,
                            unfocusedBorderColor = CardBorder,
                            focusedContainerColor = CardBackground,
                            unfocusedContainerColor = CardBackground,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )
                    Button(
                        onClick = {
                            val rate = customBaudRate.toIntOrNull()
                            if (rate != null && rate > 0) {
                                viewModel.usbManager.setBaudRate(rate)
                                customBaudRate = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = WarningYellow),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Set", color = AppBackground, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
            }
        }

        // Terminal Preferences Bento Box
        GlassCard {
            Column {
                Text("Terminal Preferences", fontSize = 14.sp, color = TextPrimary, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Show Line Timestamps [ms]", fontSize = 12.sp, color = TextPrimary)
                    Switch(
                        checked = showTimestamps,
                        onCheckedChange = { viewModel.toggleTimestamps() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = AppBackground,
                            checkedTrackColor = AccentCyan
                        )
                    )
                }
            }
        }

        // Documentation & User Guide Shortcut
        GlassCard(borderColor = SuccessGreen) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToGuide() },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = null, tint = SuccessGreen)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("User Manual / အသုံးပြုပုံ လမ်းညွှန်", fontSize = 13.sp, color = TextPrimary, fontWeight = FontWeight.Bold)
                            Text("ခေါင်းစဉ်အလိုက် အဆင့်ဆင့် အသုံးပြုပုံ ရှင်းလင်းချက်", fontSize = 11.sp, color = TextSecondary)
                        }
                    }
                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = SuccessGreen)
                }

                HorizontalDivider(color = CardBorder)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToAbout() },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = AccentCyan)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("About UART PRO AI v3.5", fontSize = 13.sp, color = TextPrimary, fontWeight = FontWeight.Bold)
                            Text("Supported Hardware, Chipsets & Specs", fontSize = 11.sp, color = TextSecondary)
                        }
                    }
                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = AccentCyan)
                }
            }
        }
    }
}

