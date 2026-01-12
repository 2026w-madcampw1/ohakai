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

class DataStoreManager(private val context: Context) {

    companion object {
        val USER_NAME = stringPreferencesKey("user_name")
        val USER_MONTH = intPreferencesKey("user_month")
        val USER_DAY = intPreferencesKey("user_day")
        val IS_ONBOARDING_COMPLETED = booleanPreferencesKey("is_onboarding_completed")
        val HIDDEN_ITEM_INDICES = stringPreferencesKey("hidden_item_indices")
        val LAST_UPDATE_DATE = stringPreferencesKey("last_update_date")
    }

    // 이름/생일 저장
    suspend fun saveUserInfo(name: String, month: Int, day: Int) {
        context.dataStore.edit { prefs ->
            prefs[USER_NAME] = name
            prefs[USER_MONTH] = month
            prefs[USER_DAY] = day
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

    // 사용자 정보 스트림
    val userInfo: Flow<Triple<String, Int, Int>> = context.dataStore.data.map { prefs ->
        Triple(
            prefs[USER_NAME] ?: "",
            prefs[USER_MONTH] ?: 1,
            prefs[USER_DAY] ?: 1
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
