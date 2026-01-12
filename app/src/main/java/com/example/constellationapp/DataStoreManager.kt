package com.example.constellationapp

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

class DataStoreManager(private val context: Context) {

    companion object {
        val USER_NAME = stringPreferencesKey("user_name")
        val USER_MONTH = intPreferencesKey("user_month")
        val USER_DAY = intPreferencesKey("user_day")
        val IS_ONBOARDING_COMPLETED = booleanPreferencesKey("is_onboarding_completed")
        // 별자리별 해금 달성도 (JSON 형태나 별도 키로 저장 가능하나 여기선 간단히 맵핑 키로 예시)
        // 실제로는 각 아이템 ID별로 키를 동적으로 생성하거나 문자열 하나에 직렬화해서 저장합니다.
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

    // 별자리별 달성도 저장 (아이템 ID를 키로 사용)
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
}
