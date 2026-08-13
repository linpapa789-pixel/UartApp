package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Power
import androidx.compose.material.icons.filled.PowerOff
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Usb
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.usb.SimulationDeviceType
import com.example.ui.components.GlassCard
import com.example.ui.components.StatusBadge
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.AppBackground
import com.example.ui.theme.CardBackground
import com.example.ui.theme.CardBorder
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.LogBootColor
import com.example.ui.theme.LogErrorColor
import com.example.ui.theme.LogInfoColor
import com.example.ui.theme.LogTimeColor
import com.example.ui.theme.LogWarnColor
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TerminalBg
import com.example.ui.theme.TextMutedColor
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarningYellow
import com.example.ui.viewmodel.MainViewModel
import kotlinx.coroutines.launch

@Composable
fun TerminalScreen(
    viewModel: MainViewModel,
    onNavigateToAi: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    val connectionState by viewModel.connectionState.collectAsState()
    val terminalOutput by viewModel.terminalOutput.collectAsState()
    val isPaused by viewModel.isTerminalPaused.collectAsState()
    val autoScroll by viewModel.autoScroll.collectAsState()

    var baudMenuExpanded by remember { mutableStateOf(false) }
    var simMenuExpanded by remember { mutableStateOf(false) }

    val baudRates = listOf(9600, 115200, 460800, 921600, 1500000, 3000000)

    val logLines = remember(terminalOutput) {
        if (terminalOutput.isBlank()) emptyList()
        else terminalOutput.lines()
    }

    val listState = rememberLazyListState()

    LaunchedEffect(logLines.size) {
        if (autoScroll && !isPaused && logLines.isNotEmpty()) {
            listState.animateScrollToItem(logLines.size - 1)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .padding(12.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // TOOLBAR: Device Name, USB Status, Baud Rate
            GlassCard(
                borderColor = AccentCyan.copy(alpha = 0.4f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (connectionState.isConnected) SuccessGreen.copy(alpha = 0.15f)
                                    else if (connectionState.isSimulationActive) WarningYellow.copy(alpha = 0.15f)
                                    else ErrorRed.copy(alpha = 0.15f)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Usb,
                                contentDescription = null,
                                tint = if (connectionState.isConnected) SuccessGreen
                                else if (connectionState.isSimulationActive) WarningYellow
                                else ErrorRed,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Column {
                            Text(
                                text = connectionState.deviceName,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                StatusBadge(
                                    status = if (connectionState.isConnected) "ONLINE"
                                    else if (connectionState.isSimulationActive) "SIMULATION"
                                    else "DISCONNECTED"
                                )
                                Text(
                                    text = "${connectionState.totalLines} lines",
                                    fontSize = 10.sp,
                                    color = TextMutedColor
                                )
                            }
                        }
                    }

                    // Baud Selector Dropdown
                    Box {
                        OutlinedButton(
                            onClick = { baudMenuExpanded = true },
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, AccentCyan.copy(alpha = 0.5f))
                        ) {
                            Text(
                                text = "${connectionState.baudRate}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = AccentCyan
                            )
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
            }

            // LIVE UART LOG - Occupying ~70% of screen height
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.72f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(TerminalBg)
                    .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
                    .padding(8.dp)
            ) {
                if (logLines.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = ">>> UART Serial Terminal Standby",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 13.sp,
                                color = AccentCyan,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Connect USB-TTL or press 'Connect' to stream logs.",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                color = TextMutedColor
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        itemsIndexed(logLines) { index, line ->
                            ColoredLogLine(lineNumber = index + 1, lineText = line)
                        }
                    }
                }
            }

            // BOTTOM CONTROLS: Connect, Disconnect, Pause, Clear, Save
            GlassCard(
                borderColor = CardBorder
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Connect Button with Simulation Popup
                    Box {
                        Button(
                            onClick = {
                                viewModel.connectToHardwareUsb()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Power,
                                contentDescription = "Connect",
                                tint = AppBackground,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Connect", fontSize = 11.sp, color = AppBackground, fontWeight = FontWeight.Bold)
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

                    // Disconnect Button
                    OutlinedButton(
                        onClick = { viewModel.stopUsbConnection() },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = ErrorRed),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ErrorRed.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PowerOff,
                            contentDescription = "Disconnect",
                            tint = ErrorRed,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Disconnect", fontSize = 11.sp, color = ErrorRed, fontWeight = FontWeight.Bold)
                    }

                    // Pause Button
                    IconButton(
                        onClick = { viewModel.togglePauseTerminal() },
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(CardBackground)
                    ) {
                        Icon(
                            imageVector = if (isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                            contentDescription = "Pause",
                            tint = if (isPaused) SuccessGreen else WarningYellow,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Clear Button
                    IconButton(
                        onClick = { viewModel.clearTerminal() },
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(CardBackground)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear",
                            tint = ErrorRed,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Save Button
                    IconButton(
                        onClick = {
                            if (terminalOutput.isNotBlank()) {
                                viewModel.saveCurrentSession(
                                    brand = "Generic",
                                    model = connectionState.deviceName,
                                    codename = "uart_log",
                                    chipset = "Snapdragon/MediaTek",
                                    repairJob = "JOB-${System.currentTimeMillis() % 10000}",
                                    technician = "Tech Pro",
                                    status = "Captured",
                                    notes = "UART Live Log Capture"
                                )
                                Toast.makeText(context, "Log session saved successfully!", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Terminal is empty!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(CardBackground)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Save,
                            contentDescription = "Save",
                            tint = AccentCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // FLOATING ACTION BUTTON: Analyze Button
        ExtendedFloatingActionButton(
            onClick = {
                viewModel.analyzeCurrentTerminalWithAi()
                onNavigateToAi()
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = "Analyze",
                    tint = AppBackground
                )
            },
            text = {
                Text(
                    text = "Analyze",
                    color = AppBackground,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            },
            containerColor = AccentCyan,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 76.dp, end = 8.dp)
        )
    }
}

@Composable
fun ColoredLogLine(lineNumber: Int, lineText: String) {
    val lowercaseText = remember(lineText) { lineText.lowercase() }
    val textColor = when {
        lowercaseText.contains("error") || lowercaseText.contains("fail") || lowercaseText.contains("panic") || lowercaseText.contains("fatal") || lowercaseText.contains("[e]") -> LogErrorColor
        lowercaseText.contains("warn") || lowercaseText.contains("warning") || lowercaseText.contains("[w]") -> LogWarnColor
        lowercaseText.contains("pass") || lowercaseText.contains("ok") || lowercaseText.contains("success") || lowercaseText.contains("init") || lowercaseText.contains("[b]") -> LogBootColor
        else -> LogInfoColor
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = "%04d ".format(lineNumber),
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            color = LogTimeColor
        )
        Text(
            text = lineText,
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            color = textColor
        )
    }
}
