package com.example.constellationapp.network

import com.example.constellationapp.ConstellationData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup

object WeekendHoroscopeService {

    private const val WEEKEND_URL = "https://www.tv-asahi.co.jp/goodmorning/uranai/"

    suspend fun fetchWeekendHoroscopes(): List<ConstellationData> {
        return withContext(Dispatchers.IO) {
            try {
                val doc = Jsoup.connect(WEEKEND_URL).get()

                // 1. 순위 정보를 먼저 가져와서 Map으로 만듭니다. (Key: 별자리 영문ID, Value: 순위)
                val rankMap = doc.select("ul.rank-box li a").associate {
                    val rank = it.selectFirst("img.rank")?.attr("src")?.substringAfterLast("-")?.removeSuffix(".png")?.toIntOrNull()
                    val signId = it.attr("data-label")
                    signId to rank
                }

                // 2. 각 별자리 상세 정보 박스를 순회합니다.
                val items = doc.select("div.seiza-box")

                val horoscopeList = items.mapNotNull { element ->
                    val seizaBoxId = element.id()
                    val rank = rankMap[seizaBoxId] ?: return@mapNotNull null

                    val japaneseSign = element.selectFirst("p.seiza-txt")?.ownText()?.trim() ?: ""
                    val content = element.selectFirst("p.read")?.text() ?: ""
                    
                    ConstellationData(
                        name = japaneseSign, // 이 단계에서는 일본어 이름입니다.
                        date = "주말 운세", // 임시
                        rank = rank,
                        content = content,
                        imageResId = 0
                    )
                }
                horoscopeList.sortedBy { it.rank }
            } catch (e: Exception) {
                e.printStackTrace()
                emptyList()
            }
        }
    }
}
