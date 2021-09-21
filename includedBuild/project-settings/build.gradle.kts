plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
}

// To make it available as direct dependency
group = "eu.balzo.authdroid.projectsettings"
version = "SNAPSHOT"

repositories {
    google()
    mavenCentral()
}

gradlePlugin {
    plugins.register("project-settings") {
        id = "project-settings"
        implementationClass = "eu.balzo.authdroid.projectsettings.ProjectSettingsPlugin"
    }
}
