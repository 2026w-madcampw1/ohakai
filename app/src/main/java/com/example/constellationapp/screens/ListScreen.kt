package com.example.constellationapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ListScreen() {
    // 임시 별자리 데이터 리스트
    val constellations = listOf("물병자리", "물고기자리", "양자리", "황소자리", "쌍둥이자리", "게자리", "사자자리", "처녀자리", "천칭자리", "전갈자리", "사수자리", "염소자리")

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "별자리 목록", fontSize = 24.sp, modifier = Modifier.padding(bottom = 16.dp))

        // 많은 양의 리스트를 보여줄 때 사용하는 LazyColumn
        LazyColumn {
            items(constellations) { name ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                ) {
                    Text(text = name, modifier = Modifier.padding(16.dp))
                }
            }
        }
    }
}