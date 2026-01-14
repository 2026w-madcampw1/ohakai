package com.example.constellationapp

import android.util.Log
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.constellationapp.screens.*
import com.example.constellationapp.ui.theme.ConstellationAppTheme
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.constellationapp.viewmodels.HoroscopeViewModel

enum class AppDestinations(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    Horoscope("horoscope", "순위", Icons.Filled.Assessment, Icons.Outlined.Assessment),
    LuckyItem("luckyItem", "운세", Icons.Filled.AutoAwesome, Icons.Outlined.AutoAwesome),
    Drawing("drawing", "그리기", Icons.Filled.Brush, Icons.Outlined.Brush),
    //Settings("settings_placeholder", "설정", Icons.Filled.Settings, Icons.Outlined.Settings)
}

class MainActivity : ComponentActivity() {
    private lateinit var dataStoreManager: DataStoreManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("KEYCHECK", "len=${BuildConfig.OPENAI_API_KEY.length}")
        dataStoreManager = DataStoreManager(this)
        enableEdgeToEdge()
        setContent {
            ConstellationAppTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    ConstellationApp(dataStoreManager)
                }
            }
        }
    }
}

@Composable
fun ConstellationApp(dataStoreManager: DataStoreManager) {
    val navController = rememberNavController()
    val horoscopeViewModel: HoroscopeViewModel = viewModel()

    LaunchedEffect(Unit) {
        horoscopeViewModel.fetchHoroscopes()
    }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val isOnboardingCompleted by dataStoreManager.isOnboardingCompleted.collectAsState(initial = null)
    val todayIndices by dataStoreManager.todayLuckyIndices.collectAsState(initial = emptyList())
    val hiddenSlots by dataStoreManager.hiddenItemIndices.collectAsState(initial = emptySet())
    val bottomBarRoutes = AppDestinations.entries.map { it.route }
    val showBottomBar = currentRoute != null && (
        bottomBarRoutes.any { currentRoute.startsWith(it) } || 
        currentRoute.startsWith("drawing/")
    )

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                Column {
                    HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
                    NavigationBar(
                        modifier = Modifier.height(80.dp),
                        containerColor = Color.White,
                        tonalElevation = 0.dp
                    ) {
                        AppDestinations.entries.forEach { destination ->
                            val isSelected = currentRoute?.startsWith(destination.route) == true || 
                                            (destination == AppDestinations.Drawing && currentRoute?.startsWith("drawing/") == true)
                            
                            NavigationBarItem(
                                selected = isSelected,
                                onClick = {
                                    if (!isSelected && !destination.route.contains("placeholder")) {
                                        navController.navigate(destination.route) {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                },
                                icon = {
                                    Icon(
                                        imageVector = if (isSelected) destination.selectedIcon else destination.unselectedIcon,
                                        contentDescription = destination.label,
                                        modifier = Modifier.size(26.dp)
                                    )
                                },
                                label = {
                                    Text(
                                        text = destination.label,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Color(0xFF3B82F6),
                                    selectedTextColor = Color(0xFF3B82F6),
                                    unselectedIconColor = Color(0xFF94A3B8),
                                    unselectedTextColor = Color(0xFF94A3B8),
                                    indicatorColor = Color.Transparent
                                )
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        if (isOnboardingCompleted != null) {
            val startRoute = if (isOnboardingCompleted == true) AppDestinations.Horoscope.route else "start"
            NavHost(
                navController = navController,
                startDestination = startRoute,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable("start") {
                    StartScreen(onStartClick = { navController.navigate("birthInput") })
                }
                composable("birthInput") {
                    BirthInputScreen(dataStoreManager = dataStoreManager, onNextClick = {
                        navController.navigate(AppDestinations.Horoscope.route) {
                            popUpTo("start") { inclusive = true }
                        }
                    })
                }
                composable(AppDestinations.Horoscope.route) {
                    ListScreen(onItemClick = { name, content ->
                        Log.d("MainActivity", "Item clicked! Navigating to luckyItem?zodiac=$name")
                        val encodedContent = URLEncoder.encode(content, StandardCharsets.UTF_8.toString())
                        navController.navigate("${AppDestinations.LuckyItem.route}?zodiac=$name&content=$encodedContent") {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                        }
                    })
                }
                composable(
                    route = "${AppDestinations.LuckyItem.route}?zodiac={zodiac}&content={content}",
                    arguments = listOf(
                        navArgument("zodiac") { 
                            type = NavType.StringType
                            nullable = true
                            defaultValue = null
                        },
                        navArgument("content") {
                            type = NavType.StringType
                            nullable = true
                            defaultValue = null
                        }
                    )
                ) { backStackEntry ->
                    val horoscopes by horoscopeViewModel.horoscopes.collectAsState()
                    val zodiac = backStackEntry.arguments?.getString("zodiac")
                    // Note: content is also available if needed, but for now we focus on zodiac focus
                    
                    ImageScreen(
                        dataStoreManager = dataStoreManager,
                        horoscopes = horoscopes,
                        initialZodiac = zodiac,
                        onNavigateToDrawing = { index ->
                            navController.navigate("drawing/$index") {
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
                composable(
                    route = "drawing/{itemIndex}",
                    arguments = listOf(navArgument("itemIndex") { type = NavType.IntType })
                ) { backStackEntry ->
                    val index = backStackEntry.arguments?.getInt("itemIndex") ?: 0
                    DrawingScreen(
                        dataStoreManager = dataStoreManager, 
                        initialItemIndex = index, 
                        onBackClick = { navController.popBackStack() }
                    )
                }
                composable(AppDestinations.Drawing.route) {
                    val randomStartIndex = remember(todayIndices, hiddenSlots) {
                        if (todayIndices.isNotEmpty()) {
                            val lockedItemIndices = if (hiddenSlots.isNotEmpty()) {
                                hiddenSlots.mapNotNull { slotIdx -> todayIndices.getOrNull(slotIdx) }
                            } else {
                                todayIndices
                            }
                            lockedItemIndices.randomOrNull() ?: todayIndices.firstOrNull() ?: 0
                        } else {
                            0
                        }
                    }
                    DrawingScreen(
                        dataStoreManager = dataStoreManager, 
                        initialItemIndex = randomStartIndex, 
                        onBackClick = { navController.popBackStack() }
                    )
                }
                composable("constellationDetail/{name}/{content}") { backStackEntry ->
                    val name = backStackEntry.arguments?.getString("name") ?: ""
                    val content = backStackEntry.arguments?.getString("content") ?: ""
                    ConstellationDetailScreen(name = name, content = content)
                }
            }
        }
    }
}
