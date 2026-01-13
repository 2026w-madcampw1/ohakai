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

@Composable
fun ListScreen(
    onItemClick: (String, String) -> Unit,
    viewModel: HoroscopeViewModel = viewModel(viewModelStoreOwner = LocalContext.current as ComponentActivity)
) {
    val constellations by viewModel.horoscopes.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchHoroscopes()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "오늘의 별자리 운세",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading && constellations.isEmpty()) {
                CircularProgressIndicator()
            } else if (constellations.size == 1 && constellations.first().rank == 0) {
                val errorData = constellations.first()
                Text(
                    text = errorData.name, // "데이터를 가져오는 데 실패했습니다."
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(constellations) { data ->
                        RankingCard(data = data, onClick = {
                            onItemClick(data.name, data.content)
                        })
                    }
                }
            }
        }
    }
}

@Composable
fun RankingCard(data: ConstellationData, onClick: () -> Unit) {
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

    val rankText = when (data.rank) {
        1 -> "🥇 1위"
        2 -> "🥈 2위"
        3 -> "🥉 3위"
        else -> "${data.rank}위"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.width(50.dp)) {
                Text(
                    text = rankText,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Text(
                text = data.name,
                modifier = Modifier.weight(1f),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = data.name,
                modifier = Modifier.size(40.dp)
            )
        }
    }
}
