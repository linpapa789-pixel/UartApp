package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Compare
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.DeveloperBoard
import androidx.compose.material.icons.filled.IntegrationInstructions
import androidx.compose.material.icons.automirrored.filled.LibraryBooks
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.AiAssistantScreen
import com.example.ui.screens.BootTimelineScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.GoodLogLibraryScreen
import com.example.ui.screens.LogCompareScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SmartParserScreen
import com.example.ui.screens.TerminalScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.MainViewModel

import androidx.compose.material.icons.filled.Psychology
import com.example.ui.screens.AiBrainStudioScreen

enum class AppScreen(val label: String, val icon: ImageVector) {
    DASHBOARD("Home", Icons.Default.Dashboard),
    TERMINAL("Terminal", Icons.Default.DeveloperBoard),
    AI_BRAIN("AI Brain", Icons.Default.Psychology),
    TIMELINE("Timeline", Icons.Default.Timeline),
    COMPARE("Compare", Icons.Default.Compare),
    DICTIONARY("Dict", Icons.Default.IntegrationInstructions),
    AI_ASSISTANT("AI Pro", Icons.Default.AutoAwesome),
    LIBRARY("Library", Icons.AutoMirrored.Filled.LibraryBooks),
    SETTINGS("Settings", Icons.Default.Settings)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppScreen()
            }
        }
    }
}

@Composable
fun MainAppScreen() {
    val mainViewModel: MainViewModel = viewModel()
    var currentScreen by remember { mutableStateOf(AppScreen.DASHBOARD) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color(0xFF000000),
        bottomBar = {
            BentoBottomNavigationBar(
                currentScreen = currentScreen,
                onScreenSelected = { currentScreen = it }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                AppScreen.DASHBOARD -> DashboardScreen(
                    viewModel = mainViewModel,
                    onNavigateToTerminal = { currentScreen = AppScreen.TERMINAL },
                    onNavigateToCompare = { currentScreen = AppScreen.COMPARE },
                    onNavigateToAi = { currentScreen = AppScreen.AI_ASSISTANT }
                )
                AppScreen.TERMINAL -> TerminalScreen(
                    viewModel = mainViewModel,
                    onNavigateToAi = { currentScreen = AppScreen.AI_ASSISTANT }
                )
                AppScreen.AI_BRAIN -> AiBrainStudioScreen(viewModel = mainViewModel)
                AppScreen.TIMELINE -> BootTimelineScreen(viewModel = mainViewModel)
                AppScreen.COMPARE -> LogCompareScreen(viewModel = mainViewModel)
                AppScreen.DICTIONARY -> SmartParserScreen(viewModel = mainViewModel)
                AppScreen.AI_ASSISTANT -> AiAssistantScreen(viewModel = mainViewModel)
                AppScreen.LIBRARY -> GoodLogLibraryScreen(viewModel = mainViewModel)
                AppScreen.SETTINGS -> SettingsScreen(viewModel = mainViewModel)
            }
        }
    }
}

@Composable
fun BentoBottomNavigationBar(
    currentScreen: AppScreen,
    onScreenSelected: (AppScreen) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .background(Color(0xFF1B1B1F).copy(alpha = 0.95f))
            .border(1.dp, Color(0x1AFFFFFF), RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            .padding(horizontal = 8.dp)
    ) {
        androidx.compose.foundation.lazy.LazyRow(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items(AppScreen.values().size) { idx ->
                val screen = AppScreen.values()[idx]
                val isSelected = currentScreen == screen
                val tintColor = if (isSelected) Color(0xFF06B6D4) else Color(0xFF94A3B8)

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onScreenSelected(screen) }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) Color(0xFF06B6D4).copy(alpha = 0.2f) else Color.Transparent),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = screen.icon,
                            contentDescription = screen.label,
                            tint = tintColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Text(
                        text = screen.label,
                        fontSize = 9.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = tintColor
                    )
                }
            }
        }
    }
}
