package config

object Builds {
    const val MIN_VERSION = 19
    const val COMPILE_VERSION = 31
    const val TARGET_VERSION = 31
    const val BUILD_TOOLS = "31.0.0"
    const val APP_ID = "pro.krit.fmpdaoexample"

    val codeDirs = arrayListOf(
        "src/main/kotlin"
    )

    val excludes = listOf(
        "com/mobrun/plugin/BuildConfig.java",
        "META-INF",
        "ru/fsight/fmp/*"
    )

    object App {
        const val VERSION_CODE = 10002
        const val VERSION_NAME = "1.0.2"
    }

    object Types {
        const val DEBUG = "debug"
        const val RELEASE = "release"
    }

    object Processor {
        const val VERSION_CODE = 103044
        const val VERSION_NAME = "1.3.44"
    }
}