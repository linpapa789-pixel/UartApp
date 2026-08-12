package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeveloperBoard
import androidx.compose.material.icons.filled.Hardware
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Usb
import androidx.compose.material3.Badge
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.GlassCard
import com.example.ui.viewmodel.MainViewModel

@Composable
fun AboutScreen(viewModel: MainViewModel) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF000000))
            .padding(16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // App Hero Header Card
        GlassCard(borderColor = Color(0xFF06B6D4)) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFF06B6D4), Color(0xFF3B82F6))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Terminal,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "UART PRO AI",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Text(
                    text = "Version 3.5.0 (Pro Engineer Edition)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF06B6D4)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "🇲🇲 မြန်မာဖုန်းပြင်ဆရာများအတွက် အဆင့်မြင့် မိုဘိုင်း Motherboard UART Boot Log သုံးသပ်နည်းပညာစနစ်",
                    fontSize = 12.sp,
                    color = Color(0xFFCBD5E1),
                    lineHeight = 18.sp
                )
            }
        }

        // Hardware Support & Compatible Chips
        GlassCard(borderColor = Color(0xFF3B82F6)) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Usb,
                        contentDescription = null,
                        tint = Color(0xFF3B82F6),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "ထောက်ပံ့ပေးထားသော USB Serial IC များ",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                HorizontalDivider(color = Color(0x1AFFFFFF))

                val usbChips = listOf(
                    "FTDI (FT232RL, FT232H, FT2232H)",
                    "Silicon Labs (CP2102, CP2104, CP2108)",
                    "WCH (CH340G, CH340C, CH341A)",
                    "Prolific (PL2303HX, PL2303TA)",
                    "Standard CDC/ACM Serial Emulators"
                )

                usbChips.forEach { chip ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF10B981),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = chip, fontSize = 12.sp, color = Color(0xFFE2E8F0))
                    }
                }
            }
        }

        // Supported Mobile Chipsets & BootROMs
        GlassCard(borderColor = Color(0xFFF59E0B)) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.PhoneAndroid,
                        contentDescription = null,
                        tint = Color(0xFFF59E0B),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "ထောက်ပံ့ပေးထားသော SoC Chipset များ",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                HorizontalDivider(color = Color(0x1AFFFFFF))

                val socList = listOf(
                    "Qualcomm Snapdragon (PBL, XBL, SBL, ABL, Linux Kernel)",
                    "MediaTek Dimensity / Helio (Preloader, LK, ATF, Android Kernel)",
                    "Samsung Exynos (S-BOOT, BL1, BL2, EL3)",
                    "Apple iPhone (iBoot, SecureROM, SEP)",
                    "Unisoc / Spreadtrum (FDL1, FDL2)"
                )

                socList.forEach { soc ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Memory,
                            contentDescription = null,
                            tint = Color(0xFFF59E0B),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = soc, fontSize = 12.sp, color = Color(0xFFE2E8F0))
                    }
                }
            }
        }

        // Wiring & Hardware Setup Specifications
        GlassCard(borderColor = Color(0xFF8B5CF6)) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Hardware,
                        contentDescription = null,
                        tint = Color(0xFF8B5CF6),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Hardware Pinout Specs (အရေးကြီး)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                HorizontalDivider(color = Color(0x1AFFFFFF))

                Text(
                    text = "• Logic Level Voltage: 1.8V (လိုအပ်ပါက 1.8V Level Shifter / Converter သုံးပါ)\n• TXD Connection: Phone Board TXD -> USB RXD Pin\n• RXD Connection: Phone Board RXD -> USB TXD Pin\n• Ground: Phone Board GND -> USB GND Pin\n• Default Baud Rate: 115200 bps (8N1) သို့မဟုတ် 921600 bps",
                    fontSize = 12.sp,
                    color = Color(0xFFCBD5E1),
                    lineHeight = 18.sp
                )
            }
        }

        // Developer & System Credits
        GlassCard {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "UART PRO AI Diagnostic Engine",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF94A3B8)
                )
                Text(
                    text = "Powered by Gemini 3.5 Flash AI Core & LCS Diff Engine",
                    fontSize = 10.sp,
                    color = Color(0xFF64748B)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Copyright © 2026. Designed for Mobile Technicians.",
                    fontSize = 10.sp,
                    color = Color(0xFF475569)
                )
            }
        }
    }
}
