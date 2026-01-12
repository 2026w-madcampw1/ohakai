package com.example.constellationapp.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import com.example.constellationapp.LuckItemProvider
import com.example.constellationapp.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageScreen() {
    val items = LuckItemProvider.items

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                // 시스템 상단바 여백(WindowInsets)이 중복 적용되지 않도록 0으로 설정
                windowInsets = WindowInsets(0, 0, 0, 0),
                modifier = Modifier.height(56.dp),
                title = { 
                    Text(
                        text = "오늘의 행운", 
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.offset(y = (-8).dp) // 텍스트를 살짝 위로 올림
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
            // 하단 상세 버튼
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
            // 1. 별자리 정보 헤더 섹션
            item(span = { GridItemSpan(maxLineSpan) }) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // 아이콘 박스 및 별 배지
                    Box(contentAlignment = Alignment.BottomEnd) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color(0xFFF5F7FF)),
                            contentAlignment = Alignment.Center
                        ) {
                            // 임시 아이콘 (적절한 리소스로 교체 가능)
                            Icon(
                                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = Color(0xFF6A8CFF)
                            )
                        }
                        // 작은 별 배지
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
                    
                    Text(
                        text = "양자리",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    
                    Text(
                        text = "2024년 3월 24일",
                        fontSize = 14.sp,
                        color = Color(0xFF6A8CFF),
                        fontWeight = FontWeight.Medium
                    )
                    
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

            // 2. 중간 타이틀 섹션
            item(span = { GridItemSpan(maxLineSpan) }) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "오늘의 행운의 아이템 10선",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    
                    Surface(
                        color = Color(0xFFF0F3FF),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "매일 업데이트",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            fontSize = 10.sp,
                            color = Color(0xFF6A8CFF),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // 3. 아이템 리스트 (카드 형태)
            items(items) { item ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFF8F9FB))
                        .padding(12.dp)
                ) {
                    Image(
                        painter = painterResource(id = item.imageResId),
                        contentDescription = item.name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Text(
                        text = item.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color.Black
                    )
                    
                    Text(
                        text = item.description,
                        fontSize = 11.sp,
                        color = Color.Gray,
                        maxLines = 2,
                        lineHeight = 15.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
            
            // 하단 여백용 아이템
            item(span = { GridItemSpan(maxLineSpan) }) {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

private fun getDistance(p1: androidx.compose.ui.geometry.Offset, p2: androidx.compose.ui.geometry.Offset): Float {
    return kotlin.math.sqrt((p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y) * (p1.y - p2.y))
}
