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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ConstellationAppTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    ConstellationApp()
                }
            }
        }
    }
}

@Composable
fun ConstellationApp() {
    val navController = rememberNavController()
    // 현재 네비게이션 상태 추적
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // 하단 네비게이션 바를 표시할 경로들 정의
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
                                        // 스택에 쌓이지 않도록 이전 목적지 팝업
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
        NavHost(
            navController = navController,
            startDestination = "start",
            modifier = Modifier.padding(innerPadding)
        ) {
            // 초기 시작 화면
            composable("start") {
                StartScreen(onStartClick = { navController.navigate("birthInput") })
            }

            // 생년월일 입력 화면
            composable("birthInput") {
                BirthInputScreen(onNextClick = {
                    // 입력 완료 후 메인 화면으로 이동 (시작/입력 화면은 스택에서 제거)
                    navController.navigate(AppDestinations.Horoscope.route) {
                        popUpTo("start") { inclusive = true }
                    }
                })
            }

            // 메인 탭 화면들
            composable(AppDestinations.Horoscope.route) {
                ListScreen(onItemClick = {
                    navController.navigate("constellationDetail/$it")
                })
            }
            composable(AppDestinations.LuckyItem.route) { ImageScreen() }
            composable(AppDestinations.Drawing.route) { DrawingScreen() }

            // 상세 화면
            composable("constellationDetail/{name}") { backStackEntry ->
                val name = backStackEntry.arguments?.getString("name") ?: ""
                ConstellationDetailScreen(name = name)
            }
        }
    }
}
