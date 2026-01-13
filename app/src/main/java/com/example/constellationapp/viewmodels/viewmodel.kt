package com.example.constellationapp.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.constellationapp.BuildConfig
import com.example.constellationapp.ConstellationData
import com.example.constellationapp.DataStoreManager
import com.example.constellationapp.HoroscopeDetailItem
import com.example.constellationapp.network.ApiClient
import com.example.constellationapp.network.HoroscopeApi
import com.example.constellationapp.network.Message
import com.example.constellationapp.network.OpenAiRequest
import com.example.constellationapp.network.WeekendHoroscopeService
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.util.Calendar

class HoroscopeViewModel(application: Application) : AndroidViewModel(application) {

    private val dataStoreManager = DataStoreManager(application)

    // 평일 API용 별자리 맵
    private val signCodeToName = mapOf(
        "01" to "양자리", "02" to "황소자리", "03" to "쌍둥이자리",
        "04" to "게자리", "05" to "사자자리", "06" to "처녀자리",
        "07" to "천칭자리", "08" to "전갈자리", "09" to "사수자리",
        "10" to "염소자리", "11" to "물병자리", "12" to "물고기자리"
    )

    // 주말 스크래핑용 별자리 맵 (일본어 -> 한국어)
    private val japaneseToKoreanSignMap = mapOf(
        "おひつじ座" to "양자리", "おうし座" to "황소자리", "ふたご座" to "쌍둥이자리",
        "かに座" to "게자리", "しし座" to "사자자리", "おとめ座" to "처녀자리",
        "てんびん座" to "천칭자리", "さそり座" to "전갈자리", "いて座" to "사수자리",
        "やぎ座" to "염소자리", "みずがめ座" to "물병자리", "うお座" to "물고기자리"
    )

    private val constellationInfo = mapOf(
        "물병자리" to "1/20-2/18", "물고기자리" to "2/19-3/20",
        "양자리" to "3/21-4/19", "황소자리" to "4/20-5/20",
        "쌍둥이자리" to "5/21-6/21", "게자리" to "6/22-7/22",
        "사자자리" to "7/23-8/22", "처녀자리" to "8/23-9/23",
        "천칭자리" to "9/24-10/22", "전갈자리" to "10/23-11/22",
        "사수자리" to "11/23-12/21", "염소자리" to "12/22-1/19"
    )

    private val _horoscopes = MutableStateFlow<List<ConstellationData>>(emptyList())
    val horoscopes: StateFlow<List<ConstellationData>> = _horoscopes

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isApiKeyMissing = MutableStateFlow(BuildConfig.OPENAI_API_KEY.isBlank())
    val isApiKeyMissing: StateFlow<Boolean> = _isApiKeyMissing

    private val _apiError = MutableStateFlow<String?>(null)
    val apiError: StateFlow<String?> = _apiError


    fun fetchHoroscopes() {
        viewModelScope.launch {
            val cachedHoroscopes = dataStoreManager.getTodaysHoroscopes()
            if (cachedHoroscopes != null) {
                _horoscopes.value = cachedHoroscopes
                return@launch
            }

            if (isLoading.value || _isApiKeyMissing.value) {
                return@launch
            }

            _isLoading.value = true
            _apiError.value = null
            try {
                val today = Calendar.getInstance()
                val dayOfWeek = today.get(Calendar.DAY_OF_WEEK)
                val isWeekend = dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY

                // 1. 주말/평일에 따라 다른 데이터 소스에서 원본 데이터를 가져옴
                val rawHoroscopes: List<ConstellationData> = if (isWeekend) {
                    Log.d("HoroscopeViewModel", "주말입니다. 스크래핑을 시작합니다.")
                    val weekendHoroscopes = WeekendHoroscopeService.fetchWeekendHoroscopes()
                    // 일본어 별자리 이름을 한국어로 변환
                    weekendHoroscopes.map {
                        val koreanName = japaneseToKoreanSignMap[it.name] ?: it.name
                        it.copy(name = koreanName, date = constellationInfo[koreanName] ?: "")
                    }
                } else {
                    Log.d("HoroscopeViewModel", "평일입니다. API를 호출합니다.")
                    val ohaAsaResponse = HoroscopeApi.retrofitService.getHoroscopes()
                    val horoscopeDetails = ohaAsaResponse.firstOrNull()?.detail ?: emptyList()
                    horoscopeDetails.map { detail ->
                        detail.toConstellationData(detail.content)
                    }
                }

                // 2. 가져온 데이터를 기반으로 번역 및 개행 처리 진행
                val translatedHoroscopes = rawHoroscopes.map { horoscope ->
                    async {
                        if (horoscope.content.isNotBlank()) {
                            try {
                                val translatedContent = translateText(horoscope.content)
                                // 마침표, 물음표, 느낌표 뒤에 공백이 오는 경우, 해당 문장 부호와 줄바꿈으로 변경하여 개행을 적용합니다.
                                val formattedContent = translatedContent.replace(Regex("([.?!])\\s+"), "$1\n")
                                horoscope.copy(content = formattedContent)
                            } catch (e: HttpException) {
                                val errorBody = e.response()?.errorBody()?.string()
                                _apiError.value = "API 오류: ${e.code()} ${e.message()}\n${errorBody}"
                                horoscope
                            } catch (e: Exception) {
                                _apiError.value = "번역 중 오류: ${e.message}"
                                horoscope
                            }
                        } else {
                            horoscope
                        }
                    }
                }.awaitAll()

                _horoscopes.value = translatedHoroscopes
                dataStoreManager.saveHoroscopes(translatedHoroscopes)

            } catch (e: Exception) {
                _apiError.value = "데이터를 가져오는 데 실패했습니다: ${e.message}"
            } finally {
                _isLoading.value = false
                Log.d("HoroscopeViewModel", _apiError.toString())
            }
        }
    }

    private suspend fun translateText(textToTranslate: String): String {
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

    private fun HoroscopeDetailItem.toConstellationData(content: String): ConstellationData {
        val name = signCodeToName[this.signCode] ?: "알 수 없는 별자리"
        return ConstellationData(
            name = name,
            date = constellationInfo[name] ?: "",
            rank = this.rank.toInt(),
            content = content,
            imageResId = 0
        )
    }
}
