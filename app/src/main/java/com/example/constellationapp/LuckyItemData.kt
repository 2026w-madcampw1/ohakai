package com.example.constellationapp

import androidx.compose.ui.geometry.Offset

data class LuckyItemData(
    val id: Int,
    val name: String,
    val description: String,
    val realImageResId: Int,
    val sketchImageResId: Int,
    val stars: List<Offset> = emptyList(),
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
            listOf(0 to 2, 0 to 3, 1 to 2, 2 to 3, 3 to 4, 1 to 5, 5 to 6, 6 to 4, 2 to 5, 3 to 6, 0 to 1, 0 to 4)
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
                Offset(0.513f, 0.830f),
                Offset(0.399f, 0.558f),
                Offset(0.638f, 0.567f),
                Offset(0.368f, 0.119f),
                Offset(0.654f, 0.167f)
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
            listOf(0 to 4, 1 to 4, 2 to 4, 3 to 4, 3 to 0)
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
        ),
        LuckyItemData(11, "커피", "일상의 활력을 깨워줘요", R.drawable.lucky_item_real_11, R.drawable.lucky_item_sketch_11,
            listOf(
                Offset(0.244f, 0.348f),
                Offset(0.708f, 0.341f),
                Offset(0.348f, 0.706f),
                Offset(0.591f, 0.702f)
            ),
            listOf(0 to 2, 0 to 1, 2 to 3, 3 to 1)
        ),
        LuckyItemData(12, "빨간 목도리", "온기를 전하는 따뜻한 마음", R.drawable.lucky_item_real_12, R.drawable.lucky_item_sketch_12,
            listOf(
                Offset(0.285f, 0.267f),
                Offset(0.708f, 0.274f),
                Offset(0.500f, 0.480f),
                Offset(0.689f, 0.777f)
            ),
            listOf(0 to 2, 2 to 1, 0 to 1, 2 to 3)
        ),
        LuckyItemData(13, "금화", "풍요로운 결실의 기쁨", R.drawable.lucky_item_real_13, R.drawable.lucky_item_sketch_13,
            listOf(
                Offset(0.532f, 0.286f),
                Offset(0.725f, 0.417f),
                Offset(0.723f, 0.612f),
                Offset(0.506f, 0.734f),
                Offset(0.321f, 0.620f),
                Offset(0.315f, 0.419f)
            ),
            listOf(0 to 5, 5 to 4, 4 to 3, 3 to 2, 2 to 1, 1 to 0)
        ),
        LuckyItemData(14, "황수정", "자신감을 불어넣는 빛", R.drawable.lucky_item_real_14, R.drawable.lucky_item_sketch_14,
            listOf(
            Offset(0.547f, 0.736f),
            Offset(0.696f, 0.589f),
            Offset(0.492f, 0.487f),
            Offset(0.472f, 0.195f)
            ),
            listOf(3 to 2, 2 to 0, 0 to 1)
        ),
        LuckyItemData(15, "울양말", "포근하게 감싸는 위로", R.drawable.lucky_item_real_15, R.drawable.lucky_item_sketch_15,
            listOf(
                Offset(0.569f, 0.179f),
                Offset(0.542f, 0.522f),
                Offset(0.281f, 0.683f),
                Offset(0.709f, 0.283f),
                Offset(0.726f, 0.627f),
                Offset(0.409f, 0.803f)
            ),
            listOf(0 to 1, 2 to 1, 5 to 4, 3 to 4)
        ),
        LuckyItemData(16, "은반지", "변치 않는 약속의 징표", R.drawable.lucky_item_real_16, R.drawable.lucky_item_sketch_16,
            listOf(
                Offset(0.513f, 0.323f),
                Offset(0.707f, 0.449f),
                Offset(0.653f, 0.646f),
                Offset(0.425f, 0.658f),
                Offset(0.327f, 0.459f)
            ),
            listOf(0 to 4, 4 to 3, 3 to 2, 2 to 1, 0 to 1)
        ),
        LuckyItemData(17, "파란색 만년필", "지혜로운 기록의 동반자", R.drawable.lucky_item_real_17, R.drawable.lucky_item_sketch_17,
            listOf(
                Offset(0.260f, 0.845f),
                Offset(0.385f, 0.701f),
                Offset(0.557f, 0.493f),
                Offset(0.850f, 0.167f)
            ),
            listOf(0 to 1, 1 to 2, 2 to 3)
        ),
        LuckyItemData(18, "문스톤", "내면의 평화를 비춰줘요", R.drawable.lucky_item_real_18, R.drawable.lucky_item_sketch_18,
            listOf(
                Offset(0.512f, 0.282f),
                Offset(0.524f, 0.700f),
                Offset(0.693f, 0.422f),
                Offset(0.682f, 0.597f),
                Offset(0.366f, 0.401f),
                Offset(0.356f, 0.550f)
            ),
            listOf(0 to 4, 4 to 5, 5 to 1, 2 to 3, 1 to 3, 0 to 2)
        ),
        LuckyItemData(19, "실크", "매끄럽게 흐르는 행운", R.drawable.lucky_item_real_19, R.drawable.lucky_item_sketch_19,
            listOf(
                Offset(0.805f, 0.159f),
                Offset(0.583f, 0.342f),
                Offset(0.517f, 0.540f),
                Offset(0.307f, 0.675f),
                Offset(0.747f, 0.367f),
                Offset(0.752f, 0.575f),
                Offset(0.598f, 0.757f)
            ),
            listOf(3 to 2, 2 to 1, 1 to 0, 6 to 5, 5 to 4, 4 to 0)
        ),
        LuckyItemData(20, "죽순", "강인한 생명력의 기운", R.drawable.lucky_item_real_20, R.drawable.lucky_item_sketch_20,
            listOf(
                Offset(0.411f, 0.757f),
                Offset(0.532f, 0.879f),
                Offset(0.699f, 0.731f),
                Offset(0.530f, 0.166f)
            ),
            listOf(0 to 1, 1 to 2, 0 to 3, 3 to 2)
        ),
        LuckyItemData(21, "가죽 다이어리", "꿈을 적어 내려가는 시간", R.drawable.lucky_item_real_21, R.drawable.lucky_item_sketch_21,
            listOf(
                Offset(0.451f, 0.212f),
                Offset(0.270f, 0.605f),
                Offset(0.636f, 0.693f),
                Offset(0.817f, 0.288f),
                Offset(0.718f, 0.481f)
            ),
            listOf(1 to 0, 0 to 3, 1 to 2, 2 to 4, 4 to 3)
        )
    )
}