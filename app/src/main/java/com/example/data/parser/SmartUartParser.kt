package com.example.data.parser

import kotlin.math.max

data class ParsedLogLine(
    val lineNumber: Int,
    val rawLine: String,
    val timestamp: String = "",
    val logLevel: String = "INFO", // ERROR, WARN, INFO, DEBUG, BOOT
    val subsystem: String = "General",
    val hardwareBlock: String = "CPU/SoC",
    val softwareBlock: String = "Bootloader",
    val powerDomain: String = "VPH_PWR",
    val voltageRail: String = "",
    val signalName: String = "",
    val resetSource: String = "",
    val matchedKeywords: List<KeywordInfo> = emptyList(),
    val bootStage: String = "BootROM",
    val confidence: Int = 85
)

data class KeywordInfo(
    val keyword: String,
    val meaning: String,
    val meaningMm: String, // Myanmar translation
    val bootStage: String,
    val possibleHardware: String,
    val possibleSoftware: String,
    val relatedIc: String,
    val relatedVoltage: String,
    val relatedSignal: String,
    val relatedPowerRail: String,
    val relatedPartition: String,
    val relatedSlot: String,
    val possibleCause: String,
    val repairSuggestion: String,
    val repairSuggestionMm: String,
    val confidenceScore: Int // 0 - 100%
)

data class BootStageInfo(
    val stageName: String,
    val description: String,
    val descriptionMm: String,
    val status: StageStatus,
    val detectedLines: List<String> = emptyList(),
    val timestampOffset: String = ""
)

enum class StageStatus {
    PASSED,
    WARNING,
    FAILED,
    NOT_REACHED
}

data class LogComparisonResult(
    val similarityPercentage: Float,
    val totalLinesGood: Int,
    val totalLinesFault: Int,
    val lineDiffs: List<LineDiffItem>,
    val missingKeywords: List<String>,
    val stageDifferences: List<String>,
    val matchedLinesCount: Int = lineDiffs.count { it.status == DiffType.MATCH },
    val missingLinesCount: Int = lineDiffs.count { it.status == DiffType.MISSING_IN_FAULT },
    val extraLinesCount: Int = lineDiffs.count { it.status == DiffType.EXTRA_IN_FAULT },
    val changedLinesCount: Int = lineDiffs.count { it.status == DiffType.CHANGED },
    val stageSimilarityMap: Map<String, Float> = emptyMap()
)

data class LineDiffItem(
    val lineNumber: Int,
    val goodLine: String?,
    val faultLine: String?,
    val status: DiffType,
    val goodLineNumber: Int? = null,
    val faultLineNumber: Int? = null,
    val bootStage: String = "Boot",
    val reason: String = ""
)

enum class DiffType {
    MATCH,
    CHANGED,
    MISSING_IN_FAULT,
    EXTRA_IN_FAULT
}

object SmartUartParser {

    val BOOT_STAGES_ORDER = listOf(
        "BootROM",
        "PBL",
        "XBL",
        "DDR Training",
        "PMIC Init",
        "Clock Init",
        "GPIO Init",
        "Storage Init",
        "eMMC Init",
        "UFS Init",
        "RPMB",
        "TrustZone",
        "ABL",
        "VBMETA",
        "AVB",
        "DTB / DTBO",
        "Kernel",
        "Vendor Boot",
        "Init",
        "Android Framework",
        "SurfaceFlinger",
        "Launcher",
        "EDL / 9008 Mode",
        "Fastboot Mode"
    )

