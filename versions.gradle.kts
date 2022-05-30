//------ APP VERSION
val hhiveVersion = "1.4.17"
val kotlin_version = "1.6.21"
extra["hhiveVersion"] = hhiveVersion
extra["hiveKspVersion"] = hhiveVersion

//------ CONFIG DATA
extra["minSdkVersion"] = 18
extra["buildSdkVersion"] = 31
extra["toolsVersion"] = "31.0.0"
extra["apiVersion"] = "1.6"
extra["jvmVersion"] = "11"
extra["agpVersion"] = "7.2.1"
extra["kotlinVersion"] = kotlin_version
extra["jitpackPath"] = "https://jitpack.io"
extra["codePath"] = "src/main/kotlin"

//------- LIBS VERSIONS
val gson = "2.9.0"
val navigation = "2.5.0-rc01"
val kodi = "1.6.3"
val leakcanary = "2.8.1"
val sresult = "1.3.45"
val junit = "4.13.2"
val coroutines = "1.6.1"
val core: String = "1.7.0"
val kotest = "5.0.3"
val runner = "1.1.0"
val espresso = "3.1.0"
val ksp = "$kotlin_version-1.0.5"
val kotlinpoet = "1.11.0"
val autoService = "1.0.1"
val rxJava3 = "3.1.4"

extra["navigation"] = navigation

//------- Libs path
extra["gson"] = "com.google.code.gson:gson:$gson"
extra["leakCanary"] = "com.squareup.leakcanary:leakcanary-android:$leakcanary"
extra["sresultpresentation"] = "com.github.Rasalexman.SResult:sresultpresentation:$sresult"
extra["coroutinesCore"] = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutines"
extra["core"] = "androidx.core:core-ktx:$core"
extra["kodi"] = "com.github.Rasalexman.KODI:kodi:$kodi"
extra["rxjava3"] = "io.reactivex.rxjava3:rxjava:$rxJava3"
extra["kotlinPoet"] = "com.squareup:kotlinpoet:$kotlinpoet"
extra["autoService"] = "com.google.auto.service:auto-service:$autoService"
extra["kotlinpoetKsp"] = "com.squareup:kotlinpoet-ksp:$kotlinpoet"
extra["kspapi"] = "com.google.devtools.ksp:symbol-processing-api:$ksp"

extra["junit"] = "junit:junit:$junit"
extra["runner"] = "androidx.test:runner:$runner"
extra["espresso"] = "androidx.test.espresso:espresso-core:$espresso"
