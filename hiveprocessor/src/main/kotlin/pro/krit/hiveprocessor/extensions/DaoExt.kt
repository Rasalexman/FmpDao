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
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import pro.krit.hiveprocessor.base.IDao
import pro.krit.hiveprocessor.base.IDao.*
import pro.krit.hiveprocessor.common.LimitedScalarParameter
import pro.krit.hiveprocessor.common.QueryBuilder
import pro.krit.hiveprocessor.common.QueryExecuter

typealias Parameter = String
typealias Value = Any
typealias ScalarMap = Map<Parameter, Value>

val IDao.fullTableName: String
    get() {
        return buildString {
            append(resourceName)
            if(tableName.isNotEmpty()) {
                append("_")
                append(tableName)
            }
        }
    }

const val ERROR_CODE_SELECT_WHERE = 10001
const val ERROR_CODE_REMOVE_WHERE = 10002
const val ERROR_CODE_COUNT_WHERE = 10003
const val ERROR_CODE_CREATE = 10004
const val ERROR_CODE_INSERT = 10005
const val ERROR_CODE_DELETE = 10006
const val ERROR_CODE_QUERY = 10007
const val ERROR_CODE_UPDATE = 10008

fun IDao.getTrigger(): Flow<String> {
    return this.fmpDatabase.getTrigger(this)
}

suspend fun IDao.isTriggerEmpty(): Boolean {
    return getTrigger().firstOrNull().isNullOrEmpty()
}

fun IDao.flowableCount(
    where: String = "",
    withStart: Boolean = true,
    emitDelay: Long = 100L,
    withDistinct: Boolean = false,
    byField: String? = null
) = flow {
    this@flowableCount.getTrigger().collect {
        if(emitDelay > 0) {
            delay(emitDelay)
        }
        val result = count(where, byField)
        emit(result)
    }
}.onStart {
    val trigger = this@flowableCount
    if (withStart && trigger.isTriggerEmpty()) {
        val result = count(where, byField)
        emit(result)
    }
}.apply {
    if (withDistinct) {
        distinctUntilChanged()
    }
}

/// Counts Queries
fun IDao.count(
    where: String = "",
    byField: String? = null
): Int {
    val localFields = byField?.let { listOf(it) }
    val selectQuery = QueryBuilder.createQuery(
        dao = this,
        prefix = QueryBuilder.COUNT_QUERY,
        where = where,
        fields = localFields
    )
    val result = QueryExecuter.executeKeyQuery(
        dao = this,
        key = QueryBuilder.COUNT_KEY,
        query = selectQuery,
        errorCode = ERROR_CODE_COUNT_WHERE,
        methodName = "countWhere",
        fields = localFields
    )
    return result.toIntOrNull() ?: 0
}

////------ TRIGGERS
fun IDao.triggerFlow() {
    (this.getTrigger() as MutableSharedFlow<String>).tryEmit(fullTableName)
}

fun <E : Any> StatusSelectTable<E>.triggerFlow(dao: IDao) {
    val statusName = this.status.name.uppercase()
    if (statusName == "OK") {
        dao.triggerFlow()
    }
}


///------Update Section
fun IDao.request(
    params: ScalarMap? = null
): BaseStatus {
    val request = requestBuilder(params)
    return request.streamCallAuto()?.execute() ?: BaseStatus()
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