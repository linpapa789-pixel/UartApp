package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ai.GeminiAiService
import com.example.data.local.AppDatabase
import com.example.data.local.entity.DeviceDatabaseEntity
import com.example.data.local.entity.FaultLogEntity
import com.example.data.local.entity.GoodLogReferenceEntity
import com.example.data.local.entity.RepairCaseEntity
import com.example.data.local.entity.UartSessionEntity
import com.example.data.parser.BootStageInfo
import com.example.data.parser.KeywordInfo
import com.example.data.parser.LogComparisonResult
import com.example.data.parser.SmartUartParser
import com.example.data.repository.UartRepository
import com.example.data.usb.SimulationDeviceType
import com.example.data.usb.UsbSerialManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    val repository = UartRepository(db)
    val usbManager = UsbSerialManager(application)
    val aiService = GeminiAiService()

    val isDarkTheme = MutableStateFlow(true)
    
    fun toggleTheme() {
        isDarkTheme.value = !isDarkTheme.value
    }

    // Database Flows
    val savedSessions: StateFlow<List<UartSessionEntity>> = repository.allSessions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val goodLogLibrary: StateFlow<List<GoodLogReferenceEntity>> = repository.allGoodLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val faultDatabase: StateFlow<List<FaultLogEntity>> = repository.allFaultLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val deviceDatabase: StateFlow<List<DeviceDatabaseEntity>> = repository.allDevices
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val repairCases: StateFlow<List<RepairCaseEntity>> = repository.allRepairCases
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // AI BRAIN STUDIO STATEFLOWS
    val aiPrompts: StateFlow<List<com.example.data.local.entity.AiPromptEntity>> = repository.aiPrompts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val aiRoles: StateFlow<List<com.example.data.local.entity.AiRoleEntity>> = repository.aiRoles
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val aiRules: StateFlow<List<com.example.data.local.entity.AiRuleEntity>> = repository.aiRules
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val aiKeywords: StateFlow<List<com.example.data.local.entity.AiKeywordEntity>> = repository.aiKeywords
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val aiRepairKnowledge: StateFlow<List<com.example.data.local.entity.AiRepairKnowledgeEntity>> = repository.aiRepairKnowledge
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val aiMemories: StateFlow<List<com.example.data.local.entity.AiMemoryEntity>> = repository.aiMemories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val aiSettings: StateFlow<com.example.data.local.entity.AiSettingsEntity?> = repository.aiSettingsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val aiBrainVersions: StateFlow<List<com.example.data.local.entity.AiBrainVersionEntity>> = repository.aiBrainVersions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Terminal & Parser States
    val connectionState = usbManager.connectionState
    val terminalOutput = usbManager.terminalOutput

    private val _isTerminalPaused = MutableStateFlow(false)
    val isTerminalPaused: StateFlow<Boolean> = _isTerminalPaused.asStateFlow()

    private val _autoScroll = MutableStateFlow(true)
    val autoScroll: StateFlow<Boolean> = _autoScroll.asStateFlow()

    private val _showTimestamps = MutableStateFlow(true)
    val showTimestamps: StateFlow<Boolean> = _showTimestamps.asStateFlow()

    private val _aiAnalysisResult = MutableStateFlow<String?>(null)
    val aiAnalysisResult: StateFlow<String?> = _aiAnalysisResult.asStateFlow()

    private val _isAiLoading = MutableStateFlow(false)
    val isAiLoading: StateFlow<Boolean> = _isAiLoading.asStateFlow()

    // Comparison States
    private val _goodCompareLog = MutableStateFlow("")
    val goodCompareLog: StateFlow<String> = _goodCompareLog.asStateFlow()

    private val _faultCompareLog = MutableStateFlow("")
    val faultCompareLog: StateFlow<String> = _faultCompareLog.asStateFlow()

    private val _comparisonResult = MutableStateFlow<LogComparisonResult?>(null)
    val comparisonResult: StateFlow<LogComparisonResult?> = _comparisonResult.asStateFlow()

    private val _isComparing = MutableStateFlow(false)
    val isComparing: StateFlow<Boolean> = _isComparing.asStateFlow()

    private var compareJob: Job? = null

    // Search Query
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Selected Language (English / Myanmar)
    private val _currentLanguage = MutableStateFlow("Myanmar") // English or Myanmar
    val currentLanguage: StateFlow<String> = _currentLanguage.asStateFlow()

    init {
        viewModelScope.launch {
            repository.populateInitialDatabaseIfEmpty()
        }
    }

    fun togglePauseTerminal() {
        _isTerminalPaused.value = !_isTerminalPaused.value
    }

    fun toggleAutoScroll() {
        _autoScroll.value = !_autoScroll.value
    }

    fun toggleTimestamps() {
        _showTimestamps.value = !_showTimestamps.value
    }

    fun setLanguage(lang: String) {
        _currentLanguage.value = lang
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun startUsbSimulation(type: SimulationDeviceType) {
        usbManager.startSimulation(type)
    }

    fun stopUsbConnection() {
        usbManager.stopSimulation()
    }

    fun setBaudRate(rate: Int) {
        usbManager.setBaudRate(rate)
    }

    fun setEncoding(enc: String) {
        usbManager.setEncoding(enc)
    }

    fun clearTerminal() {
        usbManager.clearTerminal()
        _aiAnalysisResult.value = null
    }

    fun analyzeCurrentTerminalWithAi() {
        val currentLog = terminalOutput.value
        if (currentLog.isBlank()) return

        _isAiLoading.value = true
        viewModelScope.launch {
            val activeRolesList = aiRoles.value.filter { it.isEnabled }.map { "${it.name}: ${it.systemPrompt}" }
            val activePromptsList = aiPrompts.value.filter { it.isEnabled }.map { "${it.name}: ${it.content}" }
            val activeMemoriesList = aiMemories.value.filter { it.isEnabled }.map { it.content }

            // Match rules against current log
            val matchedRulesList = aiRules.value.filter { rule ->
                rule.isEnabled && when (rule.operator) {
                    "CONTAINS" -> currentLog.contains(rule.value, ignoreCase = true)
                    "EQUALS" -> currentLog.equals(rule.value, ignoreCase = true)
                    "STARTS_WITH" -> currentLog.trim().startsWith(rule.value, ignoreCase = true)
                    "ENDS_WITH" -> currentLog.trim().endsWith(rule.value, ignoreCase = true)
                    else -> currentLog.contains(rule.value, ignoreCase = true)
                }
            }.map { "Rule [${it.ruleName}]: ${it.actionTitle} -> ICs: ${it.suggestedIcs}, Rails: ${it.suggestedRails} (${it.confidence}%)" }

            val temp = aiSettings.value?.temperature ?: 0.3f

            val result = aiService.analyzeBootLog(
                logText = currentLog,
                targetLanguage = _currentLanguage.value,
                customRoles = activeRolesList,
                customPrompts = activePromptsList,
                customMemories = activeMemoriesList,
                matchedRules = matchedRulesList,
                temperature = temp
            )
            _aiAnalysisResult.value = result
            _isAiLoading.value = false
        }
    }

    // AI BRAIN STUDIO ACTIONS
    fun savePrompt(prompt: com.example.data.local.entity.AiPromptEntity) {
        viewModelScope.launch { repository.savePrompt(prompt) }
    }

    fun updatePrompt(prompt: com.example.data.local.entity.AiPromptEntity) {
        viewModelScope.launch { repository.updatePrompt(prompt) }
    }

    fun deletePrompt(prompt: com.example.data.local.entity.AiPromptEntity) {
        viewModelScope.launch { repository.deletePrompt(prompt) }
    }

    fun duplicatePrompt(prompt: com.example.data.local.entity.AiPromptEntity) {
        viewModelScope.launch {
            repository.savePrompt(
                prompt.copy(id = 0, name = "${prompt.name} (Copy)", createdDate = "2026-08-12")
            )
        }
    }

    fun saveRole(role: com.example.data.local.entity.AiRoleEntity) {
        viewModelScope.launch { repository.saveRole(role) }
    }

    fun updateRole(role: com.example.data.local.entity.AiRoleEntity) {
        viewModelScope.launch { repository.updateRole(role) }
    }

    fun deleteRole(role: com.example.data.local.entity.AiRoleEntity) {
        viewModelScope.launch { repository.deleteRole(role) }
    }

    fun saveRule(rule: com.example.data.local.entity.AiRuleEntity) {
        viewModelScope.launch { repository.saveRule(rule) }
    }

    fun updateRule(rule: com.example.data.local.entity.AiRuleEntity) {
        viewModelScope.launch { repository.updateRule(rule) }
    }

    fun deleteRule(rule: com.example.data.local.entity.AiRuleEntity) {
        viewModelScope.launch { repository.deleteRule(rule) }
    }

    fun saveKeyword(keyword: com.example.data.local.entity.AiKeywordEntity) {
        viewModelScope.launch { repository.saveKeyword(keyword) }
    }

    fun updateKeyword(keyword: com.example.data.local.entity.AiKeywordEntity) {
        viewModelScope.launch { repository.updateKeyword(keyword) }
    }

    fun deleteKeyword(keyword: com.example.data.local.entity.AiKeywordEntity) {
        viewModelScope.launch { repository.deleteKeyword(keyword) }
    }

    fun saveRepairKnowledge(item: com.example.data.local.entity.AiRepairKnowledgeEntity) {
        viewModelScope.launch { repository.saveRepairKnowledge(item) }
    }

    fun updateRepairKnowledge(item: com.example.data.local.entity.AiRepairKnowledgeEntity) {
        viewModelScope.launch { repository.updateRepairKnowledge(item) }
    }

    fun deleteRepairKnowledge(item: com.example.data.local.entity.AiRepairKnowledgeEntity) {
        viewModelScope.launch { repository.deleteRepairKnowledge(item) }
    }

    fun saveMemory(memory: com.example.data.local.entity.AiMemoryEntity) {
        viewModelScope.launch { repository.saveMemory(memory) }
    }

    fun updateMemory(memory: com.example.data.local.entity.AiMemoryEntity) {
        viewModelScope.launch { repository.updateMemory(memory) }
    }

    fun deleteMemory(memory: com.example.data.local.entity.AiMemoryEntity) {
        viewModelScope.launch { repository.deleteMemory(memory) }
    }

    fun saveSettings(settings: com.example.data.local.entity.AiSettingsEntity) {
        viewModelScope.launch { repository.saveSettings(settings) }
    }

    fun createVersionSnapshot(note: String) {
        viewModelScope.launch {
            val json = org.json.JSONObject().apply {
                put("promptsCount", aiPrompts.value.size)
                put("rolesCount", aiRoles.value.size)
                put("rulesCount", aiRules.value.size)
                put("memoriesCount", aiMemories.value.size)
                put("timestamp", System.currentTimeMillis())
            }.toString()

            val version = com.example.data.local.entity.AiBrainVersionEntity(
                versionName = "v1.${aiBrainVersions.value.size + 1}.0",
                notes = note,
                snapshotJson = json
            )
            repository.saveVersion(version)
        }
    }

    fun setGoodCompareLog(log: String) {
        _goodCompareLog.value = log
        scheduleComparison()
    }

    fun setFaultCompareLog(log: String) {
        _faultCompareLog.value = log
        scheduleComparison()
    }

    fun scheduleComparison() {
        compareJob?.cancel()
        if (_goodCompareLog.value.isBlank() && _faultCompareLog.value.isBlank()) {
            _comparisonResult.value = null
            _isComparing.value = false
            return
        }

        _isComparing.value = true
        compareJob = viewModelScope.launch(Dispatchers.Default) {
            delay(250) // Debounce rapid pasting or typing
            val result = SmartUartParser.compareLogs(_goodCompareLog.value, _faultCompareLog.value)
            _comparisonResult.value = result
            _isComparing.value = false
        }
    }

    fun saveCurrentSession(
        brand: String, model: String, codename: String, chipset: String,
        repairJob: String, technician: String, status: String, notes: String
    ) {
        val log = terminalOutput.value
        if (log.isBlank()) return

        viewModelScope.launch {
            val entity = UartSessionEntity(
                title = "$brand $model Repair Log",
                brand = brand,
                model = model,
                codename = codename,
                chipset = chipset,
                repairJobNumber = repairJob,
                technician = technician,
                status = status,
                repairNote = notes,
                rawLogContent = log,
                date = "2026-08-12"
            )
            repository.saveSession(entity)
        }
    }

    fun saveReferenceGoodLog(brand: String, model: String, chipset: String, logContent: String) {
        viewModelScope.launch {
            repository.saveGoodLog(
                GoodLogReferenceEntity(
                    brand = brand,
                    model = model,
                    codename = model.lowercase().replace(" ", "_"),
                    chipset = chipset,
                    rawLogContent = logContent
                )
            )
        }
    }

    fun detectStages(log: String): List<BootStageInfo> {
        return SmartUartParser.detectBootStages(log)
    }

    fun parseKeywords(log: String): List<KeywordInfo> {
        return SmartUartParser.parseLineForKeywords(log)
    }

    fun getDatabaseContextSummary(): String {
        val sessions = savedSessions.value.takeLast(10).joinToString("\n") {
            "[${it.date}] Session ${it.brand} ${it.model}: ${it.rawLogContent.take(150).replace("\n", " ")}"
        }
        val cases = repairCases.value.takeLast(5).joinToString("\n") {
            "[${it.date}] Repair Case ${it.model} - ${it.fault}: Fix=${it.repairSteps} (Log: ${it.uartKeyLog})"
        }
        return "Recent Saved Sessions:\n$sessions\n\nRecent Repair Cases:\n$cases"
    }
}
