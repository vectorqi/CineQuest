package com.vector.omdbapp

import android.app.Application
import com.vector.omdbapp.util.PrewarmUtil
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application(){
    override fun onCreate() {
        super.onCreate()
        // Prewarm Retrofit and Gson to avoid VerifyClass bottleneck on main thread
        PrewarmUtil.prewarmCriticalClasses()
    }
}