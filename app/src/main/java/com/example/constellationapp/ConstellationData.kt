package com.example.constellationapp

import androidx.compose.ui.geometry.Offset
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * UI 레이어에서 별자리 운세 순위 정보를 최종적으로 표시하기 위해 사용하는 데이터 클래스
 * (두 브랜치의 필드를 모두 포함하도록 통합)
 */
data class ConstellationData (
    val name: String,
    val date: String,
    val rank: Int,
    val content: String,
    val imageResId: Int = 0 // 기본값을 0으로 설정
)

/**
 * 행운의 아이템 정보를 담는 데이터 클래스
 */
data class LuckyItemData(
    val id: Int,
    val name: String,
    val description: String,
    val imageResId: Int,
    val stars: List<Offset> = emptyList(),
    // 별자리 완성을 위해 연결해야 하는 정답 선들 (시작 인덱스 to 끝 인덱스)
    val requiredLines: List<Pair<Int, Int>> = emptyList()
)

/**
 * 행운의 아이템 목록을 제공하는 객체
 */
object LuckItemProvider {
    val items = listOf(
        LuckyItemData(1, "네잎클로버", "행운을 가져다주는 네잎클로버", R.drawable.lucky_item1,
            listOf(
                Offset(0.319f, 0.609f), // 0
                Offset(0.363f, 0.324f), // 1
                Offset(0.720f, 0.349f), // 2
                Offset(0.698f, 0.652f), // 3
                Offset(0.533f, 0.488f), // 4
                Offset(0.423f, 0.816f)  // 5
            ),
            // 임의로 정한 정답 선들
            listOf(0 to 4, 4 to 1, 4 to 2, 4 to 3, 4 to 5)
        ),
        // ... (other lucky items) ...
    )
}

// --- 아래는 네트워크로부터 받은 JSON 데이터를 파싱하기 위한 클래스들입니다. ---

/**
 * JSON 응답의 가장 바깥쪽은 배열([ ... ])이며, 그 배열 안에 이 객체가 들어있습니다.
 */
@Serializable
data class OhaAsaResponse(
    // JSON의 "detail" 키에 해당하는 값을 이 필드에 매핑합니다.
    val detail: List<HoroscopeDetailItem>
)

/**
 * "detail" 배열 안에 있는 각 별자리 항목의 구조를 나타내는 클래스
 */
@Serializable
data class HoroscopeDetailItem(
    // JSON의 "ranking_no" 키를 "rank" 필드에 매핑합니다.
    @SerialName("ranking_no")
    val rank: String,

    // JSON의 "horoscope_st" 키를 "signCode" 필드에 매핑합니다.
    @SerialName("horoscope_st")
    val signCode: String,

    // JSON의 "horoscope_text" 키를 "content" 필드에 매핑합니다.
    @SerialName("horoscope_text")
    val content: String
)
