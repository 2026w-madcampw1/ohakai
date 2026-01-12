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
import kotlin.math.sqrt

@Composable
fun DrawingScreen(dataStoreManager: DataStoreManager, initialItemIndex: Int) {
    var currentItemIndex by remember(initialItemIndex) { mutableIntStateOf(initialItemIndex) }
    val haptic = LocalHapticFeedback.current
    val primaryColor = MaterialTheme.colorScheme.primary 

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

            var animationTriggered by remember(targetIndex) { mutableStateOf(false) }
            LaunchedEffect(targetIndex) {
                animationTriggered = true
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

            // 2. 가이드 텍스트 애니메이션 (완성 시 사라짐)
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

            // 3. 완성 시 나타나는 텍스트 애니메이션 (이름 & 설명)
            val completionAlpha by animateFloatAsState(
                targetValue = if (progress >= 1f) 1f else 0f,
                animationSpec = tween(durationMillis = 1000, delayMillis = 500),
                label = "CompletionAlpha"
            )

            LaunchedEffect(progress) {
                if (progress >= 1f) {
                    dataStoreManager.removeHiddenItem(targetIndex)
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
                                if (progress >= 1f) return@detectDragGestures // 완성 후 터치 방지
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
                                            if (!connectedLines.contains(newLine) && !connectedLines.contains(hitIndex to activeStarIndex!!)) {
                                                connectedLines.add(newLine)
                                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
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
                // 완성 효과 (화이트아웃)
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

                // 1. [완성 시] 아이템 이름 (상단)
                if (progress >= 1f) {
                    Text(
                        text = currentItem?.name ?: "",
                        style = MaterialTheme.typography.displaySmall,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 90.dp)
                            .alpha(completionAlpha)
                    )
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

                // 2. [완성 시] 아이템 설명 (하단)
                if (progress >= 1f) {
                    Text(
                        text = currentItem?.description ?: "",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.Black.copy(alpha = 0.9f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 80.dp, start = 40.dp, end = 40.dp)
                            .alpha(completionAlpha)
                    )
                }
            }

            // 초기 가이드 텍스트 (완성 시 사라짐)
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

//        // 아이템 변경 버튼
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
//                onClick = { if (currentItemIndex < LuckItemProvider.items.size - 1) currentItemIndex++ },
//                modifier = Modifier.size(44.dp).clip(CircleShape).background(primaryColor.copy(alpha = 0.2f))
//            ) {
//                Icon(imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "다음", tint = primaryColor)
//            }
//        }
    }
}

private fun getDistance(p1: Offset, p2: Offset): Float = sqrt((p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y) * (p1.y - p2.y))
