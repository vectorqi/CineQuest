# OMDB App

A demo Android application built with **Jetpack Compose** and **MVVM** architecture, showcasing movie search with pagination using the OMDB API. The code uses **StateFlow**, **snapshotFlow**, and **Channel**/`Flow` for reactive state management.

## Author

**Vector Qi** 

## Features

- **Movie Search**: Search for movies by title using the [OMDB API](https://www.omdbapi.com/).
- **Infinite Scroll Pagination**: Automatically loads more movies when scrolling to the bottom of the list.
- **MVVM Architecture**: Uses `ViewModel` + `Repository` pattern to separate business logic from UI.
- **Jetpack Compose UI**: Fully declarative approach for building modern UIs.
- **Network Error Handling**: Displays error or status messages via a `Snackbar`.
- **Duplicate Filtering**: Filters out repeated movies if the server returns duplicates.
- **Internationalization**: All user-facing strings are extracted into `strings.xml` for easier localization.

## Tech Stack

- **Kotlin** (language)
- **Jetpack Compose** (UI)
- **ViewModel** / **AndroidViewModel** (Lifecycle-aware)
- **StateFlow** / **Flow** (Reactive state + data flow)
- **Retrofit + Coroutine** (Network requests in background)
- **Coil** (Async image loading)
- **OMDB API** (Movie data source)

## Setup

1. **OMDB API Key**:  
   - Obtain a free or paid API key from [omdbapi.com/apikey.aspx](http://www.omdbapi.com/apikey.aspx).
   - In `MovieRepository` or your config, replace `"YOUR_TEST_API_KEY_HERE"` with your own key.

2. **Open project in Android Studio** and ensure you have a recent version of the **Kotlin plugin** installed.

3. **Run the app** on an emulator or physical device (API 21+).

## How to Use

1. **Search**: Type a movie title (e.g., `Batman`) and press the **Search** button.
2. **Label Toggle**: Each movie item has a "Show Label" / "Hide Label" button that toggles a small label.
3. **Pagination**: Scroll to the bottom of the list to trigger more results automatically.
4. **Empty Search**: If the search field is empty, a Snackbar message prompts you to enter a query.


## Known Issues / Future Enhancements

- **Refresh**: No "pull-to-refresh" implemented yet.
- **Paging 3**: Could replace manual pagination logic with official Paging 3 library.

