package com.example.constellationapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.constellationapp.ConstellationData
import com.example.constellationapp.HoroscopeDetailItem
import com.example.constellationapp.network.HoroscopeApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * 웹사이트에서 운세 정보를 스크래핑하여 UI에 제공하는 ViewModel
 */
class HoroscopeViewModel : ViewModel() {

    // 서버가 주는 별자리 코드(horoscope_st)와 실제 별자리 이름을 연결하는 맵
    private val signCodeToName = mapOf(
        "01" to "양자리", "02" to "황소자리", "03" to "쌍둥이자리",
        "04" to "게자리", "05" to "사자자리", "06" to "처녀자리",
        "07" to "천칭자리", "08" to "전갈자리", "09" to "사수자리",
        "10" to "염소자리", "11" to "물병자리", "12" to "물고기자리"
    )

    // 별자리의 이름과 날짜 정보를 미리 정의한 맵
    private val constellationInfo = mapOf(
        "물병자리" to "1/20-2/18", "물고기자리" to "2/19-3/20",
        "양자리" to "3/21-4/19", "황소자리" to "4/20-5/20",
        "쌍둥이자리" to "5/21-6/21", "게자리" to "6/22-7/22",
        "사자자리" to "7/23-8/22", "처녀자리" to "8/23-9/23",
        "천칭자리" to "9/24-10/22", "전갈자리" to "10/23-11/22",
        "사수자리" to "11/23-12/21", "염소자리" to "12/22-1/19"
    )

    // 최종적으로 UI에 보여줄 ConstellationData 리스트
    private val _horoscopes = MutableStateFlow<List<ConstellationData>>(emptyList())
    val horoscopes: StateFlow<List<ConstellationData>> = _horoscopes

    // 네트워크 작업의 로딩 상태
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    /**
     * 네트워크 API를 통해 운세 정보를 가져와 UI용 데이터로 변환합니다.
     */
    fun fetchHoroscopes() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // 1. 네트워크 서비스를 통해 JSON 데이터(OhaAsaResponse 리스트)를 가져옵니다.
                val ohaAsaResponse = HoroscopeApi.retrofitService.getHoroscopes()

                // 2. 서버 응답은 배열 안에 객체가 하나 있는 구조이므로, 첫 번째 항목의 detail 리스트를 사용합니다.
                val horoscopeDetails = ohaAsaResponse.firstOrNull()?.detail ?: emptyList()

                // 3. 가져온 JSON 데이터(HoroscopeDetailItem)를 UI용 데이터(ConstellationData)로 변환합니다.
                val uiData = horoscopeDetails.map { 
                    it.toConstellationData()
                }

                _horoscopes.value = uiData

            } catch (e: Exception) {
                // 네트워크 오류 또는 데이터 변환 오류 발생 시
                _horoscopes.value = listOf(ConstellationData(name = "데이터를 가져오는 데 실패했습니다.", date = e.message ?: "알 수 없는 오류", score = 0))
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * 네트워크 데이터(HoroscopeDetailItem)를 UI 데이터(ConstellationData)로 변환하는 확장 함수
     */
    private fun HoroscopeDetailItem.toConstellationData(): ConstellationData {
        val name = signCodeToName[this.signCode] ?: "알 수 없는 별자리"
        return ConstellationData(
            name = name,
            date = constellationInfo[name] ?: "",
            score = 13 - this.rank.toInt() // 순위를 점수로 변환 (1위=12점)
        )
    }
}
