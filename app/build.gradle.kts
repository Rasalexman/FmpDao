plugins {
    id("com.android.application")
    kotlin("android")
    id("kotlin-parcelize")
    id("androidx.navigation.safeargs.kotlin")
    kotlin("kapt")
    id("com.google.devtools.ksp") version "1.7.10-1.0.6"
}

val kotlinApiVersion: String by extra
val jvmVersion: String by extra

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
        this.resources.excludes.add("META-INF")
        this.resources.excludes.add("META-INF/notice.txt")
        this.resources.excludes.add("META-INF/plugin_release.kotlin_module")
        this.resources.excludes.add("META-INF/gradle/incremental.annotation.processors")
        this.resources.excludes.add("ru/fsight/fmp/*")
        this.resources.excludes.add("ru/fsight/fmp/BuildConfig")

        //this.resources.excludes.add("com/mobrun/plugin/*")
    }

    // Declare the task that will monitor all configurations.
    configurations.all {
        // 2 Define the resolution strategy in case of conflicts.
        resolutionStrategy {
            // Fail eagerly on version conflict (includes transitive dependencies),
            // e.g., multiple different versions of the same dependency (group and name are equal).
            //failOnVersionConflict()

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
        languageVersion = kotlinApiVersion
        apiVersion = kotlinApiVersion
        jvmTarget = "11"
    }
}

dependencies {
    val core: String by rootProject.extra
    val coroutinesCore: String by rootProject.extra
    val coroutinesAndroid: String by rootProject.extra
    val sresultpresentation: String by rootProject.extra
    val junit: String by rootProject.extra
    val leakCanary: String by rootProject.extra
    val runner: String by rootProject.extra
    val easypermissions: String by rootProject.extra
    val espresso: String by rootProject.extra

    implementation(files("libs/fmp.aar"))
    implementation(core)
    implementation(coroutinesCore)
    implementation(coroutinesAndroid)
    implementation(sresultpresentation)
    implementation(easypermissions)
    // json
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")

    // sqlcipher
    implementation("net.zetetic:android-database-sqlcipher:4.5.1")

/*    implementation(project(":hiveprocessor")) {
        exclude(group = "com.mobrun", module = "plugin")
    }
    kapt(project(":hiveprocessor"))*/

    implementation(project(":hivecore"))
    ksp(project(":hiveksp"))

    debugImplementation(leakCanary)
    testImplementation(junit)
    androidTestImplementation(runner)
    androidTestImplementation(espresso)
}