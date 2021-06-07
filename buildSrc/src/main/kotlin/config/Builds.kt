package config

object Builds {
    const val MIN_VERSION = 21
    const val COMPILE_VERSION = 30
    const val TARGET_VERSION = 30
    const val BUILD_TOOLS = "30.0.2"
    const val APP_ID = "pro.krit.fmpdaoexample"

    val codeDirs = arrayListOf(
        "src/main/kotlin"
    )

    object App {
        const val VERSION_CODE = 10001
        const val VERSION_NAME = "1.0.2"
    }

    object Types {
        const val DEBUG = "debug"
        const val RELEASE = "release"
    }

    object Processor {
        const val VERSION_CODE = 10300
        const val VERSION_NAME = "1.3.0"
    }
}