    val KEYWORD_DATABASE = listOf(
        KeywordInfo(
            keyword = "pmic",
            meaning = "Power Management Integrated Circuit initialization or error",
            meaningMm = "ပါဝါ စီမံခန့်ခွဲမှု အိုင်စီ (PMIC) စတင်ခြင်း သို့မဟုတ် ချို့ယွင်းမှု",
            bootStage = "PMIC Init",
            possibleHardware = "Main PMIC (PM8998, PM8150, Qualcomm PMIC, MediaTek MT6358), Buck Converter, LDO Voltage Regulator",
            possibleSoftware = "PMIC driver device tree configuration, SPMI bus driver",
            relatedIc = "Qualcomm PM8150 / MediaTek MT6358 / Samsung S2MPS18",
            relatedVoltage = "VREG_S4A_1P8, VREG_L6A_1P8, VDD_CPU, VDD_CORE",
            relatedSignal = "SPMI_CLK, SPMI_DATA, PMIC_RESIN, PON_RESET",
            relatedPowerRail = "VSYS, VPH_PWR, VREG_BOOST",
            relatedPartition = "xbl_config / pmic",
            relatedSlot = "Slot A / B",
            possibleCause = "Short circuit on LDO lines, bad PMIC soldering/BGA crack, corrupted SPMI bus",
            repairSuggestion = "Check VPH_PWR rail for short to ground. Measure LDO output voltages. Reball or replace PMIC IC.",
            repairSuggestionMm = "VPH_PWR လိုင်းတွင် ရှော့ (Short) ရှိမရှိ တိုင်းတာပါ။ LDO ထွက်ဗို့များကို စစ်ဆေးပါ။ PMIC ကို Reball ပြုလုပ်ပါ သို့မဟုတ် လဲလှယ်ပါ။",
            confidenceScore = 95
        ),
        KeywordInfo(
            keyword = "ddr",
            meaning = "Double Data Rate RAM training or memory initialization failure",
            meaningMm = "RAM (DDR) Training မအောင်မြင်ခြင်း သို့မဟုတ် လိုင်းချို့ယွင်းခြင်း",
            bootStage = "DDR Training",
            possibleHardware = "LPDDR4X / LPDDR5 RAM chip, CPU (SoC) BGA connection, RAM PMIC",
            possibleSoftware = "DDR training parameters in XBL/PBL, clock frequency tables",
            relatedIc = "Samsung / SK Hynix / Micron LPDDR5 RAM stacked on CPU",
            relatedVoltage = "VDDQ_1P1, VDD2_1P1, VDD1_1P8",
            relatedSignal = "DDR_CLK_P/N, DDR_DQ, DDR_CA",
            relatedPowerRail = "VDDQ_0.6V, VDD1_1.8V",
            relatedPartition = "xbl / ddr_cfg",
            relatedSlot = "Primary Slot",
            possibleCause = "RAM chip damage, SoC pin disconnect due to thermal stress or phone drop impact",
            repairSuggestion = "Reball CPU and RAM (Double-Decker CPU repair). Check VDDQ voltage lines.",
            repairSuggestionMm = "CPU နှင့် RAM (Double Decker) ကို Reball ပြန်ရိုက်ပါ။ VDDQ ပါဝါဗို့လိုင်းများ စစ်ဆေးပါ။",
            confidenceScore = 98
        ),
        KeywordInfo(
            keyword = "ufs",
            meaning = "Universal Flash Storage initialization / read-write error",
            meaningMm = "UFS Internal Memory စတင်၍ မရခြင်း သို့မဟုတ် ဖတ်/ရေး မရခြင်း",
            bootStage = "UFS Init",
            possibleHardware = "UFS Storage IC, UFS Controller inside SoC, UFS Power Rail",
            possibleSoftware = "UFS host controller driver, UFS partition table corrupt",
            relatedIc = "KLU8G1GETD / KLUEG8UHDB UFS 3.1 / 4.0 IC",
            relatedVoltage = "VCC_2P95, VCCQ_1P2, VCCQ2_1P8",
            relatedSignal = "UFS_RESET_N, UFS_TX/RX_P/N",
            relatedPowerRail = "VCC_UFS, VCCQ_UFS",
            relatedPartition = "gpt / primary GPT table",
            relatedSlot = "Embedded Storage",
            possibleCause = "UFS health end-of-life (worn out flash), blown UFS power filter, missing reset signal",
            repairSuggestion = "Measure VCC (2.95V) and VCCQ (1.2V) lines. Read UFS SMART health via EasyJTAG/Mipi Tester. Replace UFS if health > 90%.",
            repairSuggestionMm = "VCC (2.95V) နှင့် VCCQ (1.2V) ဗို့များကို တိုင်းတာပါ။ EasyJTAG သို့မဟုတ် Mipi Tester ဖြင့် UFS Health စစ်ဆေးပါ။ 90% ကျော်ပါက UFS လဲလှယ်ပါ။",
            confidenceScore = 96
        ),
        KeywordInfo(
            keyword = "emmc",
            meaning = "eMMC Flash Memory read failure or bad blocks",
            meaningMm = "eMMC Storage မမ်မိုရီ ဖတ်၍မရခြင်း သို့မဟုတ် ပျက်စီးခြင်း",
            bootStage = "eMMC Init",
            possibleHardware = "eMMC v5.1 IC, CMD/CLK pull-up resistors",
            possibleSoftware = "eMMC driver, GPT partition corruption",
            relatedIc = "SanDisk / Samsung / Hynix eMMC IC",
            relatedVoltage = "VCC_2P8, VCCQ_1P8",
            relatedSignal = "eMMC_CMD, eMMC_CLK, eMMC_DAT0-7",
            relatedPowerRail = "VCC_3V, VCCQ_1.8V",
            relatedPartition = "GPT / MBR",
            relatedSlot = "eMMC Slot",
            possibleCause = "eMMC dead, bad CMD line resistor, corrupt bootloader partitions",
            repairSuggestion = "Check eMMC CMD resistor (10k ohm). Connect via ISP to program GPT/XBL partitions or swap eMMC.",
            repairSuggestionMm = "eMMC CMD လိုင်းရှိ ၁၀ ကီလိုအုမ်း ရီစစ်စတာ စစ်ဆေးပါ။ ISP မြောင်းဆွဲ၍ GPT ပြန်ရေးပါ သို့မဟုတ် eMMC လဲပါ။",
            confidenceScore = 94
        ),
        KeywordInfo(
            keyword = "rpmb",
            meaning = "Replay Protected Memory Block mismatch or communication failure",
            meaningMm = "RPMB လုံခြုံရေး မမ်မိုရီ သော့ မကိုက်ညီခြင်း သို့မဟုတ် ပျက်စီးခြင်း",
            bootStage = "RPMB / TrustZone",
            possibleHardware = "UFS / eMMC RPMB hardware partition, SoC security fuse",
            possibleSoftware = "TrustZone, Keymaster, TEE OS",
            relatedIc = "SoC Secure Enclave & UFS RPMB",
            relatedVoltage = "VCCQ_1P2",
            relatedSignal = "RPMB_READ_KEY",
            relatedPowerRail = "VCCQ",
            relatedPartition = "rpmb",
            relatedSlot = "Secure Slot",
            possibleCause = "RPMB key mismatched after CPU or Storage replacement without pairing",
            repairSuggestion = "Do NOT format RPMB without original keys. Ensure CPU and Storage are paired properly or reprogram original secure key.",
            repairSuggestionMm = "Original key မပါဘဲ RPMB ကို Format မလုပ်ပါနှင့်။ CPU နှင့် Storage အတွဲလိုက် မှန်မမှန် စစ်ဆေးပါ။",
            confidenceScore = 92
        ),
        KeywordInfo(
            keyword = "dtbo",
            meaning = "Device Tree Blob Overlay error during early kernel device mapping",
            meaningMm = "Device Tree Overlay ဖွဲ့စည်းပုံ အမှားအယွင်းကြောင့် Boot ရပ်သွားခြင်း",
            bootStage = "DTB / DTBO",
            possibleHardware = "Peripheral ICs (Touch, Display, Battery ID)",
            possibleSoftware = "Android DTBO partition image",
            relatedIc = "Display Driver IC / PMIC GPIOs",
            relatedVoltage = "VREG_1P8",
            relatedSignal = "I2C_SCL, I2C_SDA, SPI_CS",
            relatedPowerRail = "VREG_IO",
            relatedPartition = "dtbo",
            relatedSlot = "Slot A/B",
            possibleCause = "Incompatible firmware flash, mismatched DTBO for display or touch panel hardware revision",
            repairSuggestion = "Reflash stock firmware matching exact device model and board revision.",
            repairSuggestionMm = "မူလ Stock Firmware ကို Model အတိအကျဖြင့် Flash ပြန်ရိုက်ပါ။",
            confidenceScore = 90
        ),
        KeywordInfo(
            keyword = "vbmeta",
            meaning = "Verified Boot Metadata verification failure (AVB Error)",
            meaningMm = "Android Verified Boot လုံခြုံရေး စစ်ဆေးမှု မအောင်မြင်ခြင်း",
            bootStage = "AVB / VBMETA",
            possibleHardware = "SoC Fuse / Cryptographic Engine",
            possibleSoftware = "Android Verified Boot 2.0 signatures, corrupted boot or system partitions",
            relatedIc = "Qualcomm Crypto Core",
            relatedVoltage = "N/A",
            relatedSignal = "AVB_HASH_MATCH",
            relatedPowerRail = "N/A",
            relatedPartition = "vbmeta, vbmeta_system, vbmeta_vendor",
            relatedSlot = "Slot A / B",
            possibleCause = "Bootloader unlocked, custom ROM flashing, corrupted boot/recovery signature",
            repairSuggestion = "Flash original stock vbmeta image or patch vbmeta using --disable-verity --disable-verification in fastboot mode.",
            repairSuggestionMm = "မူလ Stock vbmeta ကို Flash ပြန်ရိုက်ပါ သို့မဟုတ် Fastboot တွင် verification ဖြုတ်၍ Flashing လုပ်ပါ။",
            confidenceScore = 95
        ),
        KeywordInfo(
            keyword = "edl",
            meaning = "Emergency Download Mode triggered (Qualcomm 9008)",
            meaningMm = "အရေးပေါ် ဒေါင်းလုဒ်စနစ် (Qualcomm 9008 EDL) သို့ ရောက်ရှိသွားခြင်း",
            bootStage = "EDL / 9008 Mode",
            possibleHardware = "Storage IC dead, XBL partition corrupt, EDL test point shorted",
            possibleSoftware = "PBL code fallback when primary bootloader is missing",
            relatedIc = "Qualcomm Snapdragon SoC & UFS/eMMC",
            relatedVoltage = "VREG_S4A (1.8V)",
            relatedSignal = "FORCE_USB_BOOT / FORCE_EDL",
            relatedPowerRail = "VPH_PWR",
            relatedPartition = "xbl / sbl1",
            relatedSlot = "Boot Slot",
            possibleCause = "Corrupted Bootloader (XBL/PBL), dead Storage IC, broken CMD line, PMIC power output missing",
            repairSuggestion = "Check if EDL pin is shorted to ground. Connect to PC via USB, unbrick using Qualcomm Firehose programmer tool. Check storage voltage.",
            repairSuggestionMm = "EDL Test Point ရှော့ဖြစ်နေသလား စစ်ပါ။ Firehose Programmer Tool သုံးပြီး Firmware ပြန်ရိုက်ပါ သို့မဟုတ် Storage လဲပါ။",
            confidenceScore = 99
        ),
        KeywordInfo(
            keyword = "charger",
            meaning = "Charger IC detection or Battery communication failed",
            meaningMm = "အားသွင်းအိုင်စီ (Charger IC) သို့မဟုတ် ဘက်ထရီ လိုင်းစစ်ဆေးမှု မအောင်မြင်ခြင်း",
            bootStage = "PMIC Init / Charger",
            possibleHardware = "OVP IC, Charging Controller, Battery ID resistor, NTC thermistor",
            possibleSoftware = "Fuel Gauge driver, SPMI/I2C battery daemon",
            relatedIc = "BQ25601 / BQ25970 / PMI632 / SMB1351",
            relatedVoltage = "VBUS_5V/9V, VBAT_4.2V, BATT_ID (1.8V)",
            relatedSignal = "USB_DP/DM, I2C_SDA/SCL, BAT_TEMP",
            relatedPowerRail = "VBUS, VBAT, VPH_PWR",
            relatedPartition = "charger / system",
            relatedSlot = "N/A",
            possibleCause = "Burnt OVP chip, missing Battery ID connection, blown charging coil/resistor",
            repairSuggestion = "Check VBUS 5V input, measure Battery ID pin resistor value (usually 10k-100k ohm). Replace Charger IC or OVP chip.",
            repairSuggestionMm = "VBUS 5V ဗို့ဝင်မဝင် စစ်ပါ။ Battery ID ပင် ရီစစ်စတာကို တိုင်းပါ။ OVP သို့မဟုတ် Charger IC လဲလှယ်ပါ။",
            confidenceScore = 93
        ),
        KeywordInfo(
            keyword = "display",
            meaning = "Display panel initialized error or DSI video signal timeout",
            meaningMm = "မျက်နှာပြင် (Display Panel / MIPI DSI) စတင်ခြင်း သို့မဟုတ် ဗို့အားမရောက်ခြင်း",
            bootStage = "Kernel / SurfaceFlinger",
            possibleHardware = "AMOLED / LCD screen, Display Driver IC, Backlight Driver, MIPI Filter Coils",
            possibleSoftware = "MIPI DSI panel driver, DRM / KMS display subsystem",
            relatedIc = "Display PMIC (TPS65132 / SM5109) / AMOLED PMIC",
            relatedVoltage = "AVDD_5P5, AVEE_NEG5P5, VREG_1P8",
            relatedSignal = "MIPI_DSI_CLK_P/N, MIPI_DSI_DATA0-3",
            relatedPowerRail = "VPOS (+5.5V), VNEG (-5.5V)",
            relatedPartition = "boot / vendor",
            relatedSlot = "MIPI Lane 0",
            possibleCause = "Blown Display PMIC (+5.5V / -5.5V missing), damaged FPC connector pins, MIPI filter array disconnected",
            repairSuggestion = "Measure AVDD (+5.5V) and AVEE (-5.5V) at Display PMIC output. Inspect FPC connector under microscope for bent pins.",
            repairSuggestionMm = "Display PMIC output မှ +5.5V နှင့် -5.5V ထွက်မထွက် တိုင်းပါ။ FPC Socket ပင်များကို မှန်ဘီလူးဖြင့် စစ်ဆေးပါ။",
            confidenceScore = 91
        ),
        KeywordInfo(
            keyword = "touch",
            meaning = "Touch Screen Controller (TS) I2C/SPI communication failed",
            meaningMm = "ထိတွေ့မျက်နှာပြင် (Touch Screen Controller) တုံ့ပြန်မှု မရခြင်း",
            bootStage = "Kernel / Init",
            possibleHardware = "Touch Digitizer IC (Goodix, FocalTech, Synaptics), Touch FPC connector",
            possibleSoftware = "Touchscreen driver (goodix_ts, focaltech_ts)",
            relatedIc = "GT9886 / FT3518 / Synaptics S3320",
            relatedVoltage = "VDD_3P3, VIO_1P8",
            relatedSignal = "TS_INT, TS_RESET, I2C_SDA, I2C_SCL",
            relatedPowerRail = "VREG_TOUCH",
            relatedPartition = "vendor / boot",
            relatedSlot = "Touch Interface",
            possibleCause = "Missing Touch VDD 3.3V, broken Touch RESET signal line, touch IC water damage",
            repairSuggestion = "Measure Touch VDD 3.3V and VIO 1.8V power lines. Verify Touch Interrupt line (TS_INT) resistor continuity.",
            repairSuggestionMm = "Touch VDD 3.3V နှင့် VIO 1.8V ဗို့ရောက်မရောက် တိုင်းပါ။ TS_INT စစ်ထုတ်လိုင်းကို လျှပ်ကူးမှု စစ်ဆေးပါ။",
            confidenceScore = 89
        ),
        KeywordInfo(
            keyword = "modem",
            meaning = "Baseband Modem initialization timeout or NVRAM missing",
            meaningMm = "ဖုန်းလိုင်း ဘေ့စ်ဘန်း (Baseband Modem) အလုပ်မလုပ်ခြင်း သို့မဟုတ် NVRAM ပျက်စီးခြင်း",
            bootStage = "Kernel / Android",
            possibleHardware = "SDR Transceiver IC, Baseband Power PMIC, NVRAM EFS memory chip",
            possibleSoftware = "Modem firmware image, EFS/Persist partitions",
            relatedIc = "Qualcomm SDR865 / MediaTek MT6177RF",
            relatedVoltage = "VDD_MODEM_1P2, VREG_RF_1P8",
            relatedSignal = "IQ_DATA, RFFE_CLK, RFFE_DATA",
            relatedPowerRail = "VDD_RF",
            relatedPartition = "modem, modemst1, modemst2, fsg, persist",
            relatedSlot = "Slot A/B",
            possibleCause = "EFS partition corrupted (Null IMEI), Transceiver IC cold joint soldering, Baseband power short",
            repairSuggestion = "Check baseband version in boot log. Restore QCN / EFS backup via QPST tool. Reball or replace RF Transceiver IC.",
            repairSuggestionMm = "Boot log တွင် Baseband Version တက်မတက် စစ်ဆေးပါ။ QPST ဖြင့် QCN/EFS ပြန် restore လုပ်ပါ သို့မဟုတ် RF IC Reball ရိုက်ပါ။",
            confidenceScore = 92
        ),
        KeywordInfo(
            keyword = "thermal",
            meaning = "Thermal zone overheating trip point triggered or thermistor error",
            meaningMm = "အပူချိန် အာရုံခံစနစ် လွန်ကဲမှုကြောင့် စက်အလိုအလျောက် ပိတ်သွားခြင်း",
            bootStage = "Kernel / Android",
            possibleHardware = "NTC Thermistor resistors (100k), PMIC Thermal Sensors",
            possibleSoftware = "Thermal engine configuration file, thermald daemon",
            relatedIc = "Main PMIC & PCB NTC Thermistors",
            relatedVoltage = "TSENSE_1P8",
            relatedSignal = "THERM_IN_P/N",
            relatedPowerRail = "VREG_1P8",
            relatedPartition = "vendor / thermal_config",
            relatedSlot = "N/A",
            possibleCause = "Missing 100k NTC Thermistor near CPU/PMIC/Charger, damaged thermal pad, shorted power rail causing real heat",
            repairSuggestion = "Check thermal log temperature reading (e.g. 100°C fake alert means missing NTC resistor). Replace broken NTC thermistor.",
            repairSuggestionMm = "အပူချိန် တိုင်းတာမှု အတု (ဥပမာ ၁၀၀ ဒီဂရီ) တက်ပါက NTC Thermistor ရီစစ်စတာ ပြုတ်နေသလား စစ်ဆေး၍ လဲလှယ်ပါ။",
            confidenceScore = 95
        )
    )

