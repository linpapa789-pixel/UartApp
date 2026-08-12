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
import com.example.ui.theme.MyApplicationTheme
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
    AI_ASSISTANT("AI Repair Pro", Icons.Default.AutoAwesome, "Main"),
    
    // Tools & Knowledge Base in Drawer
    AI_BRAIN("AI Brain Studio", Icons.Default.Psychology, "Diagnostics"),
    TIMELINE("Boot Timeline", Icons.Default.Timeline, "Diagnostics"),
    DICTIONARY("UART Dictionary", Icons.Default.IntegrationInstructions, "Knowledge"),
    LIBRARY("Good Log Reference", Icons.AutoMirrored.Filled.LibraryBooks, "Knowledge"),
    
    // System & Manual
    USER_GUIDE("အသုံးပြုပုံ လမ်းညွှန်", Icons.AutoMirrored.Filled.MenuBook, "Help"),
    SETTINGS("Settings", Icons.Default.Settings, "System"),
    ABOUT("About App", Icons.Default.Info, "System")
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
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val connectionState by mainViewModel.usbManager.connectionState.collectAsState()
    val isConnected = connectionState.isConnected
    val isSimulating = connectionState.isSimulationActive

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = Color(0xFF121215),
                drawerContentColor = Color.White,
                modifier = Modifier
                    .width(300.dp)
                    .fillMaxHeight()
                    .statusBarsPadding()
                    .navigationBarsPadding()
            ) {
                DrawerHeader(
                    isConnected = isConnected,
                    isSimulating = isSimulating
                )

                HorizontalDivider(color = Color(0x1AFFFFFF), modifier = Modifier.padding(vertical = 8.dp))

                val drawerScrollState = rememberScrollState()
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp)
                        .verticalScroll(drawerScrollState),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "PRIMARY DIAGNOSTICS",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF06B6D4),
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

                    HorizontalDivider(color = Color(0x1AFFFFFF), modifier = Modifier.padding(vertical = 6.dp))

                    Text(
                        text = "AI & DIAGNOSTIC TOOLS",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF8B5CF6),
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

                    HorizontalDivider(color = Color(0x1AFFFFFF), modifier = Modifier.padding(vertical = 6.dp))

                    Text(
                        text = "HELP & SYSTEM",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF10B981),
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
            containerColor = Color(0xFF000000),
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
                    colors = listOf(Color(0xFF06B6D4).copy(alpha = 0.15f), Color(0xFF121215))
                )
            )
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF06B6D4)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Terminal,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "UART PRO AI",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Boot Log Analyzer v3.5",
                    fontSize = 11.sp,
                    color = Color(0xFF94A3B8)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(
                    when {
                        isConnected -> Color(0xFF10B981).copy(alpha = 0.2f)
                        isSimulating -> Color(0xFF06B6D4).copy(alpha = 0.2f)
                        else -> Color(0xFFEF4444).copy(alpha = 0.2f)
                    }
                )
                .border(
                    1.dp,
                    when {
                        isConnected -> Color(0xFF10B981)
                        isSimulating -> Color(0xFF06B6D4)
                        else -> Color(0xFFEF4444)
                    },
                    RoundedCornerShape(6.dp)
                )
                .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Usb,
                    contentDescription = null,
                    tint = when {
                        isConnected -> Color(0xFF10B981)
                        isSimulating -> Color(0xFF06B6D4)
                        else -> Color(0xFFEF4444)
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
                        isConnected -> Color(0xFF10B981)
                        isSimulating -> Color(0xFF06B6D4)
                        else -> Color(0xFFEF4444)
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
            selectedContainerColor = Color(0xFF06B6D4).copy(alpha = 0.2f),
            selectedIconColor = Color(0xFF06B6D4),
            selectedTextColor = Color(0xFF06B6D4),
            unselectedContainerColor = Color.Transparent,
            unselectedIconColor = Color(0xFF94A3B8),
            unselectedTextColor = Color(0xFFE2E8F0)
        ),
        shape = RoundedCornerShape(8.dp),
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
            .background(Color(0xFF121215))
            .border(1.dp, Color(0x1AFFFFFF), RoundedCornerShape(0.dp))
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
                        tint = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                Icon(
                    imageVector = currentScreen.icon,
                    contentDescription = null,
                    tint = Color(0xFF06B6D4),
                    modifier = Modifier.size(20.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = currentScreen.label,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        when {
                            isConnected -> Color(0xFF10B981).copy(alpha = 0.2f)
                            isSimulating -> Color(0xFF06B6D4).copy(alpha = 0.2f)
                            else -> Color(0xFF27272A)
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
                        isConnected -> Color(0xFF10B981)
                        isSimulating -> Color(0xFF06B6D4)
                        else -> Color(0xFF94A3B8)
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
            .background(Color(0xFF121215))
            .border(1.dp, Color(0x1AFFFFFF), RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
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
                val tintColor = if (isSelected) Color(0xFF06B6D4) else Color(0xFF94A3B8)

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
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier.size(18.dp)
                    )
                }
                Text(
                    text = "More",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF94A3B8)
                )
            }
        }
    }
}
