package pro.krit.hiveprocessor.provider

import com.google.gson.GsonBuilder
import com.mobrun.plugin.api.DatabaseAPI
import com.mobrun.plugin.api.HyperHive
import com.mobrun.plugin.api.HyperHiveState
import com.mobrun.plugin.api.VersionAPI
import java.io.File

/**
 * Base class for extends and use as FMP database instance
 */
abstract class HyperHiveDatabase : IHyperHiveDatabase {

    private var hyperHive: HyperHive? = null
    private var hyperHiveState: HyperHiveState? = null

    override val databasePath: String
        get() = hyperHiveState?.dbPathDefault.orEmpty()

    private var savedFmpDbKey: String = ""

    override val isDbCreated: Boolean
        get() {
            val file = File(databasePath)
            return file.exists()
        }

    override val databaseApi: DatabaseAPI
        get() = provideHyperHive().databaseAPI

    override fun provideHyperHive(): HyperHive {
        return hyperHive ?: throw NullPointerException("HyperHive instance is not initialized")
    }

    override fun provideHyperHiveState(): HyperHiveState {
        return hyperHiveState ?: throw NullPointerException("HyperHiveState instance is not initialized")
    }

    @Suppress("UNCHECKED_CAST")
    fun<T : HyperHiveDatabase> initialize(hState: HyperHiveState, config: HyperHiveConfig): T {
        savedFmpDbKey = config.dbKey

        hyperHiveState = hState
            .setHostWithSchema(config.serverAddress)
            .setApiVersion(VersionAPI.V_1)
            .setEnvironmentSlug(config.environment)
            .setProjectSlug(config.project)
            .setDbPathDefault(config.dbPath)
            .setVersionProject(config.projectVersion)
            .setDefaultRetryCount(config.retryCount)
            .setDefaultRetryIntervalSec(config.retryInterval)
            .setGsonForParcelPacker(GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()).apply {
                hyperHive = buildHyperHive().setupLogs(config.logLevel)
            }
        return this as T
    }

    override fun openDatabase(dbKey: String, pathBase: String): DatabaseState {
        var isClosed = true
        var isOpened = tryToOpenDatabase(dbKey, pathBase)
        if(!isOpened) {
            isClosed = tryToCloseDatabase(pathBase)
            isOpened = tryToOpenDatabase(dbKey, pathBase)
        }
        return DatabaseState(isOpened = isOpened, isClosed = isClosed)
    }

    override fun closeDatabase(pathBase: String): DatabaseState {
        val isClosed = tryToCloseDatabase(pathBase)
        return DatabaseState(isOpened = false, isClosed = isClosed)
    }

    override fun closeAndClearProviders(pathBase: String) {
        if(tryToCloseDatabase(pathBase)) {
            clearProviders()
        }
    }

    private fun tryToOpenDatabase(fmpKey: String = "", pathBase: String = ""): Boolean {
        val currentKey = fmpKey.takeIf { it.isNotEmpty() } ?: savedFmpDbKey
        val key = generateKey(currentKey)
        val hyperHiveDatabaseApi = databaseApi
        return if(pathBase.isNotEmpty()) {
            hyperHiveDatabaseApi.openBase(pathBase, key)
        } else {
            hyperHiveDatabaseApi.openDefaultBase(key)
        }
    }

    private fun tryToCloseDatabase(pathBase: String = ""): Boolean {
        val hyperHiveDatabaseApi = databaseApi
        return if(pathBase.isNotEmpty()) {
            hyperHiveDatabaseApi.closeBase(pathBase)
        } else {
            hyperHiveDatabaseApi.closeDefaultBase()
        }
    }

    private fun generateKey(login: String): String {
        return "${login}bjasbjasbjasew"
    }

    private fun clearProviders() {
        savedFmpDbKey = ""
        hyperHive = null
        hyperHiveState = null
    }

    private fun HyperHive.setupLogs(logLevel: Int): HyperHive {
        this.loggingAPI.setLogLevel(logLevel)
        return this
    }
}