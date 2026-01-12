package com.example.constellationapp.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.constellationapp.ConstellationData
import com.example.constellationapp.viewmodels.HoroscopeViewModel

/**
 * ViewModel과 연결하여 실제 운세 순위 정보를 보여주는 화면 Composable
 */
@Composable
fun ListScreen(
    // 클릭 시 (이름, 내용) 두 개의 값을 전달하도록 콜백을 수정합니다.
    onItemClick: (String, String) -> Unit,
    viewModel: HoroscopeViewModel = viewModel()
) {
    // ViewModel로부터 운세 목록과 로딩 상태를 구독하여 실시간으로 업데이트 받습니다.
    val constellations by viewModel.horoscopes.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // 화면이 처음 그려질 때(Launched) 운세 정보를 가져오도록 ViewModel에 요청합니다.
    LaunchedEffect(Unit) {
        viewModel.fetchHoroscopes()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = "오늘의 별자리 순위", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            // 1. 로딩 중이고 데이터가 없을 때
            if (isLoading && constellations.isEmpty()) {
                CircularProgressIndicator()
            // 2. 스크래핑 실패 또는 오류가 발생했을 때 (데이터가 1개이고, rank가 0이면 오류로 간주)
            } else if (constellations.size == 1 && constellations.first().rank == 0) {
                val errorData = constellations.first()
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = errorData.name, // "데이터를 가져오는 데 실패했습니다."
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = errorData.date, // 실제 오류 내용
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                }
            // 3. 성공적으로 데이터를 가져왔을 때
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(constellations) { data ->
                        // 클릭 시 이름과 함께 운세 내용(data.content)도 전달합니다.
                        RankingCard(data = data, onClick = { onItemClick(data.name, data.content) })
                    }
                }
            }
        }
    }
}

/**
 * 개별 순위 항목을 보여주는 카드 형태의 UI Composable
 */
@Composable
fun RankingCard(data: ConstellationData, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick) // 카드를 클릭했을 때의 동작을 지정합니다.
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "${data.rank}위")
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = data.name, modifier = Modifier.weight(1f))
        }
    }
}
