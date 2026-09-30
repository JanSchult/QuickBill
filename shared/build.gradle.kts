import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
}

kotlin {
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Shared"
            isStatic = true
        }
    }

    jvm("desktop") // ## ergänzt: Desktop-Target fehlte, war aber explizit gefordert (Antwort auf Frage 5)

    android {
        namespace = "${property("app.packageName")}.shared"// ## korrigiert: Platzhalter-Namespace ersetzt
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        compilerOptions {
            jvmTarget = JvmTarget.JVM_11
        }
        androidResources {
            enable = true
        }
        withHostTest {
            isIncludeAndroidResources = true
        }
        withDeviceTestBuilder {
            sourceSetTreeName = "test"
        }.configure {
            instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }
    }

    sourceSets {
        androidMain.dependencies {
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.compose.uiTooling)
            implementation(libs.koin.android)
            implementation(libs.androidx.activity.compose) // ## ergänzt: für Activity-nahe Compose-APIs (z. B. Share-Intent für PDF)
        }
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)

            // lifecycle-viewmodelCompose hier ENTFERNT (durch koin-compose-viewmodel ersetzt)
            implementation(libs.androidx.lifecycle.runtimeCompose)

            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.datetime)
            implementation(libs.kotlinx.serialization.json)

            implementation(libs.room.runtime)
            implementation(libs.sqlite.bundled)

            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)

            implementation(libs.compose.material.icons.extended)
            implementation(libs.compose.material.icons.core)

            implementation(libs.navigation.compose)
        }
        // ## ergänzt: Desktop-Sourceset, da jvm("desktop") jetzt existiert
        val desktopMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
            }
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        // ## ergänzt: Android-spezifische Testabhängigkeiten für withHostTest/withDeviceTestBuilder
        getByName("androidHostTest").dependencies {
            implementation(libs.junit)
            implementation(libs.kotlin.testJunit)
        }
        getByName("androidDeviceTest").dependencies {
            implementation(libs.androidx.testExt.junit)
            implementation(libs.androidx.espresso.core)
        }
    }
}

room {
    schemaDirectory("$projectDir/schemas")
}

dependencies {
    androidRuntimeClasspath(libs.compose.uiTooling)
    add("kspAndroid", libs.room.compiler)
    add("kspIosArm64", libs.room.compiler)
    add("kspIosSimulatorArm64", libs.room.compiler)
    add("kspDesktop", libs.room.compiler) // funktioniert jetzt, da jvm("desktop") oben deklariert ist
}

// ## ergänzt: Desktop-Anwendung ausführbar machen (Compose-Multiplatform-Gradle-Plugin unterstützt das auch in einem Library-Modul)
compose.desktop {
    application {
        mainClass = "de.janschult.rechnungsapp.MainKt"
    }
}