package com.example.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color

object ThemeState {
    var isDark by mutableStateOf(true)
}

// Dark colors
private val DarkBg = Color(0xFF0B0F17)
private val DarkCard = Color(0xFF1E293B)
private val DarkBorder = Color(0x330EA5E9)
private val DarkTextPrim = Color(0xFFF8FAFC)
private val DarkTextSec = Color(0xFF94A3B8)
private val DarkMuted = Color(0xFF64748B)
private val DarkAccent = Color(0xFF0EA5E9)

// Light colors
private val LightBg = Color(0xFFF8FAFC)
private val LightCard = Color(0xFFFFFFFF)
private val LightBorder = Color(0xFFE2E8F0)
private val LightTextPrim = Color(0xFF0F172A)
private val LightTextSec = Color(0xFF475569)
private val LightMuted = Color(0xFF94A3B8)
private val LightAccent = Color(0xFF0284C7)

val AppBackground: Color @Composable get() = if (ThemeState.isDark) DarkBg else LightBg
val CardBackground: Color @Composable get() = if (ThemeState.isDark) DarkCard else LightCard
val CardBorder: Color @Composable get() = if (ThemeState.isDark) DarkBorder else LightBorder
val TextPrimary: Color @Composable get() = if (ThemeState.isDark) DarkTextPrim else LightTextPrim
val TextSecondary: Color @Composable get() = if (ThemeState.isDark) DarkTextSec else LightTextSec
val TextMutedColor: Color @Composable get() = if (ThemeState.isDark) DarkMuted else LightMuted
val AccentCyan: Color @Composable get() = if (ThemeState.isDark) DarkAccent else LightAccent
val TerminalBg: Color @Composable get() = if (ThemeState.isDark) Color(0xFF070A0F) else Color(0xFFF1F5F9)

// Constants
val SuccessGreen = Color(0xFF22C55E)
val WarningYellow = Color(0xFFFACC15)
val ErrorRed = Color(0xFFEF4444)

// Terminal specific colors
val LogTimeColor: Color @Composable get() = if (ThemeState.isDark) Color(0xFF64748B) else Color(0xFF94A3B8)
val LogErrorColor = ErrorRed
val LogWarnColor = WarningYellow
val LogInfoColor: Color @Composable get() = if (ThemeState.isDark) Color(0xFF38BDF8) else Color(0xFF0284C7)
val LogBootColor = SuccessGreen