    /**
     * AST Line Parser - converts raw log text line into a structured ParsedLogLine
     */
    fun parseStructuredLine(line: String, lineNumber: Int): ParsedLogLine {
        val lower = line.lowercase()
        val matched = KEYWORD_DATABASE.filter { lower.contains(it.keyword) }

        val logLevel = when {
            lower.contains("e -") || lower.contains("error") || lower.contains("failed") || lower.contains("panic") -> "ERROR"
            lower.contains("w -") || lower.contains("warn") || lower.contains("mismatch") -> "WARN"
            lower.contains("b -") || lower.contains("boot") || lower.contains("init") -> "BOOT"
            else -> "INFO"
        }

        val subsystem = when {
            lower.contains("pmic") || lower.contains("vreg") || lower.contains("spmi") -> "PMIC Subsystem"
            lower.contains("ddr") || lower.contains("dram") || lower.contains("lpddr") -> "RAM Memory Subsystem"
            lower.contains("ufs") || lower.contains("emmc") || lower.contains("storage") -> "Storage Subsystem"
            lower.contains("tz") || lower.contains("trustzone") || lower.contains("rpmb") -> "Security / TEE Subsystem"
            lower.contains("display") || lower.contains("dsi") || lower.contains("panel") -> "Display Subsystem"
            lower.contains("touch") || lower.contains("i2c") -> "Peripheral / Touch Subsystem"
            lower.contains("kernel") || lower.contains("init") -> "Linux Kernel / OS Subsystem"
            else -> "General SoC Subsystem"
        }

        val powerDomain = when {
            lower.contains("vreg") -> "LDO Regulator Domain"
            lower.contains("vph") || lower.contains("vsys") -> "VPH_PWR System Main Rail"
            lower.contains("vbus") || lower.contains("vbat") -> "Charging & Battery Power Domain"
            lower.contains("vdd") -> "Core Silicon VDD Power Domain"
            else -> "VPH_PWR Main Bus"
        }

        val bootStage = detectStageForLine(line)

        return ParsedLogLine(
            lineNumber = lineNumber,
            rawLine = line,
            timestamp = extractTimestamp(line),
            logLevel = logLevel,
            subsystem = subsystem,
            powerDomain = powerDomain,
            matchedKeywords = matched,
            bootStage = bootStage,
            confidence = if (matched.isNotEmpty()) matched.maxOf { it.confidenceScore } else 80
        )
    }

