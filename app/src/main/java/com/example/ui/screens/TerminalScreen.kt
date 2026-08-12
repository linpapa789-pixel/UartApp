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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Usb
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.usb.SimulationDeviceType
import com.example.ui.components.GlassCard
import com.example.ui.viewmodel.MainViewModel

@Composable
fun TerminalScreen(
    viewModel: MainViewModel,
    onNavigateToAi: () -> Unit
) {
    val connectionState by viewModel.connectionState.collectAsState()
    val terminalOutput by viewModel.terminalOutput.collectAsState()
    val isPaused by viewModel.isTerminalPaused.collectAsState()
    val autoScroll by viewModel.autoScroll.collectAsState()

    val scrollState = rememberScrollState()

    var baudMenuExpanded by remember { mutableStateOf(false) }
    var simMenuExpanded by remember { mutableStateOf(false) }

    val baudRates = listOf(9600, 115200, 460800, 921600, 1500000, 3000000)

    LaunchedEffect(terminalOutput) {
        if (autoScroll && !isPaused) {
            scrollState.animateScrollTo(scrollState.maxValue)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF020617))
            .padding(12.dp)
    ) {
        // Top Toolbar Controls
        GlassCard(
            borderColor = Color(0xFF00F0FF)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Usb,
                            contentDescription = null,
                            tint = if (connectionState.isConnected) Color(0xFF00F5A0) else Color(0xFFFF2A6D)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = connectionState.deviceName,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Driver: ${connectionState.driverType} | Lines: ${connectionState.totalLines} | Bytes: ${connectionState.bytesReceived}",
                                fontSize = 10.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }

                    // Baud Selector Dropdown Button
                    Box {
                        OutlinedButton(
                            onClick = { baudMenuExpanded = true },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("${connectionState.baudRate} Baud", fontSize = 11.sp, color = Color(0xFF00F0FF))
                        }
                        DropdownMenu(
                            expanded = baudMenuExpanded,
                            onDismissRequest = { baudMenuExpanded = false }
                        ) {
                            baudRates.forEach { rate ->
                                DropdownMenuItem(
                                    text = { Text("$rate Baud") },
                                    onClick = {
                                        viewModel.setBaudRate(rate)
                                        baudMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Action Buttons Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Simulation Selector Dropdown
                    Box {
                        Button(
                            onClick = { simMenuExpanded = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Simulate Log", fontSize = 11.sp, color = Color.White)
                        }
                        DropdownMenu(
                            expanded = simMenuExpanded,
                            onDismissRequest = { simMenuExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Qualcomm Snapdragon Fault") },
                                onClick = {
                                    viewModel.startUsbSimulation(SimulationDeviceType.QUALCOMM_SNAPDRAGON_FAULT)
                                    simMenuExpanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("MediaTek Dimensity Good Boot") },
                                onClick = {
                                    viewModel.startUsbSimulation(SimulationDeviceType.MEDIATEK_DIMENSITY_GOOD)
                                    simMenuExpanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Samsung Exynos Bootloop") },
                                onClick = {
                                    viewModel.startUsbSimulation(SimulationDeviceType.EXYNOS_BOOTLOOP)
                                    simMenuExpanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Apple A16 Kernel Panic") },
                                onClick = {
                                    viewModel.startUsbSimulation(SimulationDeviceType.IPHONE_A16_PANIC)
                                    simMenuExpanded = false
                                }
                            )
                        }
                    }

                    IconButton(
                        onClick = { viewModel.togglePauseTerminal() },
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF1E293B))
                    ) {
                        Icon(
                            imageVector = if (isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                            contentDescription = null,
                            tint = if (isPaused) Color(0xFF00F5A0) else Color(0xFFFFB800)
                        )
                    }

                    IconButton(
                        onClick = { viewModel.clearTerminal() },
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF1E293B))
                    ) {
                        Icon(Icons.Default.Clear, contentDescription = null, tint = Color(0xFFFF2A6D))
                    }

                    Button(
                        onClick = {
                            viewModel.analyzeCurrentTerminalWithAi()
                            onNavigateToAi()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00F0FF)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("AI Diagnose", fontSize = 11.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Main Black Terminal Window Box
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF000000))
                .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(12.dp))
                .padding(12.dp)
        ) {
            Text(
                text = if (terminalOutput.isBlank()) ">>> UART Serial Monitor Standby.\n>>> Connect USB-TTL Adapter (CH340/CP2102) or select 'Simulate Log'." else terminalOutput,
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                color = Color(0xFF00F5A0),
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
            )
        }
    }
}
