[]()

---

# 오하카이

> 사용자의 생년월일을 기반으로 운세 정보를 제공하고, 인터랙티브 그림판으로 별자리를 그려보는 안드로이드 앱
> 

**ConstellationApp**은 Jetpack Compose를 사용하여 100% 코드로 UI를 구축한 현대적인 안드로이드 애플리케이션입니다. 운세 정보를 확인하는 기능과 더불어, `Canvas`와 `detectDragGestures`를 조합하여 사용자에게 별자리를 직접 그려보는 동적이고 몰입감 높은 경험을 선사합니다. OpenAI API 연동을 통해 별자리에 대한 풍부한 설명을 제공하는 것 또한 주요 특징입니다.

## 스크린샷

| 운세 순위 | 오늘의 운세 | 별자리 그리기 |
| --- | --- | --- |
|  | *(Screenshot Placeholder)* | *(Screenshot Placeholder)* |

## 📋 목차

1. [주요 기능 (Features)](https://www.notion.so/2e85a1b8355780829be0f655fd28133a?pvs=21)
2. [데이터 처리 과정](https://www.notion.so/2e85a1b8355780829be0f655fd28133a?pvs=21)
3. [아키텍처 및 사용 기술 (Architecture & Tech Stack)](https://www.notion.so/2e85a1b8355780829be0f655fd28133a?pvs=21)
4. [설치 및 실행 방법 (Installation & How to Run)](https://www.notion.so/2e85a1b8355780829be0f655fd28133a?pvs=21)
5. [향후 개선 사항 (Future Improvements)](https://www.notion.so/2e85a1b8355780829be0f655fd28133a?pvs=21)
6. [제작자 (Authors)](https://www.notion.so/2e85a1b8355780829be0f655fd28133a?pvs=21)
7. [라이선스 (License)](https://www.notion.so/2e85a1b8355780829be0f655fd28133a?pvs=21)

---

## 주요 기능

앱의 모든 화면은 Jetpack Compose를 사용하여 선언적으로 구현되었으며, `screens` 패키지 내에 각 기능별로 모듈화되어 있습니다.

### 1. 온보딩

- **역할**: 신규 사용자를 위한 앱의 첫인상과 초기 설정을 담당합니다.
- **구현**: `StartScreen`에서 시작하여 `BirthInputScreen`으로 이동, 사용자의 이름과 생년월일을 입력받습니다. 커스텀 `NumberPicker`는 `LazyColumn`과 `rememberSnapFlingBehavior`를 조합하여 부드러운 스크롤링 경험을 제공하며, 입력된 정보는 `DataStoreManager`를 통해 기기에 영구적으로 저장됩니다.

### 2. 운세 순위 목록

- **역할**: `HoroscopeViewModel`로부터 운세 순위 데이터를 받아 사용자에게 목록 형태로 보여주는 메인 대시보드입니다.
- **구현**: `LazyColumn`을 사용하여 효율적으로 운세 순위 목록을 표시합니다. `ViewModel`의 `StateFlow`를 `collectAsState`로 구독하여 데이터 변경을 감지하고 UI를 반응적으로 업데이트합니다.

### 3. 오늘의 운세 및 행운 아이템

- **역할**: 특정 별자리의 상세 운세 내용과 오늘의 행운 아이템을 시각적으로 보여주는 화면입니다.
- **구현**: `HorizontalPager`를 사용하여 여러 별자리의 운세를 좌우로 스와이프하며 탐색할 수 있는 인터랙티브한 UI를 제공합니다. 사용자가 그린 행운 아이템의 진행도는 `DataStoreManager`에서 실시간으로 수집하여 잠금 해제 여부를 표시합니다.

### 4. 나만의 별자리 그리기

- **역할**: 앱의 핵심적인 인터랙티브 기능으로, 사용자가 직접 행운 아이템을 그려보는 게임 경험을 제공합니다.
- **구현**: `Canvas` API와 `pointerInput`의 `detectDragGestures`를 조합하여 사용자의 드래그 입력을 실시간으로 감지하고 별과 별 사이를 연결하는 선을 그립니다. `LuckItemProvider`에 정의된 정답과 사용자의 입력을 비교하여 정답/오답을 판별하고, `animateFloatAsState`를 활용한 다채로운 애니메이션으로 높은 몰입감을 선사합니다.

---

## 데이터 처리 과정

이 앱의 핵심 기능 중 하나는 외부 소스에서 운세 데이터를 가져와 가공하여 사용자에게 보여주는 것입니다. 이 모든 과정은 `HoroscopeRepository`에서 총괄하며, **캐싱, 데이터 소스 분기, 병렬 처리, AI 번역** 등 다양한 기술이 적용됩니다.

### 1. 안전한 비동기 처리와 스레드 관리

- **메인 스레드 보호**: 안드로이드에서는 UI 렌더링을 담당하는 메인 스레드에서 네트워크 요청과 같은 블로킹(blocking) I/O 작업을 수행할 수 없습니다. 이 프로젝트는 **코틀린 코루틴(Kotlin Coroutines)**을 사용하여 모든 비동기 작업을 처리함으로써 이 원칙을 철저히 준수합니다.
- **I/O 작업 스레드 전환**: Jsoup을 이용한 웹 크롤링과 같이 실행 시간이 길고 예측 불가능한 I/O 작업은 `withContext(Dispatchers.IO)` 블록으로 감싸, 실행 컨텍스트를 **I/O 작업에 최적화된 백그라운드 스레드로 명시적으로 전환**합니다. 이를 통해 앱의 UI가 멈추는 현상 없이 부드러운 사용자 경험을 보장합니다.

### 2. 캐시 우선 전략 (Cache-First Strategy)

- `HoroscopeViewModel`은 네트워크 요청 전에 `DataStore`에 오늘 날짜의 유효한 캐시 데이터가 있는지 먼저 확인합니다. 캐시가 존재하면 네트워크 요청을 생략하고 즉시 UI를 업데이트하여, 불필요한 API 호출을 줄이고 데이터 로딩 속도를 크게 향상시킵니다.

### 3. 동적 데이터 소스 분기

- 캐시가 없는 경우, `HoroscopeRepository`는 현재 날짜를 기준으로 주말과 평일을 구분하여 서로 다른 방식으로 데이터를 가져옵니다.
    - **평일**: `HoroscopeApiService`를 통해 일본 아사히 방송의 **JSON 파일**(`horoscope.json`)을 직접 가져옵니다.
    - **주말**: `WeekendHoroscopeService`에서 **Jsoup 라이브러리를 사용**하여 특정 웹사이트의 HTML을 직접 **스크래핑(웹 크롤링)**하여 운세 데이터를 추출합니다.

### 4. 병렬 AI 번역 및 가공

- 가져온 12개의 별자리 운세 원문(일본어)을 `coroutineScope`와 `async`를 활용하여 OpenAI API에 **동시에 병렬적으로 번역 요청**을 보냅니다.
- **프롬프트 엔지니어링**: 단순 번역 요청이 아닌, `system` 메시지를 통해 **AI의 페르소나('부드러운 존댓말을 쓰는 번역가')와 말투, 문장 규칙 등을 사전에 정의**하여 일관되고 자연스러운 결과물을 얻습니다.

---

## 아키텍처 및 사용 기술

이 프로젝트는 Google이 권장하는 **현대적인 안드로이드 앱 아키텍처(Modern App Architecture)** 가이드를 따르며, 관심사 분리(Separation of Concerns), 단일 진실 공급원(Single Source of Truth), 단방향 데이터 흐름(Unidirectional Data Flow) 원칙을 지향합니다.

### 아키텍처: MVVM (Model-View-ViewModel)

```
   UI Layer (Compose) <--- ViewModel (StateFlow) <--- Repository <--- (Network / Local)
      (Events)       --->     (Functions)      ---> (Suspend Func) -->   (Data Sources)

```

- **View (UI Layer)**: `screens` 패키지에 위치한 **Jetpack Compose** 함수들로 구성됩니다. `ViewModel`로부터 UI 상태(`StateFlow`)를 구독하여 화면을 그리고, 사용자 이벤트를 `ViewModel`에 전달하는 역할만 수행합니다.
- **ViewModel (`HoroscopeViewModel`)**: `viewmodels` 패키지에 위치하며, UI에 표시될 상태를 관리하고 비즈니스 로직을 처리합니다. 코루틴을 사용하여 `Repository`로부터 데이터를 비동기적으로 가져오며, UI가 소비할 수 있는 `StateFlow` 형태로 상태를 노출합니다.
- **Model (Data Layer)**: `data` 와 `network` 패키지로 구성됩니다.
    - **`Repository` (`HoroscopeRepository`)**: 앱의 모든 데이터에 대한 **단일 진실 공급원(Single Source of Truth)** 역할을 합니다. `ViewModel`은 데이터 소스를 직접 알 필요 없이, Repository가 제공하는 간단한 메소드를 호출하기만 하면 됩니다.

### 사용 기술

- **언어**: Kotlin 100%
- **UI**: Jetpack Compose (Navigation, Material3, Animation)
- **아키텍처**: Android Architecture Components (ViewModel, Lifecycle, Navigation)
- **비동기 처리**: Kotlin Coroutines & Flow
- **네트워킹**:
    - Retrofit & OkHttp: Type-safe한 REST API 통신
    - Jsoup: 주말 운세 데이터 수집을 위한 HTML 스크래핑(웹 크롤링)
- **JSON 파싱**: Kotlinx Serialization
- **데이터 지속성**: Jetpack DataStore
- **API**: OpenAI API (`v1/chat/completions`)

---

## 설치 및 실행 방법

1. **저장소 복제**:
    
    ```bash
    git clone <https://github.com/2026w-madcampw1/ohakai.git>
    
    ```
    
2. **API 키 설정**:
    - 프로젝트의 루트 디렉토리에 `local.properties` 파일을 생성합니다.
    - 파일에 다음과 같이 OpenAI API 키를 추가합니다:
        
        ```
        OPENAI_API_KEY="YOUR_API_KEY"
        
        ```
        
3. **앱 실행**:
    - Android Studio에서 프로젝트를 열고, Gradle 동기화가 완료되면 앱을 실행합니다.

---

## 향후 개선 사항

- 행운의 아이템
- 상세 정보 제공 (별자리 신화, 역사 등)
- 다양한 앱 테마 지원 (다크 모드 등)
- 홈 화면 위젯 지원

---

## 팀원

- 박은지 (@eunji)
- 이동근 (@142spp)

---

## 라이선스 (License)

This project is licensed under the MIT License.