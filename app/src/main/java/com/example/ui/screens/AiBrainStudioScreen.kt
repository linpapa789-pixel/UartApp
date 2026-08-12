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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.automirrored.filled.Rule
import androidx.compose.material.icons.filled.SafetyCheck
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Workspaces
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.AiKeywordEntity
import com.example.data.local.entity.AiMemoryEntity
import com.example.data.local.entity.AiPromptEntity
import com.example.data.local.entity.AiRepairKnowledgeEntity
import com.example.data.local.entity.AiRoleEntity
import com.example.data.local.entity.AiRuleEntity
import com.example.ui.components.GlassCard
import com.example.ui.viewmodel.MainViewModel
import kotlinx.coroutines.launch

enum class BrainSubTab(val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    PROMPTS("Prompts", Icons.Default.Psychology),
    ROLES("Roles", Icons.Default.Workspaces),
    RULES("Rules", Icons.AutoMirrored.Filled.Rule),
    KEYWORDS("Keywords", Icons.Default.Storage),
    KNOWLEDGE("Repair Know-how", Icons.Default.Memory),
    MEMORIES("AI Memory", Icons.Default.Bookmark),
    REASONING("Reasoning", Icons.Default.Tune),
    PRIORITY("Priority", Icons.Default.Layers),
    WORKFLOW("Workflow", Icons.Default.Category),
    TESTER("Prompt Tester", Icons.Default.Terminal),
    VERSIONS("Versions", Icons.Default.History),
    JSON_EDITOR("JSON Editor", Icons.Default.Code),
    SAFETY("Safety Rules", Icons.Default.SafetyCheck)
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AiBrainStudioScreen(viewModel: MainViewModel) {
    var activeSubTab by remember { mutableStateOf(BrainSubTab.PROMPTS) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .padding(12.dp)
    ) {
        // Bento Top Header
        GlassCard(borderColor = AccentCyan) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(AccentCyan.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = AccentCyan,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "🧠 AI BRAIN STUDIO",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Myanmar Hardware AI Intelligence Control Center & Rule Engine",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Horizontal SubTab Scroll Bar
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(BrainSubTab.values()) { subTab ->
                val isSelected = activeSubTab == subTab
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) AccentCyan else CardBackground)
                        .border(
                            1.dp,
                            if (isSelected) AccentCyan else CardBorder,
                            RoundedCornerShape(10.dp)
                        )
                        .clickable { activeSubTab = subTab }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = subTab.icon,
                            contentDescription = null,
                            tint = if (isSelected) AppBackground else TextSecondary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = subTab.title,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) AppBackground else TextPrimary
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Main Module Content Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            when (activeSubTab) {
                BrainSubTab.PROMPTS -> PromptsModuleView(viewModel)
                BrainSubTab.ROLES -> RolesModuleView(viewModel)
                BrainSubTab.RULES -> RulesModuleView(viewModel)
                BrainSubTab.KEYWORDS -> KeywordsModuleView(viewModel)
                BrainSubTab.KNOWLEDGE -> KnowledgeModuleView(viewModel)
                BrainSubTab.MEMORIES -> MemoriesModuleView(viewModel)
                BrainSubTab.REASONING -> ReasoningModuleView(viewModel)
                BrainSubTab.PRIORITY -> PriorityModuleView(viewModel)
                BrainSubTab.WORKFLOW -> WorkflowModuleView(viewModel)
                BrainSubTab.TESTER -> TesterModuleView(viewModel)
                BrainSubTab.VERSIONS -> VersionsModuleView(viewModel)
                BrainSubTab.JSON_EDITOR -> JsonEditorModuleView(viewModel)
                BrainSubTab.SAFETY -> SafetyModuleView()
            }
        }
    }
}

