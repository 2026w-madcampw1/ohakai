package com.example.constellationapp.screens

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.constellationapp.R
import java.util.Calendar
import kotlin.random.Random

@Composable
fun ConstellationDetailScreen(name: String) {
    val dailyHoroscope = remember(name) { // Add name to remember key
        val seed = Calendar.getInstance().get(Calendar.DAY_OF_YEAR) + name.hashCode()
        val random = Random(seed)
        val horoscopes = listOf(
            "오늘은 작은 선택이 큰 차이를 만드는 날이에요.",
            "생각보다 일이 술술 풀릴 수 있으니 너무 걱정 말아요.",
            "우연한 만남 속에 좋은 힌트가 숨어 있어요.",
            "서두르지 말고 한 박자 쉬어가면 더 좋아요.",
            "오늘의 행운은 익숙한 것보다 새로운 곳에 있어요.",
            "말 한마디가 관계를 부드럽게 만들 수 있어요.",
            "마음이 끌리는 쪽을 따라가도 괜찮은 하루예요.",
            "작은 성취가 큰 자신감으로 이어질 수 있어요.",
            "오늘은 나 자신을 조금 더 믿어도 돼요.",
            "예상치 못한 소식이 기분을 바꿀 수 있어요.",
            "너무 완벽하려 하지 않아도 충분히 잘하고 있어요.",
            "오늘의 선택은 미래의 나에게 고마운 결정이 될 거예요.",
            "잠시 멈춰서 주변을 둘러보면 답이 보여요.",
            "가벼운 대화 속에서 좋은 기회가 생길 수 있어요.",
            "마음먹은 일은 오늘 시작하기 좋아요.",
            "작은 친절이 뜻밖의 행운으로 돌아와요.",
            "오늘은 혼자보다는 함께할 때 더 좋아요.",
            "고민하던 문제에 실마리가 보이기 시작해요.",
            "감정 표현을 아끼지 말아보세요.",
            "오늘의 운은 차분함에서 나와요.",
            "평소보다 나에게 관대해져도 괜찮은 날이에요.",
            "익숙한 일 속에서 새로운 재미를 찾을 수 있어요.",
            "오늘은 결과보다 과정이 더 중요한 하루예요.",
            "잠깐의 휴식이 하루를 훨씬 가볍게 만들어줘요.",
            "기대하지 않은 곳에서 도움을 받을 수 있어요.",
            "오늘은 직감이 꽤 잘 맞는 날이에요.",
            "미뤄두었던 일을 정리하기 좋아요.",
            "나를 웃게 하는 것에 시간을 써보세요.",
            "오늘 하루는 무난하지만, 그래서 더 안정적이에요.",
            "오늘의 행운 키워드는 ‘여유’예요."
        )
        horoscopes[random.nextInt(horoscopes.size)]
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Constellation Image
        Image(
            painter = painterResource(id = R.drawable.constellation),
            contentDescription = name,
            modifier = Modifier.size(200.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Horoscope Text
        Text(
            text = dailyHoroscope,
            fontSize = 18.sp,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))

        // Lucky Items
        Text(text = "오늘의 행운의 아이템", fontSize = 20.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Image(painter = painterResource(id = R.drawable.constellation), contentDescription = "Lucky Item 1", modifier = Modifier.size(100.dp))
            Image(painter = painterResource(id = R.drawable.constellation), contentDescription = "Lucky Item 2", modifier = Modifier.size(100.dp))
            Image(painter = painterResource(id = R.drawable.constellation), contentDescription = "Lucky Item 3", modifier = Modifier.size(100.dp))
        }
    }
}
