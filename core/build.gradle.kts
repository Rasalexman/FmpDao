plugins {
    id("java-library")
    kotlin("jvm")
}

val codePath: String by rootProject.extra
val srcDirs = listOf(codePath)
sourceSets {
    getByName("main") {
        java.setSrcDirs(srcDirs)
        java.exclude("com/mobrun/plugin/*")
    }
}

kotlin {
    this.sourceSets {
        getByName("main") {
            this.kotlin.exclude("com/mobrun/plugin/*")
        }
    }
}

java {
    this.sourceSets {
        getByName("main") {
            java.setSrcDirs(srcDirs)
            java.exclude("com/mobrun/plugin/*")
        }
    }
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

dependencies {
    val kotlinPoet: String by rootProject.extra
    val gson: String by rootProject.extra
    val coroutinesCore: String by rootProject.extra
    val excludes = listOf(
        "com/mobrun/plugin/*",
        "META-INF",
        "ru/fsight/fmp/*"
    )

    api(fileTree(mapOf("include" to listOf("*.jar"), "dir" to "libs", "exclude" to excludes)))
    api(gson)
    api(kotlinPoet)
    compileOnly(coroutinesCore)
}