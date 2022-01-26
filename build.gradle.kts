// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:${config.Versions.gradle}")
        //classpath("com.google.gms:google-services:${config.Versions.google}")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${config.Versions.kotlin}")
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:${config.Versions.navigation}")


        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        apiVersion = "1.6"
        languageVersion = "1.6"
        jvmTarget = "11"
    }
}