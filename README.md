<div align="center">

# 📱 DevFeed

**An Android app to discover trending GitHub repositories with debounced search, offline bookmarks, and a custom Material 3 UI.**

[![Kotlin](https://img.shields.io/badge/Kotlin-2.0+-7F52FF.svg?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Compose](https://img.shields.io/badge/Jetpack%20Compose-Material%203-4285F4.svg?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com/jetpack/compose)
[![MinSDK](https://img.shields.io/badge/MinSDK-27%2B-3DDC84.svg?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com)
[![Architecture](https://img.shields.io/badge/Architecture-MVVM-orange.svg?style=for-the-badge)](https://developer.android.com/topic/architecture)
[![Room](https://img.shields.io/badge/Database-Room%20(SQLite)-2496ED.svg?style=for-the-badge&logo=sqlite&logoColor=white)](https://developer.android.com/training/data-storage/room)

</div>

---

## 📸 App Preview

<div align="center">
  <table>
    <tr>
      <td align="center"><b>Animated Splash</b></td>
      <td align="center"><b>Home Feed & Chips</b></td>
      <td align="center"><b>Debounced Search</b></td>
      <td align="center"><b>Offline Bookmarks</b></td>
    </tr>
    <tr>
      <td><img src="docs/screenshots/splash.jpg" width="200" alt="Splash Screen" /></td>
      <td><img src="docs/screenshots/home.jpg" width="200" alt="Home Feed" /></td>
      <td><img src="docs/screenshots/search.jpg" width="200" alt="Search Screen" /></td>
      <td><img src="docs/screenshots/saved.jpg" width="200" alt="Saved Screen" /></td>
    </tr>
  </table>
</div>

---

## ✨ Working Features

### 🔥 Live Trending Feed
- Fetches top GitHub repositories (`>1000★`) in real time using the GitHub Search API.
- Filter chips for fast language switching: **All, Kotlin, Java, Python, Rust, TypeScript, Go, C++**.
- Pull-to-refresh and shimmer skeleton loading placeholders.

### ⚡ Debounced Search
- 500 ms debounce window using Kotlin Flow to minimize unnecessary API calls.
- Minimum 2-character query before hitting the GitHub API.

### 💾 Bookmarks & Offline Access
- Bookmark and unbookmark repositories, persisted locally with **Room**.
- Dedicated **Saved** screen that works entirely offline.
- Network monitoring via `ConnectivityManager` with an animated offline banner.

### 📱 Native Android Integrations
- **Chrome Custom Tabs** to open repositories inside the app.
- **Android Share Sheet** to share repository titles and URLs.
- **Haptic feedback** on bookmarking and sharing.
- **Edge-to-edge** Mint-to-Sage gradient UI with transparent system bars and a custom animated splash screen.

---

## 🏗️ Architecture

DevFeed follows the **MVVM** pattern:

```text
  ┌───────────────────────────────────┐
  │   UI Layer (Jetpack Compose)      │
  │   Home, Search, Saved screens     │
  └─────────────────▲─────────────────┘
                    │ Observes StateFlow
  ┌─────────────────▼─────────────────┐
  │             ViewModel             │
  │   State, debounced search flow    │
  └─────────────────▲─────────────────┘
                    │ Flow
  ┌─────────────────▼─────────────────┐
  │            Repository             │
  └────────┬─────────────────┬────────┘
           │                 │
  ┌────────▼────────┐  ┌─────▼────────┐
  │  Local (Room)   │  │ Remote       │
  │                 │  │ (Retrofit)   │
  └─────────────────┘  └──────────────┘
```

---

## 🛠️ Tech Stack

| Layer | Technology | Usage |
|---|---|---|
| Language | Kotlin 2.0+ | Modern Android development |
| UI | Jetpack Compose + Material 3 | Declarative UI, theme, animations |
| Architecture | MVVM | Separation of UI and logic |
| DI | Dagger Hilt | Compile-time dependency injection |
| Local storage | Room | Saved repositories |
| Networking | Retrofit 2 + OkHttp + Gson | GitHub API and JSON parsing |
| Async | Coroutines + StateFlow / Flow | Async work and debounced search |
| Images | Coil Compose | Owner avatars |
| In-app browser | AndroidX Browser | Chrome Custom Tabs |

---

## 📂 Project Structure

```text
com.example.devfeed/
├── data/
│   ├── local/          # Room database, DAO, entity
│   ├── remote/         # Retrofit service and GitHub DTOs
│   └── repository/     # Repository implementation
├── di/                 # Hilt modules
├── ui/
│   ├── components/     # RepoCard, ShimmerItem, OfflineBanner
│   ├── explore/        # Search screen and ViewModel
│   ├── home/           # Trending feed and HomeViewModel
│   ├── saved/          # Bookmarked repositories screen
│   ├── splash/         # Animated splash screen
│   └── theme/          # Colors, typography, theme
└── util/               # Network observer
```

---

## ⚠️ Known Limitations
- Uses the unauthenticated GitHub API, so search is rate limited
  (about 10 requests per minute).
- Only bookmarked repositories are available offline.

## 🗺️ Roadmap
- [ ] Paging 3 (infinite scroll)
- [ ] Offline cache for the home feed
- [ ] Repo detail screen
- [ ] Unit tests and CI

---

## 🚀 Getting Started

**Prerequisites:** Android Studio (latest stable), JDK 17+, a device or
emulator running Android 8.0 (API 27) or higher.

```bash
git clone https://github.com/Faheem4hmad/DevFeed.git
```

1. Open the project in Android Studio.
2. Let Gradle sync.
3. Run on an emulator or device (Shift + F10).
