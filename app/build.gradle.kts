plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    kotlin("android.extensions")
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
}

dependencies {

    /* MODULES */
    implementation(project(":core"))
    implementation(project(":rx-facebook"))
    implementation(project(":rx-google"))
    implementation (project(":rx-firebase"))
    implementation (project(":rx-firebase-facebook"))
    implementation (project(":rx-firebase-google"))
    implementation (project(":arrow-google"))
    implementation(project(":arrow-facebook"))

    /* KOTLIN */
    implementation (Libs.kotlin_stdlib_jdk7)

    /* ANDROID */
    implementation (Libs.appcompat)
    implementation (Libs.constraintlayout)

    /* FACEBOOK */
    implementation (Libs.facebook_core)
    implementation (Libs.facebook_login)

    /* FIREBASE */
    implementation (Libs.firebase_auth)

    /* RX */
    implementation (Libs.rxkotlin)
    implementation (Libs.rxandroid)
    implementation (Libs.rxjava)

    /* ARROW */
    implementation(Libs.arrow_fx)
    implementation(Libs.arrow_syntax)
    kapt(Libs.arrow_meta)

    /* GLIDE */
    implementation (Libs.glide)
    annotationProcessor (Libs.com_github_bumptech_glide_compiler)

    /* TEST */
    testImplementation (Libs.junit)
    androidTestImplementation (Libs.androidx_test_runner)
    androidTestImplementation (Libs.espresso_core)
}

