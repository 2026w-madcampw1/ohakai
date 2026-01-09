package com.example.constellationapp.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.example.constellationapp.ui.theme.DeepBlueSky
import kotlin.math.sqrt

@Composable
fun DrawingScreen() {
    // 이미 연결된 별들의 인덱스 묶음들 (하나의 선 세트)
    val paths = remember { mutableStateListOf<List<Int>>() }
    // 현재 드래그 중인 별들의 인덱스 리스트
    var currentPath by remember { mutableStateOf<List<Int>>(emptyList()) }
    // 현재 드래그 중인 손가락 위치
    var currentTouchPoint by remember { mutableStateOf<Offset?>(null) }

    // 샘플 별자리 위치 (0.0 ~ 1.0 비율)
    val starRatios = remember {
        listOf(
            Offset(0.2f, 0.3f),
            Offset(0.35f, 0.25f),
            Offset(0.5f, 0.4f),
            Offset(0.45f, 0.6f),
            Offset(0.7f, 0.7f),
            Offset(0.8f, 0.5f)
        )
    }

    // 두 점 사이의 거리를 구하는 함수
    fun distance(a: Offset, b: Offset): Float {
        return sqrt((a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y))
    }


    Column(modifier = Modifier.fillMaxSize()) {
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsTopHeight(WindowInsets.statusBars)
                .background(Color.LightGray)
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxSize()
                .background(Brush.verticalGradient(
                    listOf(
                        Color(0xFF001220),
                        DeepBlueSky
                    )
                )) // 어두운 밤하늘 배경
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            currentTouchPoint = offset
                            // 시작점이 별과 가까운지 확인 (반경 50f 내외)
                            starRatios.forEachIndexed { index, ratio ->
                                val starPos = Offset(ratio.x * size.width, ratio.y * size.height)
                                if (distance(offset, starPos) < 50f) {
                                    currentPath = listOf(index)
                                }
                            }
                        },
                        onDrag = { change, _ ->
                            change.consume()
                            currentTouchPoint = change.position
                            
                            // 드래그 중 새로운 별에 닿았는지 확인
                            starRatios.forEachIndexed { index, ratio ->
                                val starPos = Offset(ratio.x * size.width, ratio.y * size.height)
                                if (distance(change.position, starPos) < 50f) {
                                    // 마지막으로 추가된 별이 아니고, 현재 경로에 없는 별이면 추가 (또는 패턴처럼 순서대로)
                                    if (currentPath.isNotEmpty() && currentPath.last() != index) {
                                        currentPath = currentPath + index
                                    } else if (currentPath.isEmpty()) {
                                        currentPath = listOf(index)
                                    }
                                }
                            }
                        },
                        onDragEnd = {
                            if (currentPath.size >= 2) {
                                paths.add(currentPath)
                            }
                            currentPath = emptyList()
                            currentTouchPoint = null
                        },
                        onDragCancel = {
                            currentPath = emptyList()
                            currentTouchPoint = null
                        }
                    )
                }
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val canvasWidth = size.width
                val canvasHeight = size.height

                // 별들의 실제 좌표 계산
                val starPositions = starRatios.map { Offset(it.x * canvasWidth, it.y * canvasHeight) }

                // 1. 별 그리기
                starPositions.forEach { pos ->
                    drawCircle(
                        color = Color.White,
                        radius = 10f,
                        center = pos
                    )
                }

                // 2. 완성된 경로들 그리기
                paths.forEach { path ->
                    for (i in 0 until path.size - 1) {
                        drawLine(
                            color = Color.White.copy(alpha = 0.6f),
                            start = starPositions[path[i]],
                            end = starPositions[path[i + 1]],
                            strokeWidth = 6f,
                            cap = StrokeCap.Round
                        )
                    }
                }

                // 3. 현재 드래그 중인 경로 그리기
                if (currentPath.isNotEmpty()) {
                    // 이미 연결된 점들끼리의 선
                    for (i in 0 until currentPath.size - 1) {
                        drawLine(
                            color = Color.White,
                            start = starPositions[currentPath[i]],
                            end = starPositions[currentPath[i + 1]],
                            strokeWidth = 8f,
                            cap = StrokeCap.Round
                        )
                    }
                    // 마지막 점부터 현재 터치 위치까지의 선 (패턴 느낌)
                    currentTouchPoint?.let { touchPoint ->
                        drawLine(
                            color = Color.White.copy(alpha = 0.5f),
                            start = starPositions[currentPath.last()],
                            end = touchPoint,
                            strokeWidth = 4f,
                            cap = StrokeCap.Round
                        )
                    }
                }
            }

            Text(
                text = "별을 터치하여 선으로 연결해보세요!",
                color = Color.White,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 48.dp),
                style = MaterialTheme.typography.titleMedium
            )
        }

        // 초기화 버튼
        Button(
            onClick = { paths.clear() },
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            Text("초기화")
        }
    }
}
