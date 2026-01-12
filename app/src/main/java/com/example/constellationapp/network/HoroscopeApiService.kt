package com.example.constellationapp.network

import com.example.constellationapp.OhaAsaResponse
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.http.GET

// 모든 네트워크 요청의 기준이 되는 기본 URL
private const val BASE_URL = "https://www.asahi.co.jp/data/ohaasa2020/"

// Json 파싱 시 알 수 없는 키가 있어도 무시하도록 설정
private val json = Json { ignoreUnknownKeys = true }

// --- 아래는 브라우저 위장을 위한 코드 ---

// 1. 모든 요청에 "User-Agent" 헤더를 추가하는 OkHttpClient 생성
private val okHttpClient = OkHttpClient.Builder()
    .addInterceptor { chain ->
        val request = chain.request().newBuilder()
            .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
            .build()
        chain.proceed(request)
    }
    .build()

// -------------------------------------

/**
 * 네트워크 통신을 실제로 수행하는 Retrofit 객체
 */
private val retrofit = Retrofit.Builder()
    // 받아온 데이터의 Content-Type 헤더와 상관없이 JSON으로 파싱하도록 설정
    .addConverterFactory(json.asConverterFactory(MediaType.parse("text/plain")!!))
    // 2. 위에서 만든 위장용 클라이언트를 Retrofit에 장착
    .client(okHttpClient)
    // API의 기본 URL을 설정
    .baseUrl(BASE_URL)
    .build()

/**
 * 운세 API와 통신하기 위한 메서드를 정의하는 인터페이스
 */
interface HoroscopeApiService {
    /**
     * horoscope.json 파일의 내용을 가져옵니다.
     */
    @GET("horoscope.json")
    // 서버가 [ { ... } ] 형태의 배열을 주므로, List<OhaAsaResponse>로 받습니다.
    suspend fun getHoroscopes(): List<OhaAsaResponse>
}

/**
 * 앱의 다른 부분에서 네트워크 서비스에 쉽게 접근할 수 있도록 제공하는 싱글톤 객체
 */
object HoroscopeApi {
    // lazy를 사용해 처음 호출될 때만 Retrofit 서비스 객체를 생성합니다.
    val retrofitService: HoroscopeApiService by lazy {
        retrofit.create(HoroscopeApiService::class.java)
    }
}
