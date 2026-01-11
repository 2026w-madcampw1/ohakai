package com.example.constellationapp.screens

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.constellationapp.LuckItemProvider // 작성하신 Provider 임포트
import com.example.constellationapp.LuckyItemData

@Composable
fun DrawingScreen() {
    // 1. 현재 작업 중인 아이템 관리
    var itemIndex by remember { mutableStateOf(0) }
    val currentItem = LuckItemProvider.items.getOrNull(itemIndex)

    // 2. 현재 화면에 찍고 있는 별들의 좌표
    val sessionStars = remember { mutableStateListOf<Offset>() }

    // 아이템이 바뀌면 별 목록 초기화
    LaunchedEffect(itemIndex) {
        sessionStars.clear()
        // (선택) 이미 저장된 좌표가 있다면 불러와서 시작할 수도 있습니다.
        // currentItem?.stars?.let { sessionStars.addAll(it) }
    }

    Scaffold(
        bottomBar = {
            BottomAppBar {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(onClick = { if (itemIndex > 0) itemIndex-- }) {
                        Text("이전")
                    }

                    Text("${currentItem?.name ?: "없음"}")

                    Button(onClick = { if (itemIndex < LuckItemProvider.items.size - 1) itemIndex++ }) {
                        Text("다음")
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .background(Color(0xFF000814)) // 밤하늘 느낌 배경
                    .pointerInput(Unit) {
                        detectTapGestures { offset ->
                            // 좌표를 비율(0~1)로 변환하여 저장
                            val xRatio = offset.x / size.width
                            val yRatio = offset.y / size.height
                            sessionStars.add(Offset(xRatio, yRatio))
                        }
                    }
            ) {
                // 1. 가이드 이미지 (옅게)
                currentItem?.let { item ->
                    Image(
                        painter = painterResource(id = item.imageResId),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .alpha(0.2f),
                        contentScale = ContentScale.Fit
                    )
                }

                // 2. 별 그리기
                Canvas(modifier = Modifier.fillMaxSize()) {
                    sessionStars.forEach { ratio ->
                        drawCircle(
                            color = Color.Yellow,
                            radius = 10f,
                            center = Offset(ratio.x * size.width, ratio.y * size.height)
                        )
                    }
                }

                // 3. 안내 텍스트
                Text(
                    "이미지 위를 터치하여 별자리를 그리세요",
                    color = Color.Gray,
                    modifier = Modifier.align(Alignment.TopCenter).padding(16.dp)
                )
            }

            // 제어 버튼들
            Row(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = { sessionStars.clear() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("초기화")
                }

                Spacer(modifier = Modifier.width(16.dp))

                Button(onClick = {
                    // 로그캣에 복사 가능한 형태로 출력
                    val logStr = sessionStars.joinToString(",\n") {
                        "Offset(${String.format("%.3ff", it.x)}, ${String.format("%.3ff", it.y)})"
                    }
                    Log.d("STARS_COORD", "\n--- ${currentItem?.name} 좌표 ---\nlistOf(\n$logStr\n)")
                }) {
                    Text("좌표 복사 (Logcat)")
                }
            }
        }
    }
}