plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.gms.google.services)
    kotlin("plugin.serialization") version "2.0.0"
    alias(libs.plugins.ksp)
}

android {
    namespace = "fr.isen.meneroud.pictisen"
    compileSdk = 35

    defaultConfig {
        applicationId = "fr.isen.meneroud.pictisen"
        minSdk = 24
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation ("androidx.recyclerview:recyclerview:1.2.1")
    implementation ("com.google.android.exoplayer:exoplayer:2.19.0")
    implementation ("androidx.appcompat:appcompat:1.6.0")
    implementation ("com.google.firebase:firebase-auth:21.1.0")

    implementation("io.github.jan-tennert.supabase:postgrest-kt")
    implementation("io.ktor:ktor-client-android:3.1.1")
    implementation("io.github.jan-tennert.supabase:storage-kt:1.0.0")
    implementation(platform("io.github.jan-tennert.supabase:bom:3.1.2"))
    implementation("io.coil-kt:coil-compose:2.2.2")
    implementation("com.github.bumptech.glide:compose:1.0.0-beta01")
    implementation("com.google.android.exoplayer:exoplayer:2.18.2")


    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.firebase.database)
    implementation(libs.firebase.storage.ktx)
    implementation(libs.firebase.crashlytics.buildtools)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation ("com.google.firebase:firebase-database-ktx:20.0.4")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.6.2")
    implementation(libs.androidx.activity.compose.v182)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.firebase.auth.ktx.v2211)
    implementation(libs.firebase.auth.ktx)
    implementation(libs.androidx.navigation.compose)
    implementation("com.google.android.exoplayer:exoplayer:2.18.2")
    implementation(platform("io.github.jan-tennert.supabase:bom:3.1.2"))
    implementation("io.github.jan-tennert.supabase:postgrest-kt")
    implementation("io.ktor:ktor-client-android:3.1.1")
    implementation("io.github.jan-tennert.supabase:storage-kt:1.0.0")
    implementation ("io.coil-kt:coil-compose:2.3.0")



}