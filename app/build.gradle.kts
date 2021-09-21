import eu.balzo.authdroid.dependencies.*
import eu.balzo.authdroid.projectsettings.ProjectSettings

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("com.google.gms.google-services")
    id("dependencies")
    id("project-settings")
}

android {
    compileSdk = ProjectSettings.compile_sdk
    defaultConfig {
        applicationId = "eu.balzo.authdroid"
        minSdk = ProjectSettings.min_sdk
        targetSdk = ProjectSettings.target_sdk

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        viewBinding = true
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    /* MODULES */
    implementation(project(":core"))
    implementation(project(":google"))
    implementation(project(":facebook"))
    implementation(project(":firebase-core"))
    implementation(project(":firebase-google"))
    implementation(project(":firebase-facebook"))

    /* KOTLIN */
    implementation(Kotlin.stdLib)

    /* ANDROID */
    implementation(AndroidX.appCompat)
    implementation(AndroidX.constraintLayout)
    implementation(AndroidX.Lifecycle.runtimeKtx)

    /* FACEBOOK */
    implementation(Facebook.core)
    implementation(Facebook.login)

    /* FIREBASE */
    implementation(Google.Firebase.authKtx)

    /* GLIDE */
    implementation(Bumptech.glide)
    annotationProcessor(Bumptech.glideCompiler)

    /* COROUTINE */
    implementation(KotlinX.Coroutines.core)
    implementation(KotlinX.Coroutines.android)

}

