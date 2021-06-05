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

package pro.krit.hiveprocessor.extensions

import com.mobrun.plugin.api.Call
import com.mobrun.plugin.api.request_assistant.CustomParameter
import com.mobrun.plugin.api.request_assistant.RequestBuilder
import com.mobrun.plugin.api.request_assistant.ScalarParameter
import com.mobrun.plugin.models.BaseStatus
import com.mobrun.plugin.models.StatusSelectTable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import pro.krit.hiveprocessor.base.IFmpDao
import pro.krit.hiveprocessor.base.IFmpLocalDao
import pro.krit.hiveprocessor.common.LimitedScalarParameter
import pro.krit.hiveprocessor.common.QueryBuilder
import pro.krit.hiveprocessor.common.QueryExecuter.executeQuery
import pro.krit.hiveprocessor.common.QueryExecuter.executeStatus

typealias Parameter = String
typealias Value = Any
typealias ScalarMap = Map<Parameter, Value>

val <E : Any, S : StatusSelectTable<E>> IFmpDao<E, S>.fullTableName: String
    get() = "\'${resourceName}_${tableName}\'"

const val ERROR_CODE_SELECT_WHERE = 10001
const val ERROR_CODE_REMOVE_WHERE = 10002
const val ERROR_CODE_COUNT_WHERE = 10003

inline fun <reified E : Any, reified S : StatusSelectTable<E>> IFmpDao<E, S>.flowable(
    where: String = "",
    withStart: Boolean = true,
    withDistinct: Boolean = false
) = flow {
    val trigger = this@flowable
    this@flowable.fmpDatabase.getTrigger(trigger).collect {
        kotlinx.coroutines.delay(100L)
        val result = selectAsync(where)
        emit(result)
    }
}.onStart {
    if (withStart) {
        val result = selectAsync(where)
        emit(result)
    }
}.apply {
    if (withDistinct) {
        distinctUntilChanged()
    }
}

inline fun <reified E : Any, reified S : StatusSelectTable<E>> IFmpDao<E, S>.select(
    where: String = "",
    limit: Int = 0
): List<E> {
    val limitQuery = limit.takeIf { it > 0 }?.run { " ${QueryBuilder.LIMIT} $limit" }.orEmpty()
    val selectQuery = if(where.isNotEmpty()) {
        "${QueryBuilder.SELECT_QUERY} $fullTableName ${QueryBuilder.WHERE} $where$limitQuery"
    } else {
        "${QueryBuilder.SELECT_QUERY} $fullTableName$limitQuery"
    }

    return executeQuery(
        dao = this,
        query = selectQuery,
        errorCode = ERROR_CODE_SELECT_WHERE,
        methodName = "selectWhere"
    )
}

suspend inline fun <reified E : Any, reified S : StatusSelectTable<E>> IFmpDao<E, S>.selectAsync(
    where: String = "",
    limit: Int = 0
): List<E> {
    return withContext(Dispatchers.IO) { select(where, limit) }
}

inline fun <reified E : Any, reified S : StatusSelectTable<E>> IFmpDao<E, S>.count(
    where: String = ""
): List<E> {
    val selectQuery = if(where.isNotEmpty()) {
        "${QueryBuilder.COUNT_QUERY} $fullTableName ${QueryBuilder.WHERE} $where"
    } else {
        "${QueryBuilder.COUNT_QUERY} $fullTableName"
    }

    return executeQuery(
        dao = this,
        query = selectQuery,
        errorCode = ERROR_CODE_COUNT_WHERE,
        methodName = "countWhere"
    )
}

suspend inline fun <reified E : Any, reified S : StatusSelectTable<E>> IFmpDao<E, S>.countAsync(
    where: String = ""
): List<E> {
    return withContext(Dispatchers.IO) { count(where) }
}

inline fun <reified E : Any, reified S : StatusSelectTable<E>> IFmpDao<E, S>.delete(
    where: String = "",
    notifyAll: Boolean = true
): StatusSelectTable<E> {
    val deleteQuery = if(where.isNotEmpty()) {
        "${QueryBuilder.DELETE_QUERY} $fullTableName ${QueryBuilder.WHERE} $where"
    } else {
        "${QueryBuilder.DELETE_QUERY} $fullTableName"
    }

    return executeStatus(
        dao = this,
        query = deleteQuery,
        errorCode = ERROR_CODE_REMOVE_WHERE,
        methodName = "delete",
        notifyAll = notifyAll
    )
}

suspend inline fun <reified E : Any, reified S : StatusSelectTable<E>> IFmpDao<E, S>.deleteAsync(
    where: String = "",
    notifyAll: Boolean = true
): StatusSelectTable<E> {
    return withContext(Dispatchers.IO) { delete(where, notifyAll) }
}

fun <E : Any, S : StatusSelectTable<E>> IFmpDao<E, S>.triggerFlow() {
    (fmpDatabase.getTrigger(this) as MutableSharedFlow<String>).tryEmit(fullTableName)
}

fun <E : Any, S : StatusSelectTable<E>> S.triggerFlow(dao: IFmpDao<E, S>): S {
    if (this.status.name == "OK") {
        dao.triggerFlow()
    }
    return this
}

///------Update Section

inline fun <reified E : Any, reified S : StatusSelectTable<E>> IFmpDao<E, S>.request(
    params: ScalarMap? = null
): BaseStatus {
    if (this is IFmpLocalDao) return LocalUpdateStatus()
    val request = requestBuilder(params)
    return request.streamCallAuto()?.execute() ?: BaseStatus()
}

suspend inline fun <reified E : Any, reified S : StatusSelectTable<E>> IFmpDao<E, S>.requestAsync(
    params: ScalarMap? = null
): BaseStatus {
    return withContext(Dispatchers.IO) { request(params) }
}

inline fun <reified E : Any, reified S : StatusSelectTable<E>> IFmpDao<E, S>.requestBuilder(
    params: ScalarMap? = null
): RequestBuilder<CustomParameter, ScalarParameter<*>> {
    if (this is IFmpLocalDao) return LocalRequestBuilder()

    val hyperHive = fmpDatabase.provideHyperHive()
    val builder: RequestBuilder<CustomParameter, ScalarParameter<*>> =
        RequestBuilder(hyperHive, resourceName, isDelta)
    if (params?.isNotEmpty() == true) {
        params.forEach {
            builder.addScalar(LimitedScalarParameter(name = it.key, value = it.value))
        }
    }
    return builder
}

class LocalUpdateStatus : BaseStatus() {
    override fun isOk(): Boolean {
        return true
    }
}

class LocalRequestBuilder : RequestBuilder<CustomParameter, ScalarParameter<*>>(
    null, "", false
) {
    override fun streamCallAuto(): Call<BaseStatus>? {
        return null
    }

    override fun streamCallDelta(): Call<BaseStatus>? {
        return null
    }

    override fun streamCallTable(): Call<BaseStatus>? {
        return null
    }
}