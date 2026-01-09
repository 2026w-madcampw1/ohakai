package com.example.constellationapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.constellationapp.ui.theme.*

@Composable
fun StartScreen(onStartClick: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        // 1. 맨 위에 하얀색 상단 바 추가 (상태바 높이만큼)
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsTopHeight(WindowInsets.statusBars)
                .background(Color.LightGray)
        )

        // 2. 기존 컨텐츠를 담은 메인 영역
        Box(
            modifier = Modifier
                .weight(1f) // 나머지 공간을 모두 차지
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFF001220),
                            DeepBlueSky
                        )
                    )
                )
                .padding(24.dp)
        ) {
            // 중앙 컨텐츠 (텍스트들)
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "별자리 이미지",
                    fontSize = 50.sp
                )
                Spacer(modifier = Modifier.height(40.dp))
                Text(
                    text = "오늘의 별자리 운세",
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "오하카이와 함께하세요",
                    color = Color.White,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "생일로 당신의 별자리를 찾고\n오늘의 운세와 밤하늘의 기억을 기록해보세요.",
                    color = Color.Gray,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )
            }

            // 하단 시작하기 버튼
            Button(
                onClick = onStartClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .align(Alignment.BottomCenter),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = StarYellow,
                    contentColor = Color.Black
                )
            ) {
                Text(
                    text = "시작하기",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}