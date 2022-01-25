import config.Builds
import config.Libs

plugins {
    id("com.android.application")
    kotlin("android")
    id("kotlin-parcelize")
    id("androidx.navigation.safeargs.kotlin")
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    packagingOptions {
        this.resources.excludes.add("META-INF/notice.txt")
        this.resources.excludes.add("META-INF/plugin_release.kotlin_module")
        this.resources.excludes.add("META-INF/fmp_release.kotlin_module")
        this.resources.excludes.add("com.mobrun.plugin.BuildConfige")
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
        languageVersion = "1.6"
        apiVersion = "1.6"
        jvmTarget = "11"
    }
}

dependencies {
    implementation(fileTree(mapOf("include" to listOf("*.jar"), "dir" to "libs")))
    //implementation(kotlin("stdlib-jdk8", config.Versions.kotlin))
    
    implementation(Libs.Core.coreKtx)
    implementation(Libs.Core.coroutinesCore)
    implementation(Libs.Common.sresultpresentation)
    implementation(Libs.Common.gson)

    implementation(project(":hhive"))

    implementation(project(":hiveprocessor")) {
        exclude(group = "com.mobrun", module = "plugin")
    }
    kapt(project(":hiveprocessor"))

    testImplementation(Libs.Tests.junit)
    androidTestImplementation(Libs.Tests.runner)
    androidTestImplementation(Libs.Tests.espresso)
}