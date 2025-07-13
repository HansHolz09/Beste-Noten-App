import java.text.SimpleDateFormat
import java.util.Date
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
    alias(libs.plugins.serialization)
}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    jvm("desktop")

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser {
            commonWebpackConfig {
                outputFileName = "composeApp.js"
            }
        }
        binaries.executable()
    }

    sourceSets {
        val desktopMain by getting

        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.materialIconsExtended)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.adaptive)
            implementation(libs.material3.adaptive.layout)
            implementation(libs.material3.adaptive.navigation)
            implementation(libs.ui.backhandler)
            implementation(libs.navigation.compose)
            implementation(libs.androidx.lifecycle.viewmodel.compose)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.oidc.appsupport)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.datetime)
            implementation(libs.multiplatform.settings.no.arg)
            implementation(libs.koalaplot.core)
            implementation(libs.haze)
            implementation(libs.material.kolor)
            implementation(libs.platformtools.darkmodedetector)
            implementation(libs.animate.compose)
            implementation(libs.emoji.compose.m3)
            implementation(libs.sonner)
        }
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.ktor.client.cio)
            implementation(libs.androidx.security.crypto)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
            implementation(libs.multiplatform.settings)
        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.jbr.api)
            implementation(libs.kotlinx.coroutinesSwing)
            implementation(libs.ktor.client.cio)
            implementation(libs.jna.core)
            implementation(libs.credential.secure.storage)
        }
        wasmJsMain.dependencies {
            implementation(libs.ktor.client.js)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

android {
    namespace = "com.hansholz.bestenotenapp"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.hansholz.bestenotenapp"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = libs.versions.appVersionCode.get().toInt()
        versionName = libs.versions.appVersion.get()

        addManifestPlaceholders(
            mapOf("oidcRedirectScheme" to "bestenotenapp://callback")
        )
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles("src/androidMain/proguard-rules.pro")

            applicationVariants.all {
                val variant = this
                variant.outputs
                    .map { it as com.android.build.gradle.internal.api.BaseVariantOutputImpl }
                    .forEach { output ->
                        val outputFileName =
                            "${libs.versions.appName.get()}-${libs.versions.appVersion.get()}-${libs.versions.appVersionCode.get()}.apk"
                        output.outputFileName = outputFileName
                    }
            }
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
        mainClass = "com.hansholz.bestenotenapp.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Exe, TargetFormat.Deb)
            packageName = libs.versions.appName.get()
            packageVersion = libs.versions.appVersion.get()
            description = libs.versions.appName.get()
            copyright = "© ${SimpleDateFormat("yyyy").format(Date())} Franz Scholz. All rights reserved."
            vendor = "Franz Scholz"

            appResourcesRootDir = layout.projectDirectory.dir("src/desktopMain/assets")
            jvmArgs += "-splash:${'$'}APPDIR/resources/splash.png"

            modules += "jdk.unsupported"

            windows {
                iconFile = project.file("src/commonMain/composeResources/drawable/icon.ico")
                menuGroup = libs.versions.appName.get()
                upgradeUuid = "a9bdc510-b2a5-4c39-8b69-27c754eea3ff"
                console = false
                dirChooser = false
                perUserInstall = true
                shortcut = true
            }

            linux {
                iconFile = project.file("src/commonMain/composeResources/drawable/logo.png")
                appRelease = libs.versions.appVersionCode.get()
                shortcut = true
            }

            macOS {
                iconFile = project.file("src/commonMain/composeResources/drawable/icon.icns")
                dockName = libs.versions.appName.get()
                packageBuildVersion = libs.versions.appVersionCode.get()
                infoPlist {
                    extraKeysRawXml =
                        """
                        <key>CFBundleLocalizations</key>
                        <array>
                            <string>de</string>
                            <string>en</string>
                        </array>
                        """.trimIndent()
                }
            }
        }

        buildTypes.release.proguard {
            isEnabled = true
            obfuscate = true
            optimize = true
            configurationFiles.from(project.file("src/desktopMain/compose-desktop.pro"))
        }
    }
}

gradle.projectsEvaluated {
    val cfg = file("../iosApp/iosApp/build/Generated.xcconfig")
    cfg.parentFile.mkdirs()
    cfg.writeText(
        """
        MARKETING_VERSION = ${libs.versions.appVersion.get()}
        CURRENT_PROJECT_VERSION = ${libs.versions.appVersionCode.get()}
        """.trimIndent(),
    )
}
