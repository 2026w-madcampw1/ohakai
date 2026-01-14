# 🌌 오하카이 (Ohakai) - 별자리 운세 플랫폼

> **"당신의 밤하늘을 읽어드립니다."**  
> 오하카이는 사용자의 생년월일을 기반으로 별자리를 판별하고, 일본의 유명 운세 사이트인 OhaAsa의 데이터를 기반으로 오늘의 운세와 행운의 아이템을 제공하는 안드로이드 애플리케이션입니다.

---

## ✨ 주요 기능 (Key Features)

### 🗓 별자리 판별 및 온보딩
- 사용자의 생년월일을 입력받아 정확한 별자리를 자동으로 판별합니다.
- 깔끔하고 감성적인 디자인의 온보딩 화면을 제공합니다.

### 🏆 오늘의 별자리 랭킹 (Tab 1: 순위)
- 실시간 데이터를 바탕으로 12성좌의 오늘의 운세 순위를 한눈에 확인하세요.
- 매일매일 달라지는 별들의 위치를 랭킹으로 만나보세요.

### 🔮 행운의 아이템 & 상세 운세 (Tab 2: 운세)
- 단순한 운세를 넘어, 오늘 나에게 행운을 가져다줄 구체적인 아이템 정보를 제공합니다.
- 스와이프 인터페이스를 통해 각 별자리의 상세한 운세 내용을 감상할 수 있습니다.

### 🎨 감성적인 별자리 그리기 (Tab 3: 그리기)
- 밤하늘의 점들을 이어 나만의 별자리를 직접 그려보는 인터페이스를 제공합니다.
- OpenAI API를 활용하여 별자리에 담긴 이야기와 연계된 창의적인 경험을 선사합니다.

---

## 🛠 기술 스택 (Tech Stack)

### **Frontend**
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose (Modern Declarative UI)
- **Theme**: Material 3 (Color, Typography, Shapes)
- **Navigation**: Jetpack Navigation Compose

### **Architecture & Data**
- **Architecture**: MVVM (Model-View-ViewModel)
- **Local Storage**: Jetpack DataStore (Proto/Preferences for user settings)
- **Async Processing**: Kotlin Coroutines & Flow

### **Network & AI**
- **Networking**: Retrofit 2, OkHttpClient
- **Serialization**: Kotlinx Serialization
- **AI Integration**: OpenAI API (for dynamic content generation)
- **Data Source**: OhaAsa Data Scraping integration

---

## 📂 프로젝트 구조 (Project Structure)

```text
com.example.constellationapp
├── data             # 전역 상수 및 데이터 클래스 (ZodiacConstants 등)
├── network          # API 통신 및 클라이언트 (ApiClient, Retrofit 서비스)
├── screens          # Jetpack Compose 기반 각 화면 구현 (Start, List, Image, Drawing 등)
├── ui.theme         # 앱의 디자인 시스템 (Color, Type, Theme)
├── viewmodels       # 비즈니스 로직 및 상태 관리 (HoroscopeViewModel)
└── MainActivity.kt  # 앱의 진입점 및 내비게이션 설정
```

---

## 🚀 시작하기 (Getting Started)

### **빌드 및 실행**
1. **Android Studio** (Ladybug 이후 권장)를 엽니다.
2. 프로젝트를 클론하거나 엽니다.
3. `local.properties` 파일에 필요한 API Key를 설정합니다 (OpenAI API 등).
4. `Run` 버튼을 클릭하여 시뮬레이터 또는 실기기에서 실행합니다.

### **편의용 스크립트**
CMD/PowerShell 환경에서 다음 스크립트를 사용할 수 있습니다:
- `./run_app.ps1`: 빌드 및 앱 실행
- `./stop_app.ps1`: 앱 중지
- `./view_logs.ps1`: Logcat 로그 확인

---

## 🤝 협업 및 규칙

### **Branch Strategy**
- `main`: 최종 배포 브랜치
- `develop`: 통합 개발 브랜치
- `feat/`: 신규 기능 개발
- `fix/`: 버그 수정

### **Commit Message Convention**
- `Feat`: 신규 기능 구현
- `Fix`: 버그 수정
- `Docs`: 문서 작업
- `Design`: UI/UX 디자인 변경
- `Refactor`: 코드 리팩토링
