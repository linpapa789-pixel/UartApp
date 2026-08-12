package com.example.ui.screens

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Compare
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.parser.DiffType
import com.example.ui.components.GlassCard
import com.example.ui.components.StatusBadge
import com.example.ui.viewmodel.MainViewModel

@Composable
fun LogCompareScreen(viewModel: MainViewModel) {
    val goodLog by viewModel.goodCompareLog.collectAsState()
    val faultLog by viewModel.faultCompareLog.collectAsState()
    val result by viewModel.comparisonResult.collectAsState()

    var sampleGoodInput by remember { mutableStateOf(goodLog.ifBlank { "PBL Start\npmic_init OK\nddr_training OK\nufs_init OK\ntrustzone OK\nkernel boot OK" }) }
    var sampleFaultInput by remember { mutableStateOf(faultLog.ifBlank { "PBL Start\npmic_init OK\nddr_training_error DQ lane 2\nEDL 9008 Mode" }) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF020617))
            .padding(12.dp)
    ) {
        // Top Summary Glass Header
        GlassCard(borderColor = Color(0xFFFFB800)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Compare, contentDescription = null, tint = Color(0xFFFFB800))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Smart Log Comparison Engine",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Text(
                        text = "Auto Difference Detection & Missing Boot Stage Comparison",
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8)
                    )
                }

                result?.let { res ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFFFFB800).copy(alpha = 0.2f))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "Match: %.1f%%".format(res.similarityPercentage),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFB800)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Split Input Section or Analysis Result
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Good Log Input Box
            OutlinedTextField(
                value = sampleGoodInput,
                onValueChange = {
                    sampleGoodInput = it
                    viewModel.setGoodCompareLog(it)
                },
                label = { Text("GOOD Reference Log", color = Color(0xFF00F5A0)) },
                modifier = Modifier
                    .weight(1f)
                    .height(140.dp),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF00F5A0),
                    unfocusedBorderColor = Color(0xFF1E293B),
                    focusedContainerColor = Color(0xFF0F172A),
                    unfocusedContainerColor = Color(0xFF0F172A),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )

            // Fault Log Input Box
            OutlinedTextField(
                value = sampleFaultInput,
                onValueChange = {
                    sampleFaultInput = it
                    viewModel.setFaultCompareLog(it)
                },
                label = { Text("FAULT Boot Log", color = Color(0xFFFF2A6D)) },
                modifier = Modifier
                    .weight(1f)
                    .height(140.dp),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFFF2A6D),
                    unfocusedBorderColor = Color(0xFF1E293B),
                    focusedContainerColor = Color(0xFF0F172A),
                    unfocusedContainerColor = Color(0xFF0F172A),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = {
                viewModel.setGoodCompareLog(sampleGoodInput)
                viewModel.setFaultCompareLog(sampleFaultInput)
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB800)),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text("Compare Log Files", color = Color.Black, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Line-by-line Diff Output
        Text(
            text = "Line-by-Line Difference Breakdown",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFF8FAFC)
        )

        Spacer(modifier = Modifier.height(6.dp))

        result?.let { res ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(res.lineDiffs) { diff ->
                    val (lineBg, lineBorder, statusLabel) = when (diff.status) {
                        DiffType.MATCH -> Triple(Color(0xFF00F5A0).copy(alpha = 0.08f), Color(0xFF00F5A0).copy(alpha = 0.3f), "MATCH")
                        DiffType.CHANGED -> Triple(Color(0xFFFFB800).copy(alpha = 0.15f), Color(0xFFFFB800), "CHANGED")
                        DiffType.MISSING_IN_FAULT -> Triple(Color(0xFFFF2A6D).copy(alpha = 0.15f), Color(0xFFFF2A6D), "MISSING IN FAULT")
                        DiffType.EXTRA_IN_FAULT -> Triple(Color(0xFF38BDF8).copy(alpha = 0.15f), Color(0xFF38BDF8), "EXTRA")
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(lineBg)
                            .border(1.dp, lineBorder, RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Line #${diff.lineNumber}: ${diff.faultLine ?: diff.goodLine ?: ""}",
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.White
                                )
                                if (diff.status == DiffType.CHANGED && diff.goodLine != null) {
                                    Text(
                                        text = "Good ref was: ${diff.goodLine}",
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 10.sp,
                                        color = Color(0xFF00F5A0)
                                    )
                                }
                            }
                            StatusBadge(statusLabel)
                        }
                    }
                }
            }
        }
    }
}
