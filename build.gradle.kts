// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {

    val kotlin_version by extra("1.4.0")
    repositories {
        google()
        jcenter()
        maven("https://dl.bintray.com/giacomoparisi/maven")
        maven("https://dl.bintray.com/balzoeu/auth-droid")
    }

    dependencies {
        classpath(Libs.com_android_tools_build_gradle)
        classpath(Libs.kotlin_gradle_plugin)
        classpath(Libs.google_services)
        classpath(Libs.gradle_bintray_plugin)
        classpath(Libs.android_maven_gradle_plugin)
        classpath(Libs.dokka_android_gradle_plugin)
        classpath(Libs.dokka_gradle_plugin)
        "classpath"("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version")
    }
}

plugins {
    id("de.fayard.buildSrcVersions") version "0.7.0"
}

allprojects {
    repositories {
        google()
        jcenter()
        maven("https://jitpack.io")
        maven("https://dl.bintray.com/arrow-kt/arrow-kt/")
        maven("https://dl.bintray.com/giacomoparisi/maven")
        maven("https://dl.bintray.com/balzoeu/auth-droid")
    }

    tasks.withType<Javadoc>().all {
        enabled = false
    }
}

tasks {

    val clean by registering(Delete::class) {
        delete(buildDir)
    }
}
