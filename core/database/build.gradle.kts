plugins {
    id("com.google.dagger.hilt.android")
    alias(libs.plugins.com.google.devtools.ksp)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.org.jetbrains.kotlin.android)
    kotlin("kapt")
}

android {
    namespace = "app.xlei.vipexam.core.database"
    compileSdk = 34

    defaultConfig {
        minSdk = 24

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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    annotationProcessor(libs.room.compiler)
    implementation(libs.room.ktx)
    implementation(libs.room.runtime)
    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.hilt.android)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.androidx.test.espresso.espresso.core2)
    kapt(libs.hilt.android.compiler)
    ksp(libs.room.compiler)
}