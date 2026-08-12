package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeveloperBoard
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PrecisionManufacturing
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.usb.SimulationDeviceType
import com.example.ui.components.GlassCard
import com.example.ui.components.MetricCard
import com.example.ui.components.StatusBadge
import com.example.ui.viewmodel.MainViewModel

@Composable
fun DashboardScreen(
    viewModel: MainViewModel,
    onNavigateToTerminal: () -> Unit,
    onNavigateToCompare: () -> Unit,
    onNavigateToAi: () -> Unit
) {
    val savedSessions by viewModel.savedSessions.collectAsState()
    val repairCases by viewModel.repairCases.collectAsState()
    val connectionState by viewModel.connectionState.collectAsState()

    val solvedCount = repairCases.count { it.isSolved }
    val totalSessions = savedSessions.size

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF020617))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Header Banner
        item {
            GlassCard(
                borderColor = Color(0xFF00F0FF)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "UART PRO AI",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF00F0FF)
                            )
                            Text(
                                text = "Professional Mobile Boot Log Analyzer",
                                fontSize = 12.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(
                                    if (connectionState.isConnected) Color(0xFF00F5A0).copy(alpha = 0.2f)
                                    else Color(0xFFFF2A6D).copy(alpha = 0.2f)
                                )
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = if (connectionState.isConnected) "ONLINE 115200" else "READY / SIM",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (connectionState.isConnected) Color(0xFF00F5A0) else Color(0xFFFF2A6D)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "🇲🇲 မြန်မာဖုန်းပြင်ဆရာများအတွက် အဆင့်မြင့် မိုဘိုင်း Motherboard UART သုံးသပ်နည်းပညာစနစ်",
                        fontSize = 13.sp,
                        color = Color(0xFFE2E8F0)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                viewModel.startUsbSimulation(SimulationDeviceType.QUALCOMM_SNAPDRAGON_FAULT)
                                onNavigateToTerminal()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00F0FF)),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.Black)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Start Qualcomm Log", color = Color.Black, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = onNavigateToAi,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("AI Repair Assistant", color = Color(0xFF38BDF8))
                        }
                    }
                }
            }
        }

        // Metrics Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricCard(
                    title = "Total Sessions",
                    value = "$totalSessions",
                    icon = Icons.Default.Terminal,
                    accentColor = Color(0xFF00F0FF),
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "Solved Cases",
                    value = "$solvedCount",
                    icon = Icons.Default.CheckCircle,
                    accentColor = Color(0xFF00F5A0),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Quick Launch Actions
        item {
            Text(
                text = "Diagnostic Modules",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFF8FAFC)
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                GlassCard(
                    modifier = Modifier.weight(1f),
                    borderColor = Color(0xFF38BDF8),
                    onClick = onNavigateToTerminal
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.DeveloperBoard, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(32.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("UART Terminal", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text("Live USB / Baud", fontSize = 10.sp, color = Color(0xFF94A3B8))
                    }
                }

                GlassCard(
                    modifier = Modifier.weight(1f),
                    borderColor = Color(0xFFFFB800),
                    onClick = onNavigateToCompare
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Memory, contentDescription = null, tint = Color(0xFFFFB800), modifier = Modifier.size(32.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Log Compare", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text("Good vs Bad Diff", fontSize = 10.sp, color = Color(0xFF94A3B8))
                    }
                }
            }
        }

        // Recent Repair Cases Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Repair Database",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFF8FAFC)
                )
            }
        }

        items(repairCases.take(5)) { repairCase ->
            GlassCard {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = repairCase.title,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFF8FAFC)
                        )
                        StatusBadge(if (repairCase.isSolved) "SOLVED" else "PENDING")
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Fault: ${repairCase.fault} | Key: ${repairCase.uartKeyLog}",
                        fontSize = 12.sp,
                        color = Color(0xFF38BDF8)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Fix: ${repairCase.repairSteps}",
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
            }
        }
    }
}
