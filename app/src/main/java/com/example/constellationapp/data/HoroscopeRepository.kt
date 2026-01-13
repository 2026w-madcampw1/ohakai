package com.example.constellationapp.data

import android.util.Log
import com.example.constellationapp.BuildConfig
import com.example.constellationapp.ConstellationData
import com.example.constellationapp.HoroscopeDetailItem
import com.example.constellationapp.network.ApiClient
import com.example.constellationapp.network.HoroscopeApi
import com.example.constellationapp.network.Message
import com.example.constellationapp.network.OpenAiRequest
import com.example.constellationapp.network.WeekendHoroscopeService
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import java.util.Calendar

class HoroscopeRepository {

    suspend fun fetchAllHoroscopes(): List<ConstellationData> = coroutineScope {
        val today = Calendar.getInstance()
        val dayOfWeek = today.get(Calendar.DAY_OF_WEEK)
        val isWeekend = dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY

        // 1. 주말/평일에 따라 다른 데이터 소스에서 원본 데이터를 가져옴
        val rawHoroscopes: List<ConstellationData> = if (isWeekend) {
            Log.d("HoroscopeRepository", "주말 - 스크래핑 시작")
            val weekendHoroscopes = WeekendHoroscopeService.fetchWeekendHoroscopes()
            weekendHoroscopes.map {
                val koreanName = ZodiacConstants.japaneseToKoreanSignMap[it.name] ?: it.name
                it.copy(
                    name = koreanName,
                    date = ZodiacConstants.constellationInfo[koreanName] ?: ""
                )
            }
        } else {
            Log.d("HoroscopeRepository", "평일 - API 호출")
            val ohaAsaResponse = HoroscopeApi.retrofitService.getHoroscopes()
            val horoscopeDetails = ohaAsaResponse.firstOrNull()?.detail ?: emptyList()
            horoscopeDetails.map { detail ->
                detail.toConstellationData()
            }
        }

        // 2. 가져온 데이터를 기반으로 번역 및 개행 처리 진행
        rawHoroscopes.map { horoscope ->
            async {
                if (horoscope.content.isNotBlank()) {
                    try {
                        val translatedContent = translateText(horoscope.content)
                        val formattedContent = translatedContent.replace(". ", ".\n")
                        horoscope.copy(content = formattedContent)
                    } catch (e: Exception) {
                        Log.e("HoroscopeRepository", "번역 오류: ${e.message}")
                        horoscope
                    }
                } else {
                    horoscope
                }
            }
        }.awaitAll()
    }

    private suspend fun translateText(textToTranslate: String): String {
        if (BuildConfig.OPENAI_API_KEY.isBlank()) return textToTranslate

        val request = OpenAiRequest(
            model = "gpt-4-turbo-preview",
            messages = listOf(
                Message("system", "너는 ChatGPT처럼 자연스러운 한국어로 번역하는 AI 번역가다.\n아래 규칙을 반드시 지켜서 번역한다:\n1. 원문의 의미를 정확하게 유지한다.\n2. 말투는 `~해요` 또는 `~에요`로 끝나는 부드러운 존댓말을 사용한다.\n3. 모든 문장 끝에는 온점(.)이나 물음표(?) 같은 문장 부호를 반드시 붙인다.\n4. 불필요한 설명, 인사, 따옴표 없이 오직 최종 번역문만 출력한다."),
                Message("user", textToTranslate)
            ),
            temperature = 0.0f
        )

        val apiKey = "Bearer ${BuildConfig.OPENAI_API_KEY}"
        val response = ApiClient.openAiApi.getTranslation(apiKey, request)

        return response.choices.firstOrNull()?.message?.content?.trim() ?: textToTranslate
    }

    private fun HoroscopeDetailItem.toConstellationData(): ConstellationData {
        val name = ZodiacConstants.signCodeToName[this.signCode] ?: "알 수 없는 별자리"
        return ConstellationData(
            name = name,
            date = ZodiacConstants.constellationInfo[name] ?: "",
            rank = this.rank.toInt(),
            content = this.content,
            imageResId = 0
        )
    }
}
