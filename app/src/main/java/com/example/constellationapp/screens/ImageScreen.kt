package com.example.constellationapp.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.constellationapp.R

@Composable
fun ImageScreen() {
    // 여기에 본인이 drawable 폴더에 넣은 이미지 이름을 적으세요
    // 일단 테스트용으로 하나만 넣었습니다. 이미지를 더 넣으면 리스트에 추가하세요.
    val imageList = listOf(
        "양자리" to R.drawable.star_aries, // 임시 이미지
        "황소자리" to R.drawable.star_taurus
    )

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "별자리 갤러리", modifier = Modifier.padding(bottom = 16.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2), // 2열로 보여주기
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(imageList) { item ->
                Card {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Image(
                            painter = painterResource(id = item.second),
                            contentDescription = null,
                            modifier = Modifier.size(150.dp).padding(8.dp)
                        )
                        Text(text = item.first, modifier = Modifier.padding(8.dp))
                    }
                }
            }
        }
    }
}