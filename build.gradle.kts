import org.jetbrains.changelog.Changelog
import org.jetbrains.changelog.markdownToHTML
import org.jetbrains.intellij.platform.gradle.IntelliJPlatformType
import org.jetbrains.intellij.platform.gradle.extensions.TestFrameworkType
import org.jetbrains.intellij.platform.gradle.models.ProductRelease

fun properties(key: String) = project.findProperty(key).toString()

// Configure IntelliJ Platform Gradle Plugin 2.x - read more: https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin.html
plugins {
    `jvm-test-suite`
    `java-test-fixtures`
    `kotlin-dsl`
    id("org.jetbrains.kotlin.jvm") version "1.9.10"
    id("org.jetbrains.intellij.platform") version "2.0.0-beta1"
    id("org.jetbrains.intellij.platform.migration") version "2.0.0-beta1"
    kotlin("plugin.serialization") version embeddedKotlinVersion
    alias(libs.plugins.changelog)
}

repositories {
    mavenCentral()

    intellijPlatform {
        defaultRepositories()
    }
}
group = properties("pluginGroup")
version = properties("pluginVersion")

intellijPlatform {
    buildSearchableOptions = false
    instrumentCode = true

    pluginConfiguration {
        version = properties("pluginVersion")

        ideaVersion {
            sinceBuild = properties("pluginSinceBuild")
            untilBuild = properties("pluginUntilBuild")
        }

        description = projectDir.resolve("README.md").readText().lines().run {
            val start = "<!-- Plugin description -->"
            val end = "<!-- Plugin description end -->"

            if (!containsAll(listOf(start, end))) {
                throw GradleException("Plugin description section not found in README.md:\n$start ... $end")
            }
            subList(indexOf(start) + 1, indexOf(end))
        }.joinToString("\n").run { markdownToHTML(this) }

        changeNotes = provider {
            changelog.renderItem(
                changelog
                    .getUnreleased()
                    .withHeader(false)
                    .withEmptySections(false),
                Changelog.OutputType.HTML
            )
        }
    }

    verifyPlugin {
        ides {
            ide(IntelliJPlatformType.PhpStorm, properties("platformVersion"))
            recommended()
            select {
                types = listOf(IntelliJPlatformType.PhpStorm)
                channels = listOf(ProductRelease.Channel.RELEASE)
                sinceBuild = properties("pluginSinceBuild")
                untilBuild = properties("pluginUntilBuild")
            }
        }
        downloadDirectory = layout.buildDirectory.dir("ides")
    }
}

dependencies {
    intellijPlatform {
        create(IntelliJPlatformType.PhpStorm, properties("platformVersion"))

        plugin("com.jetbrains.hackathon.indices.viewer", "1.26")
        plugin("com.intellij.lang.jsgraphql", "241.14494.150")
        bundledPlugins(properties("bundledPlugins").split(',').map { it.trim() })

        pluginVerifier()
        instrumentationTools()
        testFramework(TestFrameworkType.Platform.JUnit4)
        testFramework(TestFrameworkType.Plugin.Java)
    }
    implementation(libs.kotlinx.serialization.json)

    compileOnly(embeddedKotlin("gradle-plugin"))

    testImplementation(gradleTestKit())
    testImplementation(embeddedKotlin("test"))
    testImplementation(embeddedKotlin("test-junit"))

    testFixturesImplementation(gradleTestKit())
    testFixturesImplementation(embeddedKotlin("test"))
    testFixturesImplementation(embeddedKotlin("test-junit"))
    testFixturesImplementation(libs.annotations)
}

kotlin {
    jvmToolchain(17)
}

changelog {
    version = properties("pluginVersion")
    groups.empty()
}

tasks {
    wrapper {
        gradleVersion = properties("gradleVersion")
    }
}