    private fun extractTimestamp(line: String): String {
        val regex = Regex("""\[?\s*\d+(?::\d+)*(?:\.\d+)?\s*\]?""")
        return regex.find(line)?.value ?: ""
    }

    fun parseLineForKeywords(line: String): List<KeywordInfo> {
        val lower = line.lowercase()
        return KEYWORD_DATABASE.filter { lower.contains(it.keyword) }
    }

    fun detectBootStages(rawLog: String): List<BootStageInfo> {
        val lines = rawLog.lines()

        return BOOT_STAGES_ORDER.map { stage ->
            val keywords = when (stage) {
                "BootROM" -> listOf("bootrom", "sec_boot", "br_pbl")
                "PBL" -> listOf("pbl", "primary boot loader", "sbl1_main")
                "XBL" -> listOf("xbl", "secondary boot loader", "sbl1")
                "DDR Training" -> listOf("ddr", "lpddr", "dram", "memory training")
                "PMIC Init" -> listOf("pmic", "spmi", "pon_reset", "vreg")
                "Clock Init" -> listOf("clock", "clk", "pll")
                "GPIO Init" -> listOf("gpio", "tlmm", "pinctrl")
                "Storage Init" -> listOf("storage", "sdhc")
                "eMMC Init" -> listOf("emmc", "mmc0")
                "UFS Init" -> listOf("ufs", "scsi", "ufshcd")
                "RPMB" -> listOf("rpmb", "tee")
                "TrustZone" -> listOf("tz", "trustzone", "qsee")
                "ABL" -> listOf("abl", "android boot loader", "fastboot")
                "VBMETA" -> listOf("vbmeta", "avb")
                "AVB" -> listOf("avb", "android verified boot")
                "DTB / DTBO" -> listOf("dtb", "dtbo", "fdt")
                "Kernel" -> listOf("kernel", "linux version", "start_kernel")
                "Vendor Boot" -> listOf("vendor_boot", "vendor")
                "Init" -> listOf("init", "init.rc")
                "Android Framework" -> listOf("zygote", "system_server")
                "SurfaceFlinger" -> listOf("surfaceflinger", "composer")
                "Launcher" -> listOf("launcher", "boot_completed")
                "EDL / 9008 Mode" -> listOf("edl", "9008", "qdl", "emergency download")
                "Fastboot Mode" -> listOf("fastboot", "bootloader mode")
                else -> emptyList()
            }

            val matchingLines = lines.filter { line ->
                val l = line.lowercase()
                keywords.any { k -> l.contains(k) }
            }

            val status = when {
                matchingLines.isNotEmpty() && (stage == "EDL / 9008 Mode" || stage == "Fastboot Mode") -> StageStatus.FAILED
                matchingLines.isNotEmpty() -> StageStatus.PASSED
                else -> StageStatus.NOT_REACHED
            }

            BootStageInfo(
                stageName = stage,
                description = getStageDescription(stage),
                descriptionMm = getStageDescriptionMm(stage),
                status = status,
                detectedLines = matchingLines.take(5)
            )
        }
    }

