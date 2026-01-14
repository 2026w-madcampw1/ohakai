package com.example.constellationapp

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.Random
import com.example.constellationapp.data.ZodiacConstants

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

data class UserStats(
    val name: String,
    val month: Int,
    val day: Int,
    val zodiac: String,
    val zodiacIndex: Int
)

class DataStoreManager(private val context: Context) {

    companion object {
        val USER_NAME = stringPreferencesKey("user_name")
        val USER_MONTH = intPreferencesKey("user_month")
        val USER_DAY = intPreferencesKey("user_day")
        val USER_ZODIAC = stringPreferencesKey("user_zodiac")
        val USER_ZODIAC_INDEX = intPreferencesKey("user_zodiac_index")
        val IS_ONBOARDING_COMPLETED = booleanPreferencesKey("is_onboarding_completed")
        val TODAY_LUCKY_INDICES = stringPreferencesKey("today_lucky_indices")
        val HIDDEN_ITEM_SLOTS = stringPreferencesKey("hidden_item_indices")
        val LAST_UPDATE_DATE = stringPreferencesKey("last_update_date")

        // 운세 캐싱을 위한 키
        private val HOROSCOPE_CACHE_DATE = stringPreferencesKey("horoscope_cache_date")
        private val HOROSCOPE_CACHE_DATA = stringSetPreferencesKey("horoscope_cache_data")
        private val HOROSCOPE_MESSAGE_KEY = stringPreferencesKey("horoscope_message")

        val zodiacNameToIndex = ZodiacConstants.zodiacNameToIndex
    }

    val horoscopeMessage: Flow<String> =
        context.dataStore.data.map { prefs ->
            prefs[HOROSCOPE_MESSAGE_KEY] ?: ""
        }

    suspend fun updateHoroscopeMessage(message: String) {
        context.dataStore.edit { prefs ->
            prefs[HOROSCOPE_MESSAGE_KEY] = message
        }
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

        val zodiacNameToIndex = ZodiacConstants.zodiacNameToIndex
    }

    private fun calculateLuckyIndices(zodiacIndex: Int, daySeed: Long, totalCount: Int): List<Int> {
        val allIndices = (0 until totalCount).toList().shuffled(Random(daySeed))
        val startOffset = ((zodiacIndex - 1) * 4) % totalCount
        val result = mutableListOf<Int>()
        for (i in 0 until 4) {
            result.add(allIndices[(startOffset + i) % totalCount])
        }
        return result.shuffled(Random(daySeed + zodiacIndex))
    }

    private fun getZodiacSign(month: Int, day: Int): String {
        return ZodiacConstants.getZodiacSign(month, day)
    }

    suspend fun saveUserInfo(name: String, month: Int, day: Int) {
        val zodiac = getZodiacSign(month, day)
        val zodiacIndex = zodiacNameToIndex[zodiac] ?: 1
        context.dataStore.edit { prefs ->
            prefs[USER_NAME] = name
            prefs[USER_MONTH] = month
            prefs[USER_DAY] = day
            prefs[USER_ZODIAC] = zodiac
            prefs[USER_ZODIAC_INDEX] = zodiacIndex
            prefs[IS_ONBOARDING_COMPLETED] = true
        }
    }

    suspend fun updateZodiacAndItems(newZodiac: String, totalItemCount: Int) {
        val zodiacIndex = zodiacNameToIndex[newZodiac] ?: 1
        context.dataStore.edit { prefs ->
            prefs[USER_ZODIAC] = newZodiac
            prefs[USER_ZODIAC_INDEX] = zodiacIndex
            // 해금 상태(HIDDEN_ITEM_INDICES)는 유지함 (슬롯 번호 기준이므로)
        }
    }

    suspend fun getLuckyIndicesForZodiac(zodiacName: String, totalCount: Int): List<Int> {
        val zodiacIndex = zodiacNameToIndex[zodiacName] ?: 1
        val prefs = context.dataStore.data.first()
        val lastDateStr = prefs[LAST_UPDATE_DATE] ?: "0"
        return calculateLuckyIndices(zodiacIndex, lastDateStr.toLongOrNull() ?: 0L, totalCount)
    }

