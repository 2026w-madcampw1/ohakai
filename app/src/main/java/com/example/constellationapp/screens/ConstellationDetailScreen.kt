package com.example.constellationapp.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.constellationapp.R

/**
 * 별자리 상세 정보와 운세 내용을 보여주는 화면 Composable
 */
@Composable
fun ConstellationDetailScreen(name: String, content: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 별자리 이름 표시
        Text(text = name, fontSize = 24.sp, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(16.dp))

        // 별자리 이미지 (임시)
        Image(
            painter = painterResource(id = R.drawable.constellation),
            contentDescription = name,
            modifier = Modifier.size(150.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))

        // 전달받은 실제 운세 내용을 표시합니다.
        Text(
            text = content,
            fontSize = 18.sp,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))

        // 행운의 아이템 (UI 구조만 유지)
        Text(text = "오늘의 행운의 아이템", fontSize = 20.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Image(painter = painterResource(id = R.drawable.constellation), contentDescription = "Lucky Item 1", modifier = Modifier.size(100.dp))
            Image(painter = painterResource(id = R.drawable.constellation), contentDescription = "Lucky Item 2", modifier = Modifier.size(100.dp))
            Image(painter = painterResource(id = R.drawable.constellation), contentDescription = "Lucky Item 3", modifier = Modifier.size(100.dp))
        }
    }
}
