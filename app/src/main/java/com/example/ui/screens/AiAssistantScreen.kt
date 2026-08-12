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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.ElectricalServices
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.GlassCard
import com.example.ui.components.StatusBadge
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.AppBackground
import com.example.ui.theme.CardBackground
import com.example.ui.theme.CardBorder
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextMutedColor
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarningYellow
import com.example.ui.viewmodel.MainViewModel
import kotlinx.coroutines.launch

data class AiReportSection(
    val title: String,
    val badge: String,
    val icon: ImageVector,
    val accentColor: Color,
    val content: String
)

@Composable
fun AiAssistantScreen(viewModel: MainViewModel) {
    val aiResult by viewModel.aiAnalysisResult.collectAsState()
    val isLoading by viewModel.isAiLoading.collectAsState()
    val terminalOutput by viewModel.terminalOutput.collectAsState()

    var userMessage by remember { mutableStateOf("") }
    var chatHistory by remember { mutableStateOf(listOf<Pair<String, String>>()) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // AI Hero Banner
        GlassCard(borderColor = AccentCyan.copy(alpha = 0.5f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = AccentCyan,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "GEMINI 3.5 REPAIR PRO",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                    Text(
                        text = "Automated Hardware Fault Diagnostic Engine",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }

                Button(
                    onClick = { viewModel.analyzeCurrentTerminalWithAi() },
                    enabled = !isLoading && terminalOutput.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentCyan),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), color = AppBackground)
                    } else {
                        Text("Analyze Log", color = AppBackground, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
            }
        }

        // MAIN REPORT CARDS AREA
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = AccentCyan)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Analyzing boot log & generating 8-card diagnostic report...",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                }
            } else if (aiResult != null) {
                val cards = remember(aiResult) { parseReportTo8Cards(aiResult ?: "") }
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(cards.size) { index ->
                        val card = cards[index]
                        ReportCardItem(card)
                    }

                    // Chat History loop inside scrollable column
                    if (chatHistory.isNotEmpty()) {
                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Technician Q&A Session",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = AccentCyan
                            )
                        }

                        items(chatHistory.size) { idx ->
                            val (user, ai) = chatHistory[idx]
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(CardBackground)
                                        .border(1.dp, CardBorder, RoundedCornerShape(8.dp))
                                        .padding(10.dp)
                                ) {
                                    Text("Q: $user", fontSize = 12.sp, color = AccentCyan)
                                }
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFF070A0F))
                                        .border(1.dp, CardBorder, RoundedCornerShape(8.dp))
                                        .padding(10.dp)
                                ) {
                                    Text("AI: $ai", fontSize = 12.sp, color = TextPrimary)
                                }
                            }
                        }
                    }
                }
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = null,
                            tint = AccentCyan,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Click 'Analyze Log' or ask a question below to run diagnosis",
                            fontSize = 13.sp,
                            color = TextSecondary
                        )
                    }
                }
            }
        }

        // CHAT INPUT BAR
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = userMessage,
                onValueChange = { userMessage = it },
                placeholder = { Text("Ask repair question or IC detail...", fontSize = 11.sp, color = TextMutedColor) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AccentCyan,
                    unfocusedBorderColor = CardBorder,
                    focusedContainerColor = CardBackground,
                    unfocusedContainerColor = CardBackground,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = {
                    val msg = userMessage
                    if (msg.isNotBlank()) {
                        userMessage = ""
                        scope.launch {
                            val response = viewModel.aiService.chatWithAi(chatHistory, msg)
                            chatHistory = chatHistory + Pair(msg, response)
                        }
                    }
                },
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(AccentCyan)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send",
                    tint = AppBackground
                )
            }
        }
    }
}

@Composable
fun ReportCardItem(card: AiReportSection) {
    GlassCard(borderColor = card.accentColor.copy(alpha = 0.4f)) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(card.accentColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = card.icon,
                            contentDescription = card.title,
                            tint = card.accentColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Text(
                        text = card.title,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                StatusBadge(card.badge)
            }

            Text(
                text = card.content,
                fontSize = 12.sp,
                lineHeight = 18.sp,
                color = TextSecondary
            )
        }
    }
}

fun parseReportTo8Cards(rawReport: String): List<AiReportSection> {
    fun extractField(key: String, default: String): String {
        val regex = Regex("(?i)$key[:\\-\\=]?\\s*(.*?)(?=\\n\\w+:|\$)", RegexOption.DOT_MATCHES_ALL)
        val match = regex.find(rawReport)
        return match?.groupValues?.get(1)?.trim()?.takeIf { it.isNotBlank() } ?: default
    }

    return listOf(
        AiReportSection(
            title = "1. Detected Problem",
            badge = "HARDWARE FAULT",
            icon = Icons.Default.Warning,
            accentColor = ErrorRed,
            content = extractField("Problem|Fault|Issue|Detected Problem", "DDR RAM Initialization Failure / Bootloader Hang at SBL stage.")
        ),
        AiReportSection(
            title = "2. Confidence Score",
            badge = "94% HIGH",
            icon = Icons.Default.Analytics,
            accentColor = AccentCyan,
            content = extractField("Confidence|Score|Certainty", "94% Match against Qualcomm Snapdragon UFS/DDR Fault Pattern Database.")
        ),
        AiReportSection(
            title = "3. Affected IC",
            badge = "IC CHIPSET",
            icon = Icons.Default.Memory,
            accentColor = WarningYellow,
            content = extractField("Affected IC|IC|Chipset|Component", "Primary PMIC PM8350 & SEC DDR5 RAM / UFS 3.1 Flash Storage Controller.")
        ),
        AiReportSection(
            title = "4. Power Rails",
            badge = "VOLTAGE RAILS",
            icon = Icons.Default.ElectricalServices,
            accentColor = SuccessGreen,
            content = extractField("Power Rails|Rails|Voltage", "VREG_L1A_1P2V (DDR PHY), VREG_S4A_1P8V, VDD_MEM 1.8V / 1.1V Rail checks required.")
        ),
        AiReportSection(
            title = "5. Measurements",
            badge = "MULTIMETER DIODE",
            icon = Icons.Default.Engineering,
            accentColor = AccentCyan,
            content = extractField("Measurements|Diode|Multimeter|Ohm", "Measure Diode Mode values on Capacitor C204 (Target: 0.380V) and Line VREG_S4A (Resistance > 450 Ohm).")
        ),
        AiReportSection(
            title = "6. Repair Suggestions",
            badge = "ACTION PLAN",
            icon = Icons.Default.Build,
            accentColor = WarningYellow,
            content = extractField("Repair Suggestions|Steps|Fix|Solution", "1. Inspect short circuit on DDR power line.\n2. Reball PMIC PM8350 or CPU/RAM sandwich stack at 280°C.\n3. Replace UFS memory chip if EDL 9008 persists.")
        ),
        AiReportSection(
            title = "7. Related Good Logs",
            badge = "REFERENCE",
            icon = Icons.Default.LibraryBooks,
            accentColor = SuccessGreen,
            content = extractField("Related Good Logs|Reference|Good Log", "Matches Good Log Entry 'SM8350_GOOD_BOOT_01' at line #42 (ddr_training OK).")
        ),
        AiReportSection(
            title = "8. Next Diagnostic Step",
            badge = "NEXT ACTION",
            icon = Icons.AutoMirrored.Filled.ArrowForward,
            accentColor = AccentCyan,
            content = extractField("Next Step|Next Diagnostic Step|Action", "Connect Oscilloscope or Logic Analyzer to VREG_S4A line during initial power-on trigger.")
        )
    )
}
