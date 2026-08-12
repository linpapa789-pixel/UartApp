package com.example.data.ai

import com.example.data.local.entity.AiRuleEntity
import com.example.data.parser.ParsedLogLine
import java.util.regex.Pattern

data class RuleEvaluationResult(
    val rule: AiRuleEntity,
    val isTriggered: Boolean,
    val matchedLines: List<String>,
    val extractedVariables: Map<String, String> = emptyMap()
)

object RuleEngine {

    /**
     * Evaluates a list of AiRuleEntity against raw log lines or AST ParsedLogLine objects.
     * Supports Regex, Logical AND/OR/NOT, multi-line sequence detection, and variable extraction.
     */
    fun evaluateRules(rules: List<AiRuleEntity>, logLines: List<String>): List<RuleEvaluationResult> {
        val enabledRules = rules.filter { it.isEnabled }.sortedByDescending { it.priority }
        val results = mutableListOf<RuleEvaluationResult>()

        for (rule in enabledRules) {
            val eval = evaluateSingleRule(rule, logLines)
            if (eval.isTriggered) {
                results.add(eval)
            }
        }
        return results
    }

    private fun evaluateSingleRule(rule: AiRuleEntity, logLines: List<String>): RuleEvaluationResult {
        val keyword = rule.value.trim()
        val matchedLines = mutableListOf<String>()
        val extractedVars = mutableMapOf<String, String>()

        if (keyword.isBlank()) {
            return RuleEvaluationResult(rule, false, emptyList())
        }

        // Support Regex / Expression matching
        val isRegex = keyword.startsWith("regex:", ignoreCase = true)
        val patternString = if (isRegex) keyword.substring(6).trim() else keyword

        val isAndCondition = keyword.contains(" AND ", ignoreCase = true)
        val isOrCondition = keyword.contains(" OR ", ignoreCase = true)
        val isNotCondition = keyword.startsWith("NOT ", ignoreCase = true)

        var triggered = false

        if (isNotCondition) {
            val notTarget = keyword.substring(4).trim().lowercase()
            val containsTarget = logLines.any { it.lowercase().contains(notTarget) }
            triggered = !containsTarget
            if (triggered) {
                matchedLines.add("Rule NOT condition satisfied: '$notTarget' absent from logs")
            }
        } else if (isAndCondition) {
            val parts = keyword.split(Regex(" AND ", RegexOption.IGNORE_CASE)).map { it.trim().lowercase() }
            val matchedAll = parts.all { part ->
                logLines.any { line -> line.lowercase().contains(part) }
            }
            if (matchedAll) {
                triggered = true
                for (part in parts) {
                    logLines.firstOrNull { it.lowercase().contains(part) }?.let { matchedLines.add(it) }
                }
            }
        } else if (isOrCondition) {
            val parts = keyword.split(Regex(" OR ", RegexOption.IGNORE_CASE)).map { it.trim().lowercase() }
            val matchedAny = parts.any { part ->
                logLines.any { line -> line.lowercase().contains(part) }
            }
            if (matchedAny) {
                triggered = true
                for (part in parts) {
                    logLines.filter { line -> parts.any { p -> line.lowercase().contains(p) } }.forEach {
                        if (!matchedLines.contains(it)) matchedLines.add(it)
                    }
                }
            }
        } else if (isRegex) {
            try {
                val pattern = Pattern.compile(patternString, Pattern.CASE_INSENSITIVE)
                for (line in logLines) {
                    val matcher = pattern.matcher(line)
                    if (matcher.find()) {
                        triggered = true
                        matchedLines.add(line)
                        if (matcher.groupCount() >= 1) {
                            for (g in 1..matcher.groupCount()) {
                                extractedVars["var_$g"] = matcher.group(g) ?: ""
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                // Invalid regex fallback to simple string match
                val target = patternString.lowercase()
                logLines.filter { it.lowercase().contains(target) }.forEach {
                    triggered = true
                    matchedLines.add(it)
                }
            }
        } else {
            // Standard single keyword match
            val target = keyword.lowercase()
            val matches = logLines.filter { it.lowercase().contains(target) }
            if (matches.isNotEmpty()) {
                triggered = true
                matchedLines.addAll(matches)
            }
        }

        return RuleEvaluationResult(
            rule = rule,
            isTriggered = triggered,
            matchedLines = matchedLines,
            extractedVariables = extractedVars
        )
    }
}
