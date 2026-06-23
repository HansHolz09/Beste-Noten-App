
import com.mikepenz.aboutlibraries.plugin.DuplicateMode.MERGE
import com.mikepenz.aboutlibraries.plugin.DuplicateRule.GROUP
import io.github.kdroidfilter.nucleus.desktop.application.dsl.CompressionLevel
import io.github.kdroidfilter.nucleus.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import java.text.SimpleDateFormat
import java.util.Date

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidKmpLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.nucleus)
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
    android {
        namespace = "com.hansholz.bestenotenapp"
        compileSdk =
            libs.versions.android.compileSdk
                .get()
                .toInt()
        minSdk =
            libs.versions.android.minSdk
                .get()
                .toInt()

        androidResources {
            enable = true
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

    listOf(iosArm64(), iosSimulatorArm64()).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    jvm("desktop")
    jvmToolchain(25)

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
        val desktopMain = getByName("desktopMain")

        commonMain.dependencies {
            implementation(libs.runtime)
            implementation(libs.foundation)
            implementation(libs.material3)
            implementation(libs.ui)
            implementation(libs.material.symbols.rounded)
            implementation(libs.components.resources)
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
            implementation(libs.ksafe.compose)
            implementation(libs.ksafe.biometrics)
            implementation(libs.aboutlibraries.core)
            implementation(libs.aboutlibraries.compose.core)
            implementation(libs.aboutlibraries.compose.m3)
            implementation(libs.koalaplot.core)
            implementation(libs.jetlime)
            implementation(libs.haze)
            implementation(libs.haze.blur)
            implementation(libs.material.kolor)
            implementation(libs.platformtools.darkmodedetector)
            implementation(libs.multihaptic.compose)
            implementation(libs.animate.compose)
            implementation(libs.confettikit)
            implementation(libs.emoji.compose.m3)
            implementation(libs.sonner)
            implementation(libs.capturable)
            implementation(libs.capturable.extension)
            implementation(libs.filekit.dialogs)
        }
        androidMain.dependencies {
            implementation(libs.androidx.fragment)
            implementation(libs.androidx.core.splashscreen)
            implementation(libs.ktor.client.okhttp)
            implementation(libs.permission)
            implementation(libs.alarmee)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
            implementation(libs.permission)
            implementation(libs.alarmee)
        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.nucleus.core.runtime)
            implementation(libs.nucleus.aot.runtime)
            implementation(libs.kotlinx.coroutinesSwing)
            implementation(libs.ktor.client.apache5)
            implementation(libs.materialyou)
            implementation(libs.advanced.menubar)
            implementation(libs.jbr.api)
        }
        wasmJsMain.dependencies {
            implementation(libs.ktor.client.js)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

nucleus.application {
    mainClass = "com.hansholz.bestenotenapp.MainKt"

    val appName = libs.versions.appName.get()
    val appVersion = libs.versions.appVersion.get()

    nativeDistributions {
        targetFormats(TargetFormat.Dmg, TargetFormat.Nsis, TargetFormat.Deb)
        packageName = appName
        packageVersion = appVersion
        description = appName
        copyright = "© ${SimpleDateFormat("yyyy").format(Date())} Franz Scholz. Alle Rechte vorbehalten."
        vendor = "Franz Scholz"
        homepage = "https://hansholz.dev/"

        appResourcesRootDir = layout.projectDirectory.dir("src/desktopMain/assets")
        splashImage = "splash.png"
        jvmArgs += "--enable-native-access=ALL-UNNAMED"

        modules += "jdk.unsupported"

        compressionLevel = CompressionLevel.Maximum
        cleanupNativeLibs = true
        enableAotCache = true

        windows {
            iconFile = project.file("src/desktopMain/icons/icon.ico")
            menuGroup = appName
            upgradeUuid = "a9bdc510-b2a5-4c39-8b69-27c754eea3ff"
            console = false

            nsis {
                oneClick = true
                allowElevation = false
                perMachine = false
                allowToChangeInstallationDirectory = false
                createDesktopShortcut = true
                createStartMenuShortcut = true
                runAfterFinish = true
                deleteAppDataOnUninstall = true
                multiLanguageInstaller = true
                installerLanguages = listOf("en_US", "de_DE")
                installerIcon = iconFile
                uninstallerIcon = iconFile
            }
        }

        linux {
            iconFile = project.file("src/commonMain/composeResources/drawable/logo.png")
            debPackageVersion = appVersion
            debMaintainer = "HansHolz09 <mail@hansholz.dev>"
            shortcut = true

            modules += "jdk.security.auth"
        }

        macOS {
            iconFile = project.file("src/desktopMain/icons/icon.icns")
            val layeredIcons = layout.projectDirectory.dir("src/desktopMain/icons/macos-layered-icon/Beste-Noten-App.icon")
            if (layeredIcons.asFile.exists()) {
                layeredIconDir.set(layeredIcons)
            }
            dockName = appName
            packageBuildVersion = libs.versions.appVersionCode.get()
            minimumSystemVersion =
                libs.versions.macos.minSdk
                    .get()
            macOsSdkVersion =
                libs.versions.macos.targetSdk
                    .get()
            jvmArgs += "-Dapple.awt.application.appearance=system"
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

            dmg {
                title = "$appName $appVersion Installieren"
                badgeIcon = iconFile
                iconSize = 128
            }
        }
    }

    buildTypes.release.proguard {
        version = libs.versions.proguard.get()
        isEnabled = true
        obfuscate = true
        optimize = true
        configurationFiles.from(project.file("src/desktopMain/compose-desktop.pro"))
    }
}

ktlint {
    version.set("1.8.0")
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

    tasks.named("desktopJar").configure {
        dependsOn(tasks.ktlintFormat)
    }
    tasks.matching { it.name == "preBuild" }.configureEach {
        dependsOn(tasks.ktlintFormat)
    }
}

aboutLibraries {
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
