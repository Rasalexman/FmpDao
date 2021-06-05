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

const val ERROR_CODE_SELECT_ALL = 10001
const val ERROR_CODE_SELECT_WHERE = 10002
const val ERROR_CODE_REMOVE_WHERE = 10003

inline fun <reified E : Any, reified S : StatusSelectTable<E>> IFmpDao<E, S>.flowable(
    withStart: Boolean = true,
    withDistinct: Boolean = false
) = flow<List<E>> {
    this@flowable.hyperHiveDatabase.getTrigger(this@flowable).collect {
        emit(selectAllAsync())
    }
}.onStart {
    if (withStart) {
        emit(selectAllAsync())
    }
}.apply {
    if (withDistinct) {
        distinctUntilChanged()
    }
}

inline fun <reified E : Any, reified S : StatusSelectTable<E>> IFmpDao<E, S>.flowableWhere(
    expression: String = "",
    withStart: Boolean = true,
    withDistinct: Boolean = false
) = flow<List<E>> {
    this@flowableWhere.hyperHiveDatabase.getTrigger(this@flowableWhere).collect {
        emit(selectWhereAsync(expression))
    }
}.onStart {
    if (withStart) {
        emit(selectWhereAsync(expression))
    }
}.apply {
    if (withDistinct) {
        distinctUntilChanged()
    }
}

inline fun <reified E : Any, reified S : StatusSelectTable<E>> IFmpDao<E, S>.selectAll(limit: Long = 0): List<E> {
    val limitQuery = limit.takeIf { it > 0 }?.run { " ${QueryBuilder.LIMIT} $limit" }.orEmpty()
    val selectAllQuery = "${QueryBuilder.SELECT_QUERY} $fullTableName$limitQuery"
    return executeQuery(
        dao = this,
        query = selectAllQuery,
        errorCode = ERROR_CODE_SELECT_ALL,
        methodName = "selectAll"
    )
}

suspend inline fun <reified E : Any, reified S : StatusSelectTable<E>> IFmpDao<E, S>.selectAllAsync(
    limit: Long = 0
): List<E> {
    return selectAll(limit)
}

inline fun <reified E : Any, reified S : StatusSelectTable<E>> IFmpDao<E, S>.selectWhere(expression: String): List<E> {
    val selectAllQuery = "${QueryBuilder.SELECT_QUERY} $fullTableName ${QueryBuilder.WHERE} $expression"
    return executeQuery(
        dao = this,
        query = selectAllQuery,
        errorCode = ERROR_CODE_SELECT_WHERE,
        methodName = "selectWhere"
    )
}

suspend inline fun <reified E : Any, reified S : StatusSelectTable<E>> IFmpDao<E, S>.selectWhereAsync(
    expression: String
): List<E> {
    return withContext(Dispatchers.IO) { selectWhere(expression) }
}

inline fun <reified E : Any, reified S : StatusSelectTable<E>> IFmpDao<E, S>.deleteWhere(
    expression: String,
    notifyAll: Boolean = true
): StatusSelectTable<E> {
    val deleteQuery = "${QueryBuilder.DELETE_QUERY} $fullTableName ${QueryBuilder.WHERE} $expression"
    return executeStatus(
        dao = this,
        query = deleteQuery,
        errorCode = ERROR_CODE_REMOVE_WHERE,
        methodName = "removeWhere",
        notifyAll = notifyAll
    )
}

suspend inline fun <reified E : Any, reified S : StatusSelectTable<E>> IFmpDao<E, S>.deleteWhereAsync(
    expression: String,
    notifyAll: Boolean = true
): StatusSelectTable<E> {
    return withContext(Dispatchers.IO) { deleteWhere(expression, notifyAll) }
}

inline fun <reified E : Any, reified S : StatusSelectTable<E>> IFmpDao<E, S>.deleteAll(
    notifyAll: Boolean = true
): StatusSelectTable<E> {
    val deleteAllQuery = "${QueryBuilder.DELETE_QUERY} $fullTableName"
    return executeStatus(
        dao = this,
        query = deleteAllQuery,
        errorCode = ERROR_CODE_REMOVE_WHERE,
        methodName = "removeWhere",
        notifyAll = notifyAll
    )
}

suspend inline fun <reified E : Any, reified S : StatusSelectTable<E>> IFmpDao<E, S>.deleteAllAsync(
    notifyAll: Boolean = true
): StatusSelectTable<E> {
    return withContext(Dispatchers.IO) { deleteAll(notifyAll) }
}

fun <E : Any, S : StatusSelectTable<E>> IFmpDao<E, S>.triggerFlow() {
    (hyperHiveDatabase.getTrigger(this) as MutableSharedFlow<String>).tryEmit(fullTableName)
}

fun <E : Any, S : StatusSelectTable<E>> S.triggerFlow(dao: IFmpDao<E, S>): S {
    if (this.status.name == "OK") {
        dao.triggerFlow()
    }
    return this
}

///------Update Section

inline fun <reified E : Any, reified S : StatusSelectTable<E>> IFmpDao<E, S>.request(
    params: ScalarMap? = null,
    resourceName: String? = null
): BaseStatus {
    if (this is IFmpLocalDao) return LocalUpdateStatus()

    val localResourceName = resourceName ?: resourceName
    val request = newRequest(params, localResourceName)
    return request.streamCallAuto()?.execute() ?: BaseStatus()
}

suspend inline fun <reified E : Any, reified S : StatusSelectTable<E>> IFmpDao<E, S>.requestAsync(
    params: ScalarMap? = null,
    resourceName: String? = null
): BaseStatus {
    return withContext(Dispatchers.IO) { request(params, resourceName) }
}

inline fun <reified E : Any, reified S : StatusSelectTable<E>> IFmpDao<E, S>.newRequest(
    params: ScalarMap? = null,
    resourceName: String? = null
): RequestBuilder<CustomParameter, ScalarParameter<*>> {
    if (this is IFmpLocalDao) return LocalRequestBuilder()

    val hyperHive = hyperHiveDatabase.provideHyperHive()
    val localResourceName = resourceName ?: resourceName
    val builder: RequestBuilder<CustomParameter, ScalarParameter<*>> =
        RequestBuilder(hyperHive, localResourceName, isCached)
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