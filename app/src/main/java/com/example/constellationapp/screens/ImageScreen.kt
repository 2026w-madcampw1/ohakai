package com.example.constellationapp.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.constellationapp.DataStoreManager
import com.example.constellationapp.LuckItemProvider
import com.example.constellationapp.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageScreen(dataStoreManager: DataStoreManager, onNavigateToDrawing: () -> Unit) {
    val items = LuckItemProvider.items
    
    // 데이터스토어에서 실시간으로 숨겨진 인덱스 목록을 가져옴
    val hiddenIndices by dataStoreManager.hiddenItemIndices.collectAsState(initial = emptySet())

    // 화면 진입 시 매일 아침 6시 기준 갱신 로직 실행
    LaunchedEffect(Unit) {
        dataStoreManager.updateHiddenIndicesIfNeeded(items.size)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                windowInsets = WindowInsets(0, 0, 0, 0),
                modifier = Modifier.height(56.dp),
                title = { 
                    Text(
                        text = "오늘의 행운", 
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.offset(y = (-8).dp)
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = { /* 뒤로가기 로직 */ }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "뒤로", tint = Color(0xFF6A8CFF))
                    }
                },
                actions = {
                    IconButton(onClick = { /* 공유 로직 */ }) {
                        Icon(Icons.Default.Share, contentDescription = "공유")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                Button(
                    onClick = { /* 상세 운세 확인 로직 */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A8CFF))
                ) {
                    Text("상세 운세 확인하기", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        },
        containerColor = Color.White
    ) { paddingValues ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(contentAlignment = Alignment.BottomEnd) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color(0xFFF5F7FF)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = Color(0xFF6A8CFF)
                            )
                        }
                        Surface(
                            modifier = Modifier.size(24.dp).offset(x = 4.dp, y = 4.dp),
                            color = Color(0xFFFFD700),
                            shape = CircleShape,
                            shadowElevation = 2.dp
                        ) {
                            Icon(
                                Icons.Default.Star,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.padding(4.dp)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(text = "양자리", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    Text(text = "2024년 3월 24일", fontSize = 14.sp, color = Color(0xFF6A8CFF), fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "오늘 당신의 에너지가 가장 빛나는 순간입니다.\n아래의 아이템들이 행운을 더해줄 거예요.",
                        textAlign = TextAlign.Center,
                        fontSize = 14.sp,
                        color = Color.Gray,
                        lineHeight = 20.sp
                    )
                }
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "오늘의 행운의 아이템 10선", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    Surface(color = Color(0xFFF0F3FF), shape = RoundedCornerShape(12.dp)) {
                        Text(
                            text = "매일 업데이트",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            fontSize = 10.sp, color = Color(0xFF6A8CFF), fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            itemsIndexed(items) { index, item ->
                val isHidden = index in hiddenIndices
                
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isHidden) Color(0xFFE0E5F0) else Color(0xFFF8F9FB))
                        .then(if (isHidden) Modifier.clickable { onNavigateToDrawing() } else Modifier)
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (isHidden) {
                        Box(
                            modifier = Modifier.fillMaxWidth().aspectRatio(1f).clip(RoundedCornerShape(12.dp)).background(Color(0xFFD0D7E5)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("?", fontSize = 48.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(text = "???", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.Gray)
                        Text(
                            text = "터치해서 행운을 그려보세요",
                            fontSize = 10.sp, color = Color(0xFF6A8CFF), textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    } else {
                        Image(
                            painter = painterResource(id = item.imageResId),
                            contentDescription = item.name,
                            modifier = Modifier.fillMaxWidth().aspectRatio(1f).clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(text = item.name, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.Black, modifier = Modifier.fillMaxWidth())
                        Text(
                            text = item.description,
                            fontSize = 11.sp, color = Color.Gray, maxLines = 2, lineHeight = 15.sp,
                            modifier = Modifier.padding(top = 4.dp).fillMaxWidth()
                        )
                    }
                }
            }
            item(span = { GridItemSpan(maxLineSpan) }) { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}
