package com.example.constellationapp.screens

import android.util.Log
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.constellationapp.DataStoreManager
import com.example.constellationapp.LuckItemProvider
import com.example.constellationapp.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.sqrt

@Composable
fun DrawingScreen(dataStoreManager: DataStoreManager, initialItemIndex: Int, onBackClick: () -> Unit) {
    // 탭 전환 등으로 initialItemIndex가 바뀌면 상태 동기화
    var currentItemIndex by remember(initialItemIndex) { mutableIntStateOf(initialItemIndex) }
    val haptic = LocalHapticFeedback.current
    val primaryColor = MaterialTheme.colorScheme.primary 
    val scope = rememberCoroutineScope()

    // 오늘의 아이템 리스트와 잠긴 슬롯 정보(0,1,2,3) 구독
    val todayIndices by dataStoreManager.todayLuckyIndices.collectAsState(initial = emptyList())
    val hiddenSlots by dataStoreManager.hiddenItemIndices.collectAsState(initial = emptySet())

    // 지능형 다음 인덱스 추출 함수 (남은 슬롯 우선)
    val getNextIndex: () -> Int = {
        if (todayIndices.isNotEmpty()) {
            val lockedItemIndices = hiddenSlots.mapNotNull { todayIndices.getOrNull(it) }.filter { it != currentItemIndex }
            if (lockedItemIndices.isNotEmpty()) {
                lockedItemIndices.random()
            } else {
                // 다 해금 시 전체 랜덤
                val all = LuckItemProvider.items.indices.toMutableList()
                all.remove(currentItemIndex)
                all.random()
            }
        } else {
            0
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF00050A))
    ) {
        Image(
            painter = painterResource(id = R.drawable.startscreen_background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize().alpha(0.4f),
            contentScale = ContentScale.Crop
        )

        Crossfade(
            targetState = currentItemIndex,
            animationSpec = tween(durationMillis = 600),
            label = "ItemTransition"
        ) { targetIndex ->
            val currentItem = LuckItemProvider.items.getOrNull(targetIndex)
            val connectedLines = remember(targetIndex) { mutableStateListOf<Pair<Int, Int>>() }
            var dragPoint by remember(targetIndex) { mutableStateOf<Offset?>(null) }
            var activeStarIndex by remember(targetIndex) { mutableStateOf<Int?>(null) }
            
            var failedLine by remember(targetIndex) { mutableStateOf<Pair<Int, Int>?>(null) }

            var animationTriggered by remember(targetIndex) { mutableStateOf(false) }
            LaunchedEffect(targetIndex) {
                animationTriggered = true
            }

            LaunchedEffect(failedLine) {
                if (failedLine != null) {
                    delay(120)
                    failedLine = null
                }
            }

            // 1. 별 애니메이션
            val starAnimAlpha by animateFloatAsState(
                targetValue = if (animationTriggered) 1f else 0f,
                animationSpec = tween(durationMillis = 700, delayMillis = 500),
                label = "StarAlpha"
            )
            val starAnimSlideY by animateFloatAsState(
                targetValue = if (animationTriggered) 0f else 30f,
                animationSpec = tween(durationMillis = 700, delayMillis = 500),
                label = "StarSlide"
            )

            // 2. 가이드 텍스트 애니메이션
            val progress = remember(connectedLines.size, currentItem) {
                val required = currentItem?.requiredLines ?: emptyList()
                if (required.isEmpty()) 0f
                else {
                    val correctCount = required.count { line ->
                        connectedLines.contains(line) || connectedLines.contains(line.second to line.first)
                    }
                    correctCount.toFloat() / required.size
                }
            }

            val guideTextAlpha by animateFloatAsState(
                targetValue = if (animationTriggered && progress < 1f) 1f else 0f,
                animationSpec = tween(durationMillis = 600, delayMillis = if (progress < 1f) 1500 else 0),
                label = "GuideTextAlpha"
            )

            // 3. 완성 시 나타나는 이름 & 설명 애니메이션
            val completionAlpha by animateFloatAsState(
                targetValue = if (progress >= 1f) 1f else 0f,
                animationSpec = tween(durationMillis = 1000, delayMillis = 500),
                label = "CompletionAlpha"
            )

            // 버튼 상태 및 애니메이션
            var showActionButtons by remember(targetIndex) { mutableStateOf(false) }
            val actionButtonsAlpha by animateFloatAsState(
                targetValue = if (showActionButtons) 1f else 0f,
                animationSpec = tween(durationMillis = 600),
                label = "ActionButtonsAlpha"
            )

            LaunchedEffect(progress) {
                if (progress >= 1f) {
                    // [수정 완료] 현재 아이템이 오늘의 세트 중 몇 번째 슬롯인지 확인하여 해당 슬롯 해금
                    val slotIndex = todayIndices.indexOf(targetIndex)
                    if (slotIndex != -1) {
                        dataStoreManager.removeHiddenSlot(slotIndex)
                    }
                    delay(2500)
                    showActionButtons = true
                }
            }

            val alphaBasis = remember(progress) {
                if (progress <= 0f) 0f 
                else (0.2f + (progress * 0.8f)).coerceAtMost(1f)
            }

            val animatedBackgroundAlpha by animateFloatAsState(
                targetValue = alphaBasis,
                animationSpec = tween(durationMillis = 300, delayMillis = 100),
                label = "BackgroundAlphaAnimation"
            )

            val successExpandScale by animateFloatAsState(
                targetValue = if (progress >= 1f) 1.5f else 0f,
                animationSpec = tween(durationMillis = 1500),
                label = "SuccessExpandAnimation"
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(targetIndex) {
                        detectDragGestures(
                            onDragStart = { offset ->
                                if (progress >= 1f) return@detectDragGestures
                                currentItem?.stars?.let { stars ->
                                    val contentHeight = size.width * 1.2f
                                    val verticalOffset = (size.height - contentHeight) / 2
                                    val hitIndex = stars.indexOfFirst { star ->
                                        val starOffset = Offset(star.x * size.width, star.y * contentHeight + verticalOffset)
                                        getDistance(offset, starOffset) < 80f
                                    }
                                    if (hitIndex != -1) {
                                        activeStarIndex = hitIndex
                                        dragPoint = offset
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    }
                                }
                            },
                            onDrag = { change, _ -> if (progress < 1f) dragPoint = change.position },
                            onDragEnd = {
                                if (activeStarIndex != null && dragPoint != null) {
                                    currentItem?.stars?.let { stars ->
                                        val contentHeight = size.width * 1.2f
                                        val verticalOffset = (size.height - contentHeight) / 2
                                        val hitIndex = stars.indexOfFirst { star ->
                                            val starOffset = Offset(star.x * size.width, star.y * contentHeight + verticalOffset)
                                            getDistance(dragPoint!!, starOffset) < 80f
                                        }
                                        if (hitIndex != -1 && hitIndex != activeStarIndex) {
                                            val newLine = activeStarIndex!! to hitIndex
                                            val isRequired = currentItem?.requiredLines?.any { 
                                                (it.first == newLine.first && it.second == newLine.second) ||
                                                (it.first == newLine.second && it.second == newLine.first)
                                            } == true

                                            if (isRequired) {
                                                if (!connectedLines.contains(newLine) && !connectedLines.contains(hitIndex to activeStarIndex!!)) {
                                                    connectedLines.add(newLine)
                                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                }
                                            } else {
                                                failedLine = newLine
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
                if (successExpandScale > 0f) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val maxRadius = sqrt(size.width * size.width + size.height * size.height)
                        drawCircle(
                            color = Color.White.copy(alpha = 1.0f * (successExpandScale / 1.5f)),
                            radius = maxRadius * successExpandScale,
                            center = center
                        )
                    }
                }

                currentItem?.let { item ->
                    Image(
                        painter = painterResource(id = item.sketchImageResId),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .alpha(animatedBackgroundAlpha),
                        contentScale = ContentScale.Fit
                    )
                }

                Canvas(modifier = Modifier.fillMaxSize()) {
                    val stars = currentItem?.stars ?: emptyList()
                    val contentHeight = size.width * 1.2f
                    val verticalOffset = (size.height - contentHeight) / 2

                    connectedLines.forEach { (startIdx, endIdx) ->
                        if (startIdx < stars.size && endIdx < stars.size) {
                            val start = Offset(stars[startIdx].x * size.width, stars[startIdx].y * contentHeight + verticalOffset)
                            val end = Offset(stars[endIdx].x * size.width, stars[endIdx].y * contentHeight + verticalOffset)
                            drawLine(color = primaryColor.copy(alpha = 0.3f * starAnimAlpha), start = start, end = end, strokeWidth = 15f, cap = StrokeCap.Round)
                            drawLine(color = Color.White.copy(alpha = starAnimAlpha), start = start, end = end, strokeWidth = 6f, cap = StrokeCap.Round)
                        }
                    }

                    failedLine?.let { (startIdx, endIdx) ->
                        if (startIdx < stars.size && endIdx < stars.size) {
                            val start = Offset(stars[startIdx].x * size.width, stars[startIdx].y * contentHeight + verticalOffset)
                            val end = Offset(stars[endIdx].x * size.width, stars[endIdx].y * contentHeight + verticalOffset)
                            drawLine(color = Color.Red.copy(alpha = 0.3f), start = start, end = end, strokeWidth = 8f, cap = StrokeCap.Round)
                        }
                    }

                    if (activeStarIndex != null && dragPoint != null && activeStarIndex!! < stars.size) {
                        val start = Offset(stars[activeStarIndex!!].x * size.width, stars[activeStarIndex!!].y * contentHeight + verticalOffset)
                        drawLine(color = Color.White.copy(alpha = 0.4f), start = start, end = dragPoint!!, strokeWidth = 10f, cap = StrokeCap.Round)
                    }

                    stars.forEachIndexed { index, ratio ->
                        val center = Offset(ratio.x * size.width, ratio.y * contentHeight + verticalOffset + starAnimSlideY)
                        val isConnected = connectedLines.any { it.first == index || it.second == index }
                        if (isConnected) {
                            drawCircle(brush = Brush.radialGradient(colors = listOf(Color.White.copy(alpha = starAnimAlpha), primaryColor.copy(alpha = 0.4f * starAnimAlpha), Color.Transparent), center = center, radius = 45f), radius = 45f, center = center)
                            drawCircle(color = Color.White.copy(alpha = starAnimAlpha), radius = 12f, center = center)
                        } else {
                            drawCircle(brush = Brush.radialGradient(colors = listOf(Color.White.copy(alpha = 0.5f * starAnimAlpha), Color.Transparent), center = center, radius = 25f), radius = 25f, center = center)
                            drawCircle(color = Color.LightGray.copy(alpha = starAnimAlpha), radius = 8f, center = center)
                        }
                    }
                }

                if (progress >= 1f) {
                    Text(
                        text = currentItem?.name ?: "",
                        style = MaterialTheme.typography.displaySmall.copy(
                            shadow = Shadow(
                                color = Color.White,
                                offset = Offset(0f, 0f),
                                blurRadius = 60f
                            )
                        ),
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 90.dp)
                            .alpha(completionAlpha)
                    )

                    Text(
                        text = currentItem?.description ?: "",
                        style = MaterialTheme.typography.titleMedium.copy(
                            shadow = Shadow(
                                color = Color.White,
                                offset = Offset(0f, 0f),
                                blurRadius = 50f
                            )
                        ),
                        color = Color.Black.copy(alpha = 0.9f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 105.dp, start = 40.dp, end = 40.dp)
                            .alpha(completionAlpha)
                    )
                }

                if (showActionButtons) {
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 40.dp)
                            .alpha(actionButtonsAlpha),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Button(
                            onClick = { onBackClick() },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Gray.copy(alpha = 0.6f))
                        ) {
                            Text("돌아가기", fontWeight = FontWeight.Bold, color = Color.White)
                        }

                        Button(
                            onClick = {
                                currentItemIndex = getNextIndex()
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
                        ) {
                            Text("다음", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 48.dp, start = 24.dp, end = 24.dp)
                    .alpha(guideTextAlpha),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "당신의 행운아이템을 그려보세요",
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                    fontSize = 18.sp
                )
            }
        }

//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(top = 48.dp, start = 24.dp, end = 24.dp),
//            horizontalArrangement = Arrangement.SpaceBetween,
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            IconButton(
//                onClick = { if (currentItemIndex > 0) currentItemIndex-- },
//                modifier = Modifier.size(44.dp).clip(CircleShape).background(primaryColor.copy(alpha = 0.2f))
//            ) {
//                Icon(imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "이전", tint = primaryColor)
//            }
//
//            Spacer(modifier = Modifier.weight(1f))
//
//            IconButton(
//                onClick = {
//                    currentItemIndex = getNextIndex()
//                },
//                modifier = Modifier.size(44.dp).clip(CircleShape).background(primaryColor.copy(alpha = 0.2f))
//            ) {
//                Icon(imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "다음", tint = primaryColor)
//            }
//        }
    }
}

private fun getDistance(p1: Offset, p2: Offset): Float = sqrt((p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y) * (p1.y - p2.y))
