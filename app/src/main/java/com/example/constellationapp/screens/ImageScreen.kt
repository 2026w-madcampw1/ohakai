package com.example.constellationapp.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.example.constellationapp.DataStoreManager
import com.example.constellationapp.LuckItemProvider
import com.example.constellationapp.LuckyItemData
import com.example.constellationapp.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageScreen(dataStoreManager: DataStoreManager, onNavigateToDrawing: (Int) -> Unit) {
    val allItems = LuckItemProvider.items
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    
    // 데이터스토어 상태 구독
    val luckyIndices by dataStoreManager.todayLuckyIndices.collectAsState(initial = emptyList())
    // hiddenIndices는 이제 0,1,2,3 (슬롯 번호)을 의미함
    val hiddenSlotIndices by dataStoreManager.hiddenItemIndices.collectAsState(initial = emptySet())
    val userStats by dataStoreManager.userInfo.collectAsState(initial = null)
    val displayDate by dataStoreManager.lastUpdateDate.collectAsState(initial = "오늘")

    LaunchedEffect(Unit) {
        dataStoreManager.updateHiddenIndicesIfNeeded(allItems.size)
    }

    val todayItems = remember(luckyIndices) {
        luckyIndices.mapNotNull { index -> allItems.getOrNull(index) }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                windowInsets = WindowInsets(0, 0, 0, 0),
                modifier = Modifier.height(45.dp),
                title = { },
                navigationIcon = {
                    IconButton(onClick = { /* 뒤로가기 */ }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "뒤로", tint = Color(0xFF6A8CFF))
                    }
                },
                actions = {
                    IconButton(onClick = { /* 공유 */ }) {
                        Icon(Icons.Default.Share, contentDescription = "공유")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
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
                        .padding(top = 24.dp, bottom = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val zodiacIndex = userStats?.zodiacIndex ?: 1
                    val iconResId = context.resources.getIdentifier(
                        "icon_zodiac_$zodiacIndex", 
                        "drawable", 
                        context.packageName
                    )
                    
                    Image(
                        painter = painterResource(id = if (iconResId != 0) iconResId else R.drawable.ic_launcher_foreground),
                        contentDescription = null,
                        modifier = Modifier.size(120.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    ZodiacSelector(
                        currentZodiac = userStats?.zodiac ?: "별자리",
                        onZodiacSelected = { zodiac ->
                            scope.launch {
                                dataStoreManager.updateZodiacAndItems(zodiac, allItems.size)
                            }
                        }
                    )

                    Text(text = displayDate, fontSize = 14.sp, color = Color(0xFF6A8CFF), fontWeight = FontWeight.Medium)
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Text(
                        text = "오늘은 좋은 기운이 맴도는 날이에요\n아래의 아이템들이 당신의 하루를 더 빛내줄 거예요",
                        textAlign = TextAlign.Center,
                        fontSize = 14.sp,
                        color = Color.Gray,
                        lineHeight = 20.sp
                    )
                }
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                LuckSubTitle()
            }

            // [수정] 슬롯 기반(0,1,2,3) 잠금 확인 로직 적용
            itemsIndexed(
                items = todayItems,
                key = { _, item -> item.id }
            ) { slotIndex, item ->
                // 현재 슬롯(slotIndex)이 hiddenSlotIndices에 들어있는지 확인
                val isHidden = slotIndex in hiddenSlotIndices
                
                val originalIndex = allItems.indexOf(item)
                
                LuckItemCard(
                    item = item,
                    isHidden = isHidden,
                    onClick = { if (isHidden) onNavigateToDrawing(originalIndex) }
                )
            }
            
            item(span = { GridItemSpan(maxLineSpan) }) { 
                Spacer(modifier = Modifier.height(16.dp)) 
            }
        }
    }
}

private val zodiacList = listOf(
    "양자리", "황소자리", "쌍둥이자리", "게자리", 
    "사자자리", "처녀자리", "천칭자리", "전갈자리", 
    "궁수자리", "염소자리", "물병자리", "물고기자리"
)

@Composable
fun ZodiacSelector(
    currentZodiac: String,
    onZodiacSelected: (String) -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Box(contentAlignment = Alignment.TopCenter) {
        val zodiacHeader = @Composable { open: Boolean ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { showMenu = !showMenu }
            ) {
                Spacer(modifier = Modifier.width(30.dp))
                Text(
                    text = currentZodiac,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "별자리 선택",
                    modifier = Modifier
                        .size(32.dp)
                        .graphicsLayer(scaleY = if (open) -1f else 1f),
                    tint = Color.Black
                )
            }
        }

        zodiacHeader(showMenu)

        if (showMenu) {
            Popup(
                onDismissRequest = { showMenu = false },
                properties = PopupProperties(focusable = true),
                alignment = Alignment.TopCenter
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    zodiacHeader(true)

                    var isVisible by remember { mutableStateOf(false) }
                    LaunchedEffect(Unit) { isVisible = true }

                    AnimatedVisibility(
                        visible = isVisible,
                        enter = expandVertically(
                            animationSpec = tween(200),
                            expandFrom = Alignment.Top
                        ) + fadeIn(tween(200)),
                        exit = shrinkVertically(
                            animationSpec = tween(200),
                            shrinkTowards = Alignment.Top
                        ) + fadeOut(tween(200))
                    ) {
                        Surface(
                            modifier = Modifier
                                .width(180.dp)
                                .heightIn(max = 200.dp),
                            color = Color.White,
                            shape = RoundedCornerShape(12.dp),
                            shadowElevation = 8.dp, // 그림자 추가로 가독성 향상
                            border = BorderStroke(0.5.dp, Color.LightGray)
                        ) {
                            LazyColumn {
                                items(zodiacList, key = { it }) { zodiac ->
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                onZodiacSelected(zodiac)
                                                showMenu = false
                                            }
                                            .padding(vertical = 10.dp, horizontal = 16.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = zodiac,
                                            fontSize = 22.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = if (zodiac == currentZodiac) Color(0xFF6A8CFF) else Color.Black
                                        )
                                    }
                                    if (zodiac != zodiacList.last()) {
                                        HorizontalDivider(
                                            modifier = Modifier.padding(horizontal = 12.dp),
                                            thickness = 0.5.dp,
                                            color = Color.LightGray.copy(alpha = 0.5f)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LuckSubTitle() {
    Row(
        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "오늘의 행운의 아이템", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        Surface(color = Color(0xFFF0F3FF), shape = RoundedCornerShape(12.dp)) {
            Text(
                text = "매일 06시 업데이트",
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                fontSize = 10.sp, color = Color(0xFF6A8CFF), fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun LuckItemCard(item: LuckyItemData, isHidden: Boolean, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(if (isHidden) Color(0xFFE0E5F0) else Color(0xFFF8F9FB))
            .clickable(onClick = onClick)
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isHidden) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFD0D7E5)),
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
                painter = painterResource(id = item.realImageResId),
                contentDescription = item.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(12.dp)),
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
