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

package pro.krit.hhivecore.common

import com.mobrun.plugin.api.DatabaseAPI
import com.mobrun.plugin.models.Error
import com.mobrun.plugin.models.StatusRequest
import com.mobrun.plugin.models.StatusSelectTable
import pro.krit.hhivecore.base.IDao
import pro.krit.hhivecore.common.RequestExecuter.isNotBad
import pro.krit.hhivecore.extensions.fullTableName
import pro.krit.hhivecore.extensions.getErrorMessage
import pro.krit.hhivecore.extensions.initFields
import pro.krit.hhivecore.extensions.triggerDaoIfOk

object QueryExecuter {

    const val DEFAULT_ERROR_CODE = 1001
    const val ERROR_NO_TABLE_CODE = 1
    const val ERROR_NO_TABLE = "no such table"

    inline fun <reified E : Any, reified S : StatusSelectTable<E>> executeQuery(
        dao: IDao,
        query: String,
        errorCode: Int = DEFAULT_ERROR_CODE,
        methodName: String = "executeQuery",
        notifyAll: Boolean = false
    ): List<E> {
        return try {
            val status = executeStatus<E, S>(dao, query, errorCode, methodName, notifyAll)
            status.result.database.records.orEmpty()
        } catch (e: Throwable) {
            println("[ERROR]: ${dao.fullTableName} ERROR WITH QUERY $query")
            e.printStackTrace()
            emptyList()
        }
    }

    fun executeKeyQuery(
        dao: IDao,
        key: String,
        query: String,
        errorCode: Int = DEFAULT_ERROR_CODE,
        methodName: String = "executeKeyQuery",
        notifyAll: Boolean = false,
        fields: List<String>? = null
    ): String {
        return try {
            val queryKey = QueryBuilder.createWithFields(key, fields)
            val status = executeStatus<Map<String, String>, StatusSelectTable<Map<String, String>>>(
                dao,
                query,
                errorCode,
                methodName,
                notifyAll
            )
            status.result.database.records.firstOrNull()?.get(queryKey).orEmpty()
        } catch (e: Throwable) {
            e.printStackTrace()
            ""
        }
    }

    inline fun <reified E : Any, reified S : StatusSelectTable<E>> executeResultQuery(
        dao: IDao,
        query: String,
        errorCode: Int = DEFAULT_ERROR_CODE,
        methodName: String = "executeResultQuery",
        notifyAll: Boolean = false
    ): Result<List<E>> {
        return try {
            val status = executeStatus<E, S>(dao, query, errorCode, methodName, notifyAll)
            if (status.isNotBad()) {
                val result = status.result.database.records.orEmpty()
                Result.success(result)
            } else {
                println("[ERROR]: ${dao.fullTableName} ERROR WITH QUERY $query")
                val message = status.getErrorMessage()
                Result.failure(IllegalStateException(message))
            }

        } catch (e: Throwable) {
            println("[ERROR]: ${dao.fullTableName} ERROR WITH QUERY $query")
            e.printStackTrace()
            Result.failure(e)
        }
    }

    inline fun <reified E : Any, reified S : StatusSelectTable<E>> executeStatus(
        dao: IDao,
        query: String,
        errorCode: Int = DEFAULT_ERROR_CODE,
        methodName: String = "executeStatus",
        notifyAll: Boolean = false
    ): StatusSelectTable<E> {

        if (query.isEmpty()) {
            val okStatus = StatusSelectTable<E>()
            okStatus.status = StatusRequest.OK
            return okStatus.apply {
                if (notifyAll) this.triggerDaoIfOk(dao)
            }
        }

        val errorStatus: StatusSelectTable<E> = try {
            val localDao: IDao = dao
            val hyperHiveDatabaseApi = localDao.fmpDatabase.databaseApi
            val clazz = S::class.java
            val status = hyperHiveDatabaseApi.query(query, clazz).execute()!!
            // check for table creation
            val isOkStatus = status.checkTableStatus(localDao, hyperHiveDatabaseApi)
            if (isOkStatus) {
                status
            } else {
                hyperHiveDatabaseApi.query(query, clazz).execute()!!
            }.apply {
                if (notifyAll) this.triggerDaoIfOk(localDao)
            }
        } catch (e: Throwable) {
            println("[ERROR]: ${dao.fullTableName} ERROR WITH QUERY $query")
            e.printStackTrace()
            createErrorStatus(
                ex = e,
                codeType = errorCode,
                method = methodName
            )
        }
        return errorStatus
    }

    inline fun <reified E : Any, reified S : StatusSelectTable<E>> S.checkTableStatus(
        dao: IDao,
        databaseApi: DatabaseAPI
    ): Boolean {
        return if (!this.isOk && dao.fieldsData != null) {
            val isTableError = this.errors?.any { error ->
                        error.code == ERROR_NO_TABLE_CODE ||
                        error.descriptions.any { it.contains(ERROR_NO_TABLE) } ||
                        error.description?.contains(ERROR_NO_TABLE) == true
            } ?: false
            if (isTableError) {
                dao.initFields<E>()
                val clazz = S::class.java
                val createTableQuery = QueryBuilder.createTableQuery(dao)
                val createTableStatus = databaseApi.query(createTableQuery, clazz).execute()!!
                createTableStatus.isOk
            } else {
                true
            }
        } else {
            true
        }
    }

    inline fun <reified E : Any> createErrorStatus(
        ex: Throwable,
        codeType: Int,
        method: String
    ): StatusSelectTable<E> {
        val error = Error()
        error.code = codeType
        error.description = ex.message ?: "$method HyperHive Error with $ex"
        return StatusSelectTable<E>().apply {
            val currentErrors = errors ?: ArrayList()
            currentErrors.add(error)
            errors = currentErrors
        }
    }
}