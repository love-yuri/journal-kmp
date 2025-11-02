import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import app.cash.sqldelight.gradle.SqlDelightDatabase

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
    id("app.cash.sqldelight") version "2.1.0"
    kotlin("plugin.serialization") version "2.2.0"
}

val rootPackageName = "com.yuri.love"

val gitVersionProvider = providers.exec {
    commandLine("git", "describe", "--always", "--dirty")
}.standardOutput.asText.map { it.trim() }

version = project.findProperty("AppVersion") as String

/* 数据库配置 */
repositories {
    google()
    mavenCentral()
}

sqldelight {
    databases {
        val action = Action<SqlDelightDatabase> {
            packageName = rootPackageName
        }
        create("Database", action)
    }
}

kotlin {
    targets.configureEach {
        compilations.configureEach {
            compileTaskProvider.get().compilerOptions {
                /* disable actual-classes error */
                freeCompilerArgs.add("-Xexpect-actual-classes")
            }
        }
    }

    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
            languageVersion.set(KotlinVersion.KOTLIN_2_2)
            apiVersion.set(KotlinVersion.KOTLIN_2_2)
        }
    }
    
    jvm()
    
    sourceSets {
        val commonMain by getting {
            kotlin.srcDir(layout.buildDirectory.dir("generated/commonMain/kotlin"))
        }

        androidMain.dependencies {
            implementation(libs.android.driver)
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.androidx.biometric)
        }

        commonMain.dependencies {
            /* xml */
            implementation(libs.core)
            implementation(libs.serialization)

            /* sqldelight flow extensions */
            implementation(libs.coroutines.extensions)

            /* retrofit */
            implementation(libs.retrofit)
            implementation(libs.converter.kotlinx.serialization)

            /* kotlin serialization */
            implementation(libs.kotlinx.serialization.json)

            /* kotlinx datetime */
            implementation(libs.kotlinx.datetime)

            /* logger */
            implementation(libs.kotlin.logging)

            /* voyager navigator */
            implementation(libs.voyager.navigator)
            implementation(libs.voyager.koin)
            implementation(libs.voyager.transitions)

            // Material Icons - 核心图标包
            implementation(libs.androidx.material3)
            implementation(libs.androidx.material.icons.core)
            // Material Icons - 扩展图标包 (包含更多图标)
            implementation(libs.androidx.material.icons.extended)

            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        jvmMain.dependencies {
            /* database */
            implementation(libs.sqlite.driver)

            /* logback logger */
            implementation(libs.ch.logback.classic)

            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutinesSwing)
        }
    }
}

android {
    namespace = "com.yuri.love"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    /* remove link check */
    lint {
        disable.addAll(
            listOf(
                "NullSafeMutableLiveData",
                "MutableLiveData",
                "UnusedResources"
            )
        )
    }

    defaultConfig {
        applicationId = rootPackageName
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            // 排除冲突的元数据文件
            excludes += "META-INF/INDEX.LIST"
            excludes += "META-INF/*.kotlin_module"
            excludes += "META-INF/versions/*"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}

compose.desktop {
    application {
        mainClass = "$rootPackageName.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = rootPackageName
        }
    }
}

abstract class GenerateVersionFile : DefaultTask() {
    @get:Input
    abstract val versionName: Property<String>

    @get:Input
    abstract val packageName: Property<String>

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @TaskAction
    fun generate() {
        val file = outputDir.get().file("com/yuri/love/VersionInfo.kt").asFile
        file.parentFile.mkdirs()
        file.writeText(
            """
            package ${packageName.get()}

            object ProjectVersionInfo {
                const val VERSION = "${versionName.get()}"
            }
            """.trimIndent()
        )
    }
}

tasks.register<GenerateVersionFile>("generateVersionFile") {
    versionName.set(project.version.toString())
    outputDir.set(layout.buildDirectory.dir("generated/commonMain/kotlin"))
    packageName.set(rootPackageName)
}

// 更精确地指定到 Kotlin 编译任务
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    dependsOn("generateVersionFile")
}