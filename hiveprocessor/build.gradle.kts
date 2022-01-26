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
        //java.exclude("com.mobrun.plugin.BuildConfig")
    }
}

tasks.register<Jar>(name = "sourceJar") {
    from(sourceSets["main"].java.srcDirs)
    archiveClassifier.set("sources")
}

java {
    this.sourceSets {
        getByName("main") {
            java.setSrcDirs(Builds.codeDirs)
        }
    }
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11

    withSourcesJar()
    withJavadocJar()
}

dependencies {
    //libsTree.exclude(listOf("*BuildConfig*"))

    compileOnly(fileTree(mapOf("include" to listOf("*.jar"), "dir" to "libs", "exclude" to Builds.excludes)))
    compileOnly(config.Libs.Common.gson)
    compileOnly(config.Libs.Core.coroutinesCore)

    implementation(config.Libs.Processor.kotlinPoet)
    implementation(config.Libs.Processor.autoService)
    kapt(config.Libs.Processor.autoService)
}

/*tasks.create(name = "sourceJar", type = Jar::class) {
    archiveClassifier.set("sources")
}*/

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

    /*repositories {
        maven {
            name = "hiveprocessor"
            setUrl(uri("${buildDir}/publishing-repository"))
        }
    }*/
}
