import config.Builds
import config.Libs

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
}

android {
    compileSdk = (Builds.COMPILE_VERSION)
    buildToolsVersion = Builds.BUILD_TOOLS
    defaultConfig {
        applicationId = Builds.APP_ID
        minSdk = (Builds.MIN_VERSION)
        targetSdk = (Builds.TARGET_VERSION)
        //versionCode = Builds.App.VERSION_CODE
        version = Builds.App.VERSION_NAME
        multiDexEnabled = true

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName(Builds.Types.DEBUG) {
            isMinifyEnabled = false
           //isDebuggable = true
        }

        getByName(Builds.Types.RELEASE) {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    packagingOptions {
        exclude("META-INF/notice.txt")
        exclude("META-INF/plugin_release.kotlin_module")
        exclude("META-INF/fmp_release.kotlin_module")
    }

    // Declare the task that will monitor all configurations.
    configurations.all {
        // 2 Define the resolution strategy in case of conflicts.
        resolutionStrategy {
            // Fail eagerly on version conflict (includes transitive dependencies),
            // e.g., multiple different versions of the same dependency (group and name are equal).
            failOnVersionConflict()

            // Prefer modules that are part of this build (multi-project or composite build) over external modules.
            preferProjectModules()
        }
    }

    sourceSets {
        getByName("main") {
            java.setSrcDirs(Builds.codeDirs)
        }
    }

    buildFeatures {
        dataBinding = true
    }

    kotlinOptions {
        languageVersion = "1.5"
        apiVersion = "1.5"
        jvmTarget = "1.8"
    }

    kapt {
        useBuildCache = true
        generateStubs = false
        includeCompileClasspath = false
    }
}

dependencies {
    implementation(fileTree(mapOf("include" to listOf("*.jar"), "dir" to "libs")))
    //implementation(kotlin("stdlib-jdk8", config.Versions.kotlin))
    
    implementation(Libs.Core.coreKtx)
    implementation(Libs.Common.sresultpresentation)
    implementation(Libs.Common.gson)

    implementation(project(":hhive"))

    implementation(project(":hiveprocessor"))
    kapt(project(":hiveprocessor"))

    testImplementation(Libs.Tests.junit)
    androidTestImplementation(Libs.Tests.runner)
    androidTestImplementation(Libs.Tests.espresso)
}