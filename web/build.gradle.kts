import java.io.ByteArrayOutputStream
import java.nio.charset.Charset

plugins {
    kotlin("multiplatform")
    id("com.google.devtools.ksp")
}

val fritz2version = rootProject.ext["fritz2version"]

kotlin {
    js(IR) {
        browser()
        binaries.executable()
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("dev.fritz2:headless:$fritz2version")
                implementation(project(":core"))
            }
        }
        val jsMain by getting {
            dependencies {
                // tailwind
                implementation(npm("tailwindcss", "3.0.19"))
                implementation(npm("@tailwindcss/forms", "0.4.0"))

                // webpack
                implementation(devNpm("postcss", "8.4.6"))
                implementation(devNpm("postcss-loader", "6.2.1"))
                implementation(devNpm("autoprefixer", "10.4.2"))
                implementation(devNpm("css-loader", "6.6.0"))
                implementation(devNpm("style-loader", "3.3.1"))
                implementation(devNpm("cssnano", "5.0.17"))
            }
        }
    }
}

// FIXME: Entfernen, sobald nicht mehr explizit benötigt
tasks.named("jsBrowserDevelopmentRun") {
    dependsOn("jsDevelopmentExecutableCompileSync")
}

tasks.named<ProcessResources>("jsProcessResources") {
    filesMatching("index.html") {
        val output = ByteArrayOutputStream()
        exec {
            workingDir = rootProject.projectDir
            commandLine("git rev-parse --short HEAD".split(" "))
            standardOutput = output
        }

        val version = version
        val commitHash = output.toString(Charset.defaultCharset()).trim()

        expand(
            "sokobanVersion" to version,
            "sokobanCommitHash" to commitHash
        )
    }
}

/**
 * KSP support - start
 */
dependencies {
    add("kspCommonMainMetadata", "dev.fritz2:lenses-annotation-processor:$fritz2version")
}
kotlin.sourceSets.commonMain { kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin") }

// Fixes webpack-cli incompatibility by pinning the newest version.
// https://youtrack.jetbrains.com/issue/KTIJ-22030
rootProject.extensions.configure<org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension> {
    versions.webpackCli.version = "4.10.0"
}

tasks.withType<org.jetbrains.kotlin.gradle.dsl.KotlinCompile<*>>().all {
    if (name != "kspCommonMainKotlinMetadata") dependsOn("kspCommonMainKotlinMetadata")
}
/**
 * KSP support - end
 */