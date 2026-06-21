import java.util.Properties

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeCompiler)
}

android {
    namespace = "com.hansholz.bestenotenapp.android"
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
        base.archivesName = "${libs.versions.appName.get()}-${libs.versions.appVersion.get()}-${libs.versions.appVersionCode.get()}"

        addManifestPlaceholders(
            mapOf("oidcRedirectScheme" to "bestenotenapp://callback"),
        )
    }

    buildFeatures {
        compose = true
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
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles("proguard-rules.pro")
            if (hasKeystore) signingConfig = signingConfigs.getByName("release")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation(projects.composeApp)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.fragment)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.oidc.appsupport)
    implementation(libs.ksafe.biometrics)
    implementation(libs.permission)
    implementation(libs.filekit.dialogs)
}
