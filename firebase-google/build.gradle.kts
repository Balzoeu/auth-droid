import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
    id("maven-publish")
    id("com.github.dcendents.android-maven")
    id("com.jfrog.bintray")
    id("org.jetbrains.dokka-android")
}

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

    kotlinOptions {
        jvmTarget = "1.8"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

}

dependencies {

    /* AUTHDROID */
    implementation(project(":core"))
    implementation(project(":google-core"))
    implementation(project(":firebase-core"))

    /* ANDROID */
    implementation(Libs.appcompat)

    /* GOOGLE AUTH */
    implementation(Libs.play_services_auth)

    /* FIREBASE */
    implementation(Libs.firebase_auth)

    /* KOTLIN */
    implementation(Libs.kotlin_stdlib_jdk7)
    implementation(Libs.core_ktx)

    /* ACTIVITY RESULT INLINE */
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

            artifact("$buildDir/outputs/aar/${artifactId}-release.aar")
            artifact(tasks.getByName("sourcesJar"))

            pom {

                packaging = "aar"
                name.set(Library.name)
                description.set(Library.pomDescription)
                url.set(Library.pomUrl)

                licenses {
                    license {
                        name.set(Library.pomLicenseName)
                        url.set(Library.pomLicenseUrl)
                        distribution.set(Library.repo)
                    }
                }

                developers {
                    developer {
                        id.set(Library.pomDeveloperId)
                        name.set(Library.pomDeveloperName)
                        email.set(Library.pomDeveloperEmail)
                    }
                }

                scm {
                    url.set(Library.pomScmUrl)
                }

                withXml {

                    val dependenciesNode = asNode().appendNode("dependencies")

                    (configurations.releaseImplementation.get().allDependencies +
                            configurations.releaseCompile.get().allDependencies)
                            .forEach {

                                val groupId =
                                        if (it.group == "AuthDroid") Library.group else it.group

                                val artifactId = it.name

                                val version =
                                        if (it.group == "AuthDroid") AndroidConfig.version_name
                                        else it.version

                                if (groupId != null && version != null) {

                                    val dependencyNode =
                                            dependenciesNode.appendNode("dependency")

                                    dependencyNode.appendNode("groupId", groupId)
                                    dependencyNode.appendNode("artifactId", artifactId)
                                    dependencyNode.appendNode("version", version)

                                }

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