    private data class IndexedLine(
        val index: Int, // 1-based original line index
        val text: String,
        val stage: String
    )

    /**
     * Production-grade Log Comparison Engine
     * Scalable to 100,000+ lines without OOM using streaming chunk/stage processing.
     */
    fun compareLogs(goodLog: String, faultLog: String): LogComparisonResult {
        try {
            val goodIndexed = goodLog.lineSequence()
                .mapIndexed { idx, line -> IndexedLine(idx + 1, line, detectStageForLine(line)) }
                .filter { it.text.isNotBlank() }
                .toList()

            val faultIndexed = faultLog.lineSequence()
                .mapIndexed { idx, line -> IndexedLine(idx + 1, line, detectStageForLine(line)) }
                .filter { it.text.isNotBlank() }
                .toList()

            val totalGoodCount = goodIndexed.size
            val totalFaultCount = faultIndexed.size

            if (totalGoodCount == 0 && totalFaultCount == 0) {
                return LogComparisonResult(
                    similarityPercentage = 100f,
                    totalLinesGood = 0,
                    totalLinesFault = 0,
                    lineDiffs = emptyList(),
                    missingKeywords = emptyList(),
                    stageDifferences = emptyList()
                )
            }

            val globalDiffItems = mutableListOf<LineDiffItem>()
            val stageSimilarityMap = mutableMapOf<String, Float>()
            val maxLines = max(totalGoodCount, totalFaultCount)

            var matchCount = 0

            if (maxLines <= 2000) {
                // Direct Full DP LCS for standard log sizes <= 2000 lines
                matchCount += compareChunkLcs(goodIndexed, faultIndexed, "Full Log", globalDiffItems)
            } else {
                // Scalable Chunk / Stage Mode for massive logs > 2000 lines
                val CHUNK_SIZE = 500
                val allStages = (BOOT_STAGES_ORDER + listOf("General")).distinct()

                for (stage in allStages) {
                    val gStage = goodIndexed.filter { it.stage == stage }
                    val fStage = faultIndexed.filter { it.stage == stage }
                    if (gStage.isEmpty() && fStage.isEmpty()) continue

                    val maxCount = max(gStage.size, fStage.size)
                    val numChunks = (maxCount + CHUNK_SIZE - 1) / CHUNK_SIZE

                    var stageMatches = 0
                    var stageTotal = 0

                    for (c in 0 until numChunks) {
                        val gChunk = gStage.drop(c * CHUNK_SIZE).take(CHUNK_SIZE)
                        val fChunk = fStage.drop(c * CHUNK_SIZE).take(CHUNK_SIZE)
                        val chunkStageName = if (numChunks > 1) "$stage (Part ${c + 1})" else stage

                        val matches = compareChunkLcs(gChunk, fChunk, chunkStageName, globalDiffItems)
                        stageMatches += matches
                        stageTotal += max(gChunk.size, fChunk.size)
                    }

                    val stageSim = if (stageTotal > 0) (stageMatches.toFloat() / stageTotal) * 100f else 100f
                    stageSimilarityMap[stage] = stageSim
                }

                matchCount = globalDiffItems.count { it.status == DiffType.MATCH }
            }

            val totalMax = max(totalGoodCount, totalFaultCount)
            val overallSimilarity = if (totalMax > 0) (matchCount.toFloat() / totalMax) * 100f else 100f

            // Keyword and Stage Difference Analysis
            val goodKeywords = parseLineForKeywords(goodLog).map { it.keyword }.toSet()
            val faultKeywords = parseLineForKeywords(faultLog).map { it.keyword }.toSet()
            val missingKeywords = (goodKeywords - faultKeywords).toList()

            val goodStages = detectBootStages(goodLog).filter { it.status == StageStatus.PASSED }.map { it.stageName }
            val faultStages = detectBootStages(faultLog).filter { it.status == StageStatus.PASSED }.map { it.stageName }
            val stageDiffs = (goodStages - faultStages.toSet()).map { "Missing Boot Stage: $it" }

            val matchedLinesCount = globalDiffItems.count { it.status == DiffType.MATCH }
            val missingLinesCount = globalDiffItems.count { it.status == DiffType.MISSING_IN_FAULT }
            val extraLinesCount = globalDiffItems.count { it.status == DiffType.EXTRA_IN_FAULT }
            val changedLinesCount = globalDiffItems.count { it.status == DiffType.CHANGED }

            return LogComparisonResult(
                similarityPercentage = overallSimilarity,
                totalLinesGood = totalGoodCount,
                totalLinesFault = totalFaultCount,
                lineDiffs = globalDiffItems,
                missingKeywords = missingKeywords,
                stageDifferences = stageDiffs,
                matchedLinesCount = matchedLinesCount,
                missingLinesCount = missingLinesCount,
                extraLinesCount = extraLinesCount,
                changedLinesCount = changedLinesCount,
                stageSimilarityMap = stageSimilarityMap
            )
        } catch (e: Throwable) {
            e.printStackTrace()
            return LogComparisonResult(
                similarityPercentage = 0f,
                totalLinesGood = 0,
                totalLinesFault = 0,
                lineDiffs = listOf(
                    LineDiffItem(
                        lineNumber = 1,
                        goodLine = null,
                        faultLine = "Error processing logs: ${e.message}",
                        status = DiffType.CHANGED,
                        goodLineNumber = null,
                        faultLineNumber = 1,
                        bootStage = "Error",
                        reason = e.message ?: "Log processing error"
                    )
                ),
                missingKeywords = emptyList(),
                stageDifferences = listOf("Log parsing error encountered")
            )
        }
    }

