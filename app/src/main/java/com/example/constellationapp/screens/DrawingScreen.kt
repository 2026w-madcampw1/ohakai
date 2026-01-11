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
import androidx.compose.ui.unit.dp
import com.example.constellationapp.LuckItemProvider
import com.example.constellationapp.R
import kotlin.math.sqrt

@Composable
fun DrawingScreen() {
    var itemIndex by remember { mutableStateOf(0) }
    val haptic = LocalHapticFeedback.current

    val primaryColor = MaterialTheme.colorScheme.primary 

    // 하단 바와의 중복을 피하기 위해 Scaffold 제거
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF00050A))
    ) {
        // 배경 이미지
        Image(
            painter = painterResource(id = R.drawable.startscreen_background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize().alpha(0.4f),
            contentScale = ContentScale.Crop
        )

        Crossfade(
            targetState = itemIndex,
            animationSpec = tween(durationMillis = 600),
            label = "ItemTransition"
        ) { targetIndex ->
            val currentItem = LuckItemProvider.items.getOrNull(targetIndex)
            val connectedLines = remember(targetIndex) { mutableStateListOf<Pair<Int, Int>>() }
            var dragPoint by remember(targetIndex) { mutableStateOf<Offset?>(null) }
            var activeStarIndex by remember(targetIndex) { mutableStateOf<Int?>(null) }

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

            val alphaBasis = remember(progress) {
                if (progress <= 0f) 0f 
                else (0.2f + (progress * 0.8f)).coerceAtMost(1f)
            }

            val animatedBackgroundAlpha by animateFloatAsState(
                targetValue = alphaBasis,
                animationSpec = tween(durationMillis = 300, delayMillis = 300),
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
                                currentItem?.stars?.let { stars ->
                                    val contentHeight = size.width * 1.2f
                                    val verticalOffset = (size.height - contentHeight) / 2

                                    val hitIndex = stars.indexOfFirst { star ->
                                        val starOffset = Offset(
                                            star.x * size.width,
                                            star.y * contentHeight + verticalOffset
                                        )
                                        getDistance(offset, starOffset) < 80f
                                    }
                                    if (hitIndex != -1) {
                                        activeStarIndex = hitIndex
                                        dragPoint = offset
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    }
                                }
                            },
                            onDrag = { change, _ ->
                                dragPoint = change.position
                            },
                            onDragEnd = {
                                if (activeStarIndex != null && dragPoint != null) {
                                    currentItem?.stars?.let { stars ->
                                        val contentHeight = size.width * 1.2f
                                        val verticalOffset = (size.height - contentHeight) / 2

                                        val hitIndex = stars.indexOfFirst { star ->
                                            val starOffset = Offset(
                                                star.x * size.width,
                                                star.y * contentHeight + verticalOffset
                                            )
                                            getDistance(dragPoint!!, starOffset) < 80f
                                        }
                                        if (hitIndex != -1 && hitIndex != activeStarIndex) {
                                            val newLine = activeStarIndex!! to hitIndex
                                            if (!connectedLines.contains(newLine) && !connectedLines.contains(hitIndex to activeStarIndex!!)) {
                                                connectedLines.add(newLine)
                                                Log.d("ConstellationLog", "Item[$targetIndex] 연결됨")
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
                        painter = painterResource(id = item.imageResId),
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
                            
                            drawLine(color = primaryColor.copy(alpha = 0.3f), start = start, end = end, strokeWidth = 15f, cap = StrokeCap.Round)
                            drawLine(color = Color.White, start = start, end = end, strokeWidth = 6f, cap = StrokeCap.Round)
                        }
                    }

                    if (activeStarIndex != null && dragPoint != null && activeStarIndex!! < stars.size) {
                        val start = Offset(stars[activeStarIndex!!].x * size.width, stars[activeStarIndex!!].y * contentHeight + verticalOffset)
                        drawLine(color = Color.White.copy(alpha = 0.4f), start = start, end = dragPoint!!, strokeWidth = 10f, cap = StrokeCap.Round)
                    }

                    stars.forEachIndexed { index, ratio ->
                        val center = Offset(ratio.x * size.width, ratio.y * contentHeight + verticalOffset)
                        val isConnected = connectedLines.any { it.first == index || it.second == index }
                        
                        if (isConnected) {
                            drawCircle(brush = Brush.radialGradient(colors = listOf(Color.White, primaryColor.copy(alpha = 0.4f), Color.Transparent), center = center, radius = 45f), radius = 45f, center = center)
                            drawCircle(color = Color.White, radius = 12f, center = center)
                        } else {
                            drawCircle(brush = Brush.radialGradient(colors = listOf(Color.White.copy(alpha = 0.5f), Color.Transparent), center = center, radius = 25f), radius = 25f, center = center)
                            drawCircle(color = Color.LightGray, radius = 8f, center = center)
                        }
                    }
                }
            }
        }

        // [상단 버튼]
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 48.dp, start = 24.dp, end = 24.dp), // 상단 여백 조정
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { if (itemIndex > 0) itemIndex-- },
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(primaryColor.copy(alpha = 0.2f))
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "이전",
                    tint = primaryColor
                )
            }

            Text(
                text = LuckItemProvider.items.getOrNull(itemIndex)?.name ?: "",
                color = Color.White,
                style = MaterialTheme.typography.titleLarge
            )

            IconButton(
                onClick = { if (itemIndex < LuckItemProvider.items.size - 1) itemIndex++ },
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(primaryColor.copy(alpha = 0.2f))
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "다음",
                    tint = primaryColor
                )
            }
        }
    }
}

private fun getDistance(p1: Offset, p2: Offset): Float = sqrt((p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y) * (p1.y - p2.y))
