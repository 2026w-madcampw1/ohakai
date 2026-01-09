package com.example.constellationapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
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
    Horoscope("horoscope", "오늘의 운세", Icons.Default.DateRange),
    LuckyItem("luckyItem", "행운의 아이템", Icons.Default.Star),
    Drawing("drawing", "별자리 그리기", Icons.Default.Create)
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

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            AppDestinations.entries.forEach { destination ->
                item(
                    icon = { Icon(destination.icon, contentDescription = destination.label) },
                    label = { Text(destination.label) },
                    selected = currentDestination == destination,
                    onClick = {
                        currentDestination = destination
                        navController.navigate(destination.route) {
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) {
        NavHost(navController = navController, startDestination = AppDestinations.Horoscope.route) {
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