    private fun compareChunkLcs(
        goodLines: List<IndexedLine>,
        faultLines: List<IndexedLine>,
        bootStageName: String,
        globalDiffItems: MutableList<LineDiffItem>
    ): Int {
        val m = goodLines.size
        val n = faultLines.size
        if (m == 0 && n == 0) return 0

        if (m == 0) {
            for (f in faultLines) {
                globalDiffItems.add(
                    LineDiffItem(
                        lineNumber = globalDiffItems.size + 1,
                        goodLine = null,
                        faultLine = f.text,
                        status = DiffType.EXTRA_IN_FAULT,
                        goodLineNumber = null,
                        faultLineNumber = f.index,
                        bootStage = bootStageName,
                        reason = "Extra line in fault log during $bootStageName"
                    )
                )
            }
            return 0
        }

        if (n == 0) {
            for (g in goodLines) {
                globalDiffItems.add(
                    LineDiffItem(
                        lineNumber = globalDiffItems.size + 1,
                        goodLine = g.text,
                        faultLine = null,
                        status = DiffType.MISSING_IN_FAULT,
                        goodLineNumber = g.index,
                        faultLineNumber = null,
                        bootStage = bootStageName,
                        reason = "Missing line in fault log during $bootStageName"
                    )
                )
            }
            return 0
        }

        // Build LCS DP matrix for chunk safely
        val dp = Array(m + 1) { IntArray(n + 1) }
        for (i in 1..m) {
            val normG = normalizeLine(goodLines[i - 1].text)
            for (j in 1..n) {
                val normF = normalizeLine(faultLines[j - 1].text)
                if (normG == normF) {
                    dp[i][j] = dp[i - 1][j - 1] + 1
                } else {
                    dp[i][j] = max(dp[i - 1][j], dp[i][j - 1])
                }
            }
        }

        // Backtrack to construct chunk diffs
        val chunkDiffs = mutableListOf<LineDiffItem>()
        var i = m
        var j = n
        var chunkMatchCount = 0

        while (i > 0 || j > 0) {
            val gLine = if (i > 0) goodLines[i - 1] else null
            val fLine = if (j > 0) faultLines[j - 1] else null

            val normG = gLine?.let { normalizeLine(it.text) } ?: ""
            val normF = fLine?.let { normalizeLine(it.text) } ?: ""

            if (i > 0 && j > 0 && normG == normF) {
                chunkMatchCount++
                chunkDiffs.add(
                    0,
                    LineDiffItem(
                        lineNumber = 0,
                        goodLine = gLine!!.text,
                        faultLine = fLine!!.text,
                        status = DiffType.MATCH,
                        goodLineNumber = gLine.index,
                        faultLineNumber = fLine.index,
                        bootStage = bootStageName,
                        reason = "Exact match during $bootStageName"
                    )
                )
                i--
                j--
            } else if (j > 0 && (i == 0 || dp[i][j - 1] >= dp[i - 1][j])) {
                chunkDiffs.add(
                    0,
                    LineDiffItem(
                        lineNumber = 0,
                        goodLine = null,
                        faultLine = fLine!!.text,
                        status = DiffType.EXTRA_IN_FAULT,
                        goodLineNumber = null,
                        faultLineNumber = fLine.index,
                        bootStage = bootStageName,
                        reason = "Extra line in fault log during $bootStageName"
                    )
                )
                j--
            } else if (i > 0 && (j == 0 || dp[i][j - 1] < dp[i - 1][j])) {
                chunkDiffs.add(
                    0,
                    LineDiffItem(
                        lineNumber = 0,
                        goodLine = gLine!!.text,
                        faultLine = null,
                        status = DiffType.MISSING_IN_FAULT,
                        goodLineNumber = gLine.index,
                        faultLineNumber = null,
                        bootStage = bootStageName,
                        reason = "Missing line in fault log during $bootStageName"
                    )
                )
                i--
            }
        }

        val postProcessed = postProcessChangedLines(chunkDiffs, bootStageName)
        for (item in postProcessed) {
            globalDiffItems.add(
                item.copy(lineNumber = globalDiffItems.size + 1)
            )
        }

        return chunkMatchCount
    }

