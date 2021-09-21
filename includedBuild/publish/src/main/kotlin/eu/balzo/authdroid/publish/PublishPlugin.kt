package eu.balzo.authdroid.publish

import com.android.build.gradle.BaseExtension
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.BasePlugin
import org.gradle.api.plugins.BasePluginExtension
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.register

class PublishPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.plugins.apply(MavenPublishPlugin::class)
        setupPublishing(target)
    }
}

private fun setupPublishing(project: Project) {

    val a = project
    project.tasks.register("androidJavadocJar", Jar::class) {
        archiveClassifier.set("javadoc")
        from("${project.buildDir}/javadoc")
        //dependsOn(dokkaJavadoc)
    }

    project.tasks.register("androidSourcesJar", Jar::class) {
        archiveClassifier.set("sources")
        from(
            project.extensions
                .getByType(BaseExtension::class)
                .sourceSets
                .getByName("main")
                .java
                .getName()
        )
    }

    val publishing = project.extensions.getByType(PublishingExtension::class)
    project.afterEvaluate {
        publishing.repositories {

            val publish =
                project
                    .extensions
                    .getByType(PublishingExtension::class)

            publish.publications {

                create("release", MavenPublication::class) {

                    val artifactName = project.name
                    val artifactGroup = "eu.balzo.auth-droid"
                    val artifactVersion = Library.version

                    // Two artifacts, the `aar` (or `jar`) and the sources
                    val plugin = project.plugins.findPlugin("com.android.library")
                    if (plugin != null) {
                        artifact("$buildDir/outputs/aar/${project.name}-release.aar")
                    } else {
                        artifact("$buildDir/libs/${project.name}-${version}.jar")
                    }
                    artifact(tasks.getByName("androidSourcesJar"))

                    pom {
                        packaging = "aar"
                        name.set(artifactName)
                        description.set("Android Social Auth library")
                        url.set("https://github.com/balzo-tech/auth-droid")
                        licenses {
                            license {
                                name.set("MIT")
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
                                        if (it.group == rootProject.name) Library.version
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
}
