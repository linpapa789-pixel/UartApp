package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Usb
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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

@Composable
fun SettingsScreen(viewModel: MainViewModel) {
    val currentLang by viewModel.currentLanguage.collectAsState()
    val showTimestamps by viewModel.showTimestamps.collectAsState()

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
                        text = "Hardware Baud, Drivers, Language & Gemini Configuration",
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

        // About & App Information Bento Box
        GlassCard(borderColor = Color(0xFF10B981)) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFF10B981))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("About UART PRO AI v3.5", fontSize = 14.sp, color = Color.White, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Engineered for Professional Phone Technicians & Embedded Hardware Engineers in Myanmar.\n\n• USB Driver Support: CH340G, CP2102, FT232R, PL2303\n• Protocol Support: Qualcomm BootROM/XBL/PBL, MTK Preloader/LK, Exynos S-BOOT, iPhone iBoot\n• Gemini 3.5 AI Core Integrated",
                    fontSize = 11.sp,
                    lineHeight = 18.sp,
                    color = Color(0xFF94A3B8)
                )
            }
        }
    }
}
