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
        "물병자리" to R.drawable.constellation,
        "물고기자리" to R.drawable.constellation,
        "양자리" to R.drawable.constellation,
        "황소자리" to R.drawable.constellation,
        "쌍둥이자리" to R.drawable.constellation,
        "게자리" to R.drawable.constellation,
        "사자자리" to R.drawable.constellation,
        "처녀자리" to R.drawable.constellation,
        "천칭자리" to R.drawable.constellation,
        "전갈자리" to R.drawable.constellation,
        "사수자리" to R.drawable.constellation,
        "염소자리" to R.drawable.constellation
    )

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "행운의 아이템", modifier = Modifier.padding(bottom = 16.dp))

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
                            contentDescription = item.first,
                            modifier = Modifier.size(150.dp).padding(8.dp)
                        )
                        Text(text = item.first, modifier = Modifier.padding(8.dp))
                    }
                }
            }
        }
    }
}
