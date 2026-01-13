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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Star
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
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val isOnboardingCompleted by dataStoreManager.isOnboardingCompleted.collectAsState(initial = null)
    
    // 오늘의 4개 아이템 인덱스 리스트 (실제 아이템 번호들)
    val todayIndices by dataStoreManager.todayLuckyIndices.collectAsState(initial = emptyList())
    // 현재 잠겨있는 슬롯 번호들 (0, 1, 2, 3 중 일부)
    val hiddenSlots by dataStoreManager.hiddenItemIndices.collectAsState(initial = emptySet())

    val bottomBarRoutes = AppDestinations.entries.map { it.route }
    val showBottomBar = currentRoute in bottomBarRoutes || currentRoute?.startsWith("drawing/") == true

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    modifier = Modifier.height(60.dp),
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                    tonalElevation = 0.dp
                ) {
                    AppDestinations.entries.forEach { destination ->
                        val isSelected = currentRoute == destination.route || 
                                        (destination == AppDestinations.Drawing && currentRoute?.startsWith("drawing/") == true)
                        
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                if (!isSelected) {
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
                    ListScreen(onItemClick = { name, content ->
                        val encodedContent = URLEncoder.encode(content, StandardCharsets.UTF_8.toString())
                        navController.navigate("constellationDetail/$name/$encodedContent")
                    })
                }
                
                composable(AppDestinations.LuckyItem.route) { 
                    ImageScreen(dataStoreManager = dataStoreManager, onNavigateToDrawing = { index ->
                        navController.navigate("${AppDestinations.Drawing.route}/$index") {
                            launchSingleTop = true
                            restoreState = true
                        }
                    }) 
                }

                // 특정 아이템 번호를 받아서 들어가는 경우
                composable(
                    route = "${AppDestinations.Drawing.route}/{itemIndex}",
                    arguments = listOf(navArgument("itemIndex") { type = NavType.IntType })
                ) { backStackEntry ->
                    val index = backStackEntry.arguments?.getInt("itemIndex") ?: 0
                    DrawingScreen(
                        dataStoreManager = dataStoreManager, 
                        initialItemIndex = index, 
                        onBackClick = { navController.popBackStack() }
                    )
                }

                // [수정] 탭 버튼 클릭해서 들어가는 경우: 미해금 아이템 중 랜덤 선택
                composable(AppDestinations.Drawing.route) {
                    val randomStartIndex = remember(todayIndices, hiddenSlots) {
                        if (todayIndices.isNotEmpty()) {
                            // 아직 해금 안 된 슬롯(hiddenSlots)에 해당하는 실제 아이템 번호들 필터링
                            val lockedItemIndices = if (hiddenSlots.isNotEmpty()) {
                                hiddenSlots.mapNotNull { slotIdx -> todayIndices.getOrNull(slotIdx) }
                            } else {
                                todayIndices // 다 해금했으면 전체 4개 중 랜덤
                            }
                            lockedItemIndices.random()
                        } else {
                            0 // 데이터 로딩 전 기본값
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
