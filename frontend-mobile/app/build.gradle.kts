plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "cit.edu.pawfectmatch"
    compileSdk = 35

    defaultConfig {
        applicationId = "cit.edu.pawfectmatch"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.play.services.maps)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // Google Sign-In and Credential Manager
    implementation("androidx.credentials:credentials:1.5.0")
    implementation("androidx.credentials:credentials-play-services-auth:1.5.0")
    implementation("com.google.android.libraries.identity.googleid:googleid:1.1.1")
    implementation("com.google.android.gms:play-services-auth:21.3.0")

    // Firebase Auth
    implementation("com.google.firebase:firebase-auth:23.0.0")

    // Retrofit and Gson for API calls
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.squareup.okhttp3:logging-interceptor:4.10.0")

    // UI libraries
    implementation("com.facebook.shimmer:shimmer:0.5.0")
    implementation("com.github.bumptech.glide:glide:4.12.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.12.0")
    implementation ("com.google.android.material:material:1.12.0")
    implementation ("com.google.android.flexbox:flexbox:3.0.0")
    implementation("androidx.viewpager2:viewpager2:1.0.0")

    implementation ("com.android.tools:desugar_jdk_libs:2.0.4")
    implementation ("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    implementation ("androidx.lifecycle:lifecycle-viewmodel:2.6.1")
    implementation ("androidx.lifecycle:lifecycle-livedata:2.6.1")
}