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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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


@Composable
fun NumberPicker(
    range: IntRange,
    label: String,
    onValueChange: (Int) -> Unit
) {
    val listState = rememberLazyListState()
    val numbers = range.toList()

    val snapBehavior = rememberSnapFlingBehavior(lazyListState = listState)

    // 스크롤이 멈췄을 때 중앙에 있는 값을 감지
    LaunchedEffect(listState.isScrollInProgress) {
        if (!listState.isScrollInProgress) {
            val centerIndex = listState.firstVisibleItemIndex
            if (centerIndex in numbers.indices) {
                onValueChange(numbers[centerIndex])
            }
        }
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier
            .width(50.dp)
            .height(120.dp)) {
            LazyColumn(
                state = listState,
                flingBehavior = snapBehavior,
                contentPadding = PaddingValues(vertical = 35.dp), // 중앙 정렬 효과용 여백
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(numbers.size) { index ->
                    val isSelected = listState.firstVisibleItemIndex == index
                    Text(
                        text = numbers[index].toString().padStart(2, '0'),
                        fontSize = if (isSelected) 26.sp else 20.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        modifier = Modifier.padding(vertical = 10.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primary else Color.LightGray
                    )
                }
            }
        }
        Text(text = label, modifier = Modifier.padding(start = 8.dp))
    }
}