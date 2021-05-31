import config.Builds

plugins {
    id("java-library")
    id("kotlin")
    kotlin("kapt")
}

sourceSets {
    getByName("main") {
        java.setSrcDirs(Builds.codeDirs)
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
    api(fileTree(mapOf("include" to listOf("*.jar"), "dir" to "libs")))
    implementation(kotlin("stdlib-jdk8", config.Versions.kotlin))

    api(config.Libs.Common.gson)

    implementation("com.squareup:kotlinpoet:1.8.0")
    implementation("com.google.auto.service:auto-service:1.0")
    kapt("com.google.auto.service:auto-service:1.0")
}