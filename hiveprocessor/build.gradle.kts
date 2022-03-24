plugins {
    id("java-library")
    id("kotlin")
    id("maven-publish")
    kotlin("kapt")
}

val codePath: String by rootProject.extra
val hhiveVersion: String by rootProject.extra
val srcDirs = listOf(codePath)

group = "pro.krit.hiveprocessor"
version = hhiveVersion

sourceSets {
    getByName("main") {
        java.setSrcDirs(srcDirs)
        java.exclude("com.mobrun.plugin.BuildConfig")
    }
}

tasks.create(name = "sourceJar", type = Jar::class) {
    from(sourceSets["main"].java.srcDirs)
    archiveClassifier.set("sources")
}

java {
    this.sourceSets {
        getByName("main") {
            java.setSrcDirs(srcDirs)
            java.exclude("com.mobrun.plugin.BuildConfig")
        }
    }
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11

    withSourcesJar()
    withJavadocJar()
}

dependencies {
    val autoService: String by rootProject.extra

    api(project(":core"))
    implementation(autoService)
    kapt(autoService)
}

publishing {
    publications {
        create<MavenPublication>("hiveprocessor") {
            from(components["kotlin"])
            // You can then customize attributes of the publication as shown below.
            groupId = "pro.krit.hiveprocessor"
            artifactId = "hiveprocessor"
            version = hhiveVersion

            artifact(tasks["sourcesJar"])
            artifact(tasks["javadocJar"])
        }
    }

    repositories {
        maven {
            name = "hiveprocessor"
            setUrl(uri("${buildDir}/publishing-repository"))
        }
    }
}
