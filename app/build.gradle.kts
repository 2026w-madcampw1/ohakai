import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    // 최상단 build.gradle.kts에 등록된 serialization 플러그인을 이 모듈에서 사용하도록 적용합니다.
    id("org.jetbrains.kotlin.plugin.serialization")
}

// local.properties 파일 로드
val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localPropertiesFile.inputStream().use { input ->
        localProperties.load(input)
    }
}

android {
    namespace = "com.example.constellationapp"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.constellationapp"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // BuildConfig에 API 키 추가
        buildConfigField("String", "OPENAI_API_KEY", "\"${localProperties.getProperty("OPENAI_API_KEY")}\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
        // Serialization 관련 Opt-In 경고를 프로젝트 전체에서 무시하도록 설정합니다.
        freeCompilerArgs += "-opt-in=kotlinx.serialization.InternalSerializationApi"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    
    sourceSets {
        getByName("main") {
            res.srcDirs("src/main/res", "src/main/res-lucky")
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material3.adaptive.navigation.suite)
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation("androidx.datastore:datastore-preferences-core:1.2.0")
    implementation("androidx.datastore:datastore-preferences:1.2.0")
    //implementation("io.coil-kt:coil-compose:2.5.0")

    // JSON 처리를 위한 라이브러리 추가
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")

    // 네트워크 통신을 위한 Retrofit 라이브러리 추가
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    // Retrofit이 Kotlinx Serialization을 사용하도록 해주는 변환기 라이브러리
    implementation("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:1.0.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // HTML 파싱을 위한 Jsoup 라이브러리
    implementation("org.jsoup:jsoup:1.22.1")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