    private fun postProcessChangedLines(
        diffs: List<LineDiffItem>,
        bootStageName: String
    ): List<LineDiffItem> {
        if (diffs.isEmpty()) return diffs
        val result = mutableListOf<LineDiffItem>()
        var idx = 0
        while (idx < diffs.size) {
            val curr = diffs[idx]
            if (idx < diffs.size - 1) {
                val next = diffs[idx + 1]
                if (curr.status == DiffType.MISSING_IN_FAULT && next.status == DiffType.EXTRA_IN_FAULT) {
                    result.add(
                        LineDiffItem(
                            lineNumber = curr.lineNumber,
                            goodLine = curr.goodLine,
                            faultLine = next.faultLine,
                            status = DiffType.CHANGED,
                            goodLineNumber = curr.goodLineNumber,
                            faultLineNumber = next.faultLineNumber,
                            bootStage = bootStageName,
                            reason = "Line content modified during $bootStageName"
                        )
                    )
                    idx += 2
                    continue
                } else if (curr.status == DiffType.EXTRA_IN_FAULT && next.status == DiffType.MISSING_IN_FAULT) {
                    result.add(
                        LineDiffItem(
                            lineNumber = curr.lineNumber,
                            goodLine = next.goodLine,
                            faultLine = curr.faultLine,
                            status = DiffType.CHANGED,
                            goodLineNumber = curr.goodLineNumber,
                            faultLineNumber = next.faultLineNumber,
                            bootStage = bootStageName,
                            reason = "Line content modified during $bootStageName"
                        )
                    )
                    idx += 2
                    continue
                }
            }
            result.add(curr)
            idx++
        }
        return result
    }

