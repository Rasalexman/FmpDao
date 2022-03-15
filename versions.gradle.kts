//------ APP VERSION
extra["hhiveVersion"] = "1.3.51"

//------ CONFIG DATA
extra["minSdkVersion"] = 19
extra["buildSdkVersion"] = 31
extra["toolsVersion"] = "31.0.0"
extra["apiVersion"] = "1.6"
extra["jvmVersion"] = "11"
extra["agpVersion"] = "7.1.2"
extra["kotlinVersion"] = "1.6.10"
extra["jitpackPath"] = "https://jitpack.io"
extra["codePath"] = "src/main/kotlin"

//------- LIBS VERSIONS
val gson = "2.8.9"
val navigation = "2.5.0-alpha03"
val kodi = "1.6.2"
val leakcanary = "2.8.1"
val sresult = "1.3.43"
val junit = "4.13.2"
val coroutines = "1.6.0"
val core: String = "1.7.0"
val kotest = "5.0.3"
val runner = "1.1.0"
val espresso = "3.1.0"

extra["navigation"] = navigation

//------- Libs path
extra["gson"] = "com.google.code.gson:gson:$gson"
extra["leakCanary"] = "com.squareup.leakcanary:leakcanary-android:$leakcanary"
extra["sresultpresentation"] = "com.github.Rasalexman.SResult:sresultpresentation:$sresult"
extra["coroutinesCore"] = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutines"
extra["core"] = "androidx.core:core-ktx:$core"
extra["kotlinPoet"] = "com.squareup:kotlinpoet:1.10.2"
extra["autoService"] = "com.google.auto.service:auto-service:1.0.1"
extra["kodi"] = "com.github.Rasalexman.KODI:kodi:$kodi"

extra["junit"] = "junit:junit:$junit"
extra["runner"] = "androidx.test:runner:$runner"
extra["espresso"] = "androidx.test.espresso:espresso-core:$espresso"
