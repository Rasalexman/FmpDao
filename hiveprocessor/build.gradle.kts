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
        //java.exclude("com.mobrun.plugin.BuildConfig")
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
        }
    }
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11

    withSourcesJar()
    withJavadocJar()
}

dependencies {
    //libsTree.exclude(listOf("*BuildConfig*"))
    val gson: String by rootProject.extra
    val coroutinesCore: String by rootProject.extra
    val kotlinPoet: String by rootProject.extra
    val autoService: String by rootProject.extra
    val excludes = listOf(
        "com/mobrun/plugin/BuildConfig.java",
        "META-INF",
        "ru/fsight/fmp/*"
    )

    compileOnly(fileTree(mapOf("include" to listOf("*.jar"), "dir" to "libs", "exclude" to excludes)))
    compileOnly(gson)
    compileOnly(coroutinesCore)

    implementation(kotlinPoet)
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
