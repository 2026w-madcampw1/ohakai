package com.example.constellationapp.screens

import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.abs

@Composable
fun BirthInputScreen(onNextClick:() -> Unit){
    var name by rememberSaveable{ mutableStateOf("") }

    var selectedMonth by rememberSaveable { mutableIntStateOf(1)}
    var selectedDay by rememberSaveable { mutableIntStateOf(1)}

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "당신의 별자리를 찾아보세요",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("이름을 입력하세요") },
            placeholder = { Text("예: 홍길동") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Text(text = "생일을 선택해주세요", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(16.dp))

        // 드래그 가능한 월/일 선택 영역
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 월 선택 (1~12)
            NumberPicker(
                range = 1..12,
                label = "월",
                onValueChange = { selectedMonth = it}
            )
            Spacer(modifier = Modifier.width(32.dp))
            // 일 선택 (1~31)
            NumberPicker(
                range = 1..31,
                label = "일",
                onValueChange = { selectedDay = it }
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = onNextClick,
            modifier = Modifier.fillMaxWidth(),
            enabled = name.isNotEmpty()
        ) {
            Text("확인")
        }
    }

}
// 숫자를 위아래로 스크롤해서 선택하게 하는 UI
@Composable
fun NumberPicker(
    range: IntRange,
    label: String,
    onValueChange: (Int) -> Unit
) {
    val listState = rememberLazyListState()
    val numbers = range.toList()
    val snapBehavior = rememberSnapFlingBehavior(lazyListState = listState)


    val itemHeight = 50.dp// 각 숫자의 높이
    val pickerHeight = 160.dp// 전체 피커 UI 높이

    // 중앙에 있는 아이템을 계산하는 로직
    val centerIndex by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val visibleItems = layoutInfo.visibleItemsInfo
            if (visibleItems.isEmpty()) 0
            else {
                // 뷰포트 사용해서 뷰포트 시작지점 + 끝지점 /2 로 중앙 지점 구함
                val viewportCenter = (layoutInfo.viewportStartOffset + layoutInfo.viewportEndOffset) / 2
                // 가장 가까운값을 구함 ( 원리 이해 필요 )
                visibleItems.minByOrNull { abs((it.offset + it.size / 2) - viewportCenter) }?.index ?: 0
            }
        }
    }

    LaunchedEffect(listState.isScrollInProgress) {
        if (!listState.isScrollInProgress) { // 스크롤이 멈출때
            // 인덱스 범위를 벗어나지 않도록 방어 코드 추가
            val safeIndex = centerIndex.coerceIn(0, numbers.size - 1)
            onValueChange(numbers[safeIndex])
        }
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .width(60.dp)
                .height(pickerHeight)
        ) {
            LazyColumn(
                state = listState,
                flingBehavior = snapBehavior,
                // ⭐ 핵심: 첫 번째와 마지막 숫자가 정중앙(80dp 지점)에 올 수 있도록
                // 상하단 패딩을 (전체높이/2 - 아이템절반/2) 정도로 넉넉히 줍니다.
                contentPadding = PaddingValues(vertical = 50.dp),
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(numbers.size) { index ->
                    val isSelected = centerIndex == index

                    Text(
                        text = numbers[index].toString().padStart(2, '0'),
                        fontSize = if (isSelected) 28.sp else 18.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        // 아이템 간격을 일정하게 유지
                        modifier = Modifier
                            .height(itemHeight)
                            .wrapContentHeight(Alignment.CenterVertically),
                        color = if (isSelected) MaterialTheme.colorScheme.primary else Color.LightGray
                    )
                }
            }
        }
        Text(text = label, modifier = Modifier.padding(start = 8.dp), fontWeight = FontWeight.Bold)
    }
}
