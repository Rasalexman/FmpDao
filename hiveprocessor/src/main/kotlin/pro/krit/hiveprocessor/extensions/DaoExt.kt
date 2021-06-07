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

import com.mobrun.plugin.api.request_assistant.CustomParameter
import com.mobrun.plugin.api.request_assistant.RequestBuilder
import com.mobrun.plugin.api.request_assistant.ScalarParameter
import com.mobrun.plugin.models.BaseStatus
import com.mobrun.plugin.models.StatusSelectTable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import pro.krit.hiveprocessor.base.IDao
import pro.krit.hiveprocessor.base.IDao.*
import pro.krit.hiveprocessor.common.FieldsBuilder
import pro.krit.hiveprocessor.common.LimitedScalarParameter
import pro.krit.hiveprocessor.common.QueryBuilder
import pro.krit.hiveprocessor.common.QueryExecuter

typealias Parameter = String
typealias Value = Any
typealias ScalarMap = Map<Parameter, Value>

val IDao.fullTableName: String
    get() = "\'${resourceName}_${tableName}\'"

const val ERROR_CODE_SELECT_WHERE = 10001
const val ERROR_CODE_REMOVE_WHERE = 10002
const val ERROR_CODE_COUNT_WHERE = 10003

inline fun <reified E : Any> IDao.flowable(
    where: String = "",
    withStart: Boolean = true,
    withDistinct: Boolean = false
) = flow {
    val trigger = this@flowable
    this@flowable.fmpDatabase.getTrigger(trigger).collect {
        kotlinx.coroutines.delay(100L)
        val result = selectAsync<E>(where)
        emit(result)
    }
}.onStart {
    if (withStart) {
        val result = selectAsync<E>(where)
        emit(result)
    }
}.apply {
    if (withDistinct) {
        distinctUntilChanged()
    }
}

inline fun <reified E : Any> IDao.select(
    where: String = "",
    limit: Int = 0
): List<E> {
    val selectQuery = QueryBuilder.createQuery(this, QueryBuilder.SELECT_QUERY, where, limit)
    return QueryExecuter.executeQuery(
        dao = this,
        query = selectQuery,
        errorCode = ERROR_CODE_SELECT_WHERE,
        methodName = "selectWhere"
    )
}

suspend inline fun <reified E : Any> IDao.selectAsync(
    where: String = "",
    limit: Int = 0
): List<E> {
    return withContext(Dispatchers.IO) { select(where, limit) }
}

inline fun <reified E : Any> IDao.count(
    where: String = ""
): List<E> {
    val selectQuery = QueryBuilder.createQuery(this, QueryBuilder.COUNT_QUERY, where)
    return QueryExecuter.executeQuery(
        dao = this,
        query = selectQuery,
        errorCode = ERROR_CODE_COUNT_WHERE,
        methodName = "countWhere"
    )
}

suspend inline fun <reified E : Any> IDao.countAsync(
    where: String = ""
): List<E> {
    return withContext(Dispatchers.IO) { count(where) }
}

inline fun <reified E : Any> IDao.delete(
    where: String = "",
    notifyAll: Boolean = true
): StatusSelectTable<E> {
    val deleteQuery = QueryBuilder.createQuery(this, QueryBuilder.DELETE_QUERY, where)
    return QueryExecuter.executeStatus(
        dao = this,
        query = deleteQuery,
        errorCode = ERROR_CODE_REMOVE_WHERE,
        methodName = "delete",
        notifyAll = notifyAll
    )
}

suspend inline fun <reified E : Any> IDao.deleteAsync(
    where: String = "",
    notifyAll: Boolean = true
): StatusSelectTable<E> {
    return withContext(Dispatchers.IO) { delete(where, notifyAll) }
}

const val ERROR_CODE_CREATE = 10004
const val ERROR_CODE_INSERT = 10005
const val ERROR_CODE_DELETE = 10006

inline fun <reified E : Any> IFieldsDao.initFields() {
    FieldsBuilder.initFields(this, E::class.java.fields)
}

suspend inline fun <reified E : Any> IFieldsDao.initFieldsAsync() {
    withContext(Dispatchers.IO) {
        FieldsBuilder.initFields(
            this@initFieldsAsync,
            E::class.java.fields
        )
    }
}

