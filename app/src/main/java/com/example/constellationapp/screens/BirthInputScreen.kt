package com.example.constellationapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.abs

@Composable
fun BirthInputScreen(onNextClick: () -> Unit) {
    var name by rememberSaveable { mutableStateOf("") }
    var selectedMonth by rememberSaveable { mutableIntStateOf(1) }
    var selectedDay by rememberSaveable { mutableIntStateOf(1) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(50.dp))
        Text(
            text = "운명의 별자리 찾기",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(40.dp))
        Text(
            text = "당신의 이름과 생일을 알려주세요",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 16.sp
        )

        Spacer(modifier = Modifier.height(40.dp))

        Column(
            modifier = Modifier
                .fillMaxHeight(0.7f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 이름 입력
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("이름") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.outline
                )
            )
            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "생년월일 선택",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 피커 영역
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                NumberPicker(range = 1..12, label = "월", onValueChange = { selectedMonth = it })
                Spacer(modifier = Modifier.width(24.dp))
                NumberPicker(range = 1..31, label = "일", onValueChange = { selectedDay = it })
            }
        }


        Spacer(modifier = Modifier.height(24.dp))

        // 확인 버튼
        Button(
            onClick = {
                // 선택된 날짜 정보 활용 가능
                println("Selected: $selectedMonth / $selectedDay")
                onNextClick()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = name.isNotEmpty(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("나의 별자리 확인하기", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun NumberPicker(
    range: IntRange,
    label: String,
    onValueChange: (Int) -> Unit
) {
    val listState = rememberLazyListState()
    val numbers = range.toList()
    val snapBehavior = rememberSnapFlingBehavior(lazyListState = listState)
    val itemHeight = 50.dp
    val pickerHeight = 150.dp

    val centerIndex by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val visibleItems = layoutInfo.visibleItemsInfo
            if (visibleItems.isEmpty()) 0
            else {
                val viewportCenter = (layoutInfo.viewportStartOffset + layoutInfo.viewportEndOffset) / 2
                visibleItems.minByOrNull { abs((it.offset + it.size / 2) - viewportCenter) }?.index ?: 0
            }
        }
    }

    LaunchedEffect(listState.isScrollInProgress) {
        if (!listState.isScrollInProgress) {
            val safeIndex = centerIndex.coerceIn(0, numbers.size - 1)
            onValueChange(numbers[safeIndex])
        }
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .width(70.dp)
                .height(pickerHeight),
            contentAlignment = Alignment.Center
        ) {
            // 선택 표시 가이드 라인 (위, 아래 선)
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 8.dp).offset(y = (-25).dp),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
            )
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 8.dp).offset(y = 25.dp),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
            )

            LazyColumn(
                state = listState,
                flingBehavior = snapBehavior,
                contentPadding = PaddingValues(vertical = 50.dp),
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(numbers.size) { index ->
                    val isSelected = centerIndex == index
                    Text(
                        text = numbers[index].toString().padStart(2, '0'),
                        fontSize = if (isSelected) 26.sp else 18.sp,
                        fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Normal,
                        color = if (isSelected) MaterialTheme.colorScheme.primary else Color.LightGray.copy(alpha = 0.6f),
                        modifier = Modifier
                            .height(itemHeight)
                            .wrapContentHeight(Alignment.CenterVertically)
                    )
                }
            }
        }
        Text(text = label, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
    }
}
