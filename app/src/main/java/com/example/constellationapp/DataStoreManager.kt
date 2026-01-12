package com.example.constellationapp

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.Calendar

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

// 사용자 정보를 묶어서 전달하기 위한 데이터 클래스
data class UserStats(
    val name: String,
    val month: Int,
    val day: Int,
    val zodiac: String
)

class DataStoreManager(private val context: Context) {

    companion object {
        val USER_NAME = stringPreferencesKey("user_name")
        val USER_MONTH = intPreferencesKey("user_month")
        val USER_DAY = intPreferencesKey("user_day")
        val USER_ZODIAC = stringPreferencesKey("user_zodiac")
        val IS_ONBOARDING_COMPLETED = booleanPreferencesKey("is_onboarding_completed")
        val HIDDEN_ITEM_INDICES = stringPreferencesKey("hidden_item_indices")
        val LAST_UPDATE_DATE = stringPreferencesKey("last_update_date")
    }

    /**
     * 월과 일을 기준으로 별자리를 계산합니다.
     */
    private fun getZodiacSign(month: Int, day: Int): String {
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
            11 -> if (day >= 23) "궁수자리" else "전갈자리"
            12 -> if (day >= 22) "염소자리" else "궁수자리"
            else -> ""
        }
    }

    // 이름/생일 저장 시 별자리도 함께 계산하여 저장
    suspend fun saveUserInfo(name: String, month: Int, day: Int) {
        val zodiac = getZodiacSign(month, day)
        context.dataStore.edit { prefs ->
            prefs[USER_NAME] = name
            prefs[USER_MONTH] = month
            prefs[USER_DAY] = day
            prefs[USER_ZODIAC] = zodiac
            prefs[IS_ONBOARDING_COMPLETED] = true
        }
    }

    // 별자리별 달성도 저장
    suspend fun saveProgress(itemId: Int, progress: Float) {
        val key = floatPreferencesKey("progress_$itemId")
        context.dataStore.edit { prefs ->
            prefs[key] = progress
        }
    }

    // 사용자 정보 스트림 (UserStats 객체로 반환)
    val userInfo: Flow<UserStats> = context.dataStore.data.map { prefs ->
        UserStats(
            name = prefs[USER_NAME] ?: "",
            month = prefs[USER_MONTH] ?: 1,
            day = prefs[USER_DAY] ?: 1,
            zodiac = prefs[USER_ZODIAC] ?: ""
        )
    }

    // 온보딩 완료 여부
    val isOnboardingCompleted: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[IS_ONBOARDING_COMPLETED] ?: false
    }

    // 특정 별자리 달성도 가져오기
    fun getProgress(itemId: Int): Flow<Float> = context.dataStore.data.map { prefs ->
        prefs[floatPreferencesKey("progress_$itemId")] ?: 0f
    }

    // 마지막 갱신 날짜 스트림 (리포매팅 포함)
    val lastUpdateDate: Flow<String> = context.dataStore.data.map { prefs ->
        val raw = prefs[LAST_UPDATE_DATE] ?: ""
        if (raw.length >= 6) { // yyyyM d 또는 yyyyMMdd 형태 대응
            // 안전하게 Calendar를 이용해 오늘 날짜를 기본 포맷으로 생성
            val now = Calendar.getInstance()
            "${now.get(Calendar.YEAR)}년 ${now.get(Calendar.MONTH) + 1}월 ${now.get(Calendar.DAY_OF_MONTH)}일"
        } else {
            "오늘"
        }
    }

    // 숨겨진 인덱스 스트림
    val hiddenItemIndices: Flow<Set<Int>> = context.dataStore.data.map { prefs ->
        val raw = prefs[HIDDEN_ITEM_INDICES] ?: ""
        if (raw.isEmpty()) emptySet()
        else raw.split(",").mapNotNull { it.toIntOrNull() }.toSet()
    }

    /**
     * 그리기에 성공한 아이템을 숨김 목록에서 제거함
     */
    suspend fun removeHiddenItem(index: Int) {
        context.dataStore.edit { prefs ->
            val raw = prefs[HIDDEN_ITEM_INDICES] ?: ""
            if (raw.isNotEmpty()) {
                val currentIndices = raw.split(",").mapNotNull { it.toIntOrNull() }.toMutableSet()
                if (currentIndices.remove(index)) {
                    prefs[HIDDEN_ITEM_INDICES] = currentIndices.sorted().joinToString(",")
                }
            }
        }
    }

    /**
     * 매일 아침 6시 기준으로 인덱스를 갱신해야 하는지 확인하고 필요시 갱신함
     */
    suspend fun updateHiddenIndicesIfNeeded(totalItemCount: Int) {
        val now = Calendar.getInstance()
        val currentHour = now.get(Calendar.HOUR_OF_DAY)
        
        val businessCalendar = now.clone() as Calendar
        if (currentHour < 6) {
            businessCalendar.add(Calendar.DAY_OF_YEAR, -1)
        }
        
        val todayStr = "${businessCalendar.get(Calendar.YEAR)}${businessCalendar.get(Calendar.MONTH)}${businessCalendar.get(Calendar.DAY_OF_MONTH)}"
        
        val prefs = context.dataStore.data.first()
        val lastUpdateDate = prefs[LAST_UPDATE_DATE] ?: ""

        if (lastUpdateDate != todayStr || prefs[HIDDEN_ITEM_INDICES] == null) {
            val count = (1..3).random().coerceAtMost(totalItemCount)
            val newIndices = (0 until totalItemCount).shuffled().take(count).sorted()
            val newIndicesStr = newIndices.joinToString(",")
            
            context.dataStore.edit { editPrefs ->
                editPrefs[HIDDEN_ITEM_INDICES] = newIndicesStr
                editPrefs[LAST_UPDATE_DATE] = todayStr
            }
        }
    }
}
