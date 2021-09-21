plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
}

// To make it available as direct dependency
group = "eu.balzo.authdroid.publish"
version = "SNAPSHOT"

repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation(kotlin("stdlib"))
}

gradlePlugin {
    plugins.register("publish") {
        id = "publish"
        implementationClass = "eu.balzo.authdroid.publish.ProjectSettingsPlugin"
    }
}
