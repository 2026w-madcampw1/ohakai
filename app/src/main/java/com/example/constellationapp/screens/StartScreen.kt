package com.example.constellationapp.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.constellationapp.R
import com.example.constellationapp.ui.theme.*

@Composable
fun StartScreen(onStartClick: () -> Unit) {
    val visible = remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        visible.value = true
    }

    // 텍스트 가독성을 위한 그림자 스타일
    val textShadow = Shadow(
        color = Color.Black.copy(alpha = 0.8f),
        offset = Offset(4f, 4f),
        blurRadius = 8f
    )

    Box(modifier = Modifier.fillMaxSize()) {
        // 1. 배경 이미지
        Image(
            painter = painterResource(id = R.drawable.startscreen_background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // 배경 가독성을 위한 레이어
        Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.25f)))

        // 2. 컨텐츠 레이어
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(modifier = Modifier.height(40.dp))

                AnimatedVisibility(
                    visible = visible.value,
                    enter = fadeIn(animationSpec = tween(1500)) + slideInHorizontally()
                ) {
                    Text(
                        text = "오늘의 별자리 운세",
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center,
                        style = TextStyle(shadow = textShadow)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                AnimatedVisibility(
                    visible = visible.value,
                    enter = fadeIn(animationSpec = tween(2000)) + slideInVertically(initialOffsetY = { 40 })
                ) {
                    Text(
                        text = "오하카이와 함께하세요",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        style = TextStyle(shadow = textShadow)
                    )
                }

                Spacer(modifier = Modifier.height(60.dp))

                Text(
                    text = "생일로 당신의 별자리를 찾고\n오늘의 운세와 밤하늘의 기억을 기록해보세요.",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp,
                    style = TextStyle(shadow = textShadow)
                )
            }

            Button(
                onClick = onStartClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp), // 높이 고정
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = StarYellow,
                    contentColor = Color.Black
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
            ) {
                Text(
                    text = "시작하기",
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // 이 Spacer가 버튼을 위로 밀어 올립니다. (40.dp만큼)
            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}