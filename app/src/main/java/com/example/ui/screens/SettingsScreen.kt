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
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Usb
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF000000))
            .padding(14.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Top Header
        GlassCard(borderColor = Color(0xFF06B6D4)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Settings, contentDescription = null, tint = Color(0xFF06B6D4))
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "UART System Settings",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Hardware Baud, Drivers, Language & System Preferences",
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8)
                    )
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
                        Icon(Icons.Default.Language, contentDescription = null, tint = Color(0xFF06B6D4))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("App Language / ဘာသာစကား", fontSize = 14.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { viewModel.setLanguage("Myanmar") },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (currentLang == "Myanmar") Color(0xFF06B6D4) else Color(0xFF2E2E32)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("🇲🇲 Myanmar", color = if (currentLang == "Myanmar") Color.Black else Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { viewModel.setLanguage("English") },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (currentLang == "English") Color(0xFF06B6D4) else Color(0xFF2E2E32)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("🇬🇧 English", color = if (currentLang == "English") Color.Black else Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // UART Hardware Baud Rate Configuration
        GlassCard(borderColor = Color(0xFFF59E0B)) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Speed, contentDescription = null, tint = Color(0xFFF59E0B))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Default Hardware Baud Rate", fontSize = 14.sp, color = Color.White, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text("Current: $baudRate bps (8N1 Standard Serial)", fontSize = 11.sp, color = Color(0xFF94A3B8))
                Spacer(modifier = Modifier.height(10.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf(115200, 921600, 3000000).forEach { rate ->
                        val isSelected = baudRate == rate
                        Button(
                            onClick = { viewModel.usbManager.setBaudRate(rate) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSelected) Color(0xFFF59E0B) else Color(0xFF2E2E32)
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "$rate",
                                color = if (isSelected) Color.Black else Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Terminal Preferences Bento Box
        GlassCard {
            Column {
                Text("Terminal Preferences", fontSize = 14.sp, color = Color.White, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Show Line Timestamps [ms]", fontSize = 12.sp, color = Color(0xFFE3E2E6))
                    Switch(
                        checked = showTimestamps,
                        onCheckedChange = { viewModel.toggleTimestamps() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.Black,
                            checkedTrackColor = Color(0xFF06B6D4)
                        )
                    )
                }
            }
        }

        // Documentation & User Guide Shortcut
        GlassCard(borderColor = Color(0xFF10B981)) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToGuide() },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = null, tint = Color(0xFF10B981))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("User Manual / အသုံးပြုပုံ လမ်းညွှန်", fontSize = 13.sp, color = Color.White, fontWeight = FontWeight.Bold)
                            Text("ခေါင်းစဉ်အလိုက် အဆင့်ဆင့် အသုံးပြုပုံ ရှင်းလင်းချက်", fontSize = 11.sp, color = Color(0xFF94A3B8))
                        }
                    }
                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color(0xFF10B981))
                }

                HorizontalDivider(color = Color(0x1AFFFFFF))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToAbout() },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFF06B6D4))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("About UART PRO AI v3.5", fontSize = 13.sp, color = Color.White, fontWeight = FontWeight.Bold)
                            Text("Supported Hardware, Chipsets & Specs", fontSize = 11.sp, color = Color(0xFF94A3B8))
                        }
                    }
                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color(0xFF06B6D4))
                }
            }
        }
    }
}

