plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
}

// To make it available as direct dependency
group = "eu.balzo.authdroid.dependencies"
version = "SNAPSHOT"

repositories {
    google()
    mavenCentral()
}

gradlePlugin {
    plugins.register("dependencies") {
        id = "dependencies"
        implementationClass = "eu.balzo.authdroid.dependencies.DependenciesPlugin"
    }
}
