plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("com.google.gms.google-services")
}

android {
    compileSdkVersion(AndroidConfig.compile_sdk)
    defaultConfig {
        applicationId = "eu.balzo.authdroid"
        minSdkVersion(AndroidConfig.min_sdk)
        targetSdkVersion(AndroidConfig.target_sdk)
        versionCode = AndroidConfig.version_code
        versionName = AndroidConfig.version_name
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
    implementation(Libs.kotlin_stdlib_jdk7)

    /* ANDROID */
    implementation(Libs.appcompat)
    implementation(Libs.constraintlayout)
    implementation(Libs.lifecycle_runtime_ktx)

    /* FACEBOOK */
    implementation(Libs.facebook_core)
    implementation (Libs.facebook_login)

    /* FIREBASE */
    implementation (Libs.firebase_auth)

    /* GLIDE */
    implementation(Libs.glide)
    annotationProcessor(Libs.com_github_bumptech_glide_compiler)

    /* COROUTINE */
    implementation(Libs.kotlinx_coroutines_core)
    implementation(Libs.kotlinx_coroutines_android)

    /* TEST */
    testImplementation(Libs.junit)
    androidTestImplementation(Libs.androidx_test_runner)
    androidTestImplementation(Libs.espresso_core)
}

