package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.LibraryBooks
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Compare
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.DeveloperBoard
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.IntegrationInstructions
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.Usb
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.AboutScreen
import com.example.ui.screens.AiAssistantScreen
import com.example.ui.screens.AiBrainStudioScreen
import com.example.ui.screens.BootTimelineScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.GoodLogLibraryScreen
import com.example.ui.screens.LogCompareScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SmartParserScreen
import com.example.ui.screens.TerminalScreen
import com.example.ui.screens.UserGuideScreen
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.AppBackground
import com.example.ui.theme.CardBackground
import com.example.ui.theme.CardBorder
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextMutedColor
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarningYellow
import com.example.ui.viewmodel.MainViewModel
import kotlinx.coroutines.launch

enum class AppScreen(
    val label: String,
    val icon: ImageVector,
    val category: String = "Core"
) {
    DASHBOARD("Home", Icons.Default.Dashboard, "Main"),
    TERMINAL("UART Terminal", Icons.Default.DeveloperBoard, "Main"),
    COMPARE("Log Compare", Icons.Default.Compare, "Main"),
    AI_ASSISTANT("AI Repair", Icons.Default.AutoAwesome, "Main"),
    
    // Advanced Tools
    AI_BRAIN("Brain Studio", Icons.Default.Psychology, "Advanced Tools"),
    TIMELINE("Boot Timeline", Icons.Default.Timeline, "Advanced Tools"),
    DICTIONARY("UART Dictionary", Icons.Default.IntegrationInstructions, "Advanced Tools"),
    LIBRARY("Good Log Database", Icons.AutoMirrored.Filled.LibraryBooks, "Advanced Tools"),
    
    // Settings & About
    USER_GUIDE("User Manual", Icons.AutoMirrored.Filled.MenuBook, "System"),
    SETTINGS("Settings", Icons.Default.Settings, "System"),
    ABOUT("About", Icons.Default.Info, "System")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val mainViewModel: MainViewModel by viewModels()
        setContent {
            val isDark by mainViewModel.isDarkTheme.collectAsState()
            com.example.ui.theme.ThemeState.isDark = isDark
            MyApplicationTheme {
                MainAppScreen(mainViewModel)
            }
        }
    }
}

@Composable
fun MainAppScreen(mainViewModel: MainViewModel) {
    var currentScreen by remember { mutableStateOf(AppScreen.DASHBOARD) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val connectionState by mainViewModel.usbManager.connectionState.collectAsState()
    val isConnected = connectionState.isConnected
    val isSimulating = connectionState.isSimulationActive

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = CardBackground,
                drawerContentColor = TextPrimary,
                modifier = Modifier
                    .width(280.dp)
                    .fillMaxHeight()
                    .statusBarsPadding()
                    .navigationBarsPadding()
            ) {
                DrawerHeader(
                    isConnected = isConnected,
                    isSimulating = isSimulating
                )

                HorizontalDivider(color = CardBorder, modifier = Modifier.padding(vertical = 8.dp))

                val drawerScrollState = rememberScrollState()
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp)
                        .verticalScroll(drawerScrollState),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // MAIN SECTION
                    Text(
                        text = "CORE DIAGNOSTICS",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = AccentCyan,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )

                    listOf(AppScreen.DASHBOARD, AppScreen.TERMINAL, AppScreen.COMPARE, AppScreen.AI_ASSISTANT).forEach { screen ->
                        DrawerItem(
                            screen = screen,
                            isSelected = currentScreen == screen,
                            onClick = {
                                currentScreen = screen
                                scope.launch { drawerState.close() }
                            }
                        )
                    }

                    HorizontalDivider(color = CardBorder, modifier = Modifier.padding(vertical = 6.dp))

                    // ADVANCED TOOLS SECTION
                    Text(
                        text = "ADVANCED TOOLS",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = AccentCyan,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )

                    listOf(AppScreen.AI_BRAIN, AppScreen.TIMELINE, AppScreen.DICTIONARY, AppScreen.LIBRARY).forEach { screen ->
                        DrawerItem(
                            screen = screen,
                            isSelected = currentScreen == screen,
                            onClick = {
                                currentScreen = screen
                                scope.launch { drawerState.close() }
                            }
                        )
                    }

                    HorizontalDivider(color = CardBorder, modifier = Modifier.padding(vertical = 6.dp))

                    // SYSTEM & ABOUT SECTION
                    Text(
                        text = "SETTINGS & INFO",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextMutedColor,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )

                    listOf(AppScreen.USER_GUIDE, AppScreen.SETTINGS, AppScreen.ABOUT).forEach { screen ->
                        DrawerItem(
                            screen = screen,
                            isSelected = currentScreen == screen,
                            onClick = {
                                currentScreen = screen
                                scope.launch { drawerState.close() }
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = AppBackground,
            topBar = {
                TopAppBarWithDrawer(
                    currentScreen = currentScreen,
                    isConnected = isConnected,
                    isSimulating = isSimulating,
                    onOpenDrawer = { scope.launch { drawerState.open() } }
                )
            },
            bottomBar = {
                CompactBottomNavigationBar(
                    currentScreen = currentScreen,
                    onScreenSelected = { currentScreen = it },
                    onOpenDrawer = { scope.launch { drawerState.open() } }
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
                    AppScreen.COMPARE -> LogCompareScreen(viewModel = mainViewModel)
                    AppScreen.AI_ASSISTANT -> AiAssistantScreen(viewModel = mainViewModel)
                    AppScreen.AI_BRAIN -> AiBrainStudioScreen(viewModel = mainViewModel)
                    AppScreen.TIMELINE -> BootTimelineScreen(viewModel = mainViewModel)
                    AppScreen.DICTIONARY -> SmartParserScreen(viewModel = mainViewModel)
                    AppScreen.LIBRARY -> GoodLogLibraryScreen(viewModel = mainViewModel)
                    AppScreen.USER_GUIDE -> UserGuideScreen(viewModel = mainViewModel)
                    AppScreen.SETTINGS -> SettingsScreen(
                        viewModel = mainViewModel,
                        onNavigateToAbout = { currentScreen = AppScreen.ABOUT },
                        onNavigateToGuide = { currentScreen = AppScreen.USER_GUIDE }
                    )
                    AppScreen.ABOUT -> AboutScreen(viewModel = mainViewModel)
                }
            }
        }
    }
}

@Composable
fun DrawerHeader(
    isConnected: Boolean,
    isSimulating: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(AccentCyan.copy(alpha = 0.12f), CardBackground)
                )
            )
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(AccentCyan),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Terminal,
                    contentDescription = null,
                    tint = AppBackground,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "UART PRO AI",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "Motherboard Repair Tool",
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(
                    when {
                        isConnected -> SuccessGreen.copy(alpha = 0.15f)
                        isSimulating -> WarningYellow.copy(alpha = 0.15f)
                        else -> ErrorRed.copy(alpha = 0.15f)
                    }
                )
                .border(
                    1.dp,
                    when {
                        isConnected -> SuccessGreen
                        isSimulating -> WarningYellow
                        else -> ErrorRed
                    },
                    RoundedCornerShape(8.dp)
                )
                .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Usb,
                    contentDescription = null,
                    tint = when {
                        isConnected -> SuccessGreen
                        isSimulating -> WarningYellow
                        else -> ErrorRed
                    },
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = when {
                        isConnected -> "USB HARDWARE ONLINE"
                        isSimulating -> "SIMULATION ACTIVE"
                        else -> "DISCONNECTED"
                    },
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = when {
                        isConnected -> SuccessGreen
                        isSimulating -> WarningYellow
                        else -> ErrorRed
                    }
                )
            }
        }
    }
}

