plugins {
    id("java-library")
    kotlin("jvm")
    id("maven-publish")
}

val codePath: String by rootProject.extra
val hiveKspVersion: String by rootProject.extra

val srcDirs = listOf(codePath)
group = "pro.krit.hivecore"
version = hiveKspVersion

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

    withSourcesJar()
    withJavadocJar()
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


publishing {
    publications {
        create<MavenPublication>("hivecore") {
            from(components["kotlin"])
            // You can then customize attributes of the publication as shown below.
            groupId = "pro.krit.hivecore"
            artifactId = "hivecore"
            version = hiveKspVersion

            artifact(tasks["sourcesJar"])
            artifact(tasks["javadocJar"])
        }
    }

    repositories {
        maven {
            name = "hivecore"
            url = uri("${buildDir}/publishing-repository")
        }
    }
}