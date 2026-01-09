package com.example.constellationapp.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.constellationapp.ConstellationData
import com.example.constellationapp.R
import java.util.Calendar
import kotlin.random.Random

@Composable
fun ListScreen(onItemClick: (String) -> Unit) {
    val allConstellations = remember {
        val seed = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
        val random = Random(seed)
        
        // 별자리와 해당 이미지 리소스를 매핑 (순서대로 1~12)
        val rawData = listOf(
            Triple("물병자리", "1/20-2/18", R.drawable.icon_aquarius),
            Triple("물고기자리", "2/19-3/20", R.drawable.icon_pisces),
            Triple("양자리", "3/21-4/19", R.drawable.icon_aries),
            Triple("황소자리", "4/20-5/20", R.drawable.icon_taurus),
            Triple("쌍둥이자리", "5/21-6/21", R.drawable.icon_gemini),
            Triple("게자리", "6/22-7/22", R.drawable.icon_cancer),
            Triple("사자자리", "7/23-8/22", R.drawable.icon_leo),
            Triple("처녀자리", "8/23-9/23", R.drawable.icon_virgo),
            Triple("천칭자리", "9/24-10/22", R.drawable.icon_libra),
            Triple("전갈자리", "10/23-11/22", R.drawable.icon_scorpius),
            Triple("사수자리", "11/23-12/21", R.drawable.icon_sagittarius),
            Triple("염소자리", "12/22-1/19", R.drawable.icon_capricorn)
        )

        rawData.map { (name, date, imgRes) ->
            ConstellationData(name, date, random.nextInt(60, 101), imgRes)
        }.sortedByDescending { it.score }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "오늘의 별자리 순위", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            itemsIndexed(allConstellations) { index, data ->
                RankingCard(rank = index + 1, data = data, onClick = { onItemClick(data.name) })
            }
        }
    }
}

@Composable
fun RankingCard(rank: Int, data: ConstellationData, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "${rank}위", modifier = Modifier.width(40.dp))
            
            // 별자리 이미지 추가
            Image(
                painter = painterResource(id = data.imageResId),
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .padding(end = 12.dp)
            )
            
            Column(modifier = Modifier.weight(1f)) {
                Text(text = data.name, fontWeight = FontWeight.Bold)
                Text(text = data.date, fontSize = 12.sp)
            }
            
            Text(text = "${data.score}점", fontSize = 14.sp)
        }
    }
}
