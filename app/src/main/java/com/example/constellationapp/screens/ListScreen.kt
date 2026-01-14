package com.example.constellationapp.screens

import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.constellationapp.ConstellationData
import com.example.constellationapp.R
import com.example.constellationapp.viewmodels.HoroscopeViewModel
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ListScreen(
    onItemClick: (String, String) -> Unit,
    viewModel: HoroscopeViewModel = viewModel(viewModelStoreOwner = LocalContext.current as ComponentActivity)
) {
    val constellations by viewModel.horoscopes.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val userInfo by viewModel.userInfo.collectAsState(initial = null)

    LaunchedEffect(Unit) {
        viewModel.fetchHoroscopes()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFBFBFF))
    ) {
        BackgroundPattern()

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            
            // Personalized Ranking Banner & Date
            userInfo?.let { user ->
                val userRank = constellations.find { it.name == user.zodiac }?.rank
                if (userRank != null) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp, horizontal = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val annotatedText = buildAnnotatedString {
                            withStyle(style = SpanStyle(color = Color(0xFF3B82F6), fontWeight = FontWeight.Bold)) {
                                append(user.name)
                            }
                            withStyle(style = SpanStyle(color = Color(0xFF1A1C1E), fontWeight = FontWeight.Bold)) {
                                append("님의 오늘 별자리 순위는 \n")
                            }
                            withStyle(style = SpanStyle(color = Color(0xFF3B82F6), fontWeight = FontWeight.Bold)) {
                                append("${userRank}위")
                            }
                            withStyle(style = SpanStyle(color = Color(0xFF1A1C1E), fontWeight = FontWeight.Bold)) {
                                append(" 입니다")
                            }
                        }
                        Text(
                            text = annotatedText,
                            fontSize = 20.sp,
                            textAlign = TextAlign.Center
                        )
                        
                        val currentDate = SimpleDateFormat("yyyy년 M월 d일 EEEE", Locale.KOREAN).format(Date())
                        Text(
                            text = currentDate,
                            fontSize = 12.sp,
                            color = Color(0xFF94A3B8),
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.Center
            ) {
                if (isLoading && constellations.isEmpty()) {
                    CircularProgressIndicator(color = Color(0xFF6C63FF))
                } else if (constellations.size == 1 && constellations.first().rank == 0) {
                    val errorData = constellations.first()
                    Text(
                        text = errorData.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item { Spacer(modifier = Modifier.height(8.dp)) }
                        items(constellations.sortedBy { it.rank }) { data ->
                            RankingCardRedesign(
                                data = data, 
                                isUserZodiac = data.name == userInfo?.zodiac,
                                onClick = {
                                    onItemClick(data.name, data.content.lines().filter { it.isNotBlank() }.joinToString("\n"))
                                }
                            )
                        }
                        item { Spacer(modifier = Modifier.height(24.dp)) }
                    }
                }
            }
        }
    }
}

@Composable
fun BackgroundPattern() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val dotRadius = 1.dp.toPx()
        val spacing = 40.dp.toPx()
        val color = Color(0xFFE0E0FF).copy(alpha = 0.5f)
        
        for (x in 0..size.width.toInt() step spacing.toInt()) {
            for (y in 0..size.height.toInt() step spacing.toInt()) {
                drawCircle(
                    color = color,
                    radius = dotRadius,
                    center = Offset(x.toFloat(), y.toFloat())
                )
            }
        }
    }
}


@Composable
fun RankingCardRedesign(
    data: ConstellationData, 
    isUserZodiac: Boolean,
    onClick: () -> Unit
) {
    val rankCircleColor = when (data.rank) {
        1 -> Color(0xFFFEF9C3) // Yellow
        2 -> Color(0xFFE0F2FE) // Blue
        else -> Color(0xFFF1F5F9) // Gray
    }
    
    val rankTextColor = when (data.rank) {
        1 -> Color(0xFFB45309)
        2 -> Color(0xFF0369A1)
        else -> Color(0xFF64748B)
    }

    val iconRes = when (data.name) {
        "양자리" -> R.drawable.icon_zodiac_1
        "황소자리" -> R.drawable.icon_zodiac_2
        "쌍둥이자리" -> R.drawable.icon_zodiac_3
        "게자리" -> R.drawable.icon_zodiac_4
        "사자자리" -> R.drawable.icon_zodiac_5
        "처녀자리" -> R.drawable.icon_zodiac_6
        "천칭자리" -> R.drawable.icon_zodiac_7
        "전갈자리" -> R.drawable.icon_zodiac_8
        "사수자리" -> R.drawable.icon_zodiac_9
        "염소자리" -> R.drawable.icon_zodiac_10
        "물병자리" -> R.drawable.icon_zodiac_11
        "물고기자리" -> R.drawable.icon_zodiac_12
        else -> R.drawable.ic_launcher_foreground
    }

    val iconBgColor = when (data.name) {
        "양자리", "황소자리", "사자자리", "전갈자리" -> Color(0xFFFFF7ED)
        "쌍둥이자리", "게자리", "처녀자리", "천칭자리" -> Color(0xFFF8FAFC)
        "사수자리", "염소자리", "물병자리", "물고기자리" -> Color(0xFFEFF6FF)
        else -> Color.White
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF1F3F9)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Rank Circle
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(rankCircleColor, androidx.compose.foundation.shape.CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${data.rank}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = rankTextColor
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Zodiac Icon
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(iconBgColor, RoundedCornerShape(16.dp))
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = iconRes),
                    contentDescription = data.name,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.width(20.dp)) // Increased spacing

            // Info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = data.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )
                    if (isUserZodiac) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .background(Color(0xFFDBEAFE), RoundedCornerShape(4.dp))
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "MY",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2563EB)
                            )
                        }
                    }
                }
                Text(
                    text = data.date,
                    fontSize = 14.sp,
                    color = Color(0xFF3B82F6),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
