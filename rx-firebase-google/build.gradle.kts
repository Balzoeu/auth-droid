import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
    kotlin("android.extensions")
    id("maven-publish")
    id("com.github.dcendents.android-maven")
    id("com.jfrog.bintray")
    id("org.jetbrains.dokka-android")
}

androidExtensions { isExperimental = true }

android {
    compileSdkVersion(AndroidConfig.compile_sdk)
    defaultConfig {
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

    /* AUTHDROID */
    implementation(project(":core"))
    implementation(project(":google-core"))
    implementation(project(":rx-firebase"))

    /* ANDROID */
    implementation(Libs.appcompat)

    /* GOOGLE AUTH */
    implementation(Libs.play_services_auth)

    /* FIREBASE */
    implementation(Libs.firebase_auth)

    /* KOTLIN */
    implementation(Libs.kotlin_stdlib_jdk7)
    implementation(Libs.core_ktx)

    /* RX */
    implementation(Libs.rxjava)
    implementation(Libs.rxandroid)
    implementation(Libs.rxkotlin)

    /* ACTIVITY RESULT */
    implementation(Libs.inline_activity_result_kotlin)

    /* TEST */
    testImplementation(Libs.junit)
    androidTestImplementation(Libs.androidx_test_runner)
    androidTestImplementation(Libs.espresso_core)
}

/* ======== BINTRAY ======== */

tasks {

    val dokka by getting(org.jetbrains.dokka.gradle.DokkaTask::class) {
        outputFormat = "html"
        outputDirectory = "$buildDir/dokka"
    }

    val sourcesJar by creating(Jar::class) {
        archiveClassifier.set("sources")
        from(android.sourceSets.getByName("main").java.srcDirs)
    }

    artifacts {
        archives(sourcesJar)
    }
}

val artifactName: String = project.name
val artifactGroup: String = Library.group
val artifactVersion: String = AndroidConfig.version_name

publishing {
    publications {
        create<MavenPublication>("auth-droid") {

            groupId = artifactGroup
            artifactId = artifactName
            version = artifactVersion

            artifact(tasks.getByName("sourcesJar"))

            pom.withXml {
                asNode().apply {
                    appendNode("packaging", "jar")
                    appendNode("description", Library.pomDescription)
                    appendNode("name", "auth-droid")
                    appendNode("url", Library.pomUrl)
                    appendNode("licenses").appendNode("license").apply {
                        appendNode("name", Library.pomLicenseName)
                        appendNode("url", Library.pomLicenseUrl)
                        appendNode("distribution", Library.pomLicenseDist)
                    }
                    appendNode("developers").appendNode("developer").apply {
                        appendNode("id", Library.pomDeveloperId)
                        appendNode("name", Library.pomDeveloperName)
                    }
                    appendNode("scm").apply {
                        appendNode("url", Library.pomScmUrl)
                    }
                }
            }
        }
    }
}


bintray {
    user = gradleLocalProperties(rootDir).getProperty("bintray.user").toString()
    key = gradleLocalProperties(rootDir).getProperty("bintray.apikey").toString()
    publish = true

    setPublications("auth-droid")

    pkg.apply {
        repo = Library.repo
        name = artifactName
        userOrg = Library.organization
        githubRepo = Library.githubRepo
        vcsUrl = Library.pomScmUrl
        description = Library.pomDescription
        setLabels("auth", "android", "social", "login", "signin")
        setLicenses(Library.pomLicenseName)
        desc = Library.pomDescription
        websiteUrl = Library.pomUrl
        issueTrackerUrl = Library.pomIssueUrl
        githubReleaseNotesFile = Library.githubReadme

        version.apply {
            name = artifactVersion
            desc = Library.pomDescription
            vcsTag = artifactVersion
            gpg.sign = true
            gpg.passphrase = gradleLocalProperties(rootDir).getProperty("bintray.gpg.password")
        }
    }
}