plugins {
    id("com.android.application")
    id("com.google.dagger.hilt.android")
    id("org.jetbrains.kotlin.android")
    alias(libs.plugins.serialization)
    kotlin("kapt")
}

android {
    namespace = "app.xlei.vipexam"
    compileSdk = 34

    defaultConfig {
        applicationId = "app.xlei.vipexam"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        ndk {
            abiFilters += listOf("armeabi-v7a", "arm64-v8a", "x86", "x86_64")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
        languageVersion = "1.9"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.7"
    }
    packaging {
        resources.excludes.add("META-INF/DEPENDENCIES")
        resources.excludes.add("META-INF/LICENSE")
        resources.excludes.add("META-INF/LICENSE.txt")
        resources.excludes.add("META-INF/license.txt")
        resources.excludes.add("META-INF/NOTICE")
        resources.excludes.add("META-INF/NOTICE.txt")
        resources.excludes.add("META-INF/notice.txt")
        resources.excludes.add("META-INF/ASL2.0")
        resources.excludes.add("META-INF/*.kotlin_module")
    }
}

dependencies {
    implementation(projects.core.data)
    implementation(projects.core.database)
    implementation(projects.core.domain)
    implementation(projects.core.network)
    implementation(projects.core.ui)
    implementation(projects.core.template)
    implementation(projects.core.preference)

    implementation(projects.feature.wordlist)
    implementation(projects.feature.settings)
    implementation(projects.feature.history)
    implementation(projects.feature.bookmarks)

    androidTestImplementation(libs.androidx.test.espresso.espresso.core2)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(platform(libs.compose.bom))
    debugImplementation(libs.ui.tooling)
    implementation(libs.activity.compose)
    implementation(libs.activity.ktx)
    implementation(libs.androidx.compose.material.material2)
    implementation(libs.coil.compose)
    implementation(libs.collection.ktx)
    implementation(libs.commons.csv)
    implementation(libs.core.ktx)
    implementation(libs.datastore.core)
    implementation(libs.datastore.preferences)
    implementation(libs.feather)
    implementation(libs.hilt.android)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.lifecycle.viewmodel.compose)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.material)
    implementation(libs.material3)
    implementation(libs.navigation.compose)
    implementation(libs.paging.compose)
    implementation(libs.paging.runtime)
    implementation(libs.palette.ktx)
    implementation(libs.tabler.icons)
    implementation(libs.ui)
    implementation(libs.ui.graphics)
    implementation(libs.ui.tooling.preview)
    implementation(libs.window.size)
    implementation(platform(libs.compose.bom))
    kapt(libs.hilt.android.compiler)
    testImplementation(libs.junit)
}
