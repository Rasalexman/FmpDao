plugins {
    id("java-library")
    kotlin("jvm")
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

dependencies {
    val kotlinPoet: String by rootProject.extra
    val gson: String by rootProject.extra
    val coroutinesCore: String by rootProject.extra
    val excludes = listOf(
        "com/mobrun/plugin/BuildConfig.java",
        "META-INF",
        "ru/fsight/fmp/*"
    )

    api(fileTree(mapOf("include" to listOf("*.jar"), "dir" to "libs", "exclude" to excludes)))
    api(gson)
    api(kotlinPoet)
    compileOnly(coroutinesCore)
}