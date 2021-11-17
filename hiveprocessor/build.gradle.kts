import config.Builds

plugins {
    id("java-library")
    id("kotlin")
    id("maven-publish")
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
    sourceSets {
        getByName("main") {
            java.setSrcDirs(Builds.codeDirs)
        }
    }

    withJavadocJar()
    withSourcesJar()
}

dependencies {
    implementation(fileTree(mapOf("include" to listOf("*.jar"), "dir" to "libs")))
    implementation(kotlin("stdlib-jdk8", config.Versions.kotlin))
    implementation(config.Libs.Common.gson)
    implementation(config.Libs.Core.coroutinesCore)

    //implementation(project(":hhive"))

    implementation(config.Libs.Processor.kotlinPoet)
    implementation(config.Libs.Processor.autoService)
    kapt(config.Libs.Processor.autoService)
}

kapt {
    useBuildCache = true
    generateStubs = false
}

group = "pro.krit.hiveprocessor"
version = Builds.Processor.VERSION_NAME

publishing {
    publications {
        create<MavenPublication>("hiveprocessor") {
            from(components["kotlin"])
            // You can then customize attributes of the publication as shown below.
            groupId = "pro.krit.hiveprocessor"
            artifactId = "hiveprocessor"
            version = Builds.Processor.VERSION_NAME

            artifact(tasks["sourcesJar"])
            artifact(tasks["javadocJar"])
        }
    }

    repositories {
        maven {
            name = "hiveprocessor"
            url = uri("${buildDir}/publishing-repository")
        }
    }
}
