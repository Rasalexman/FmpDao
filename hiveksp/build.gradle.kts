plugins {
    id("java-library")
    kotlin("jvm")
    id("maven-publish")
}

val codePath: String by rootProject.extra
val hiveKspVersion: String by rootProject.extra

val srcDirs = listOf(codePath)
group = "pro.krit.hiveksp"
version = hiveKspVersion

sourceSets {
    getByName("main") {
        java.setSrcDirs(srcDirs)
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().all {
    kotlinOptions {
        this.freeCompilerArgs += listOf(
            "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
            "-opt-in=kotlin.RequiresOptIn"
        )
    }
}

tasks.register<Jar>(name = "sourceJar") {
    from(sourceSets["main"].java.srcDirs)
    archiveClassifier.set("sources")
}

java {
    this.sourceSets {
        getByName("main") {
            java.setSrcDirs(srcDirs)
        }
    }
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11

    withJavadocJar()
    withSourcesJar()
}


dependencies {
    val kotlinpoetKsp: String by rootProject.extra
    val kspapi: String by rootProject.extra
    val gson: String by rootProject.extra
    val excludes = listOf(
        "com/mobrun/plugin/BuildConfig.java",
        "META-INF",
        "ru/fsight/fmp/*"
    )

    compileOnly(fileTree(mapOf("include" to listOf("*.jar"), "dir" to "libs", "exclude" to excludes)))

    implementation(project(":hiveprocessor")) {
        exclude(group = "com.mobrun", module = "plugin")
    }

    compileOnly(gson)
    implementation(kotlinpoetKsp)
    implementation(kspapi)
}

publishing {
    publications {
        create<MavenPublication>("hiveksp") {
            from(components["kotlin"])
            // You can then customize attributes of the publication as shown below.
            groupId = "pro.krit.hiveksp"
            artifactId = "hiveksp"
            version = hiveKspVersion

            artifact(tasks["sourcesJar"])
            artifact(tasks["javadocJar"])
        }
    }

    repositories {
        maven {
            name = "kodiksp"
            url = uri("${buildDir}/publishing-repository")
        }
    }
}