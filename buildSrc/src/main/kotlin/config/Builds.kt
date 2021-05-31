package config

import java.util.*

object Builds {
    const val MIN_VERSION = 21
    const val COMPILE_VERSION = 30
    const val TARGET_VERSION = 30
    const val BUILD_TOOLS = "30.0.2"
    const val APP_ID = "pro.krit.fmpdaoexample"

    val codeDirs = arrayListOf(
        "src/main/kotlin"
    )

    val resDirs = arrayListOf(
        "src/main/res",
        "src/main/res/fragments",
        "src/main/res/pages",
        "src/main/res/items",
        "src/main/res/icons"
    )

    object App {
        const val VERSION_CODE = 10001
        const val VERSION_NAME = "1.0.2"
    }

    object Types {
        const val DEBUG = "debug"
        const val DEBUG_NOT_LOADED = "debugNotLoad"
        const val RELEASE = "release"
    }

    private const val ASSEMBLE = "assemble"
    private val incrementalTaskNames by lazy {
        listOf<String>(
            "${ASSEMBLE}${Types.DEBUG.capitalize(Locale.getDefault())}",
            "${ASSEMBLE}${Types.DEBUG_NOT_LOADED.capitalize(Locale.getDefault())}"
        )
    }

    fun isNeedIncrementBuildVersion(taskName: String): Boolean {
        return incrementalTaskNames.contains(taskName)
    }
}