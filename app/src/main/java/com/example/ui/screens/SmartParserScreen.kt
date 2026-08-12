package com.example.ui.screens

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.IntegrationInstructions
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.parser.SmartUartParser
import com.example.ui.components.GlassCard
import com.example.ui.viewmodel.MainViewModel

@Composable
fun SmartParserScreen(viewModel: MainViewModel) {
    val terminalOutput by viewModel.terminalOutput.collectAsState()
    var searchKeyword by remember { mutableStateOf("") }
    var selectedKeywordName by remember { mutableStateOf<String?>(null) }

    val detectedKeywords = if (terminalOutput.isNotBlank()) {
        SmartUartParser.parseLineForKeywords(terminalOutput)
    } else emptyList()

    val displayKeywords = if (searchKeyword.isNotBlank()) {
        SmartUartParser.parseLineForKeywords(searchKeyword)
    } else {
        detectedKeywords.ifEmpty { SmartUartParser.parseLineForKeywords("pmic ddr ufs emmc rpmb dtbo vbmeta edl charger display touch modem thermal") }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF020617))
            .padding(16.dp)
    ) {
        // Header
        GlassCard(borderColor = Color(0xFF00F0FF)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.IntegrationInstructions, contentDescription = null, tint = Color(0xFF00F0FF))
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Smart Hardware UART Dictionary",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Indexed Hardware ICs, Voltages, Power Rails, & Repair Instructions",
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Search Field
        OutlinedTextField(
            value = searchKeyword,
            onValueChange = { searchKeyword = it },
            label = { Text("Search Keyword (e.g. pmic, ddr, ufs, edl, touch, display)") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF00F0FF)) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF00F0FF),
                unfocusedBorderColor = Color(0xFF1E293B),
                focusedContainerColor = Color(0xFF0F172A),
                unfocusedContainerColor = Color(0xFF0F172A),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(displayKeywords) { kw ->
                val isExpanded = selectedKeywordName == kw.keyword

                GlassCard(
                    onClick = {
                        selectedKeywordName = if (isExpanded) null else kw.keyword
                    }
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = kw.keyword.uppercase(),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF00F0FF)
                                )
                                Text(
                                    text = kw.meaningMm,
                                    fontSize = 12.sp,
                                    color = Color(0xFFE2E8F0)
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF00F0FF).copy(alpha = 0.2f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "${kw.confidenceScore}% Match",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF00F0FF)
                                )
                            }
                        }

                        if (isExpanded) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF000000))
                                    .padding(10.dp)
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text("• Boot Stage: ${kw.bootStage}", fontSize = 11.sp, color = Color(0xFF38BDF8))
                                    Text("• Related IC: ${kw.relatedIc}", fontSize = 11.sp, color = Color(0xFF00F5A0))
                                    Text("• Related Voltage: ${kw.relatedVoltage}", fontSize = 11.sp, color = Color(0xFFFFB800))
                                    Text("• Power Rail: ${kw.relatedPowerRail}", fontSize = 11.sp, color = Color(0xFFE2E8F0))
                                    Text("• Possible Hardware Cause: ${kw.possibleHardware}", fontSize = 11.sp, color = Color(0xFFFF2A6D))
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("🇲🇲 ပြုပြင်ရန် အကြံပြုချက် (Repair Suggestion):", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF00F0FF))
                                    Text(kw.repairSuggestionMm, fontSize = 11.sp, color = Color(0xFFF8FAFC))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
