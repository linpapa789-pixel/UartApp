package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Compare
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.parser.DiffType
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

enum class DiffFilter { ALL, MATCHES, MISSING, EXTRA, WARNINGS, ERRORS }

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LogCompareScreen(viewModel: MainViewModel) {
    val goodLog by viewModel.goodCompareLog.collectAsState()
    val faultLog by viewModel.faultCompareLog.collectAsState()
    val result by viewModel.comparisonResult.collectAsState()
    val isComparing by viewModel.isComparing.collectAsState()
    val terminalOutput by viewModel.terminalOutput.collectAsState()

    val clipboardManager = LocalClipboardManager.current

    var sampleGoodInput by remember {
        mutableStateOf(
            goodLog.ifBlank {
                "PBL Start\nnvram_read OK\npmic_init OK\nddr_training OK\nufs_init OK\ntrustzone OK\nkernel boot OK"
            }
        )
    }

    var sampleFaultInput by remember {
        mutableStateOf(
            faultLog.ifBlank {
                "PBL Start\nnvram_read OK\npmic_init OK\nddr_training_error DQ lane 2\nEDL 9008 Mode"
            }
        )
    }

    var showAdvancedDiff by remember { mutableStateOf(false) }
    var activeFilter by remember { mutableStateOf(DiffFilter.ALL) }

    LaunchedEffect(Unit) {
        if (goodLog.isBlank()) viewModel.setGoodCompareLog(sampleGoodInput)
        if (faultLog.isBlank()) viewModel.setFaultCompareLog(sampleFaultInput)
    }

    val filteredDiffs = remember(result, activeFilter) {
        val diffs = result?.lineDiffs ?: emptyList()
        when (activeFilter) {
            DiffFilter.ALL -> diffs
            DiffFilter.MATCHES -> diffs.filter { it.status == DiffType.MATCH }
            DiffFilter.MISSING -> diffs.filter { it.status == DiffType.MISSING_IN_FAULT }
            DiffFilter.EXTRA -> diffs.filter { it.status == DiffType.EXTRA_IN_FAULT }
            DiffFilter.WARNINGS -> diffs.filter { it.status == DiffType.CHANGED }
            DiffFilter.ERRORS -> diffs.filter { it.status == DiffType.MISSING_IN_FAULT || it.status == DiffType.CHANGED }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // TOP HEADER CARD
        item {
            GlassCard(borderColor = AccentCyan.copy(alpha = 0.5f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Compare,
                                contentDescription = null,
                                tint = AccentCyan,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "UART Log Comparison Engine",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }
                        Text(
                            text = "LCS High-Precision Stage & Line Analysis",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }

                    if (isComparing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = AccentCyan,
                            strokeWidth = 2.dp
                        )
                    } else if (result != null) {
                        StatusBadge(status = "READY")
                    }
                }
            }
        }

        // QUICK ACTION BUTTONS
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        if (terminalOutput.isNotBlank()) {
                            sampleFaultInput = terminalOutput
                            viewModel.setFaultCompareLog(terminalOutput)
                        }
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = AccentCyan),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AccentCyan.copy(alpha = 0.5f))
                ) {
                    Icon(Icons.Default.Terminal, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Terminal -> Fault", fontSize = 10.sp)
                }

                OutlinedButton(
                    onClick = {
                        clipboardManager.getText()?.text?.let { text ->
                            sampleGoodInput = text
                            viewModel.setGoodCompareLog(text)
                        }
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = SuccessGreen),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SuccessGreen.copy(alpha = 0.5f))
                ) {
                    Icon(Icons.Default.ContentPaste, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Paste Good", fontSize = 10.sp)
                }

                OutlinedButton(
                    onClick = {
                        clipboardManager.getText()?.text?.let { text ->
                            sampleFaultInput = text
                            viewModel.setFaultCompareLog(text)
                        }
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ErrorRed),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ErrorRed.copy(alpha = 0.5f))
                ) {
                    Icon(Icons.Default.ContentPaste, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Paste Fault", fontSize = 10.sp)
                }
            }
        }

        // INPUT LOG BOXES
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = sampleGoodInput,
                    onValueChange = {
                        sampleGoodInput = it
                        viewModel.setGoodCompareLog(it)
                    },
                    label = { Text("GOOD Reference Log", color = SuccessGreen, fontSize = 10.sp) },
                    trailingIcon = {
                        if (sampleGoodInput.isNotEmpty()) {
                            IconButton(onClick = {
                                sampleGoodInput = ""
                                viewModel.setGoodCompareLog("")
                            }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear", tint = TextMutedColor, modifier = Modifier.size(14.dp))
                            }
                        }
                    },
                    maxLines = 5,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SuccessGreen,
                        unfocusedBorderColor = CardBorder,
                        focusedContainerColor = CardBackground,
                        unfocusedContainerColor = CardBackground,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                OutlinedTextField(
                    value = sampleFaultInput,
                    onValueChange = {
                        sampleFaultInput = it
                        viewModel.setFaultCompareLog(it)
                    },
                    label = { Text("FAULT Boot Log", color = ErrorRed, fontSize = 10.sp) },
                    trailingIcon = {
                        if (sampleFaultInput.isNotEmpty()) {
                            IconButton(onClick = {
                                sampleFaultInput = ""
                                viewModel.setFaultCompareLog("")
                            }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear", tint = TextMutedColor, modifier = Modifier.size(14.dp))
                            }
                        }
                    },
                    maxLines = 5,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ErrorRed,
                        unfocusedBorderColor = CardBorder,
                        focusedContainerColor = CardBackground,
                        unfocusedContainerColor = CardBackground,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )
            }
        }

        // OVERVIEW HIGHLIGHTS (Before Advanced Diff)
        result?.let { res ->
            // SIMILARITY GAUGE & SUMMARY CARD
            item {
                GlassCard(borderColor = AccentCyan.copy(alpha = 0.3f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Similarity Gauge Circular Badge
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(end = 16.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(CircleShape)
                                    .background(AccentCyan.copy(alpha = 0.1f))
                                    .border(3.dp, AccentCyan, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "%.0f%%".format(res.similarityPercentage),
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AccentCyan
                                    )
                                    Text(
                                        text = "MATCH",
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextSecondary
                                    )
                                }
                            }
                        }

                        // Metrics Summary
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "Comparison Overview",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Matched Lines:", fontSize = 11.sp, color = TextSecondary)
                                Text("${res.lineDiffs.count { it.status == DiffType.MATCH }}", fontSize = 11.sp, color = SuccessGreen, fontWeight = FontWeight.Bold)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Missing Lines:", fontSize = 11.sp, color = TextSecondary)
                                Text("${res.missingLinesCount}", fontSize = 11.sp, color = ErrorRed, fontWeight = FontWeight.Bold)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Extra Lines:", fontSize = 11.sp, color = TextSecondary)
                                Text("${res.extraLinesCount}", fontSize = 11.sp, color = WarningYellow, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // DETECTED FAILURE STAGE
            item {
                val failureStage = res.stageDifferences.firstOrNull()
                    ?: if (res.missingKeywords.isNotEmpty()) "Missing Keyword: ${res.missingKeywords.first()}"
                    else "Post-Bootloader / Normal"
                GlassCard(borderColor = ErrorRed.copy(alpha = 0.5f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(ErrorRed.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Error,
                                contentDescription = null,
                                tint = ErrorRed,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Detected Failure Stage",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                            Text(
                                text = failureStage,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = ErrorRed
                            )
                        }
                    }
                }
            }

            // BOOT STAGE PROGRESS & TIMELINE
            item {
                GlassCard(borderColor = CardBorder) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Timeline,
                                contentDescription = null,
                                tint = AccentCyan,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Boot Timeline & Stage Progress",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }

                        if (res.stageSimilarityMap.isEmpty()) {
                            Text(
                                text = "Stage progression verified consistently.",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        } else {
                            res.stageSimilarityMap.forEach { (stageName, similarity) ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 2.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = stageName,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = TextPrimary
                                    )
                                    StatusBadge(if (similarity >= 90f) "PASSED" else if (similarity > 50f) "WARNING" else "FAILED")
                                }
                            }
                        }
                    }
                }
            }

            // MISSING KEYWORDS
            if (res.missingKeywords.isNotEmpty()) {
                item {
                    GlassCard(borderColor = WarningYellow.copy(alpha = 0.4f)) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = WarningYellow,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Missing Boot Keywords (${res.missingKeywords.size})",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = WarningYellow
                                )
                            }

                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.padding(top = 4.dp)
                            ) {
                                res.missingKeywords.forEach { kw ->
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(WarningYellow.copy(alpha = 0.15f))
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = kw,
                                            fontSize = 10.sp,
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold,
                                            color = WarningYellow
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // TOGGLE BUTTON FOR ADVANCED LINE-BY-LINE DIFF
            item {
                Button(
                    onClick = { showAdvancedDiff = !showAdvancedDiff },
                    colors = ButtonDefaults.buttonColors(containerColor = CardBackground),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AccentCyan.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (showAdvancedDiff) "Hide Advanced Line-by-Line Diff" else "Show Advanced Line-by-Line Diff",
                        color = AccentCyan,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = if (showAdvancedDiff) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = AccentCyan
                    )
                }
            }

            // DETAILED LCS LINE-BY-LINE DIFF SECTION (Revealed on click)
            if (showAdvancedDiff) {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Diff Filters",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary
                        )

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            DiffFilter.values().forEach { filter ->
                                FilterChip(
                                    selected = activeFilter == filter,
                                    onClick = { activeFilter = filter },
                                    label = { Text(filter.name, fontSize = 10.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = AccentCyan,
                                        selectedLabelColor = AppBackground,
                                        containerColor = CardBackground,
                                        labelColor = TextSecondary
                                    )
                                )
                            }
                        }
                    }
                }

                items(filteredDiffs) { diff ->
                    val (lineBg, lineBorder, statusLabel) = when (diff.status) {
                        DiffType.MATCH -> Triple(SuccessGreen.copy(alpha = 0.08f), SuccessGreen.copy(alpha = 0.3f), "MATCH")
                        DiffType.CHANGED -> Triple(WarningYellow.copy(alpha = 0.12f), WarningYellow, "CHANGED")
                        DiffType.MISSING_IN_FAULT -> Triple(ErrorRed.copy(alpha = 0.12f), ErrorRed, "MISSING")
                        DiffType.EXTRA_IN_FAULT -> Triple(AccentCyan.copy(alpha = 0.12f), AccentCyan, "EXTRA")
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(lineBg)
                            .border(1.dp, lineBorder, RoundedCornerShape(8.dp))
                            .padding(8.dp)
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
                                    color = TextPrimary
                                )
                                if (diff.status == DiffType.CHANGED && diff.goodLine != null) {
                                    Text(
                                        text = "Good Ref: ${diff.goodLine}",
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 10.sp,
                                        color = SuccessGreen
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            StatusBadge(statusLabel)
                        }
                    }
                }
            }
        } ?: item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Paste or type logs above to view boot comparison insights.",
                    fontSize = 12.sp,
                    color = TextMutedColor
                )
            }
        }
    }
}
