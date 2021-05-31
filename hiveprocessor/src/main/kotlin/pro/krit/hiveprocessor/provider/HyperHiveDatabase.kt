package pro.krit.hiveprocessor.provider

import com.google.gson.GsonBuilder
import com.mobrun.plugin.api.HyperHive
import com.mobrun.plugin.api.HyperHiveState
import com.mobrun.plugin.api.VersionAPI
import java.io.File

abstract class HyperHiveDatabase : IHyperHiveDatabase {

    private var hyperHive: HyperHive? = null
    private var hyperHiveState: HyperHiveState? = null

    override val databasePath: String
        get() = hyperHiveState?.dbPathDefault.orEmpty()

    override val isDbCreated: Boolean
        get() {
            val file = File(databasePath)
            return file.exists()
        }

    override fun provideHyperHive(): HyperHive {
        return hyperHive ?: throw NullPointerException("HyperHive instance is not initialized")
    }

    override fun provideHyperHiveState(): HyperHiveState {
        return hyperHiveState ?: throw NullPointerException("HyperHiveState instance is not initialized")
    }

    fun initialize(hState: HyperHiveState, config: HyperHiveConfig): IHyperHiveDatabase {
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

        return this
    }

    fun closeAndClear(pathBase: String = "") {
        if(pathBase.isNotEmpty()) {
            hyperHive?.databaseAPI?.closeBase(pathBase)
        } else {
            hyperHive?.databaseAPI?.closeDefaultBase()
        }
        clear()
    }

    fun clear() {
        hyperHive = null
        hyperHiveState = null
    }

    private fun HyperHive.setupLogs(logLevel: Int): HyperHive {
        this.loggingAPI.setLogLevel(logLevel)
        return this
    }
}