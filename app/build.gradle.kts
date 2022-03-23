plugins {
    id("com.android.application")
    kotlin("android")
    id("kotlin-parcelize")
    id("androidx.navigation.safeargs.kotlin")
    kotlin("kapt")
    id("com.google.devtools.ksp") version "1.6.10-1.0.4"
}

android {
    val buildSdkVersion: Int by extra
    val minSdkVersion: Int by extra
    val codePath: String by extra
    val srcDirs = listOf(codePath)

    compileSdk = buildSdkVersion
    defaultConfig {
        applicationId = "pro.krit.fmpdaoexample"
        minSdk = minSdkVersion
        targetSdk = buildSdkVersion
        version = "1.0.2"
        multiDexEnabled = true

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
           //isDebuggable = true
        }

        getByName("release") {
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
        this.resources.excludes.add("META-INF/gradle/incremental.annotation.processors")
        this.resources.excludes.add("com.mobrun.plugin.BuildConfig")
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
            java.setSrcDirs(srcDirs)
        }
    }

    buildFeatures {
        dataBinding = true
    }

    kotlin {
        sourceSets.release {
            kotlin.srcDirs("build/generated/ksp/release/kotlin")
        }
        sourceSets.debug {
            kotlin.srcDirs("build/generated/ksp/debug/kotlin")
        }
    }

    kotlinOptions {
        languageVersion = "1.6"
        apiVersion = "1.6"
        jvmTarget = "11"
    }
}

dependencies {
    val core: String by rootProject.extra
    val coroutinesCore: String by rootProject.extra
    val sresultpresentation: String by rootProject.extra
    val gson: String by rootProject.extra
    val junit: String by rootProject.extra
    val leakCanary: String by rootProject.extra
    val runner: String by rootProject.extra
    val espresso: String by rootProject.extra

    implementation(core)
    implementation(coroutinesCore)
    implementation(sresultpresentation)
    implementation(gson)

    implementation(project(":hhive"))

/*    implementation(project(":hiveprocessor")) {
        exclude(group = "com.mobrun", module = "plugin")
    }
    kapt(project(":hiveprocessor"))*/

    implementation(project(":hiveksp"))/* {
        exclude(group = "com.mobrun", module = "plugin")
    }*/
    ksp(project(":hiveksp"))

    debugImplementation(leakCanary)
    testImplementation(junit)
    androidTestImplementation(runner)
    androidTestImplementation(espresso)
}