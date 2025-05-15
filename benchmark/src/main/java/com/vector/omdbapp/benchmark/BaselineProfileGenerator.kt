package com.vector.omdbapp.benchmark

import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * 1. Run the below command in Terminal to collect all necessary baseline profiles:
 * "./gradlew :benchmark:pixel2Api31BenchmarkAndroidTest -P android.testInstrumentationRunnerArguments.androidx.benchmark.enabledRules=BaselineProfile"
 * 2. Copy the file below to app/src/main and rename to "baseline-prof.txt"
 * "benchmark/build/outputs/managed_device_android_test_additional_output/benchmark/pixel2Api31/BaselineProfileGenerator_generate-baseline-prof.txt"
 * 3. Add the code below to libs.versions.toml
 * [versions]
 * profileinstaller = "1.4.1"
 *
 * [libraries]
 * androidx-profileinstaller = { group = "androidx.profileinstaller", name = "profileinstaller", version.ref = "profileinstaller" }
 * 4. Add the code below to app module's build.gradle.kts
 * implementation(libs.androidx.profileinstaller)
 */
@RunWith(AndroidJUnit4ClassRunner::class)
class BaselineProfileGenerator {
    @get:Rule
    val rule = BaselineProfileRule()

    @Test
    fun generate():Unit = rule.collect("com.vector.omdbapp"){
        startActivityAndWait()
    }

}