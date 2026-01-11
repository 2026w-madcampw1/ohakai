package com.example.constellationapp.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
    val imageList = listOf(
        "행운의 아이템 1" to R.drawable.lucky_item1,
        "행운의 아이템 2" to R.drawable.lucky_item2,
        "행운의 아이템 3" to R.drawable.lucky_item3,
        "행운의 아이템 4" to R.drawable.lucky_item4,
        "행운의 아이템 5" to R.drawable.lucky_item5,
        "행운의 아이템 6" to R.drawable.lucky_item6,
        "행운의 아이템 7" to R.drawable.lucky_item7,
        "행운의 아이템 8" to R.drawable.lucky_item8,
        "행운의 아이템 9" to R.drawable.lucky_item9,
        "행운의 아이템 10" to R.drawable.lucky_item10,
        "행운의 아이템 11" to R.drawable.lucky_item11,
        "행운의 아이템 12" to R.drawable.lucky_item12,
    )

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "행운의 아이템 갤러리", modifier = Modifier.padding(bottom = 16.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2), // 2열로 나열
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(imageList) { item ->
                Card {
                    Column(
                        modifier = Modifier.padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(id = item.second),
                            contentDescription = item.first,
                            modifier = Modifier.size(150.dp)
                        )
                        Text(
                            text = item.first,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }
        }
    }
}
