package com.example.constellationapp

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * UI 레이어에서 별자리 정보를 최종적으로 표시하기 위해 사용하는 데이터 클래스
 */
data class ConstellationData (
    val name: String,
    val date: String,
    val score: Int
)

// --- 아래는 네트워크로부터 받은 JSON 데이터의 실제 구조와 일치하는 클래스들입니다. ---

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
