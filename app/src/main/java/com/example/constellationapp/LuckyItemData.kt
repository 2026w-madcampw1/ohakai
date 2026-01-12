package com.example.constellationapp

import androidx.compose.ui.geometry.Offset

data class LuckyItemData(
    val id: Int,
    val name: String,
    val description: String,
    val realImageResId: Int,
    val sketchImageResId: Int,
    val stars: List<Offset> = emptyList(),
    // 별자리 완성을 위해 연결해야 하는 정답 선들 (시작 인덱스 to 끝 인덱스)
    val requiredLines: List<Pair<Int, Int>> = emptyList()
)

object LuckItemProvider {
    val items = listOf(
        LuckyItemData(1, "네잎클로버", "마음의 평화를 찾아줘요", R.drawable.lucky_item_real_1, R.drawable.lucky_item_sketch_1,
            listOf(
                Offset(0.319f, 0.609f), // 0
                Offset(0.363f, 0.324f), // 1
                Offset(0.720f, 0.349f), // 2
                Offset(0.698f, 0.652f), // 3
                Offset(0.533f, 0.488f), // 4
                Offset(0.423f, 0.816f)  // 5
            ),
            listOf(0 to 4, 4 to 1, 4 to 2, 4 to 3, 4 to 5)
        ),
        LuckyItemData(2, "보석", "하루를 밝히는 영롱한 빛", R.drawable.lucky_item_real_2, R.drawable.lucky_item_sketch_2,
            listOf(
                Offset(0.513f, 0.770f), // 0
                Offset(0.236f, 0.441f), // 1
                Offset(0.392f, 0.477f), // 2
                Offset(0.604f, 0.476f), // 3
                Offset(0.767f, 0.430f), // 4
                Offset(0.355f, 0.323f), // 5
                Offset(0.637f, 0.327f)  // 6
            ),
            listOf(0 to 2, 0 to 3, 1 to 2, 2 to 3, 3 to 4, 1 to 5, 5 to 6, 6 to 4, 2 to 5, 3 to 6)
        ),
        LuckyItemData(3, "무당벌레", "반가운 소식을 가져와요", R.drawable.lucky_item_real_3, R.drawable.lucky_item_sketch_3,
            listOf(
                Offset(0.508f, 0.389f),
                Offset(0.321f, 0.532f),
                Offset(0.444f, 0.739f),
                Offset(0.642f, 0.704f),
                Offset(0.710f, 0.500f)
            ),
            listOf(2 to 0, 1 to 2, 1 to 0, 0 to 3, 3 to 4, 0 to 4)
        ),
        LuckyItemData(4, "돼지저금통", "정성껏 쌓아가는 미래", R.drawable.lucky_item_real_4, R.drawable.lucky_item_sketch_4,
            listOf(
                Offset(0.348f, 0.538f),
                Offset(0.728f, 0.536f),
                Offset(0.344f, 0.770f),
                Offset(0.448f, 0.798f),
                Offset(0.604f, 0.788f),
                Offset(0.714f, 0.777f)
            ),
            listOf(0 to 2, 0 to 3, 4 to 1, 5 to 1, 0 to 1)
        ),
        LuckyItemData(5, "열쇠", "새로운 기회를 열어줘요", R.drawable.lucky_item_real_5, R.drawable.lucky_item_sketch_5,
            listOf(
                Offset(0.436f, 0.712f),
                Offset(0.344f, 0.649f),
                Offset(0.594f, 0.380f),
                Offset(0.603f, 0.230f),
                Offset(0.754f, 0.312f)
            ),
            listOf(0 to 1, 1 to 2, 2 to 3, 3 to 4, 4 to 2)
        ),
        LuckyItemData(6, "라벤더", "활력을 주는 은은한 향기", R.drawable.lucky_item_real_6, R.drawable.lucky_item_sketch_6,
            listOf(
                Offset(0.276f, 0.514f),
                Offset(0.765f, 0.344f),
                Offset(0.812f, 0.460f),
                Offset(0.780f, 0.577f),
                Offset(0.773f, 0.705f)
            ),
            listOf(0 to 1, 0 to 2, 0 to 3, 0 to 4)
        ),
        LuckyItemData(7, "책", "새로운 지혜를 선사해요", R.drawable.lucky_item_real_7, R.drawable.lucky_item_sketch_7,
            listOf(
                Offset(0.276f, 0.514f),
                Offset(0.765f, 0.344f),
                Offset(0.812f, 0.460f),
                Offset(0.780f, 0.577f),
                Offset(0.773f, 0.705f)
            ),
            listOf(0 to 1, 0 to 2, 0 to 3, 0 to 4)
        ),
        LuckyItemData(8, "나침반", "올바른 길을 안내해요", R.drawable.lucky_item_real_8, R.drawable.lucky_item_sketch_8,
            listOf(
                Offset(0.496f, 0.323f),
                Offset(0.506f, 0.799f),
                Offset(0.762f, 0.548f),
                Offset(0.253f, 0.546f),
                Offset(0.498f, 0.550f)
            ),
            listOf(0 to 4, 1 to 4, 2 to 4, 3 to 4, 3 to 1)
        ),
        LuckyItemData(9, "해바라기", "긍정의 에너지를 전해요", R.drawable.lucky_item_real_9, R.drawable.lucky_item_sketch_9,
            listOf(
                Offset(0.520f, 0.168f),
                Offset(0.293f, 0.310f),
                Offset(0.761f, 0.313f),
                Offset(0.398f, 0.568f),
                Offset(0.728f, 0.548f),
                Offset(0.526f, 0.388f),
                Offset(0.515f, 0.826f)
            ),
            listOf(6 to 5, 3 to 5, 5 to 4, 5 to 2, 1 to 5, 5 to 0)
        ),
        LuckyItemData(10, "무지개", "희망을 품은 일곱 빛깔", R.drawable.lucky_item_real_10, R.drawable.lucky_item_sketch_10,
            listOf(
                Offset(0.170f, 0.680f),
                Offset(0.293f, 0.427f),
                Offset(0.551f, 0.353f),
                Offset(0.789f, 0.463f),
                Offset(0.887f, 0.685f),
                Offset(0.677f, 0.673f),
                Offset(0.369f, 0.683f),
                Offset(0.525f, 0.533f)
            ),
            listOf(6 to 7, 7 to 5, 5 to 4, 3 to 4, 0 to 6, 0 to 1, 1 to 2, 2 to 3)
        )
    )
}