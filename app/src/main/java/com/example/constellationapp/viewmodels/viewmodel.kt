package com.example.constellationapp.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.constellationapp.BuildConfig
import com.example.constellationapp.ConstellationData
import com.example.constellationapp.DataStoreManager
import com.example.constellationapp.data.HoroscopeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HoroscopeViewModel(application: Application) : AndroidViewModel(application) {

    private val dataStoreManager = DataStoreManager(application)
    private val repository = HoroscopeRepository()

    private val _horoscopes = MutableStateFlow<List<ConstellationData>>(emptyList())
    val horoscopes: StateFlow<List<ConstellationData>> = _horoscopes

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isApiKeyMissing = MutableStateFlow(BuildConfig.OPENAI_API_KEY.isBlank())
    val isApiKeyMissing: StateFlow<Boolean> = _isApiKeyMissing

    private val _apiError = MutableStateFlow<String?>(null)
    val apiError: StateFlow<String?> = _apiError

    val userInfo = dataStoreManager.userInfo

    fun fetchHoroscopes() {
        viewModelScope.launch {
            val cachedHoroscopes = dataStoreManager.getTodaysHoroscopes()
            if (cachedHoroscopes != null) {
                _horoscopes.value = cachedHoroscopes
                return@launch
            }

            if (isLoading.value || _isApiKeyMissing.value) {
                return@launch
            }

            _isLoading.value = true
            _apiError.value = null
            try {
                val translatedHoroscopes = repository.fetchAllHoroscopes()
                _horoscopes.value = translatedHoroscopes
                dataStoreManager.saveHoroscopes(translatedHoroscopes)
            } catch (e: Exception) {
                _apiError.value = "데이터를 가져오는 데 실패했습니다: ${e.message}"
            } finally {
                _isLoading.value = false
                Log.d("HoroscopeViewModel", _apiError.toString())
            }
        }
    }
}
