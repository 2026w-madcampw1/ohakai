package com.example.constellationapp.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.constellationapp.ConstellationData
import java.util.Calendar
import kotlin.random.Random

@Composable
fun ListScreen(onItemClick: (String) -> Unit) {
    val allConstellations = remember {
        val seed = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
        val random = Random(seed)
        listOf(
            "물병자리" to "1/20-2/18", "물고기자리" to "2/19-3/20",
            "양자리" to "3/21-4/19", "황소자리" to "4/20-5/20",
            "쌍둥이자리" to "5/21-6/21", "게자리" to "6/22-7/22",
            "사자자리" to "7/23-8/22", "처녀자리" to "8/23-9/23",
            "천칭자리" to "9/24-10/22", "전갈자리" to "10/23-11/22",
            "사수자리" to "11/23-12/21", "염소자리" to "12/22-1/19"
        ).map { (name, date) ->
            ConstellationData(name, date, random.nextInt(60, 101))
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
            .clickable(onClick = onClick) // Add clickable modifier
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "${rank}위")
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = data.name, modifier = Modifier.weight(1f))
        }
    }
}
