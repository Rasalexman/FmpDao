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

package pro.krit.hiveprocessor.common

import com.mobrun.plugin.models.Error
import com.mobrun.plugin.models.StatusSelectTable
import pro.krit.hiveprocessor.base.IDao
import pro.krit.hiveprocessor.extensions.triggerFlow

object QueryExecuter {

    const val DEFAULT_ERRORCODE = 1001

    inline fun <reified E : Any, reified S : StatusSelectTable<E>> executeQuery(
        dao: IDao,
        query: String,
        errorCode: Int = DEFAULT_ERRORCODE,
        methodName: String = "",
        notifyAll: Boolean = false
    ): List<E> {
        return try {
            val status = executeStatus<E, S>(dao, query, errorCode, methodName, notifyAll)
            status.result.database.records.orEmpty()
        } catch (e: Exception) {
            e.printStackTrace()
            //println("[ERROR]: ${dao.fullTableName} ERROR WITH QUERY $query")
            emptyList()
        }
    }

    fun executeKeyQuery(
        dao: IDao,
        key: String,
        query: String,
        errorCode: Int = DEFAULT_ERRORCODE,
        methodName: String = "",
        notifyAll: Boolean = false
    ): String {
        return try {
            val status = executeStatus<Map<String, String>, StatusSelectTable<Map<String, String>>>(
                dao,
                query,
                errorCode,
                methodName,
                notifyAll
            )
            status.result.database.records.firstOrNull()?.get(key).orEmpty()
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    @Suppress("UNCHECKED_CAST")
    inline fun <reified E : Any, reified S : StatusSelectTable<E>> executeResultQuery(
        dao: IDao,
        query: String,
        errorCode: Int = DEFAULT_ERRORCODE,
        methodName: String = "",
        notifyAll: Boolean = false
    ): Result<List<E>> {
        return try {
            val status = executeStatus<E, S>(dao, query, errorCode, methodName, notifyAll)
            if (status is S) {
                Result.success(status.result.database.records.orEmpty())
            } else {
                val firstError = status.errors.firstOrNull()
                val message = firstError?.run {
                    description ?: descriptions.firstOrNull()
                }.orEmpty()
                Result.failure(IllegalStateException(message))
            }

        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    inline fun <reified E : Any, reified S : StatusSelectTable<E>> executeStatus(
        dao: IDao,
        query: String,
        errorCode: Int = DEFAULT_ERRORCODE,
        methodName: String = "executeStatus",
        notifyAll: Boolean = false
    ): S {
        return try {
            val hyperHiveDatabaseApi = dao.fmpDatabase.databaseApi
            hyperHiveDatabaseApi.query(query, S::class.java).execute()!!.apply {
                if (notifyAll) this.triggerFlow(dao)
            }
        } catch (e: Exception) {
            createErrorStatus<E>(
                ex = e,
                codeType = errorCode,
                method = methodName
            ) as S
        }
    }

    inline fun <reified E : Any, reified S : StatusSelectTable<E>> executeTransactionStatus(
        dao: IDao,
        query: String,
        errorCode: Int = 1001,
        methodName: String = "",
        notifyAll: Boolean = false
    ): S {
        var status = executeStatus<E, S>(dao, QueryBuilder.BEGIN_TRANSACTION_QUERY)
        if (status.isOk) {
            status = executeStatus<E, S>(dao, query, errorCode, methodName)
        }
        val endStatus = executeStatus<E, S>(
            dao,
            QueryBuilder.END_TRANSACTION_QUERY,
            DEFAULT_ERRORCODE,
            methodName,
            notifyAll
        )
        if (!endStatus.isOk) {
            status = endStatus
        }
        return status
    }

    inline fun <reified E : Any> createErrorStatus(
        ex: Exception,
        codeType: Int,
        method: String
    ): StatusSelectTable<E> {
        val error = Error()
        error.code = codeType
        error.description = ex.message ?: "$method HyperHive Error with $ex"
        return StatusSelectTable<E>().apply {
            errors.add(error)
        }
    }
}