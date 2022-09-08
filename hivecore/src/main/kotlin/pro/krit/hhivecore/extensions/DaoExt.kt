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

package pro.krit.hhivecore.extensions

import com.mobrun.plugin.api.request_assistant.CustomParameter
import com.mobrun.plugin.api.request_assistant.RequestBuilder
import com.mobrun.plugin.api.request_assistant.ScalarParameter
import com.mobrun.plugin.models.BaseStatus
import com.mobrun.plugin.models.StatusSelectTable
import io.reactivex.rxjava3.subjects.Subject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import pro.krit.hhivecore.base.IDao
import pro.krit.hhivecore.common.FieldsBuilder
import pro.krit.hhivecore.common.LimitedScalarParameter
import pro.krit.hhivecore.common.QueryBuilder
import pro.krit.hhivecore.common.QueryExecuter
import java.util.*

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

fun IDao.getFlowTrigger(): Flow<String> {
    return this.fmpDatabase.getFlowTrigger(this)
}

fun IDao.getRxTrigger(): Subject<String> {
    return this.fmpDatabase.getRxTrigger(this)
}

/**
 * Инициализация полей таблицы из модели данных
 */
inline fun <reified E : Any> IDao.initFields() {
    FieldsBuilder.initFields(this, E::class.java.fields)
}

/**
 * сбрасываем триггер для обновления данных в таблицах
 */
fun IDao.dropTrigger() {
    when(val trigger = this.getFlowTrigger()) {
        is MutableSharedFlow -> trigger.tryEmit("")
        is MutableStateFlow -> trigger.tryEmit("")
    }
}

/**
 * Создает Flow, который эмитит количество данных в таблице
 *
 * @param where - тело запроса для count(*), если пустой то выбирает все данные
 * @param withStart - начать эмитить данные при создании потока
 * @param emitDelay - задержка при эмитинге данных
 * @param withDistinct - использовать эмитинг только уникальных данных
 * @param byField - поиск количества по не пустому полю
 *
 * @return - Flow<Int> поток данных из базы данных
 */
fun IDao.flowableCount(
    where: String = "",
    withStart: Boolean = true,
    emitDelay: Long = 0L,
    withDistinct: Boolean = false,
    byField: String? = null
): Flow<Int> {
    val trigger = this.getFlowTrigger()
    return flow {
        trigger.collect {
            if(emitDelay > 0) {
                delay(emitDelay)
            }
            val result = count(where, byField)
            emit(result)
            /*if(withStart) {
                dropTrigger()
            }*/
        }
    }.onStart {
        if(withStart && trigger.firstOrNull().isNullOrEmpty()) {
            this@flowableCount.triggerFlow()
        }
    }.apply {
        if (withDistinct) {
            distinctUntilChanged()
        }
    }
}

/**
 * Возвращает количество данных в таблице
 *
 * @param where - тело запроса для count(*), если пустой то выбирает все данные
 * @param byField - поиск количества по не пустому полю
 *
 * @return Int
 */
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
    val trigger = this.getFlowTrigger()
    val triggerValue = UUID.randomUUID().toString()
    when(trigger) {
        is MutableSharedFlow -> trigger.tryEmit(triggerValue)
        is MutableStateFlow -> trigger.tryEmit(triggerValue)
    }
}

fun IDao.triggerRx() {
    val trigger = this.getRxTrigger()
    val triggerValue = UUID.randomUUID().toString()
    trigger.onNext(triggerValue)
}

fun <E : Any> StatusSelectTable<E>.triggerDaoIfOk(dao: IDao) {
    val statusName = this.status.name.uppercase()
    if (statusName == "OK") {
        dao.triggerFlow()
        dao.triggerRx()
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
    params: ScalarMap? = null,
    requestData: List<CustomParameter>? = null
): RequestBuilder<CustomParameter, ScalarParameter<*>> {
    val hyperHive = fmpDatabase.provideHyperHive()
    val builder: RequestBuilder<CustomParameter, ScalarParameter<*>> =
        RequestBuilder(hyperHive, resourceName, isDelta)
    if (params?.isNotEmpty() == true) {
        params.forEach {
            builder.addScalar(LimitedScalarParameter(name = it.key, value = it.value))
        }
    }
    if(!requestData.isNullOrEmpty()) {
        builder.addTableItems(requestData)
    }
    return builder
}