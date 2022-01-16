import config.Builds

plugins {
    id("java-library")
    id("kotlin")
    id("maven-publish")
    kotlin("kapt")
}

group = "pro.krit.hiveprocessor"
version = Builds.Processor.VERSION_NAME

sourceSets {
    getByName("main") {
        java.setSrcDirs(Builds.codeDirs)
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
    /*sourceSets {
        getByName("main") {
            java.setSrcDirs(Builds.codeDirs)
        }
    }*/

    withSourcesJar()
    withJavadocJar()
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        apiVersion = "1.6"
        languageVersion = "1.6"
        jvmTarget = "11"
    }
}

dependencies {
    implementation(fileTree(mapOf("include" to listOf("*.jar"), "dir" to "libs")))
    //implementation(kotlin("stdlib-jdk8", config.Versions.kotlin))
    implementation(config.Libs.Common.gson)
    compileOnly(config.Libs.Core.coroutinesCore)

    implementation(config.Libs.Processor.kotlinPoet)
    implementation(config.Libs.Processor.autoService)
    kapt(config.Libs.Processor.autoService)
}

/*tasks.create(name = "sourceJar", type = Jar::class) {
    archiveClassifier.set("sources")
}*/

/*publishing {
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
            setUrl(uri("${buildDir}/publishing-repository"))
        }
    }
}*/
