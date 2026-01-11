package com.example.constellationapp.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.constellationapp.LuckItemProvider
import kotlin.math.sqrt

@Composable
fun DrawingScreen() {
    var itemIndex by remember { mutableStateOf(0) }
    val currentItem = LuckItemProvider.items.getOrNull(itemIndex)

    // 사용자가 연결한 별들의 인덱스 쌍 (시작 인덱스 to 끝 인덱스)
    val connectedLines = remember { mutableStateListOf<Pair<Int, Int>>() }
    // 현재 드래그 중인 선의 위치
    var dragPoint by remember { mutableStateOf<Offset?>(null) }
    var activeStarIndex by remember { mutableStateOf<Int?>(null) }

    // 아이템 변경 시 초기화
    LaunchedEffect(itemIndex) {
        connectedLines.clear()
        dragPoint = null
        activeStarIndex = null
    }

    Scaffold(
        bottomBar = {
            BottomAppBar {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(onClick = { if (itemIndex > 0) itemIndex-- }) { Text("이전") }
                    Text(currentItem?.name ?: "없음", style = MaterialTheme.typography.titleMedium, color = Color.White)
                    Button(onClick = { if (itemIndex < LuckItemProvider.items.size - 1) itemIndex++ }) { Text("다음") }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(Color(0xFF000814))
                .pointerInput(itemIndex) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            // 터치 시작점이 어느 별과 가까운지 확인
                            currentItem?.stars?.let { stars ->
                                val hitIndex = stars.indexOfFirst { star ->
                                    val starOffset = Offset(star.x * size.width, star.y * size.height)
                                    getDistance(offset, starOffset) < 60f // 터치 인식 반경 확대 (40f -> 60f)
                                }
                                if (hitIndex != -1) {
                                    activeStarIndex = hitIndex
                                    dragPoint = offset
                                }
                            }
                        },
                        onDrag = { change, _ ->
                            dragPoint = change.position
                        },
                        onDragEnd = {
                            // 드래그가 끝난 지점이 다른 별 위라면 연결
                            if (activeStarIndex != null && dragPoint != null) {
                                currentItem?.stars?.let { stars ->
                                    val hitIndex = stars.indexOfFirst { star ->
                                        val starOffset = Offset(star.x * size.width, star.y * size.height)
                                        getDistance(dragPoint!!, starOffset) < 60f // 터치 인식 반경 확대 (40f -> 60f)
                                    }
                                    // 자기 자신과 연결하는 것 방지 및 이미 연결된 선 방지
                                    if (hitIndex != -1 && hitIndex != activeStarIndex) {
                                        val newLine = if (activeStarIndex!! < hitIndex) 
                                            activeStarIndex!! to hitIndex 
                                        else 
                                            hitIndex to activeStarIndex!!
                                        
                                        if (!connectedLines.contains(newLine)) {
                                            connectedLines.add(newLine)
                                        }
                                    }
                                }
                            }
                            activeStarIndex = null
                            dragPoint = null
                        }
                    )
                }
        ) {
            // 1. 가이드 이미지 (배경)
            currentItem?.let { item ->
                Image(
                    painter = painterResource(id = item.imageResId),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize().alpha(0.15f),
                    contentScale = ContentScale.Fit
                )
            }

            Canvas(modifier = Modifier.fillMaxSize()) {
                val stars = currentItem?.stars ?: emptyList()
                
                // 2. 이미 연결된 선 그리기
                connectedLines.forEach { (startIdx, endIdx) ->
                    val start = Offset(stars[startIdx].x * size.width, stars[startIdx].y * size.height)
                    val end = Offset(stars[endIdx].x * size.width, stars[endIdx].y * size.height)
                    drawLine(
                        color = Color.Cyan,
                        start = start,
                        end = end,
                        strokeWidth = 4f,
                        cap = StrokeCap.Round
                    )
                }

                // 3. 현재 드래그 중인 선 그리기
                if (activeStarIndex != null && dragPoint != null) {
                    val start = Offset(stars[activeStarIndex!!].x * size.width, stars[activeStarIndex!!].y * size.height)
                    drawLine(
                        color = Color.White.copy(alpha = 0.5f),
                        start = start,
                        end = dragPoint!!,
                        strokeWidth = 3f,
                        cap = StrokeCap.Round
                    )
                }

                // 4. 별 점 그리기
                stars.forEachIndexed { index, ratio ->
                    val center = Offset(ratio.x * size.width, ratio.y * size.height)
                    val isConnected = connectedLines.any { it.first == index || it.second == index }
                    
                    drawCircle(
                        color = if (isConnected) Color.Cyan else Color.LightGray,
                        radius = if (isConnected) 18f else 12f, // 별 크기 확대 (12/8 -> 18/12)
                        center = center
                    )
                    
                    // 별 주위 글로우 효과 (선택)
                    if (isConnected) {
                        drawCircle(
                            color = Color.Cyan.copy(alpha = 0.3f),
                            radius = 30f, // 글로우 크기 확대 (20f -> 30f)
                            center = center
                        )
                    }
                }
            }

            // 안내 메시지
            Text(
                text = "별과 별을 드래그해서 연결하세요",
                color = Color.Gray,
                modifier = Modifier.align(Alignment.TopCenter).padding(24.dp)
            )
            
            // 완료 상태 표시 (모든 별이 한 번 이상 연결되었을 때)
            val allStarsConnected = currentItem?.stars?.indices?.all { idx ->
                connectedLines.any { it.first == idx || it.second == idx }
            } == true
            
            if (allStarsConnected) {
                Text(
                    text = "✨ 별자리 완성! ✨",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.Yellow,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

// 두 지점 사이의 거리 계산 함수
private fun getDistance(p1: Offset, p2: Offset): Float {
    return sqrt((p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y) * (p1.y - p2.y))
}
