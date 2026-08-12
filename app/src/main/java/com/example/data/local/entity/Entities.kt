package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "uart_sessions")
data class UartSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String = "Boot Log Session",
    val brand: String = "",
    val model: String = "",
    val codename: String = "",
    val chipset: String = "",
    val boardName: String = "",
    val serialNumber: String = "",
    val repairJobNumber: String = "",
    val technician: String = "",
    val date: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "PENDING", // GOOD, FAULT, SOLVED, PENDING
    val customerNote: String = "",
    val repairNote: String = "",
    val tags: String = "", // Comma-separated
    val imagePath: String = "",
    val voiceNote: String = "",
    val rawLogContent: String = "",
    val isFavorite: Boolean = false,
    val folder: String = "Default"
)

@Entity(tableName = "good_log_references")
data class GoodLogReferenceEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val brand: String,
    val model: String,
    val codename: String,
    val chipset: String,
    val firmwareVersion: String = "",
    val androidVersion: String = "",
    val kernelVersion: String = "",
    val securityPatch: String = "",
    val vendor: String = "",
    val boardRevision: String = "",
    val rawLogContent: String,
    val bootStagesJson: String = "",
    val tags: String = ""
)

@Entity(tableName = "fault_logs")
data class FaultLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val brand: String,
    val model: String,
    val codename: String,
    val faultCategory: String, // No Power, Logo Hang, Bootloop, 9008, EDL, etc.
    val chipset: String = "",
    val boardRevision: String = "",
    val rawLogContent: String,
    val solutionNote: String = "",
    val technician: String = "",
    val date: String = ""
)

@Entity(tableName = "device_database")
data class DeviceDatabaseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val brand: String,
    val model: String,
    val codename: String,
    val cpu: String,
    val pmic: String,
    val ram: String,
    val storage: String,
    val displayIc: String = "",
    val touchIc: String = "",
    val backlightIc: String = "",
    val chargingIc: String = "",
    val rfIc: String = "",
    val wifiIc: String = "",
    val bluetoothIc: String = "",
    val nfcIc: String = "",
    val fingerprintIc: String = "",
    val audioCodec: String = "",
    val boardRevision: String = "",
    val pcbVersion: String = ""
)

@Entity(tableName = "repair_cases")
data class RepairCaseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val brand: String,
    val model: String,
    val fault: String,
    val uartKeyLog: String,
    val currentConsumption: String = "",
    val voltageData: String = "",
    val resistanceData: String = "",
    val cause: String,
    val repairSteps: String,
    val isSolved: Boolean = true,
    val technician: String = "Master Tech",
    val date: String = ""
)

@Entity(tableName = "current_measurements")
data class CurrentMeasurementEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sessionId: Long = 0,
    val timestampMs: Long = 0,
    val currentValuemA: Float = 0f,
    val bootStage: String = ""
)

// ==========================================
// AI BRAIN STUDIO ENTITIES
// ==========================================

@Entity(tableName = "ai_prompts")
data class AiPromptEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String = "",
    val category: String = "General",
    val priority: Int = 1,
    val content: String,
    val isEnabled: Boolean = true,
    val isFavorite: Boolean = false,
    val createdDate: String = "",
    val modifiedDate: String = "",
    val author: String = "Technician",
    val notes: String = ""
)

@Entity(tableName = "ai_roles")
data class AiRoleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String = "",
    val systemPrompt: String,
    val isEnabled: Boolean = true,
    val icon: String = "Engineering"
)

@Entity(tableName = "ai_rules")
data class AiRuleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val ruleName: String,
    val conditionField: String = "UART Log Line",
    val operator: String = "CONTAINS", // CONTAINS, EQUALS, STARTS_WITH, ENDS_WITH, REGEX, NUMERIC_COMPARE
    val value: String,
    val actionTitle: String,
    val suggestedIcs: String = "",
    val suggestedRails: String = "",
    val confidence: Int = 85,
    val isEnabled: Boolean = true,
    val priority: Int = 1
)

@Entity(tableName = "ai_keywords")
data class AiKeywordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val keyword: String,
    val meaning: String,
    val myanmarMeaning: String,
    val bootStage: String = "BOOT",
    val hardwareBlock: String = "CPU",
    val softwareBlock: String = "Kernel",
    val relatedIc: String = "",
    val relatedVoltage: String = "",
    val relatedSignal: String = "",
    val powerRail: String = "",
    val relatedPartition: String = "",
    val possibleCause: String = "",
    val repairSuggestion: String = "",
    val confidence: Int = 90,
    val notes: String = "",
    val tags: String = ""
)

@Entity(tableName = "ai_repair_knowledge")
data class AiRepairKnowledgeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val fault: String,
    val uartLog: String,
    val currentConsumption: String = "",
    val voltageData: String = "",
    val resistanceData: String = "",
    val rootCause: String,
    val repairMethod: String,
    val isSolved: Boolean = true,
    val model: String = "",
    val boardRevision: String = "",
    val technicianNotes: String = ""
)

@Entity(tableName = "ai_memories")
data class AiMemoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val content: String,
    val category: String = "Instruction",
    val isEnabled: Boolean = true,
    val priority: Int = 1
)

@Entity(tableName = "ai_settings")
data class AiSettingsEntity(
    @PrimaryKey val id: Long = 1,
    val temperature: Float = 0.3f,
    val topP: Float = 0.9f,
    val maxTokens: Int = 2048,
    val thinkingDepth: String = "Deep",
    val confidenceThreshold: Int = 75,
    val responseStyle: String = "Detailed",
    val technicalLevel: String = "Expert",
    val language: String = "Myanmar",
    val verboseLevel: String = "High",
    val knowledgePriorityJson: String = """["1 Current UART Log","2 Good Log Library","3 Fault Database","4 Repair Cases","5 Keyword Database","6 AI Memory","7 Prompt Manager","8 Gemini General Knowledge"]""",
    val workflowNodesJson: String = """["UART","Parser","Keyword Engine","Rule Engine","Good Log Compare","Repair Case Search","Gemini","Myanmar Explanation"]""",
    val activeVersion: String = "v1.0.0"
)

@Entity(tableName = "ai_brain_versions")
data class AiBrainVersionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val versionName: String,
    val timestamp: Long = System.currentTimeMillis(),
    val snapshotJson: String,
    val notes: String = ""
)

