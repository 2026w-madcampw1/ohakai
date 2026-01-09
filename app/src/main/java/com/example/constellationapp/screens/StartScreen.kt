package com.example.constellationapp.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.constellationapp.ui.theme.*

@Composable
fun StartScreen(onStartClick: () -> Unit){
    // 화면 전체 감싸는 박스
    Box(
        modifier = Modifier
            .fillMaxSize()
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
        // 그리드 UI
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // 추후 별자리 이미지 추가할 곳
            Text(
                text = "별자리 이미지",
                fontSize = 50.sp
            )
            // ---------
            // 빈공간 = 패딩으로대체 가능?
            Spacer(modifier = Modifier.height(40.dp))
            // 텍스트 두줄로
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
            // 빈공간
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "생일로 당신의 별자리를 찾고\n오늘의 운세와 밤하늘의 기억을 기록해보세요.",
                color = Color.Gray,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )
        } // 그리드 UI 끝
        // 버튼 배치
        Button(
            onClick = onStartClick, // onStartClick을 파라미터로 받음
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .align(Alignment.BottomCenter),
            shape = RoundedCornerShape(12.dp), // 모서리가 둥근 사각형
            colors = ButtonDefaults.buttonColors(
                containerColor = StarYellow, // AI가 추천해준건데, 카카오톡 버튼이랑 똑같이 생김 ;;
                contentColor = Color.Black
            )
        ) {
            // 버튼 내 텍스트
            Text(
                text="시작하기",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}