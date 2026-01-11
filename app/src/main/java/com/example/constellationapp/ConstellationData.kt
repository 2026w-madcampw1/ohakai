package com.example.constellationapp

import androidx.compose.ui.geometry.Offset

data class ConstellationData (
    val name: String,
    val date: String,
    val score: Int,
    val imageResId: Int
)

data class LuckyItemData(
    val id: Int,
    val name: String,
    val description: String,
    val imageResId: Int,
    val stars: List<Offset> = emptyList()
)

object LuckItemProvider {
    val items = listOf(
        LuckyItemData(1, "네잎클로버", "행운을 가져다주는 네잎클로버", R.drawable.lucky_item1,
            listOf(
                Offset(0.319f, 0.609f),
                Offset(0.363f, 0.324f),
                Offset(0.720f, 0.349f),
                Offset(0.698f, 0.652f),
                Offset(0.533f, 0.488f),
                Offset(0.423f, 0.816f)
            )),
        LuckyItemData(2, "보석", "반짝이는 행운의 보석", R.drawable.lucky_item2),
        LuckyItemData(3, "무당벌레", "", R.drawable.lucky_item3),
        LuckyItemData(4, "돼지저금통", "", R.drawable.lucky_item4),
        LuckyItemData(5, "열쇠", "", R.drawable.lucky_item5),
        LuckyItemData(6, "", "", R.drawable.lucky_item6),
        LuckyItemData(7, "책", "", R.drawable.lucky_item7),
        LuckyItemData(8, "나침반", "", R.drawable.lucky_item8),
        LuckyItemData(9, "해바라기", "", R.drawable.lucky_item9),
        LuckyItemData(10, "무지개", "", R.drawable.lucky_item10),
        LuckyItemData(11, "종", "", R.drawable.lucky_item11),
        LuckyItemData(12, "말발굽", "", R.drawable.lucky_item12)
    )
}
