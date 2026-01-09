package com.example.constellationapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.constellationapp.screens.HoroscopeScreen
import com.example.constellationapp.screens.ImageScreen
import com.example.constellationapp.screens.ListScreen
import com.example.constellationapp.ui.theme.ConstellationAppTheme
import com.example.constellationapp.screens.StartScreen
import kotlin.system.exitProcess

// 1. 탭 메뉴 정의 (이게 꼭 있어야 에러가 안 납니다!)
enum class AppDestinations(
    val label: String,
    val icon: ImageVector
) {
    ListPage("리스트", Icons.Default.Menu),
    GalleryPage("이미지", Icons.Default.Star),
    HoroscopePage("운세", Icons.Default.DateRange)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ConstellationAppTheme {
                var hasStarted by remember { mutableStateOf(false) }

                Surface(modifier = Modifier.fillMaxSize()) {
                    if (!hasStarted) {
                        StartScreen(onStartClick = { hasStarted = true })
                    } else {
                        ConstellationApp()
                    }
                }
            }
        }
    }
}

@Composable
fun ConstellationApp() {
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.ListPage) }

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            AppDestinations.entries.forEach { destination ->
                item(
                    icon = { Icon(destination.icon, contentDescription = destination.label) },
                    label = { Text(destination.label) },
                    selected = currentDestination == destination,
                    onClick = { currentDestination = destination }
                )
            }
        }
    ) {
        // 선택된 메뉴에 따라 화면 교체
        when (currentDestination) {
            AppDestinations.ListPage -> ListScreen()
            AppDestinations.GalleryPage -> ImageScreen()
            AppDestinations.HoroscopePage -> HoroscopeScreen()
        }
    }
}