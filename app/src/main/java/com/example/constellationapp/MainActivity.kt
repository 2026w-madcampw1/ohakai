package com.example.constellationapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.constellationapp.screens.ConstellationDetailScreen
import com.example.constellationapp.screens.DrawingScreen
import com.example.constellationapp.screens.BirthInputScreen
import com.example.constellationapp.screens.ImageScreen
import com.example.constellationapp.screens.ListScreen
import com.example.constellationapp.ui.theme.ConstellationAppTheme
import com.example.constellationapp.screens.StartScreen

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
                var appStep by rememberSaveable { mutableIntStateOf(0) }
                Surface(modifier = Modifier.fillMaxSize()) {
                    AnimatedContent(targetState = appStep, label="screen_transition"){ targetStep ->
                    when (targetStep) {
                        0 -> StartScreen(onStartClick = { appStep = 1 })
                        1 -> BirthInputScreen(onNextClick = { appStep = 2 })
                        2 -> ConstellationApp()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ConstellationApp() {
    val navController = rememberNavController()
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.Horoscope) }

    Scaffold(
        bottomBar = {
            NavigationBar(
                modifier = Modifier.height(60.dp),
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                tonalElevation = 0.dp
            ) {
                AppDestinations.entries.forEach { destination ->
                    val isSelected = currentDestination == destination
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = {
                            currentDestination = destination
                            navController.navigate(destination.route) {
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = destination.icon,
                                contentDescription = destination.label,
                                tint = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray,
                                modifier = Modifier.offset(y = 4.dp) // 아이콘을 아래로 내려 텍스트와 가깝게 배치
                            )
                        },
                        label = {
                            // 작고 얇은 텍스트 추가
                            Text(
                                text = destination.label,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray,
                                modifier = Modifier.offset(y = 2.dp) // 텍스트도 아주 살짝 내려서 균형을 맞춤
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = Color.Transparent // 선택 시 배경 원 제거하여 더 슬림하게
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController, 
            startDestination = AppDestinations.Horoscope.route,
            modifier = Modifier.padding(innerPadding) // 모든 탭에 동일한 패딩 적용
        ) {
            composable(AppDestinations.Horoscope.route) {
                ListScreen(onItemClick = {
                    navController.navigate("constellationDetail/$it")
                })
            }
            composable(AppDestinations.LuckyItem.route) { ImageScreen() }
            composable(AppDestinations.Drawing.route) { DrawingScreen() }
            composable("constellationDetail/{name}") { backStackEntry ->
                val name = backStackEntry.arguments?.getString("name") ?: ""
                ConstellationDetailScreen(name = name)
            }
        }
    }
}