@Composable
fun DrawerItem(
    screen: AppScreen,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    NavigationDrawerItem(
        label = {
            Text(
                text = screen.label,
                fontSize = 13.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )
        },
        icon = {
            Icon(
                imageVector = screen.icon,
                contentDescription = screen.label,
                modifier = Modifier.size(20.dp)
            )
        },
        selected = isSelected,
        onClick = onClick,
        colors = NavigationDrawerItemDefaults.colors(
            selectedContainerColor = AccentCyan.copy(alpha = 0.15f),
            selectedIconColor = AccentCyan,
            selectedTextColor = AccentCyan,
            unselectedContainerColor = Color.Transparent,
            unselectedIconColor = TextSecondary,
            unselectedTextColor = TextPrimary
        ),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.height(44.dp)
    )
}

@Composable
fun TopAppBarWithDrawer(
    currentScreen: AppScreen,
    isConnected: Boolean,
    isSimulating: Boolean,
    onOpenDrawer: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .background(CardBackground)
            .border(1.dp, CardBorder, RoundedCornerShape(0.dp))
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onOpenDrawer) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Menu Drawer",
                        tint = TextPrimary
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                Icon(
                    imageVector = currentScreen.icon,
                    contentDescription = null,
                    tint = AccentCyan,
                    modifier = Modifier.size(20.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = currentScreen.label,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        when {
                            isConnected -> SuccessGreen.copy(alpha = 0.15f)
                            isSimulating -> WarningYellow.copy(alpha = 0.15f)
                            else -> CardBackground
                        }
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = when {
                        isConnected -> "READY"
                        isSimulating -> "SIM"
                        else -> "OFFLINE"
                    },
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = when {
                        isConnected -> SuccessGreen
                        isSimulating -> WarningYellow
                        else -> TextMutedColor
                    }
                )
            }
        }
    }
}

@Composable
fun CompactBottomNavigationBar(
    currentScreen: AppScreen,
    onScreenSelected: (AppScreen) -> Unit,
    onOpenDrawer: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(CardBackground)
            .border(1.dp, CardBorder, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            .navigationBarsPadding()
            .padding(vertical = 6.dp, horizontal = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val primaryScreens = listOf(
                AppScreen.DASHBOARD,
                AppScreen.TERMINAL,
                AppScreen.COMPARE,
                AppScreen.AI_ASSISTANT
            )

            primaryScreens.forEach { screen ->
                val isSelected = currentScreen == screen
                val tintColor = if (isSelected) AccentCyan else TextSecondary

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onScreenSelected(screen) }
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) AccentCyan.copy(alpha = 0.15f) else Color.Transparent),
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
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = tintColor
                    )
                }
            }

            // More Menu Button (Opens Drawer)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onOpenDrawer() }
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color.Transparent),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "More Menu",
                        tint = TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Text(
                    text = "More",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Normal,
                    color = TextSecondary
                )
            }
        }
    }
}
