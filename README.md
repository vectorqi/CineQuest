# CineQuest 🎬

**CineQuest** is a modern movie search and discovery Android app built with Jetpack Compose and Material 3, following Android's latest best practices.

---

## 📱 Screenshots


---

## ✨ Features

- 🔍 **Search Movies** with skeleton loading animation
- 🎞️ **View Movie Details** with full metadata
- 📋 **Manage Favorites** locally (Room database)
- 🔄 **Infinite Scroll Pagination**
- 🌙 **Light & Dark Theme** dynamic adaptation
- 🚫 **Offline/Network Error Handling** with Retry flow
- ✅ **Hilt DI**, **MVVM Architecture**, **Room Persistence**
- 🚀 **CI/CD** ready with GitHub Actions

---

## 🎨 Highlights

- 100% Jetpack Compose — no XML layouts
- Clean MVVM Architecture with clear separation of concerns
- Material 3 theming with Light and Dark Mode full support
- Local caching using Room Database for favourites
- Smooth skeleton loading animation for better UX
- Friendly network/offline error handling with Retry support
- Dependency Injection with Hilt
- Infinite scroll pagination ready
- CI/CD ready (GitHub Actions integrated)

---

## 🎯 Future Improvements

- 🌐 Integrate TMDb API for live search and data
- 📦 Modularize project (`:data`, `:domain`, `:ui`)
- 🧪 Add more Unit and UI Testing (Compose Testing)
- 📸 Add more dynamic UI (animations, transitions)
- 🧹 Improve Repository and Data Layer abstraction

---

## 🛠️ Tech Stack

| Layer | Libraries |
|:------|:----------|
| UI | Jetpack Compose, Material 3 |
| Architecture | MVVM, Hilt (DI), ViewModel |
| Local Storage | Room Database |
| Network | Retrofit, Coroutine Flows |
| Utilities | Kotlin Coroutines |
| Testing | JUnit |
| CI/CD | GitHub Actions |

---

## 📦 Project Structure

```bash
CineQuest/
├── data/
│   ├── db/          # Room Database setup (Favorite movies)
│   ├── model/       # Data models (Movie, Filters)
│   ├── remote/      # Remote data source (reserved for API integration)
│   └── repository/  # Repository layer to abstract data sources
│
├── di/
│   └── AppModule.kt # Hilt Dependency Injection setup
│
├── ui/
│   ├── components/   # Reusable Composables (SearchBar, ErrorState, Skeletons)
│   ├── navigation/   # Navigation setup with NavController
│   ├── theme/        # Material 3 theming (ColorScheme, Typography)
│   ├── CineQuestAppScreen.kt # Main Scaffold with BottomNavigation
│   ├── FavoriteScreen.kt
│   ├── HomeScreen.kt
│   ├── MovieDetailScreen.kt
│   └── PosterScreen.kt
│
├── util/
│   └── LocalAppImageLoader.kt # Global ImageLoader for Coil
│
├── viewmodel/
│   ├── DetailViewModel.kt
│   ├── FavoriteViewModel.kt
│   └── MovieViewModel.kt
│
├── App.kt         # Application class
└── MainActivity.kt # Entry point

---


