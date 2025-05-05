# === Basic Configuration ===

# Retain source file and line number for crash stack traces
-keepattributes SourceFile,LineNumberTable

# Ignore warnings to prevent build-time noise
-ignorewarnings

# === Gson and Retrofit Support ===

# Keep all Gson and Retrofit classes to ensure proper JSON serialization/deserialization
-keep class com.google.gson.** { *; }
-keep class retrofit2.** { *; }

# Keep Retrofit HTTP annotations (e.g., @GET, @POST)
-keepclassmembers class * {
    @retrofit2.http.* <methods>;
}

# Suppress warnings from these libraries
-dontwarn com.google.gson.**
-dontwarn retrofit2.**

# Keep your app's data model classes (used for JSON mapping)
-keep class com.vector.omdbapp.data.model.** { *; }

# === Hilt (Dependency Injection) ===

# Keep annotation metadata used by Hilt
-keepattributes *Annotation*

# Keep Hilt internal and generated classes
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class dagger.internal.codegen.** { *; }

# Suppress warnings from Hilt
-dontwarn dagger.hilt.**

# === Jetpack Compose ===

# Keep all Compose classes and suppress related warnings
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

# Keep ViewModel and ViewModelProvider (used in Compose + Hilt)
-keep class androidx.lifecycle.ViewModel
-keep class androidx.lifecycle.ViewModelProvider

# Required for Kotlin metadata and coroutines used by Compose
-keep class kotlin.Metadata
-keep class kotlin.coroutines.** { *; }
-dontwarn kotlin.coroutines.**

# === Coil (Image Loading Library) ===

# Keep Coil classes to prevent runtime image loading issues
-keep class coil.** { *; }
-dontwarn coil.**

# === App Entry Points ===

# Keep Application and MainActivity classes
-keep class com.vector.omdbapp.App { *; }
-keep class com.vector.omdbapp.MainActivity { *; }

# === SplashScreen & Navigation ===

# Keep AndroidX SplashScreen classes
-keep class androidx.core.splashscreen.** { *; }
-dontwarn androidx.core.splashscreen.**

# Keep Navigation Compose related classes
-keep class androidx.navigation.** { *; }

# Keep Hilt-generated dependency classes
-keep class hilt_aggregated_deps.** { *; }
