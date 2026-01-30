
import com.android.build.gradle.internal.tasks.factory.dependsOn
import com.mikepenz.aboutlibraries.plugin.DuplicateMode.MERGE
import com.mikepenz.aboutlibraries.plugin.DuplicateRule.GROUP
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Properties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
    alias(libs.plugins.serialization)
    alias(libs.plugins.buildConfig)
    alias(libs.plugins.aboutLibraries)
    alias(libs.plugins.ktlint)
}

buildConfig {
    buildConfigField("VERSION_NAME", provider { libs.versions.appVersion.get() })
    buildConfigField("VERSION_CODE", provider { libs.versions.appVersionCode.get() })
}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    targets.configureEach {
        compilations.configureEach {
            compileTaskProvider.get().compilerOptions {
                freeCompilerArgs.add("-Xexpect-actual-classes")
                freeCompilerArgs.add("-Xcontext-sensitive-resolution")
            }
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64(),
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
            implementation(libs.lifecycle.runtime.compose)
            implementation(libs.lifecycle.viewmodel.compose)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.oidc.appsupport)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.datetime)
            implementation(libs.multiplatform.settings.no.arg)
            implementation(libs.aboutlibraries.core)
            implementation(libs.aboutlibraries.compose.core)
            implementation(libs.aboutlibraries.compose.m3)
            implementation(libs.koalaplot.core)
            implementation(libs.jetlime)
            implementation(libs.haze)
            implementation(libs.material.kolor)
            implementation(libs.platformtools.darkmodedetector)
            implementation(libs.multihaptic.compose)
            implementation(libs.animate.compose)
            implementation(libs.confettikit)
            implementation(libs.emoji.compose.m3)
            implementation(libs.sonner)
        }
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.androidx.security.crypto)
            implementation(libs.androidx.biometric)
            implementation(libs.androidx.core.splashscreen)
            implementation(libs.ktor.client.okhttp)
            implementation(libs.smartspacer.sdk)
            implementation(libs.permission)
            implementation(libs.alarmee)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
            implementation(libs.multiplatform.settings)
            implementation(libs.permission)
            implementation(libs.alarmee)
        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.jbr.api)
            implementation(libs.kotlinx.coroutinesSwing)
            implementation(libs.ktor.client.apache5)
            implementation(libs.jna.core)
            implementation(libs.credential.secure.storage)
            implementation(libs.materialyou)
            implementation(libs.advanced.menubar)
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
    compileSdk =
        libs.versions.android.compileSdk
            .get()
            .toInt()

    val keystorePropsFile = rootProject.file("keystore.properties")
    val hasKeystore = keystorePropsFile.exists()
    val keystoreProps =
        Properties().apply {
            if (hasKeystore) keystorePropsFile.inputStream().use { load(it) }
        }

    defaultConfig {
        applicationId = "com.hansholz.bestenotenapp"
        minSdk =
            libs.versions.android.minSdk
                .get()
                .toInt()
        targetSdk =
            libs.versions.android.targetSdk
                .get()
                .toInt()
        versionCode =
            libs.versions.appVersionCode
                .get()
                .toInt()
        versionName = libs.versions.appVersion.get()

        addManifestPlaceholders(
            mapOf("oidcRedirectScheme" to "bestenotenapp://callback"),
        )
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    signingConfigs {
        if (hasKeystore) {
            create("release") {
                storeFile = file(keystoreProps["storeFile"]!!.toString())
                storePassword = keystoreProps["storePassword"]!!.toString()
                keyAlias = keystoreProps["keyAlias"]!!.toString()
                keyPassword = keystoreProps["keyPassword"]!!.toString()
                enableV1Signing = true
                enableV2Signing = true
                enableV3Signing = true
            }
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles("src/androidMain/proguard-rules.pro")
            if (hasKeystore) signingConfig = signingConfigs.getByName("release")

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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
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
                iconFile = project.file("src/desktopMain/icons/icon.ico")
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
                iconFile = project.file("src/desktopMain/icons/icon.icns")
                dockName = libs.versions.appName.get()
                packageBuildVersion = libs.versions.appVersionCode.get()
                jvmArgs("-Dapple.awt.application.appearance=system")
                infoPlist {
                    extraKeysRawXml =
                        """
                        <key>CFBundleLocalizations</key>
                        <array>
                            <string>de</string>
                            <string>en</string>
                        </array>
                        <key>CFBundleIconName</key>
                        <string>Beste-Noten-App</string>
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

ktlint {
    enableExperimentalRules.set(true)
    additionalEditorconfig.set(
        mapOf(
            "max_line_length" to "180",
            "ktlint_function_naming_ignore_when_annotated_with" to "Composable",
        ),
    )
    filter {
        exclude { element ->
            val path = element.file.path
            path.contains("\\generated\\") || path.contains("/generated/")
        }
    }

    tasks.named("desktopJar").dependsOn(tasks.ktlintFormat)
    tasks.preBuild.dependsOn(tasks.ktlintFormat)
}

val copyAssetsCarToMacResources =
    tasks.register<Copy>("copyAssetsCarToMacResources") {
        dependsOn("createReleaseDistributable")
        from(layout.projectDirectory.file("src/desktopMain/assets/Assets.car"))
        into(layout.buildDirectory.dir("compose/binaries/main-release/app/${libs.versions.appName.get()}.app/Contents").map { it.dir("Resources") })
    }
tasks.register("createReleaseDmg") {
    group = "packaging"
    description = "Creates DMG for MacOS with MacOS 26 Icon"
    dependsOn(copyAssetsCarToMacResources)
    finalizedBy("packageReleaseDmg")
}

aboutLibraries {
    android {
        registerAndroidTasks = false
    }
    library {
        duplicationMode = MERGE
        duplicationRule = GROUP
    }
    export {
        outputPath = project.file("src/commonMain/composeResources/files/aboutlibraries.json")
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
