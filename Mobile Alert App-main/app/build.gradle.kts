plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android") version "2.48" apply false
}

android {
    namespace = "com.example.todoapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.todoapp"
        minSdk = 25
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

    packaging {
        resources {
            excludes += "META-INF/DEPENDENCIES"
        }
    }
}

dependencies {
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.play.services.location)
    val room_version = "2.6.1"
    val camerax_version = "1.3.1"

    implementation ("androidx.navigation:navigation-compose:2.7.0")
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)



    implementation("androidx.room:room-runtime:$room_version")

    // If this project uses any Kotlin source, use Kotlin Symbol Processing (KSP)
    // See Add the KSP plugin to your project
    ksp("androidx.room:room-compiler:$room_version")

    // If this project only uses Java source, use the Java annotationProcessor
    // No additional plugins are necessary
    annotationProcessor("androidx.room:room-compiler:$room_version")

    // optional - Kotlin Extensions and Coroutines support for Room
    implementation("androidx.room:room-ktx:$room_version")

    // optional - RxJava2 support for Room
    implementation("androidx.room:room-rxjava2:$room_version")

    // optional - RxJava3 support for Room
    implementation("androidx.room:room-rxjava3:$room_version")

    // optional - Guava support for Room, including Optional and ListenableFuture
    implementation("androidx.room:room-guava:$room_version")

    // optional - Test helpers
    testImplementation("androidx.room:room-testing:$room_version")

    // optional - Paging 3 Integration
    implementation("androidx.room:room-paging:$room_version")


    // .............THIS IS FOR ANDROID WEAR................
    implementation("androidx.wear.compose:compose-foundation:1.4.0")

    // For Wear Material Design UX guidelines and specifications
    implementation("androidx.wear.compose:compose-material:1.4.0")

    // For integration between Wear Compose and Androidx Navigation libraries
    implementation("androidx.wear.compose:compose-navigation:1.4.0")

    // For Wear preview annotations
    implementation("androidx.wear.compose:compose-ui-tooling:1.4.0")

    // DI using Hilt
    // Hilt Core
    implementation("com.google.dagger:hilt-android:2.51.1")
    ksp("com.google.dagger:hilt-android-compiler:2.51.1")


    // Hilt for ViewModels
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")

    // Twilio API
    implementation("com.twilio.sdk:twilio:10.0.0")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.6.4")

    // OkhttpClient for making request
    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    // Core camera support
    implementation("androidx.camera:camera-core:$camerax_version")
    implementation("androidx.camera:camera-camera2:$camerax_version")

// Lifecycle support (to automatically manage camera start/stop)
    implementation("androidx.camera:camera-lifecycle:$camerax_version")

// Video capture support
    implementation("androidx.camera:camera-video:$camerax_version")

// Preview UI (optional but needed if you want to show live camera preview)
    implementation("androidx.camera:camera-view:$camerax_version")

    implementation ("com.github.PhilJay:MPAndroidChart:v3.1.0")

    // charting and graphings
    implementation ("com.patrykandpatrick.vico:compose:1.13.1")
    implementation ("com.patrykandpatrick.vico:core:1.13.1")

    implementation ("androidx.media3:media3-exoplayer:1.2.1")
    implementation ("androidx.media3:media3-ui:1.2.1")
    implementation ("androidx.media3:media3-common:1.2.1")
}