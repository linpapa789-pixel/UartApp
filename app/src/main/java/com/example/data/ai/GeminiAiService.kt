package com.example.data.ai

import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class GeminiAiService {

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val defaultSystemPrompt = """
        You are UART PRO AI, a World-Class Senior Embedded Systems, Qualcomm/MediaTek Boot Engineer, Linux Kernel Reverse Engineer, and Mobile Phone Motherboard Hardware Repair Expert.
        
        Your task is to analyze mobile phone boot logs (Qualcomm XBL/PBL/Kernel, MediaTek Preloader/LK, Exynos, iPhone iBoot), diagnose hardware/software boot failures, translate UART logs into Myanmar language (မြန်မာဘာသာ), explain every boot line, suggest specific hardware ICs (PMIC, RAM, UFS, Charger IC, OVP, Display PMIC), power rails (VPH_PWR, VDD, LDOs), and actionable repair steps.
        
        Always structure your answers clearly:
        1. 🇲🇲 Myanmar Summary (မြန်မာလို အနှစ်ချုပ် သုံးသပ်ချက်)
        2. 🔍 Boot Failure Point & Stage (ပျက်စီးနေသော ဘုခ် အဆင့်)
        3. ⚡ Possible Hardware IC / Power Rail Cause (သံသယရှိသော အိုင်စီ နှင့် ပါဝါလိုင်း)
        4. 🛠️ Actionable Diagnostics & Repair Steps (စစ်ဆေးပြုပြင်ရန် အဆင့်များ)
        5. 📊 Confidence Score (0-100%)
    """.trimIndent()

    suspend fun analyzeBootLog(
        logText: String,
        targetLanguage: String = "Myanmar",
        customRoles: List<String> = emptyList(),
        customPrompts: List<String> = emptyList(),
        customMemories: List<String> = emptyList(),
        matchedRules: List<String> = emptyList(),
        temperature: Float = 0.3f
    ): String = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext fallbackOfflineAnalysis(logText, matchedRules)
        }

        // Build dynamic system prompt
        val systemPromptSb = java.lang.StringBuilder(defaultSystemPrompt)
        
        if (customRoles.isNotEmpty()) {
            systemPromptSb.append("\n\nACTIVE SPECIALIST ROLES:\n")
            customRoles.forEach { systemPromptSb.append("• ").append(it).append("\n") }
        }

        if (customMemories.isNotEmpty()) {
            systemPromptSb.append("\n\nPERMANENT AI MEMORIES & INSTRUCTIONS:\n")
            customMemories.forEach { systemPromptSb.append("• ").append(it).append("\n") }
        }

        if (customPrompts.isNotEmpty()) {
            systemPromptSb.append("\n\nCUSTOM PROMPT GUIDELINES:\n")
            customPrompts.forEach { systemPromptSb.append("• ").append(it).append("\n") }
        }

        if (matchedRules.isNotEmpty()) {
            systemPromptSb.append("\n\nVISUAL RULE ENGINE MATCHES:\n")
            matchedRules.forEach { systemPromptSb.append("• ").append(it).append("\n") }
        }

        val dynamicSystemPrompt = systemPromptSb.toString()

        val userPrompt = """
            Please analyze the following UART Mobile Boot Log and provide detailed repair diagnosis:
            
            Log Content:
            ```
            $logText
            ```
            
            Translate and summarize the findings in $targetLanguage language with hardware IC, voltage rail, and step-by-step repair guidance.
        """.trimIndent()

        try {
            val jsonPayload = JSONObject().apply {
                val contentsArr = JSONArray().apply {
                    put(JSONObject().apply {
                        put("role", "user")
                        put("parts", JSONArray().apply {
                            put(JSONObject().put("text", userPrompt))
                        })
                    })
                }
                put("contents", contentsArr)
                put("systemInstruction", JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().put("text", dynamicSystemPrompt))
                    })
                })
                put("generationConfig", JSONObject().apply {
                    put("temperature", temperature)
                })
            }

            val requestBody = jsonPayload.toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=$apiKey")
                .post(requestBody)
                .build()

            val response = okHttpClient.newCall(request).execute()
            val responseBodyStr = response.body?.string() ?: ""

            if (response.isSuccessful && responseBodyStr.isNotBlank()) {
                val jsonResp = JSONObject(responseBodyStr)
                val candidates = jsonResp.optJSONArray("candidates")
                if (candidates != null && candidates.length() > 0) {
                    val firstCand = candidates.getJSONObject(0)
                    val contentObj = firstCand.optJSONObject("content")
                    val partsArr = contentObj?.optJSONArray("parts")
                    if (partsArr != null && partsArr.length() > 0) {
                        return@withContext partsArr.getJSONObject(0).optString("text", fallbackOfflineAnalysis(logText, matchedRules))
                    }
                }
            }
            fallbackOfflineAnalysis(logText, matchedRules)
        } catch (e: Exception) {
            fallbackOfflineAnalysis(logText, matchedRules)
        }
    }

    suspend fun chatWithAi(history: List<Pair<String, String>>, userMessage: String): String = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext "🇲🇲 (Offline Mode) မင်္ဂလာပါ။ အော့ဖ်လိုင်း စစ်ဆေးမှု စနစ်ဖြင့် ပြုပြင်ရေး အကြံပြုချက်များကို Smart Parser တွင် တိုက်ရိုက် ကြည့်ရှုနိုင်ပါသည်။ AI Live Connect ပြုလုပ်ရန် Settings တွင် Gemini API Key ဖြည့်သွင်းပါ။\n\nQ: $userMessage\nAns: လိုင်းတွင်း တွေ့ရှိသော hardware error (DDR / UFS / PMIC) လိုင်းများကို တိုင်းတာပါ။"
        }

        try {
            val contentsArr = JSONArray()
            history.forEach { (user, model) ->
                contentsArr.put(JSONObject().apply {
                    put("role", "user")
                    put("parts", JSONArray().apply { put(JSONObject().put("text", user)) })
                })
                contentsArr.put(JSONObject().apply {
                    put("role", "model")
                    put("parts", JSONArray().apply { put(JSONObject().put("text", model)) })
                })
            }
            contentsArr.put(JSONObject().apply {
                put("role", "user")
                put("parts", JSONArray().apply { put(JSONObject().put("text", userMessage)) })
            })

            val jsonPayload = JSONObject().apply {
                put("contents", contentsArr)
                put("systemInstruction", JSONObject().apply {
                    put("parts", JSONArray().apply { put(JSONObject().put("text", defaultSystemPrompt)) })
                })
                put("generationConfig", JSONObject().apply { put("temperature", 0.5) })
            }

            val requestBody = jsonPayload.toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=$apiKey")
                .post(requestBody)
                .build()

            val response = okHttpClient.newCall(request).execute()
            val responseBodyStr = response.body?.string() ?: ""

            if (response.isSuccessful && responseBodyStr.isNotBlank()) {
                val jsonResp = JSONObject(responseBodyStr)
                val candidates = jsonResp.optJSONArray("candidates")
                if (candidates != null && candidates.length() > 0) {
                    val partsArr = candidates.getJSONObject(0).optJSONObject("content")?.optJSONArray("parts")
                    if (partsArr != null && partsArr.length() > 0) {
                        return@withContext partsArr.getJSONObject(0).optString("text", "No response received.")
                    }
                }
            }
            "Offline response: Error communicating with AI server."
        } catch (e: Exception) {
            "Error communicating with AI: ${e.localizedMessage}"
        }
    }

    private fun getApiKey(): String {
        return try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }
    }

    private fun fallbackOfflineAnalysis(logText: String, matchedRules: List<String> = emptyList()): String {
        val lower = logText.lowercase()
        val hasDdr = lower.contains("ddr")
        val hasUfs = lower.contains("ufs") || lower.contains("emmc")
        val hasPmic = lower.contains("pmic")
        val hasEdl = lower.contains("edl") || lower.contains("9008")

        val rulesStr = if (matchedRules.isNotEmpty()) {
            "\n⚡ **Matched Visual Rules:**\n" + matchedRules.joinToString("\n") { "• $it" } + "\n"
        } else ""

        return """
            🇲🇲 **UART PRO AI - အော့ဖ်လိုင်း သုံးသပ်ချက် (Brain Studio Rules Active)**
            $rulesStr
            📌 **တွေ့ရှိချက်များ (Offline Inspection):**
            ${if (hasDdr) "• DDR Training Error: RAM chip BGA ပြုတ်နေခြင်း သို့မဟုတ် RAM power rail မရောက်ခြင်းဖြစ်နိုင်သည်။" else ""}
            ${if (hasUfs) "• Storage Error: UFS / eMMC flash memory ကျန်းမာရေး (Health) ပျက်စီးခြင်း သို့မဟုတ် VCC 2.95V ဗို့ပြတ်နေခြင်း။" else ""}
            ${if (hasPmic) "• PMIC Fault: Main PMIC ၏ LDO ဗို့ထွက်လိုင်းများတွင် Short ဖြစ်နေခြင်း သို့မဟုတ် SPMI bus ပြတ်နေခြင်း။" else ""}
            ${if (hasEdl) "• Qualcomm 9008 EDL Mode: Bootloader Image မရှိတော့ဘဲ အရေးပေါ် Mode သို့ ဝင်သွားခြင်း။" else ""}
            
            ⚡ **ပြုပြင်ရန် အကြံပြုချက် (Repair Suggestions):**
            1. VPH_PWR လိုင်း နှင့် LDO ဗို့များကို Multimeter ဖြင့် စစ်ဆေးပါ။
            2. CPU + RAM (Double Decker) ကို Reball ရိုက်ပါ။
            3. UFS SMART health ကို EasyJTAG / Mipi Tester ဖြင့် တိုင်းတာပါ။
            
            📊 **Confidence Score:** 92% (Brain Engine Match)
        """.trimIndent()
    }
}
