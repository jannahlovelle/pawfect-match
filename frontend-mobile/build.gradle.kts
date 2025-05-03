// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    // Add Android library plugin if you have other modules
    id("com.android.library") version "8.2.0" apply false
    // Add Kotlin plugin if used
    id("org.jetbrains.kotlin.android") version "1.9.0" apply false
}