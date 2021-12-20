// Copyright (c) 2021 Aleksandr Minkin aka Rasalexman (sphc@yandex.ru)
//
// Permission is hereby granted, free of charge, to any person obtaining a copy of this software
// and associated documentation files (the "Software"), to deal in the Software without restriction,
// including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
// and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so,
// subject to the following conditions:
// The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
// WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
// IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
// WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH
// THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

package pro.krit.hiveprocessor.provider

import com.google.gson.GsonBuilder
import com.mobrun.plugin.api.DatabaseAPI
import com.mobrun.plugin.api.HyperHive
import com.mobrun.plugin.api.HyperHiveState
import com.mobrun.plugin.api.VersionAPI
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import pro.krit.hiveprocessor.base.IDao
import pro.krit.hiveprocessor.extensions.fullTableName
import java.io.File

/**
 * Base class for extends and use as FMP database instance
 */
abstract class AbstractFmpDatabase : IFmpDatabase {

    private var hyperHive: HyperHive? = null
    private var hyperHiveState: HyperHiveState? = null

    private var requestHeaders: Map<String, String>? = null

    override val databasePath: String
        get() = hyperHiveState?.dbPathDefault.orEmpty()

    private var savedFmpDbKey: String = ""

    private val triggers: HashMap<String, Flow<String>> = hashMapOf()

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
        return hyperHiveState
            ?: throw NullPointerException("HyperHiveState instance is not initialized")
    }

    override fun getTrigger(dao: IDao): Flow<String> {
        return triggers.getOrPut(dao.fullTableName) {
            MutableSharedFlow(
                extraBufferCapacity = 1,
                onBufferOverflow = BufferOverflow.DROP_OLDEST
            )
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : AbstractFmpDatabase> initialize(hState: HyperHiveState, config: DatabaseConfig): T {
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
            .setGsonForParcelPacker(GsonBuilder().excludeFieldsWithoutExposeAnnotation().create())
            .apply {
                hyperHive = buildHyperHive().setupLogs(config.logLevel)
            }
        return this as T
    }

    override fun setDefaultHeaders(headers: Map<String, String>?) {
        this.requestHeaders = headers
    }

    override fun getDefaultHeaders(): Map<String, String>? {
        return this.requestHeaders
    }

    override fun openDatabase(dbKey: String, pathBase: String): DatabaseState {
        var isClosed = false
        var isOpened = tryToOpenDatabase(dbKey, pathBase)
        if (!isOpened) {
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
        if (tryToCloseDatabase(pathBase)) {
            clearProviders()
        }
    }

    private fun tryToOpenDatabase(fmpKey: String = "", pathBase: String = ""): Boolean {
        val currentKey = fmpKey.takeIf { it.isNotEmpty() } ?: savedFmpDbKey
        val key = generateKey(currentKey)
        val hyperHiveDatabaseApi = databaseApi
        return if (pathBase.isNotEmpty()) {
            hyperHiveDatabaseApi.openBase(pathBase, key)
        } else {
            hyperHiveDatabaseApi.openDefaultBase(key)
        }
    }

    private fun tryToCloseDatabase(pathBase: String = ""): Boolean {
        val hyperHiveDatabaseApi = databaseApi
        return if (pathBase.isNotEmpty()) {
            hyperHiveDatabaseApi.closeBase(pathBase)
        } else {
            hyperHiveDatabaseApi.closeDefaultBase()
        }
    }

    private fun generateKey(login: String): String {
        return login.takeIf { it.isNotEmpty() }?.let { "${login}bjasbjasbjasew" }.orEmpty()
    }

    private fun clearProviders() {
        triggers.clear()
        savedFmpDbKey = ""
        hyperHive = null
        hyperHiveState = null
    }

    private fun HyperHive.setupLogs(logLevel: Int): HyperHive {
        this.loggingAPI.setLogEnabled(logLevel > 0)
        this.loggingAPI.setLogLevel(logLevel)
        return this
    }
}