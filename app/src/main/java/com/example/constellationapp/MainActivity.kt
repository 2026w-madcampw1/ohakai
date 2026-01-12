package com.example.constellationapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.constellationapp.screens.*
import com.example.constellationapp.ui.theme.ConstellationAppTheme

enum class AppDestinations(
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    Horoscope("horoscope", "운세", Icons.Default.DateRange),
    LuckyItem("luckyItem", "아이템", Icons.Default.Star),
    Drawing("drawing", "그리기", Icons.Default.Create)
}

class MainActivity : ComponentActivity() {
    private lateinit var dataStoreManager: DataStoreManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // 데이터 저장소에서 온보딩 완료 여부 읽기
    val isOnboardingCompleted by dataStoreManager.isOnboardingCompleted.collectAsState(initial = null)

    val bottomBarRoutes = AppDestinations.entries.map { it.route }
    val showBottomBar = currentRoute in bottomBarRoutes

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    modifier = Modifier.height(60.dp),
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                    tonalElevation = 0.dp
                ) {
                    AppDestinations.entries.forEach { destination ->
                        val isSelected = currentRoute == destination.route
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                if (!isSelected) {
                                    navController.navigate(destination.route) {
                                        popUpTo(AppDestinations.Horoscope.route) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = destination.icon,
                                    contentDescription = destination.label,
                                    tint = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray,
                                    modifier = Modifier.offset(y = 4.dp)
                                )
                            },
                            label = {
                                Text(
                                    text = destination.label,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray,
                                    modifier = Modifier.offset(y = 2.dp)
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent)
                        )
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
                    ListScreen(onItemClick = {
                        navController.navigate("constellationDetail/$it")
                    })
                }
                composable(AppDestinations.LuckyItem.route) { 
                    ImageScreen(dataStoreManager = dataStoreManager, onNavigateToDrawing = {
                        navController.navigate(AppDestinations.Drawing.route) {
                            popUpTo(AppDestinations.Horoscope.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }) 
                }
                composable(AppDestinations.Drawing.route) { DrawingScreen(dataStoreManager) }

                composable("constellationDetail/{name}") { backStackEntry ->
                    val name = backStackEntry.arguments?.getString("name") ?: ""
                    ConstellationDetailScreen(name = name)
                }
            }
        }
    }
}