    fun normalizeLine(line: String): String {
        if (line.isEmpty()) return ""
        return line.lowercase()
            .replace(Regex("""\[?\s*\d+(?::\d+)*(?:\.\d+)?\s*\]?"""), "")
            .replace(Regex("""0x[0-9a-fA-F]+"""), "")
            .replace(Regex("""\b(?:pid|tid|seq|id|thread|cpu|core)[=:\s]*\d+\b"""), "")
            .replace(Regex("""\s+"""), " ")
            .trim()
    }

    private fun detectStageForLine(line: String): String {
        val lower = line.lowercase()
        return when {
            lower.contains("bootrom") || lower.contains("sec_boot") || lower.contains("br_pbl") -> "BootROM"
            lower.contains("pbl") || lower.contains("primary boot loader") || lower.contains("sbl1_main") -> "PBL"
            lower.contains("xbl") || lower.contains("secondary boot loader") || lower.contains("sbl1") -> "XBL"
            lower.contains("ddr") || lower.contains("lpddr") || lower.contains("dram") || lower.contains("memory training") -> "DDR Training"
            lower.contains("pmic") || lower.contains("spmi") || lower.contains("pon_reset") || lower.contains("vreg") -> "PMIC Init"
            lower.contains("clock") || lower.contains("clk") || lower.contains("pll") -> "Clock Init"
            lower.contains("gpio") || lower.contains("tlmm") || lower.contains("pinctrl") -> "GPIO Init"
            lower.contains("ufs") || lower.contains("scsi") || lower.contains("ufshcd") -> "UFS Init"
            lower.contains("emmc") || lower.contains("mmc0") || lower.contains("sdhc") -> "eMMC Init"
            lower.contains("rpmb") || lower.contains("tee") -> "RPMB"
            lower.contains("tz") || lower.contains("trustzone") || lower.contains("qsee") -> "TrustZone"
            lower.contains("abl") || lower.contains("fastboot") -> "ABL"
            lower.contains("vbmeta") || lower.contains("avb") -> "VBMETA"
            lower.contains("dtb") || lower.contains("dtbo") || lower.contains("fdt") -> "DTB / DTBO"
            lower.contains("kernel") || lower.contains("linux version") || lower.contains("start_kernel") -> "Kernel"
            lower.contains("vendor") -> "Vendor Boot"
            lower.contains("init") || lower.contains("init.rc") -> "Init"
            lower.contains("zygote") || lower.contains("system_server") -> "Android Framework"
            lower.contains("surfaceflinger") || lower.contains("composer") -> "SurfaceFlinger"
            lower.contains("launcher") || lower.contains("boot_completed") -> "Launcher"
            lower.contains("edl") || lower.contains("9008") || lower.contains("qdl") -> "EDL / 9008 Mode"
            else -> "General"
        }
    }

    fun cleanNoiseAndCrc(rawLog: String): Pair<String, Boolean> {
        val lines = rawLog.lines()
        var hasNoise = false
        val cleaned = lines.map { line ->
            val garbageCount = line.count { char -> char.code in 0..8 || char.code in 14..31 || char.code > 127 }
            if (garbageCount > line.length * 0.3 && line.length > 5) {
                hasNoise = true
                "[UART NOISE / GARBAGE FILTERED]"
            } else {
                line
            }
        }.joinToString("\n")

        return Pair(cleaned, hasNoise)
    }

    private fun getStageDescription(stage: String): String = when (stage) {
        "BootROM" -> "Primary hardware ROM embedded inside CPU core"
        "PBL" -> "Primary BootLoader executed from internal SRAM"
        "XBL" -> "eXtensible Boot Loader, initializes DDR RAM & PMIC"
        "DDR Training" -> "Calibrates RAM timing clock signals with CPU"
        "PMIC Init" -> "Configures power rails, LDOs and bucks"
        "UFS Init" -> "Mounts UFS 3.1/4.0 flash storage chip"
        "TrustZone" -> "Starts Secure OS, Keymaster and DRM security"
        "ABL" -> "Android Bootloader (Fastboot/Kernel handoff)"
        "Kernel" -> "Loads Linux kernel drivers and device tree"
        "Android Framework" -> "Launches Zygote, SystemServer & ART VM"
        else -> "Hardware & Software Initialization Stage"
    }

    private fun getStageDescriptionMm(stage: String): String = when (stage) {
        "BootROM" -> "CPU အတွင်းပါရှိသော အခြေခံ ROM စတင်ခြင်း"
        "PBL" -> "SRAM တွင် အလုပ်လုပ်သော ပထမအဆင့် Boot Loader"
        "XBL" -> "RAM နှင့် PMIC ကို စတင်ပေးသော ဒုတိယအဆင့် Boot Loader"
        "DDR Training" -> "RAM လိုင်းများ၏ Clock တိုင်မင် စိတ်ကြိုက်ညှိခြင်း"
        "PMIC Init" -> "ပါဝါ လိုင်းများ (LDO/Buck) ဗို့ထုတ်ပေးခြင်း"
        "UFS Init" -> "UFS Internal Memory ကို ချိတ်ဆက်ခြင်း"
        "TrustZone" -> "လုံခြုံရေး စနစ် (TEE / Keymaster) စတင်ခြင်း"
        "ABL" -> "Android Bootloader မှ Linux Kernel သို့ လွှဲပြောင်းခြင်း"
        "Kernel" -> "Linux Kernel Driver များ စတင်လုပ်ဆောင်ခြင်း"
        "Android Framework" -> "Android စနစ် (Zygote/SystemServer) တက်လာခြင်း"
        else -> "စက်၏ စတင်အလုပ်လုပ်မှု အဆင့်"
    }
}
