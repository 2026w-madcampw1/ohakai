package com.example.constellationapp

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

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

        // 운세 캐싱을 위한 키
        private val HOROSCOPE_CACHE_DATE = stringPreferencesKey("horoscope_cache_date")
        private val HOROSCOPE_CACHE_DATA = stringSetPreferencesKey("horoscope_cache_data")
    }

    // --- 신규 추가: 운세 캐싱 관련 함수 ---

    suspend fun saveHoroscopes(horoscopes: List<ConstellationData>) {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val cacheData = horoscopes.map { "${it.name}|${it.date}|${it.rank}|${it.content}" }.toSet()
        context.dataStore.edit {
            it[HOROSCOPE_CACHE_DATE] = today
            it[HOROSCOPE_CACHE_DATA] = cacheData
        }
    }

    suspend fun getTodaysHoroscopes(): List<ConstellationData>? {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val prefs = context.dataStore.data.first()

        if (prefs[HOROSCOPE_CACHE_DATE] == today) {
            val cacheData = prefs[HOROSCOPE_CACHE_DATA]
            if (!cacheData.isNullOrEmpty()) {
                return cacheData.mapNotNull { 
                    val parts = it.split("|")
                    if (parts.size == 4) {
                        ConstellationData(parts[0], parts[1], parts[2].toIntOrNull() ?: 0, parts[3])
                    } else {
                        null
                    }
                }.sortedBy { it.rank }
            }
        }
        return null
    }

    // --- 기존 함수 복원 ---

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

    suspend fun saveProgress(itemId: Int, progress: Float) {
        val key = floatPreferencesKey("progress_$itemId")
        context.dataStore.edit { prefs ->
            prefs[key] = progress
        }
    }

    val userInfo: Flow<UserStats> = context.dataStore.data.map { prefs ->
        UserStats(
            name = prefs[USER_NAME] ?: "",
            month = prefs[USER_MONTH] ?: 1,
            day = prefs[USER_DAY] ?: 1,
            zodiac = prefs[USER_ZODIAC] ?: ""
        )
    }

    val isOnboardingCompleted: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[IS_ONBOARDING_COMPLETED] ?: false
    }

    fun getProgress(itemId: Int): Flow<Float> = context.dataStore.data.map { prefs ->
        prefs[floatPreferencesKey("progress_$itemId")] ?: 0f
    }

    val lastUpdateDate: Flow<String> = context.dataStore.data.map { prefs ->
        val raw = prefs[LAST_UPDATE_DATE] ?: ""
        if (raw.length >= 6) {
            val now = Calendar.getInstance()
            "${now.get(Calendar.YEAR)}년 ${now.get(Calendar.MONTH) + 1}월 ${now.get(Calendar.DAY_OF_MONTH)}일"
        } else {
            "오늘"
        }
    }

    val hiddenItemIndices: Flow<Set<Int>> = context.dataStore.data.map { prefs ->
        val raw = prefs[HIDDEN_ITEM_INDICES] ?: ""
        if (raw.isEmpty()) emptySet()
        else raw.split(",").mapNotNull { it.toIntOrNull() }.toSet()
    }

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