inline fun <reified E : Any> IFieldsDao.createTable(): StatusSelectTable<E> {
    initFields<E>()
    val query = QueryBuilder.createTableQuery(this)
    return QueryExecuter.executeStatus(
        dao = this,
        query = query,
        errorCode = ERROR_CODE_CREATE,
        methodName = "createTable"
    )
}

suspend inline fun <reified E : Any> IFieldsDao.createTableAsync(): StatusSelectTable<E> {
    return withContext(Dispatchers.IO) {
        initFieldsAsync<E>()
        createTable()
    }
}

inline fun <reified E : Any> IFieldsDao.insertOrReplace(
    item: E,
    notifyAll: Boolean = false
): StatusSelectTable<E> {
    val query = QueryBuilder.createInsertOrReplaceQuery(this, item)
    return QueryExecuter.executeStatus(
        dao = this,
        query = query,
        errorCode = ERROR_CODE_INSERT,
        methodName = "insertOrReplace",
        notifyAll
    )
}

suspend inline fun <reified E : Any> IFieldsDao.insertOrReplaceAsync(
    item: E,
    notifyAll: Boolean = false
): StatusSelectTable<E> {
    return withContext(Dispatchers.IO) { insertOrReplace(item, notifyAll) }
}

inline fun <reified E : Any> IFieldsDao.insertOrReplace(
    items: List<E>,
    notifyAll: Boolean = false
): StatusSelectTable<E> {
    val query = QueryBuilder.createInsertOrReplaceQuery(this, items)
    return QueryExecuter.executeTransactionStatus(
        dao = this,
        query = query,
        errorCode = ERROR_CODE_INSERT,
        methodName = "insertOrReplaceList",
        notifyAll = notifyAll
    )
}

suspend inline fun <reified E : Any> IFieldsDao.insertOrReplaceAsync(
    items: List<E>,
    notifyAll: Boolean = false
): StatusSelectTable<E> {
    return withContext(Dispatchers.IO) { insertOrReplace(items, notifyAll) }
}

inline fun <reified E : Any> IFieldsDao.delete(
    item: E,
    notifyAll: Boolean = true
): StatusSelectTable<E> {
    val query = QueryBuilder.createDeleteQuery(this, item)
    return QueryExecuter.executeStatus(
        dao = this,
        query = query,
        errorCode = ERROR_CODE_DELETE,
        methodName = "delete",
        notifyAll = notifyAll
    )
}

suspend inline fun <reified E : Any> IFieldsDao.deleteAsync(
    item: E,
    notifyAll: Boolean = true
): StatusSelectTable<E> {
    return withContext(Dispatchers.IO) { delete(item, notifyAll) }
}

inline fun <reified E : Any> IFieldsDao.delete(
    items: List<E>,
    notifyAll: Boolean = true
): StatusSelectTable<E> {
    val query = QueryBuilder.createDeleteQuery(this, items)
    return QueryExecuter.executeTransactionStatus(
        dao = this,
        query = query,
        errorCode = ERROR_CODE_DELETE,
        methodName = "deleteList",
        notifyAll = notifyAll
    )
}

suspend inline fun <reified E : Any> IFieldsDao.deleteAsync(
    items: List<E>,
    notifyAll: Boolean = true
): StatusSelectTable<E> {
    return withContext(Dispatchers.IO) { delete(items, notifyAll) }
}

////------ TRIGGERS

fun IDao.triggerFlow() {
    (fmpDatabase.getTrigger(this) as MutableSharedFlow<String>).tryEmit(fullTableName)
}

fun <E : Any> StatusSelectTable<E>.triggerFlow(dao: IDao): StatusSelectTable<E> {
    if (this.status.name == "OK") {
        dao.triggerFlow()
    }
    return this
}


///------Update Section

fun IDao.request(
    params: ScalarMap? = null
): BaseStatus {
    val request = requestBuilder(params)
    return request.streamCallAuto()?.execute() ?: BaseStatus()
}

suspend fun IDao.requestAsync(
    params: ScalarMap? = null
): BaseStatus {
    return withContext(Dispatchers.IO) { request(params) }
}

fun IDao.requestBuilder(
    params: ScalarMap? = null
): RequestBuilder<CustomParameter, ScalarParameter<*>> {
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