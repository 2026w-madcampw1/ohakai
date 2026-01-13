package com.example.constellationapp.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.constellationapp.ConstellationData
import com.example.constellationapp.viewmodels.HoroscopeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HoroscopeScreen(viewModel: HoroscopeViewModel = viewModel()) {
    val horoscopes by viewModel.horoscopes.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isApiKeyMissing by viewModel.isApiKeyMissing.collectAsState()
    val apiError by viewModel.apiError.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchHoroscopes()
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("오늘의 별자리 운세") })
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator()
            } else if (isApiKeyMissing) {
                Text(
                    text = "OpenAI API 키가 설정되지 않았습니다.\nlocal.properties 파일을 확인해주세요.",
                    color = Color.Red,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(16.dp)
                )
            } else if (apiError != null) {
                Text(
                    text = "오류가 발생했습니다:\n$apiError",
                    color = Color.Red,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(horoscopes) { horoscope ->
                        HoroscopeItem(horoscope)
                    }
                }
            }
        }
    }
}

@Composable
private fun HoroscopeItem(horoscope: ConstellationData) {
    Card(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "${horoscope.rank}위: ${horoscope.name} (${horoscope.date})")
            Text(text = horoscope.content)
        }
    }
}
