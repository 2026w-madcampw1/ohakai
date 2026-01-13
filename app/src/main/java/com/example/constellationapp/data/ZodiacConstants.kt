package com.example.constellationapp.data

object ZodiacConstants {
    // 평일 API용 별자리 맵 (코드 -> 한국어)
    val signCodeToName = mapOf(
        "01" to "양자리", "02" to "황소자리", "03" to "쌍둥이자리",
        "04" to "게자리", "05" to "사자자리", "06" to "처녀자리",
        "07" to "천칭자리", "08" to "전갈자리", "09" to "사수자리",
        "10" to "염소자리", "11" to "물병자리", "12" to "물고기자리"
    )

    // 주말 스크래핑용 별자리 맵 (일본어 -> 한국어)
    val japaneseToKoreanSignMap = mapOf(
        "おひつじ座" to "양자리", "おうし座" to "황소자리", "ふたご座" to "쌍둥이자리",
        "かに座" to "게자리", "しし座" to "사자자리", "おとめ座" to "처녀자리",
        "てんびん座" to "천칭자리", "さそ리座" to "전갈자리", "いて座" to "사수자리",
        "やぎ座" to "염소자리", "みずがめ座" to "물병자리", "うお座" to "물고기자리"
    )

    // 별자리별 기간 정보
    val constellationInfo = mapOf(
        "물병자리" to "1/20-2/18", "물고기자리" to "2/19-3/20",
        "양자리" to "3/21-4/19", "황소자리" to "4/20-5/20",
        "쌍둥이자리" to "5/21-6/21", "게자리" to "6/22-7/22",
        "사자자리" to "7/23-8/22", "처녀자리" to "8/23-9/23",
        "천칭자리" to "9/24-10/22", "전갈자리" to "10/23-11/22",
        "사수자리" to "11/23-12/21", "염소자리" to "12/22-1/19"
    )

    // 별자리 이름 -> 인덱스 매핑 (DataStoreManager 등에서 사용)
    val zodiacNameToIndex = mapOf(
        "양자리" to 1, "황소자리" to 2, "쌍둥이자리" to 3, "게자리" to 4,
        "사자자리" to 5, "처녀자리" to 6, "천칭자리" to 7, "전갈자리" to 8,
        "사수자리" to 9, "염소자리" to 10, "물병자리" to 11, "물고기자리" to 12
    )

    /**
     * 월/일을 바탕으로 별자리 이름을 반환합니다.
     */
    fun getZodiacSign(month: Int, day: Int): String {
        return when (month) {
            1 -> if (day >= 20) "물병자리" else "염소자리"
            2 -> if (day >= 19) "물고기자리" else "물병자리"
            3 -> if (day >= 21) "양자리" else "물고기자리"
            4 -> if (day >= 20) "황소자리" else "양자리"
            5 -> if (day >= 21) "쌍둥이자리" else "황소자리"
            6 -> if (day >= 22) "게자리" else "쌍둥이자리"
            7 -> if (day >= 23) "사자자리" else "게자리"
            8 -> if (day >= 23) "처녀자리" else "사자자리"
            9 -> if (day >= 23) "천칭자리" else "처녀자리"
            10 -> if (day >= 23) "전갈자리" else "천칭자리"
            11 -> if (day >= 23) "사수자리" else "전갈자리"
            12 -> if (day >= 22) "염소자리" else "사수자리"
            else -> ""
        }
    }
}