    val userInfo: Flow<UserStats> = context.dataStore.data.map { prefs ->
        val month = prefs[USER_MONTH] ?: 1
        val day = prefs[USER_DAY] ?: 1
        val zodiac = prefs[USER_ZODIAC] ?: getZodiacSign(month, day)
        val zodiacIndex: Int = prefs[USER_ZODIAC_INDEX] ?: (zodiacNameToIndex[zodiac] ?: 1)
        UserStats(prefs[USER_NAME] ?: "", month, day, zodiac, zodiacIndex)
    }.distinctUntilChanged()

    val isOnboardingCompleted: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[IS_ONBOARDING_COMPLETED] ?: false
    }.distinctUntilChanged()

    val todayLuckyIndices: Flow<List<Int>> = context.dataStore.data.map { prefs ->
        val zodiacIndex = prefs[USER_ZODIAC_INDEX] ?: 1
        val lastDateStr = prefs[LAST_UPDATE_DATE] ?: "0"
        calculateLuckyIndices(zodiacIndex, lastDateStr.toLongOrNull() ?: 0L, 21)
    }.distinctUntilChanged()

    val hiddenItemIndices: Flow<Set<Int>> = context.dataStore.data.map { prefs ->
        val raw = prefs[HIDDEN_ITEM_SLOTS] ?: ""
        if (raw.isEmpty()) emptySet()
        else raw.split(",").mapNotNull { it.toIntOrNull() }.toSet()
    }.distinctUntilChanged()

    val lastUpdateDate: Flow<String> = context.dataStore.data.map { prefs ->
        val raw = prefs[LAST_UPDATE_DATE] ?: ""
        if (raw.length >= 8) {
            val y = raw.substring(0, 4)
            val m = raw.substring(4, 6).toIntOrNull()?.toString() ?: ""
            val d = raw.substring(6, 8).toIntOrNull()?.toString() ?: ""
            "${y}년 ${m}월 ${d}일"
        } else {
            "오늘"
        }
    }.distinctUntilChanged()

    /**
     * 슬롯 인덱스(0, 1, 2, 3)를 직접 받아서 잠금 해제
     */
    suspend fun removeHiddenSlot(slotIndex: Int) {
        context.dataStore.edit { prefs ->
            val raw = prefs[HIDDEN_ITEM_SLOTS] ?: ""
            val currentIndices = raw.split(",").mapNotNull { it.toIntOrNull() }.toMutableSet()
            if (currentIndices.remove(slotIndex)) {
                prefs[HIDDEN_ITEM_SLOTS] = currentIndices.sorted().joinToString(",")
            }
        }
    }

    suspend fun updateHiddenIndicesIfNeeded(totalItemCount: Int) {
        val now = Calendar.getInstance()
        val currentHour = now.get(Calendar.HOUR_OF_DAY)
        val businessCalendar = now.clone() as Calendar
        if (currentHour < 6) businessCalendar.add(Calendar.DAY_OF_YEAR, -1)
        
        val dateInt = businessCalendar.get(Calendar.YEAR) * 10000 +
                     (businessCalendar.get(Calendar.MONTH) + 1) * 100 +
                     businessCalendar.get(Calendar.DAY_OF_MONTH)
        val todayStr = dateInt.toString()
        
        val prefs = context.dataStore.data.first()
        val lastUpdateDateVal = prefs[LAST_UPDATE_DATE] ?: ""

        if (lastUpdateDateVal != todayStr || prefs[HIDDEN_ITEM_SLOTS] == null) {
            context.dataStore.edit { editPrefs ->
                editPrefs[HIDDEN_ITEM_SLOTS] = "0,1,2,3"
                editPrefs[LAST_UPDATE_DATE] = todayStr
            }
        }
    }
}
