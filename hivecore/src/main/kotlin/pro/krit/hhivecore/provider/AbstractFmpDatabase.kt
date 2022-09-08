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

package pro.krit.hhivecore.provider

import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import io.reactivex.rxjava3.subjects.Subject
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import pro.krit.hhivecore.base.IDao
import pro.krit.hhivecore.extensions.fullTableName
import ru.fsight.fmp.FMP
import ru.fsight.fmp.FMPDatabase
import ru.fsight.fmp.FMPUser
import ru.fsight.fmp.model.fmp.FMPResult
import java.io.File

/**
 * Base class for extends and use as FMP database instance
 */
abstract class AbstractFmpDatabase : IFmpDatabase {

    //----NEW API
    private var fmp: FMP? = null
    private var fmpDatabase: FMPDatabase? = null
    private var currentFmpUser: FMPUser? = null

    private var fmpStoragePath: String = ""
    private var requestHeaders: Map<String, String>? = null

    override val databasePath: String
        get() = provideFmpDatabase().path

    private val flowTriggers: HashMap<String, Flow<String>> = hashMapOf()
    private val rxTriggers: HashMap<String, Subject<String>> = hashMapOf()
    private var isSharedTriggers: Boolean = true

    override val isDbCreated: Boolean
        get() {
            val file = File(databasePath)
            return file.exists()
        }

    override fun provideFmp(): FMP {
        return fmp ?: throw NullPointerException("FMP instance is not initialized. Call 'initialize' method first")
    }

    override fun provideUser(): FMPUser {
        return currentFmpUser ?: throw NullPointerException("FmpUser is not authorized. Call 'auth' method first")
    }

    override fun provideFmpDatabase(): FMPDatabase {
        return fmpDatabase ?: throw NullPointerException("FMPDatabase instance is not initialized. Call 'openDatabase' method first")
    }

    override fun getFlowTrigger(dao: IDao): Flow<String> {
        val triggerKey = dao.fullTableName
        return flowTriggers.getOrPut(triggerKey) {
            createTrigger(triggerKey)
        }
    }

    override fun getRxTrigger(dao: IDao): Subject<String> {
        val triggerKey = dao.fullTableName
        return rxTriggers.getOrPut(triggerKey) {
            createRxTrigger(triggerKey)
        }
    }

    private fun createTrigger(tableName: String): Flow<String> {
        return if(isSharedTriggers) {
            val trigger = MutableSharedFlow<String>(
                replay = 1,
                extraBufferCapacity = 1,
                onBufferOverflow = BufferOverflow.DROP_OLDEST
            )
            trigger.tryEmit(tableName)
            trigger
        } else {
            MutableStateFlow(tableName)
        }
    }

    private fun createRxTrigger(tableName: String): Subject<String> {
        return if(isSharedTriggers) {
            val sub = PublishSubject.create<String>()
            sub.onNext(tableName)
            sub
        } else {
            BehaviorSubject.createDefault(tableName)
        }
    }

    override fun auth(login: String, password: String): Pair<FMPUser, FMPResult<Boolean>> {
        val fmpUser: FMPUser = provideFmp().user.username(login).password(password).build()
        this.currentFmpUser = fmpUser
        val authResult = fmpUser.auth()
        return fmpUser to authResult
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : AbstractFmpDatabase> initialize(config: DatabaseConfig): T {
        isSharedTriggers = config.isSharedTrigger

        this.fmp = FMP.Builder()             // Создать конструктор FMP.
            .api(FMP.API_V1)                     // Указать версию API сервера.
            .apply {
                if(config.certCheck && config.certPath.isNotEmpty()) {
                    certCheck(config.certCheck)                 // Выключить проверку TLS сертификата сервера.
                    cert(config.certPath)                       // Указать путь к самоподписному сертификату сервера.
                }
            }
            .deviceID(config.deviceId)                  // Указать ID устройства.
            .environment(config.environment)            // Среда на сервере.
            .headers(config.headers.orEmpty())          // Указать кастомные хедеры для каждого HTTP запроса.
            .address(config.serverAddress)              // Адрес сервера платформы.
            .project(config.project)                    // Проект внутри среды.
            .retryCount(config.retryCount)                      // Повторять неудачные HTTP запросы 10 раз.
            .retryInterval(config.retryInterval)                    // Повторять неудачные HTTP запросы каждые 6 секунд.
            .storage(config.storage)                                // Директория, где фреймворк будет хранить файлы.
            //.debugNoEncryption(config.disableEncryption)            // True отключает шифрование. По умолчанию false - шифрование активно.
            .build()

        // обязательно записываем путь к локальному хранилищу
        fmpStoragePath = config.storage
        // устанавлиаваем хедеры для запросов
        config.headers?.let(::setDefaultHeaders)

        return this as T
    }

    override fun setDefaultHeaders(headers: Map<String, String>?) {
        this.requestHeaders = headers
    }

    override fun getDefaultHeaders(): Map<String, String>? {
        return this.requestHeaders
    }

    override fun openDatabase(dbKey: String): DatabaseState {
        var isClosed = false
        // сначала создаем базу данных
        createFmpDatabase(dbKey)
        var isOpened = tryToOpenDatabase()
        if (!isOpened) {
            isClosed = tryToCloseDatabase()
            isOpened = tryToOpenDatabase()
        }
        return DatabaseState(isOpened = isOpened, isClosed = isClosed)
    }

    override fun closeDatabase(): DatabaseState {
        val isClosed = tryToCloseDatabase()
        return DatabaseState(isOpened = false, isClosed = isClosed)
    }

    override fun closeAndClearProviders(): DatabaseState {
        var isClosed = tryToCloseDatabase()
        if (!isClosed) {
            isClosed = tryToCloseDatabase()
        }
        if(isClosed) {
            clearProviders()
        }
        return DatabaseState(isOpened = false, isClosed = isClosed)
    }

    private fun createFmpDatabase(dbKey: String): Boolean {
        var isCreated = true
        if(fmpDatabase == null) {
            val fmpUser = currentFmpUser ?: provideFmp().user.username(dbKey).build().also {
                currentFmpUser = it
            }
            // путь к базе данных береться только из локального кэша юзера
            val databasePath = "${fmpUser.cache}/${dbKey}.db"
            // вызывается только один раз при инициализации ФМП
            try {
                val databaseBuilder = provideFmp().database
                fmpDatabase = databaseBuilder.path(databasePath).build()
                isCreated = true
            } catch (e: Exception) {
                provideFmp().log.error("ERROR build localDatabase: ${e.message}")
                isCreated = false
            }
        }
        return isCreated
    }

    private fun tryToOpenDatabase(): Boolean {
        val fmpDatabaseInstance = provideFmpDatabase()
        val openResult = fmpDatabaseInstance.open()
        if(!openResult.status) {
            provideFmp().log.error("tryToOpenDatabase error: ${openResult.error}")
        }
        return openResult.result
    }

    private fun tryToCloseDatabase(): Boolean {
        val fmpDatabaseInstance = provideFmpDatabase()
        val closeResult = fmpDatabaseInstance.close()
        if(!closeResult.status) {
            provideFmp().log.error("tryToCloseDatabase error: ${closeResult.error}")
        }
        return closeResult.result
    }

    private fun generateKey(login: String): String {
        return login.takeIf { it.isNotEmpty() }?.let { "${login}bjasbjasbjasew" }.orEmpty()
    }

    override fun clearProviders() {
        flowTriggers.clear()
        rxTriggers.clear()
        fmp = null
        fmpDatabase = null
    }
}