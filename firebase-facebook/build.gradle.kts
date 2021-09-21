import eu.balzo.authdroid.dependencies.*
import eu.balzo.authdroid.projectsettings.ProjectSettings

plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
    id("dependencies")
    id("project-settings")
    id("maven-publish")
    id("signing")
}

android {
    compileSdk = ProjectSettings.compile_sdk
    defaultConfig {
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
    implementation(project(":facebook-core"))
    implementation(project(":firebase-core"))

    /* FIREBASE */
    implementation(Google.Firebase.authKtx)

    /* FACEBOOK AUTH */
    implementation(Facebook.login)

    /* KOTLIN */
    implementation(Kotlin.stdLib)
    implementation(AndroidX.Core.coreKtx)

}

/* --- maven central --- */

tasks {

    register("androidJavadocJar", Jar::class) {
        archiveClassifier.set("javadoc")
        from("$buildDir/javadoc")
        //dependsOn(dokkaJavadoc)
    }

    register("androidSourcesJar", Jar::class) {
        archiveClassifier.set("sources")
        from(project.android.sourceSets.getByName("main").java.name)
    }

}

val artifactName: String = project.name
val artifactGroup: String = "eu.balzo.auth-droid"
val artifactVersion: String = ProjectSettings.version_name

afterEvaluate {

    publishing {

        publications {

            create<MavenPublication>("release") {

                groupId = artifactGroup
                artifactId = artifactName
                version = artifactVersion

                // Two artifacts, the `aar` (or `jar`) and the sources
                if (project.plugins.findPlugin("com.android.library") != null) {
                    artifact("$buildDir/outputs/aar/${project.name}-release.aar")
                } else {
                    artifact("$buildDir/libs/${project.name}-${version}.jar")
                }
                artifact(tasks.getByName("androidSourcesJar"))

                pom {
                    packaging = "aar"
                    name.set(artifactName)
                    description.set("ForYouAndMe Android SDK")
                    url.set("https://github.com/balzo-tech/auth-droid")
                    licenses {
                        license {
                            name.set("ForYouAndMe Android SDK")
                            url.set("https://github.com/balzo-tech/auth-droid")
                        }
                    }
                    developers {
                        developer {
                            id.set("giacomo.balzo")
                            name.set("Giacomo Parisi")
                            email.set("giacomo@balzo.eu")
                        }
                    }
                    scm {
                        connection.set("scm:git:github.com/balzo-tech/auth-droid.git")
                        developerConnection.set("scm:git:ssh://github.com/balzo-tech/auth-droid.git")
                        url.set("https://github.com/balzo-tech/auth-droid/tree/main")
                    }
                    withXml {

                        // Add dependencies to pom file
                        val dependenciesNode = asNode().appendNode("dependencies")
                        configurations.getByName("implementation")
                            .allDependencies
                            .forEach {
                                val groupId =
                                    if (it.group == rootProject.name) artifactGroup else it.group
                                val artifactId = it.name
                                val version =
                                    if (it.group == rootProject.name) ProjectSettings.version_name
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
}

signing {

    useInMemoryPgpKeys(
        rootProject.extra["signing.keyId"].toString(),
        rootProject.extra["signing.key"].toString(),
        rootProject.extra["signing.password"].toString(),
    )
    sign(publishing.publications)

}
