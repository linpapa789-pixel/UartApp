package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.entity.CurrentMeasurementEntity
import com.example.data.local.entity.DeviceDatabaseEntity
import com.example.data.local.entity.FaultLogEntity
import com.example.data.local.entity.GoodLogReferenceEntity
import com.example.data.local.entity.RepairCaseEntity
import com.example.data.local.entity.UartSessionEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class UartRepository(private val db: AppDatabase) {

    val allSessions: Flow<List<UartSessionEntity>> = db.uartSessionDao().getAllSessions()
    val favoriteSessions: Flow<List<UartSessionEntity>> = db.uartSessionDao().getFavoriteSessions()
    val allGoodLogs: Flow<List<GoodLogReferenceEntity>> = db.goodLogDao().getAllGoodLogs()
    val allFaultLogs: Flow<List<FaultLogEntity>> = db.faultLogDao().getAllFaultLogs()
    val allDevices: Flow<List<DeviceDatabaseEntity>> = db.deviceDatabaseDao().getAllDevices()
    val allRepairCases: Flow<List<RepairCaseEntity>> = db.repairCaseDao().getAllRepairCases()

    suspend fun saveSession(session: UartSessionEntity): Long = withContext(Dispatchers.IO) {
        db.uartSessionDao().insertSession(session)
    }

    suspend fun updateSession(session: UartSessionEntity) = withContext(Dispatchers.IO) {
        db.uartSessionDao().updateSession(session)
    }

    suspend fun deleteSession(session: UartSessionEntity) = withContext(Dispatchers.IO) {
        db.uartSessionDao().deleteSession(session)
    }

    fun searchSessions(query: String): Flow<List<UartSessionEntity>> {
        return db.uartSessionDao().searchSessions(query)
    }

    fun searchDevices(query: String): Flow<List<DeviceDatabaseEntity>> {
        return db.deviceDatabaseDao().searchDevices(query)
    }

    suspend fun saveGoodLog(log: GoodLogReferenceEntity) = withContext(Dispatchers.IO) {
        db.goodLogDao().insertGoodLog(log)
    }

    suspend fun saveFaultLog(log: FaultLogEntity) = withContext(Dispatchers.IO) {
        db.faultLogDao().insertFaultLog(log)
    }

    suspend fun saveDevice(device: DeviceDatabaseEntity) = withContext(Dispatchers.IO) {
        db.deviceDatabaseDao().insertDevice(device)
    }

    suspend fun saveRepairCase(repairCase: RepairCaseEntity) = withContext(Dispatchers.IO) {
        db.repairCaseDao().insertRepairCase(repairCase)
    }

    suspend fun getMeasurementsForSession(sessionId: Long): Flow<List<CurrentMeasurementEntity>> {
        return db.currentMeasurementDao().getMeasurementsForSession(sessionId)
    }

    // ==========================================
    // AI BRAIN STUDIO REPOSITORY METHODS
    // ==========================================

    val aiPrompts: Flow<List<com.example.data.local.entity.AiPromptEntity>> = db.aiBrainDao().getAllPrompts()
    val aiRoles: Flow<List<com.example.data.local.entity.AiRoleEntity>> = db.aiBrainDao().getAllRoles()
    val aiRules: Flow<List<com.example.data.local.entity.AiRuleEntity>> = db.aiBrainDao().getAllRules()
    val aiKeywords: Flow<List<com.example.data.local.entity.AiKeywordEntity>> = db.aiBrainDao().getAllKeywords()
    val aiRepairKnowledge: Flow<List<com.example.data.local.entity.AiRepairKnowledgeEntity>> = db.aiBrainDao().getAllRepairKnowledge()
    val aiMemories: Flow<List<com.example.data.local.entity.AiMemoryEntity>> = db.aiBrainDao().getAllMemories()
    val aiSettingsFlow: Flow<com.example.data.local.entity.AiSettingsEntity?> = db.aiBrainDao().getSettingsFlow()
    val aiBrainVersions: Flow<List<com.example.data.local.entity.AiBrainVersionEntity>> = db.aiBrainDao().getAllVersions()

    suspend fun savePrompt(prompt: com.example.data.local.entity.AiPromptEntity) = withContext(Dispatchers.IO) {
        db.aiBrainDao().insertPrompt(prompt)
    }

    suspend fun updatePrompt(prompt: com.example.data.local.entity.AiPromptEntity) = withContext(Dispatchers.IO) {
        db.aiBrainDao().updatePrompt(prompt)
    }

    suspend fun deletePrompt(prompt: com.example.data.local.entity.AiPromptEntity) = withContext(Dispatchers.IO) {
        db.aiBrainDao().deletePrompt(prompt)
    }

    suspend fun saveRole(role: com.example.data.local.entity.AiRoleEntity) = withContext(Dispatchers.IO) {
        db.aiBrainDao().insertRole(role)
    }

    suspend fun updateRole(role: com.example.data.local.entity.AiRoleEntity) = withContext(Dispatchers.IO) {
        db.aiBrainDao().updateRole(role)
    }

    suspend fun deleteRole(role: com.example.data.local.entity.AiRoleEntity) = withContext(Dispatchers.IO) {
        db.aiBrainDao().deleteRole(role)
    }

    suspend fun saveRule(rule: com.example.data.local.entity.AiRuleEntity) = withContext(Dispatchers.IO) {
        db.aiBrainDao().insertRule(rule)
    }

    suspend fun updateRule(rule: com.example.data.local.entity.AiRuleEntity) = withContext(Dispatchers.IO) {
        db.aiBrainDao().updateRule(rule)
    }

    suspend fun deleteRule(rule: com.example.data.local.entity.AiRuleEntity) = withContext(Dispatchers.IO) {
        db.aiBrainDao().deleteRule(rule)
    }

    suspend fun saveKeyword(keyword: com.example.data.local.entity.AiKeywordEntity) = withContext(Dispatchers.IO) {
        db.aiBrainDao().insertKeyword(keyword)
    }

    suspend fun updateKeyword(keyword: com.example.data.local.entity.AiKeywordEntity) = withContext(Dispatchers.IO) {
        db.aiBrainDao().updateKeyword(keyword)
    }

    suspend fun deleteKeyword(keyword: com.example.data.local.entity.AiKeywordEntity) = withContext(Dispatchers.IO) {
        db.aiBrainDao().deleteKeyword(keyword)
    }

    suspend fun saveRepairKnowledge(item: com.example.data.local.entity.AiRepairKnowledgeEntity) = withContext(Dispatchers.IO) {
        db.aiBrainDao().insertRepairKnowledge(item)
    }

    suspend fun updateRepairKnowledge(item: com.example.data.local.entity.AiRepairKnowledgeEntity) = withContext(Dispatchers.IO) {
        db.aiBrainDao().updateRepairKnowledge(item)
    }

    suspend fun deleteRepairKnowledge(item: com.example.data.local.entity.AiRepairKnowledgeEntity) = withContext(Dispatchers.IO) {
        db.aiBrainDao().deleteRepairKnowledge(item)
    }

    suspend fun saveMemory(memory: com.example.data.local.entity.AiMemoryEntity) = withContext(Dispatchers.IO) {
        db.aiBrainDao().insertMemory(memory)
    }

    suspend fun updateMemory(memory: com.example.data.local.entity.AiMemoryEntity) = withContext(Dispatchers.IO) {
        db.aiBrainDao().updateMemory(memory)
    }

    suspend fun deleteMemory(memory: com.example.data.local.entity.AiMemoryEntity) = withContext(Dispatchers.IO) {
        db.aiBrainDao().deleteMemory(memory)
    }

    suspend fun saveSettings(settings: com.example.data.local.entity.AiSettingsEntity) = withContext(Dispatchers.IO) {
        db.aiBrainDao().insertOrUpdateSettings(settings)
    }

    suspend fun getSettingsDirect(): com.example.data.local.entity.AiSettingsEntity? = withContext(Dispatchers.IO) {
        db.aiBrainDao().getSettingsDirect()
    }

    suspend fun saveVersion(version: com.example.data.local.entity.AiBrainVersionEntity) = withContext(Dispatchers.IO) {
        db.aiBrainDao().insertVersion(version)
    }

    suspend fun deleteVersion(version: com.example.data.local.entity.AiBrainVersionEntity) = withContext(Dispatchers.IO) {
        db.aiBrainDao().deleteVersion(version)
    }

    suspend fun populateInitialDatabaseIfEmpty() = withContext(Dispatchers.IO) {
        // Seed initial Good Logs if empty
        val goodDao = db.goodLogDao()
        val currentGood = goodDao.getAllGoodLogs()
        
        // Seed Device Database
        db.deviceDatabaseDao().insertDevice(
            DeviceDatabaseEntity(
                brand = "Xiaomi", model = "13 Pro", codename = "nuwa",
                cpu = "Snapdragon 8 Gen 2", pmic = "PM8550", ram = "12GB LPDDR5X", storage = "512GB UFS 4.0",
                displayIc = "NT37701 AMOLED", touchIc = "Goodix GT9886", backlightIc = "OLED Built-in",
                chargingIc = "BQ25970 120W", rfIc = "SDR865", wifiIc = "WCN7850", bluetoothIc = "WCN7850 Integrated",
                audioCodec = "WCD9385", boardRevision = "Rev 2.1", pcbVersion = "V2"
            )
        )
        db.deviceDatabaseDao().insertDevice(
            DeviceDatabaseEntity(
                brand = "Samsung", model = "Galaxy S24 Ultra", codename = "e3q",
                cpu = "Snapdragon 8 Gen 3", pmic = "PM8550B", ram = "12GB LPDDR5X", storage = "1TB UFS 4.0",
                displayIc = "S6E3HA3 Dynamic AMOLED 2X", touchIc = "SEC Touch Controller", backlightIc = "OLED Built-in",
                chargingIc = "S2DOS05", rfIc = "SDR875", wifiIc = "FastConnect 7800", bluetoothIc = "Integrated",
                audioCodec = "CS47L93", boardRevision = "Rev 1.0", pcbVersion = "R01"
            )
        )

        // Seed Sample Repair Cases
        db.repairCaseDao().insertRepairCase(
            RepairCaseEntity(
                title = "Xiaomi 13 Pro No Boot (Stuck on 120mA)",
                brand = "Xiaomi", model = "13 Pro", fault = "No Boot / 9008 EDL",
                uartKeyLog = "ddr_phy_init: DQ calibration failed at byte lane 2",
                currentConsumption = "120mA static draw",
                voltageData = "VPH_PWR 3.8V OK, VDD_RAM 0.0V (MISSING)",
                resistanceData = "VDD_RAM to GND = 2.1 ohms (SHORT)",
                cause = "RAM PMIC shorted capacitor C4205",
                repairSteps = "Replaced shorted capacitor C4205 near RAM IC. VDD_RAM restored to 1.1V. Booted OK.",
                isSolved = true, technician = "Ko Kyaw (Master Tech)"
            )
        )

        db.repairCaseDao().insertRepairCase(
            RepairCaseEntity(
                title = "Samsung S24 Ultra Logo Hang Bootloop",
                brand = "Samsung", model = "S24 Ultra", fault = "Bootloop",
                uartKeyLog = "UFS_RESET_N timeout, unable to mount /userdata",
                currentConsumption = "250mA -> 850mA -> 0mA restart cycle",
                voltageData = "VCC 2.95V OK, VCCQ 1.2V OK",
                resistanceData = "UFS Reset line 100k ohm OK",
                cause = "UFS 4.0 Health degraded (End of Life block corruption)",
                repairSteps = "Extracted partition dump, replaced UFS 4.0 IC with EasyJTAG 2, flashed stock binaries.",
                isSolved = true, technician = "Ko Aung (Embedded Tech)"
            )
        )

        // Seed Initial AI Brain Roles
        val brainDao = db.aiBrainDao()
        val defaultRoles = listOf(
            com.example.data.local.entity.AiRoleEntity(
                name = "Qualcomm Boot Engineer",
                description = "Specializes in PBL, XBL, ABL, SPMI, RPM, and Snapdragon kernel boot logs",
                systemPrompt = "You are a Senior Qualcomm Snapdragon Boot System Specialist. Focus heavily on PBL, XBL, ABL, SPMI, RPM, and Linux Kernel boot logs."
            ),
            com.example.data.local.entity.AiRoleEntity(
                name = "MediaTek Boot Engineer",
                description = "Specializes in Preloader, LK, ATF, and Dimensity initialization",
                systemPrompt = "You are a MediaTek Boot Specialist. Focus on Preloader, LK, ATF (ARM Trusted Firmware), and Dimensity chipset initialization."
            ),
            com.example.data.local.entity.AiRoleEntity(
                name = "Samsung Engineer",
                description = "Specializes in S-BOOT, Knox, eMMC/UFS RPMB, and Exynos power tree",
                systemPrompt = "You are a Samsung Exynos & Snapdragon Repair Specialist. Focus on S-BOOT, Knox, RPMB, and Samsung power rails."
            ),
            com.example.data.local.entity.AiRoleEntity(
                name = "iPhone Repair Engineer",
                description = "Specializes in iBoot, SEP, Apple A-Series SOC, NAND, and Tigris/Chestnut rails",
                systemPrompt = "You are an iPhone iBoot Expert. Focus on Apple A-Series SOC, SEP, NAND initialization, and iPhone power rails."
            ),
            com.example.data.local.entity.AiRoleEntity(
                name = "PMIC Expert",
                description = "Analyzes VPH_PWR, BUCKs, LDOs, SPMI bus, and current draw anomalies",
                systemPrompt = "You are a Power Management IC (PMIC) Specialist. Inspect VPH_PWR, BUCK regulators, LDOs, SPMI bus errors, and power rail shorts."
            ),
            com.example.data.local.entity.AiRoleEntity(
                name = "CPU Expert",
                description = "Specializes in Double Decker BGA fractures, RAM training, and thermal throttling",
                systemPrompt = "You are a CPU & BGA Reball Specialist. Diagnose CPU-to-RAM double decker soldering fractures, RAM training, and thermal issues."
            ),
            com.example.data.local.entity.AiRoleEntity(
                name = "Memory Expert",
                description = "Analyzes UFS 4.0 / eMMC SMART Health, lifetime wear, and bus timing",
                systemPrompt = "You are a Storage IC Specialist. Analyze UFS 4.0 and eMMC SMART health status, bad block corruption, and bus timing."
            ),
            com.example.data.local.entity.AiRoleEntity(
                name = "Display Expert",
                description = "Specializes in MIPI DSI, TE signal, OLED PMIC, and PWM backlight ICs",
                systemPrompt = "You are a Display & Backlight Specialist. Diagnose MIPI DSI errors, TE signal timeouts, OLED PMIC, and Backlight driver ICs."
            ),
            com.example.data.local.entity.AiRoleEntity(
                name = "Charging Expert",
                description = "Diagnoses OVP, USB-PD, SMB/BQ chargers, and thermal NTC sensors",
                systemPrompt = "You are a Battery & Charging Subsystem Specialist. Diagnose OVP, USB-PD controllers, BQ/SMB chargers, and thermal NTC lines."
            ),
            com.example.data.local.entity.AiRoleEntity(
                name = "RF Expert",
                description = "Analyzes SDR/WTR ICs, RFFE bus, PA power supplies, and baseband crashes",
                systemPrompt = "You are an RF Transceiver & Baseband Specialist. Focus on SDR/WTR transceivers, RFFE bus communications, and baseband power."
            ),
            com.example.data.local.entity.AiRoleEntity(
                name = "Camera Expert",
                description = "Specializes in MIPI CSI, ISP, OIS driver ICs, and sensor LDO rails",
                systemPrompt = "You are a Camera Subsystem Specialist. Inspect MIPI CSI interfaces, ISP initialization, OIS drivers, and sensor LDOs."
            ),
            com.example.data.local.entity.AiRoleEntity(
                name = "Software Flash Expert",
                description = "Specializes in Fastboot, Qualcomm 9008, MTK SP Flash Tool, and partition tables",
                systemPrompt = "You are a Firmware & Partition Specialist. Focus on EDL 9008 mode, SP Flash Tool, partition tables, and bootloader flashing."
            ),
            com.example.data.local.entity.AiRoleEntity(
                name = "UART Specialist",
                description = "Inspects baud rates, parity, flow control, and serial log logic",
                systemPrompt = "You are a High-Speed Serial UART Analyst. Inspect baud rates, framing errors, parity, and serial communication integrity."
            ),
            com.example.data.local.entity.AiRoleEntity(
                name = "Linux Kernel Engineer",
                description = "Analyzes dmesg, kernel panic, devicetree, and driver crashes",
                systemPrompt = "You are an Embedded Linux Kernel Engineer. Analyze dmesg, devicetree device tree bindings, kernel panics, and driver crashes."
            ),
            com.example.data.local.entity.AiRoleEntity(
                name = "Reverse Engineer",
                description = "Analyzes BGA pinouts, schematics, and signal trace integrity",
                systemPrompt = "You are a Mobile Hardware Reverse Engineer. Analyze BGA pinouts, schematics, test points, and PCB signal traces."
            )
        )
        defaultRoles.forEach { brainDao.insertRole(it) }

        // Seed Initial Prompts
        val defaultPrompts = listOf(
            com.example.data.local.entity.AiPromptEntity(
                name = "Hardware Diagnosis Master",
                description = "Comprehensive hardware breakdown with Myanmar language translation",
                category = "Diagnosis",
                priority = 1,
                content = "Thoroughly analyze the UART boot log, identify hardware IC failures, voltage rail anomalies, and list step-by-step repair actions in Myanmar.",
                isFavorite = true
            ),
            com.example.data.local.entity.AiPromptEntity(
                name = "Quick Summary",
                description = "Concise 3-bullet summary for fast diagnosis on customer counter",
                category = "Summary",
                priority = 2,
                content = "Provide a concise 3-bullet summary in Myanmar detailing the main boot failure and recommended IC replacement."
            ),
            com.example.data.local.entity.AiPromptEntity(
                name = "DDR / Storage Diagnostic",
                description = "Focus strictly on RAM and UFS/eMMC initialization errors",
                category = "Storage & RAM",
                priority = 3,
                content = "Focus exclusively on DDR RAM and UFS/eMMC initialization errors, power rail measurements, and EasyJTAG repair steps."
            )
        )
        defaultPrompts.forEach { brainDao.insertPrompt(it) }

        // Seed Initial Rules
        val defaultRules = listOf(
            com.example.data.local.entity.AiRuleEntity(
                ruleName = "Backlight IC Fault Rule",
                conditionField = "UART Log Line",
                operator = "CONTAINS",
                value = "bklic = 0",
                actionTitle = "Display Initialization Failure",
                suggestedIcs = "LCD Connector, Backlight IC, OLED PMIC",
                suggestedRails = "BL_EN, PWM, VDD_DISPLAY",
                confidence = 85
            ),
            com.example.data.local.entity.AiRuleEntity(
                ruleName = "DDR Training Failure Rule",
                conditionField = "UART Log Line",
                operator = "CONTAINS",
                value = "ddr_phy_init",
                actionTitle = "RAM Training Failed / Double Decker Soldering Issue",
                suggestedIcs = "LPDDR5 RAM Chip, CPU Double Decker BGA",
                suggestedRails = "VDD_RAM 1.1V, VDD_CORE",
                confidence = 92
            ),
            com.example.data.local.entity.AiRuleEntity(
                ruleName = "UFS Unresponsive Rule",
                conditionField = "UART Log Line",
                operator = "CONTAINS",
                value = "UFS_RESET_N",
                actionTitle = "UFS Storage Timeout / Health Degraded",
                suggestedIcs = "UFS 4.0 / eMMC Storage Chip",
                suggestedRails = "VCC 2.95V, VCCQ 1.2V",
                confidence = 88
            )
        )
        defaultRules.forEach { brainDao.insertRule(it) }

        // Seed Initial Memories
        val defaultMemories = listOf(
            com.example.data.local.entity.AiMemoryEntity(content = "Never guess hardware failures without log evidence.", priority = 1),
            com.example.data.local.entity.AiMemoryEntity(content = "Always explain in Myanmar (မြန်မာဘာသာ).", priority = 2),
            com.example.data.local.entity.AiMemoryEntity(content = "Always compare with Reference Good Log when available.", priority = 3),
            com.example.data.local.entity.AiMemoryEntity(content = "Always separate Hardware and Software causes clearly.", priority = 4),
            com.example.data.local.entity.AiMemoryEntity(content = "Always show Confidence Score (0-100%).", priority = 5),
            com.example.data.local.entity.AiMemoryEntity(content = "Always explain Boot Stage (PBL, XBL, Kernel).", priority = 6),
            com.example.data.local.entity.AiMemoryEntity(content = "Always list Related IC and Power Rails.", priority = 7)
        )
        defaultMemories.forEach { brainDao.insertMemory(it) }

        // Seed Initial Keywords
        val defaultKeywords = listOf(
            com.example.data.local.entity.AiKeywordEntity(
                keyword = "PBL",
                meaning = "Primary Boot Loader",
                myanmarMeaning = "Qualcomm CPU ၏ အခြေခံ ပထမဆုံး Boot ROM စနစ်",
                bootStage = "BOOTROM",
                hardwareBlock = "CPU",
                softwareBlock = "PBL ROM",
                relatedIc = "Snapdragon CPU",
                powerRail = "VDD_CPU",
                possibleCause = "CPU BGA Fault / Cold Joint",
                repairSuggestion = "Reball CPU or check VDD_CPU rail"
            ),
            com.example.data.local.entity.AiKeywordEntity(
                keyword = "XBL",
                meaning = "eXtensible Boot Loader",
                myanmarMeaning = "DDR RAM နှင့် PMIC မီးလိုင်းများကို စတင်သော ဒုတိယ အဆင့် စနစ်",
                bootStage = "XBL",
                hardwareBlock = "RAM & PMIC",
                softwareBlock = "XBL Image",
                relatedIc = "PM8550 / LPDDR5 RAM",
                powerRail = "VDD_RAM / VPH_PWR",
                possibleCause = "RAM BGA fracture / RAM power rail missing",
                repairSuggestion = "Reball RAM IC, check VDD_RAM LDOs"
            ),
            com.example.data.local.entity.AiKeywordEntity(
                keyword = "UFS",
                meaning = "Universal Flash Storage",
                myanmarMeaning = "ဖုန်း၏ အဓိက သိုလှောင်မှု Flash Memory Chip",
                bootStage = "STORAGE_INIT",
                hardwareBlock = "Storage",
                softwareBlock = "Partition Table",
                relatedIc = "UFS 4.0 Chip",
                powerRail = "VCC 2.95V / VCCQ 1.2V",
                possibleCause = "UFS BAD BLOCK / Health 90% depleted",
                repairSuggestion = "Program with EasyJTAG / Replace UFS IC"
            )
        )
        defaultKeywords.forEach { brainDao.insertKeyword(it) }

        // Seed Initial Settings
        brainDao.insertOrUpdateSettings(
            com.example.data.local.entity.AiSettingsEntity()
        )
    }
}