// ==========================================
// MODULE 1: PROMPT MANAGER
// ==========================================
@Composable
fun PromptsModuleView(viewModel: MainViewModel) {
    val prompts by viewModel.aiPrompts.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }

    var newPromptName by remember { mutableStateOf("") }
    var newPromptDesc by remember { mutableStateOf("") }
    var newPromptCategory by remember { mutableStateOf("Diagnosis") }
    var newPromptContent by remember { mutableStateOf("") }

    val filtered = prompts.filter {
        it.name.contains(searchQuery, ignoreCase = true) || it.content.contains(searchQuery, ignoreCase = true)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search prompts...", fontSize = 11.sp, color = TextMutedColor) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = AccentCyan) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp),
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

            Button(
                onClick = { showAddDialog = !showAddDialog },
                colors = ButtonDefaults.buttonColors(containerColor = AccentCyan),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = AppBackground)
                Spacer(modifier = Modifier.width(4.dp))
                Text("New Prompt", color = AppBackground, fontWeight = FontWeight.Bold, fontSize = 11.sp)
            }
        }

        if (showAddDialog) {
            Spacer(modifier = Modifier.height(10.dp))
            GlassCard(borderColor = AccentCyan) {
                Column {
                    Text("Add Custom Prompt", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = newPromptName,
                        onValueChange = { newPromptName = it },
                        placeholder = { Text("Prompt Name (e.g. RAM Voltage Checker)", fontSize = 11.sp, color = TextMutedColor) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = newPromptContent,
                        onValueChange = { newPromptContent = it },
                        placeholder = { Text("Enter prompt instructions...", fontSize = 11.sp, color = TextMutedColor) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                        Button(
                            onClick = {
                                if (newPromptName.isNotBlank() && newPromptContent.isNotBlank()) {
                                    viewModel.savePrompt(
                                        AiPromptEntity(
                                            name = newPromptName,
                                            description = newPromptDesc,
                                            category = newPromptCategory,
                                            content = newPromptContent,
                                            createdDate = "2026-08-12"
                                        )
                                    )
                                    newPromptName = ""
                                    newPromptContent = ""
                                    showAddDialog = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen)
                        ) {
                            Text("Save Prompt", color = AppBackground, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(filtered) { prompt ->
                GlassCard(borderColor = if (prompt.isEnabled) AccentCyan else CardBorder) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = { viewModel.updatePrompt(prompt.copy(isFavorite = !prompt.isFavorite)) }
                                ) {
                                    Icon(
                                        imageVector = if (prompt.isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                                        contentDescription = null,
                                        tint = if (prompt.isFavorite) WarningYellow else TextMutedColor
                                    )
                                }
                                Text(prompt.name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Switch(
                                    checked = prompt.isEnabled,
                                    onCheckedChange = { viewModel.updatePrompt(prompt.copy(isEnabled = it)) },
                                    colors = SwitchDefaults.colors(checkedThumbColor = AppBackground, checkedTrackColor = AccentCyan)
                                )
                                IconButton(onClick = { viewModel.duplicatePrompt(prompt) }) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = null, tint = AccentCyan, modifier = Modifier.size(16.dp))
                                }
                                IconButton(onClick = { viewModel.deletePrompt(prompt) }) {
                                    Icon(Icons.Default.Delete, contentDescription = null, tint = ErrorRed, modifier = Modifier.size(16.dp))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = prompt.content,
                            fontSize = 11.sp,
                            color = TextSecondary,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }
    }
}

// ==========================================
// MODULE 2: AI ROLES
// ==========================================
@Composable
fun RolesModuleView(viewModel: MainViewModel) {
    val roles by viewModel.aiRoles.collectAsState()

    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(roles) { role ->
            GlassCard(borderColor = if (role.isEnabled) SuccessGreen else CardBorder) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(role.name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(role.description, fontSize = 11.sp, color = TextSecondary)
                    }

                    Switch(
                        checked = role.isEnabled,
                        onCheckedChange = { viewModel.updateRole(role.copy(isEnabled = it)) },
                        colors = SwitchDefaults.colors(checkedThumbColor = AppBackground, checkedTrackColor = SuccessGreen)
                    )
                }
            }
        }
    }
}

// ==========================================
// MODULE 3: RULE ENGINE
// ==========================================
@Composable
fun RulesModuleView(viewModel: MainViewModel) {
    val rules by viewModel.aiRules.collectAsState()
    var showAddRule by remember { mutableStateOf(false) }

    var ruleName by remember { mutableStateOf("") }
    var conditionVal by remember { mutableStateOf("") }
    var actionTitle by remember { mutableStateOf("") }
    var suggestedIcs by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Visual Rule Engine", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Button(
                onClick = { showAddRule = !showAddRule },
                colors = ButtonDefaults.buttonColors(containerColor = WarningYellow),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("+ New Rule", color = AppBackground, fontWeight = FontWeight.Bold, fontSize = 11.sp)
            }
        }

        if (showAddRule) {
            Spacer(modifier = Modifier.height(8.dp))
            GlassCard(borderColor = WarningYellow) {
                Column {
                    Text("Create IF ... THEN Visual Rule", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = WarningYellow)
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = ruleName, onValueChange = { ruleName = it },
                        placeholder = { Text("Rule Name (e.g. Backlight Voltage Fail)", fontSize = 11.sp, color = TextMutedColor) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = conditionVal, onValueChange = { conditionVal = it },
                        placeholder = { Text("IF Log CONTAINS text (e.g. bklic = 0)", fontSize = 11.sp, color = TextMutedColor) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = actionTitle, onValueChange = { actionTitle = it },
                        placeholder = { Text("THEN Failure Cause (e.g. Backlight Driver Short)", fontSize = 11.sp, color = TextMutedColor) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Button(
                        onClick = {
                            if (ruleName.isNotBlank() && conditionVal.isNotBlank()) {
                                viewModel.saveRule(
                                    AiRuleEntity(
                                        ruleName = ruleName,
                                        value = conditionVal,
                                        actionTitle = actionTitle,
                                        suggestedIcs = suggestedIcs
                                    )
                                )
                                showAddRule = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = WarningYellow)
                    ) {
                        Text("Save Rule", color = AppBackground, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(rules) { rule ->
                GlassCard(borderColor = WarningYellow) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("IF Log CONTAINS '${rule.value}'", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = WarningYellow)
                            Switch(
                                checked = rule.isEnabled,
                                onCheckedChange = { viewModel.updateRule(rule.copy(isEnabled = it)) },
                                colors = SwitchDefaults.colors(checkedThumbColor = AppBackground, checkedTrackColor = WarningYellow)
                            )
                        }
                        Text("THEN: ${rule.actionTitle}", fontSize = 11.sp, color = TextPrimary)
                        if (rule.suggestedIcs.isNotBlank()) {
                            Text("Suggested ICs: ${rule.suggestedIcs}", fontSize = 10.sp, color = TextSecondary)
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// MODULE 4: KEYWORD DATABASE
// ==========================================
@Composable
fun KeywordsModuleView(viewModel: MainViewModel) {
    val keywords by viewModel.aiKeywords.collectAsState()

    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(keywords) { kw ->
            GlassCard(borderColor = AccentCyan) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(kw.keyword, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = AccentCyan)
                        Text("Stage: ${kw.bootStage}", fontSize = 10.sp, color = SuccessGreen, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(kw.meaning, fontSize = 11.sp, color = TextPrimary)
                    Text("🇲🇲 ${kw.myanmarMeaning}", fontSize = 11.sp, color = TextSecondary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Related IC: ${kw.relatedIc} | Power Rail: ${kw.powerRail}", fontSize = 10.sp, color = WarningYellow)
                }
            }
        }
    }
}

// ==========================================
// MODULE 5: REPAIR KNOWLEDGE
// ==========================================
@Composable
fun KnowledgeModuleView(viewModel: MainViewModel) {
    val cases by viewModel.repairCases.collectAsState()

    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(cases) { c ->
            GlassCard(borderColor = SuccessGreen) {
                Column {
                    Text("${c.brand} ${c.model} - ${c.fault}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("UART Log: ${c.uartKeyLog}", fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = AccentCyan)
                    Text("Current: ${c.currentConsumption} | Voltage: ${c.voltageData}", fontSize = 10.sp, color = WarningYellow)
                    Text("Root Cause: ${c.cause}", fontSize = 11.sp, color = TextPrimary)
                    Text("Repair: ${c.repairSteps}", fontSize = 11.sp, color = SuccessGreen)
                }
            }
        }
    }
}

// ==========================================
// MODULE 6: AI MEMORY
// ==========================================
@Composable
fun MemoriesModuleView(viewModel: MainViewModel) {
    val memories by viewModel.aiMemories.collectAsState()

    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(memories) { mem ->
            GlassCard(borderColor = if (mem.isEnabled) AccentCyan else CardBorder) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Default.Bookmark, contentDescription = null, tint = AccentCyan, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(mem.content, fontSize = 12.sp, color = TextPrimary)
                    }

                    Switch(
                        checked = mem.isEnabled,
                        onCheckedChange = { viewModel.updateMemory(mem.copy(isEnabled = it)) },
                        colors = SwitchDefaults.colors(checkedThumbColor = AppBackground, checkedTrackColor = AccentCyan)
                    )
                }
            }
        }
    }
}

// ==========================================
// MODULE 7: REASONING SETTINGS
// ==========================================
@Composable
fun ReasoningModuleView(viewModel: MainViewModel) {
    val settings by viewModel.aiSettings.collectAsState()
    var temp by remember { mutableFloatStateOf(settings?.temperature ?: 0.3f) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        GlassCard(borderColor = AccentCyan) {
            Column {
                Text("Temperature (Creativity): ${String.format("%.2f", temp)}", fontSize = 12.sp, color = TextPrimary)
                Slider(
                    value = temp,
                    onValueChange = {
                        temp = it
                        settings?.let { s -> viewModel.saveSettings(s.copy(temperature = temp)) }
                    },
                    valueRange = 0.0f..1.0f,
                    colors = SliderDefaults.colors(thumbColor = AccentCyan, activeTrackColor = AccentCyan)
                )
                Text("0.1 = Strict Deterministic Hardware Analysis | 0.8 = Creative General Suggestions", fontSize = 10.sp, color = TextSecondary)
            }
        }

        GlassCard {
            Column {
                Text("Language: Myanmar (မြန်မာ)", fontSize = 12.sp, color = TextPrimary, fontWeight = FontWeight.Bold)
                Text("Technical Detail Level: Senior Hardware Engineer", fontSize = 11.sp, color = TextSecondary)
                Text("Confidence Threshold: 75%", fontSize = 11.sp, color = TextSecondary)
            }
        }
    }
}

// ==========================================
// MODULE 8: KNOWLEDGE PRIORITY
// ==========================================
@Composable
fun PriorityModuleView(viewModel: MainViewModel) {
    val defaultPriority = listOf(
        "1. Current UART Log",
        "2. Visual Rule Engine",
        "3. Good Log Library",
        "4. Keyword Database",
        "5. Repair Know-how Cases",
        "6. Permanent AI Memory",
        "7. Custom System Prompts",
        "8. Gemini General Knowledge"
    )

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("AI Knowledge Priority Order (Hierarchy)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        defaultPriority.forEach { item ->
            GlassCard(borderColor = SuccessGreen) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Layers, contentDescription = null, tint = SuccessGreen)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(item, fontSize = 12.sp, color = TextPrimary, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// ==========================================
// MODULE 9: WORKFLOW BUILDER
// ==========================================
@Composable
fun WorkflowModuleView(viewModel: MainViewModel) {
    val nodes = listOf(
        "1. UART Input Stream",
        "2. Smart UART Parser",
        "3. Keyword Extraction",
        "4. Visual Rule Engine",
        "5. Good Log Comparison",
        "6. Repair Case Lookup",
        "7. Gemini 2.5 AI Reasoning",
        "8. Myanmar Diagnostic Report"
    )

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text("Visual Processing Pipeline", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        nodes.forEachIndexed { idx, node ->
            GlassCard(borderColor = AccentCyan) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(AccentCyan),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("${idx + 1}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AppBackground)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(node, fontSize = 12.sp, color = TextPrimary)
                }
            }
        }
    }
}

// ==========================================
// MODULE 10: PROMPT TESTER
// ==========================================
@Composable
fun TesterModuleView(viewModel: MainViewModel) {
    var testLog by remember { mutableStateOf("ddr_phy_init: DQ calibration failed\nUFS_RESET_N timeout\nbklic = 0") }
    var testOutput by remember { mutableStateOf("") }
    var isTesting by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        GlassCard {
            Column {
                Text("Prompt Tester", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = testLog,
                    onValueChange = { testLog = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    shape = RoundedCornerShape(8.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        isTesting = true
                        scope.launch {
                            testOutput = viewModel.aiService.analyzeBootLog(testLog, "Myanmar")
                            isTesting = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentCyan),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (isTesting) {
                        Text("Analyzing...", color = AppBackground)
                    } else {
                        Text("Run Prompt Test", color = AppBackground, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        if (testOutput.isNotBlank()) {
            GlassCard(borderColor = SuccessGreen) {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Text("Test Result Output:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SuccessGreen)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(testOutput, fontSize = 11.sp, color = TextPrimary)
                }
            }
        }
    }
}

// ==========================================
// MODULE 11: VERSION MANAGER
// ==========================================
@Composable
fun VersionsModuleView(viewModel: MainViewModel) {
    val versions by viewModel.aiBrainVersions.collectAsState()

    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Button(
            onClick = { viewModel.createVersionSnapshot("Manual Snapshot") },
            colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Create Snapshot Backup", color = AppBackground, fontWeight = FontWeight.Bold)
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(versions) { ver ->
                GlassCard(borderColor = AccentCyan) {
                    Column {
                        Text(ver.versionName, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text("Note: ${ver.notes}", fontSize = 11.sp, color = TextSecondary)
                    }
                }
            }
        }
    }
}

// ==========================================
// MODULE 12: JSON EDITOR
// ==========================================
@Composable
fun JsonEditorModuleView(viewModel: MainViewModel) {
    var rawJson by remember {
        mutableStateOf(
            """
            {
              "engine": "UART PRO AI BRAIN",
              "version": "3.5.0",
              "language": "Myanmar",
              "safetyRules": true,
              "roles": ["Qualcomm Boot Engineer", "PMIC Expert"]
            }
            """.trimIndent()
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Text("Built-in Configuration JSON Editor", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = rawJson,
            onValueChange = { rawJson = it },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            textStyle = androidx.compose.ui.text.TextStyle(fontFamily = FontFamily.Monospace, fontSize = 12.sp),
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = TerminalBg,
                unfocusedContainerColor = TerminalBg,
                focusedTextColor = AccentCyan,
                unfocusedTextColor = AccentCyan
            )
        )
    }
}

// ==========================================
// MODULE 13: SAFETY RULES
// ==========================================
@Composable
fun SafetyModuleView() {
    val safetyList = listOf(
        "1. Never fabricate hardware failures without raw log evidence.",
        "2. Always separate Verified Facts, Possible Causes, and Recommendations.",
        "3. Always show Confidence Score (0-100%) for diagnostic results.",
        "4. Always suggest multimeter & oscilloscope measurements before replacing ICs."
    )

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("AI Safety & Fact Guardrails", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = ErrorRed)
        safetyList.forEach { rule ->
            GlassCard(borderColor = ErrorRed) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.SafetyCheck, contentDescription = null, tint = ErrorRed)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(rule, fontSize = 12.sp, color = TextPrimary)
                }
            }
        }
    }
}
