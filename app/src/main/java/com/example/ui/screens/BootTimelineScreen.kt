package com.example.ui.screens

import com.example.ui.theme.AppBackground
import com.example.ui.theme.CardBackground
import com.example.ui.theme.CardBorder
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextMutedColor
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.WarningYellow
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.TerminalBg

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.ui.components.GlassCard
import com.example.ui.components.StageStatusDot
import com.example.ui.components.StatusBadge
import com.example.ui.viewmodel.MainViewModel

@Composable
fun BootTimelineScreen(viewModel: MainViewModel) {
    val terminalOutput by viewModel.terminalOutput.collectAsState()
    val stages = viewModel.detectStages(terminalOutput)

    var expandedStageName by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF020617))
            .padding(16.dp)
    ) {
        // Header
        GlassCard(borderColor = Color(0xFF00F5A0)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Timeline, contentDescription = null, tint = Color(0xFF00F5A0))
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Animated Boot Stage Timeline",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Automatic Detection of BootROM, XBL, DDR, UFS, TrustZone & Kernel",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Stages Vertical Timeline
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(stages) { stage ->
                val isExpanded = expandedStageName == stage.stageName

                GlassCard(
                    onClick = {
                        expandedStageName = if (isExpanded) null else stage.stageName
                    }
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                StageStatusDot(stage.status)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = stage.stageName,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = stage.descriptionMm,
                                        fontSize = 11.sp,
                                        color = AccentCyan
                                    )
                                }
                            }
                            StatusBadge(stage.status.name)
                        }

                        if (isExpanded && stage.detectedLines.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(AppBackground)
                                    .padding(8.dp)
                            ) {
                                Column {
                                    Text(
                                        text = "Detected Serial Lines:",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextSecondary
                                    )
                                    stage.detectedLines.forEach { line ->
                                        Text(
                                            text = "> $line",
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 10.sp,
                                            color = Color(0xFF00F5A0)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
