package pro.krit.hiveprocessor.extensions

import com.mobrun.plugin.api.request_assistant.CustomParameter
import com.mobrun.plugin.api.request_assistant.RequestBuilder
import com.mobrun.plugin.api.request_assistant.ScalarParameter
import com.mobrun.plugin.models.BaseStatus
import com.mobrun.plugin.models.StatusSelectTable
import pro.krit.hiveprocessor.base.IFmpDao
import pro.krit.hiveprocessor.common.LimitedScalarParameter
import pro.krit.hiveprocessor.common.QueryExecuter.executeQuery
import pro.krit.hiveprocessor.common.QueryExecuter.executeStatus

typealias Parameter = String
typealias Value = Any
typealias ScalarMap = Map<Parameter, Value>

val <E : Any, S : StatusSelectTable<E>> IFmpDao<E, S>.tableName: String
    get() = nameResource + "_" + nameParameter

const val ERROR_CODE_SELECT_ALL = 10001
const val ERROR_CODE_SELECT_WHERE = 10002
const val ERROR_CODE_REMOVE_WHERE = 10003

inline fun <reified E : Any, reified S : StatusSelectTable<E>> IFmpDao<E, S>.selectAll(): List<E> {
    val selectAllQuery = "SELECT * FROM $tableName"
    return executeQuery(
        dao = this,
        query = selectAllQuery,
        errorCode = ERROR_CODE_SELECT_ALL,
        methodName = "selectAll"
    )
}

suspend inline fun <reified E : Any, reified S : StatusSelectTable<E>> IFmpDao<E, S>.selectAllAsync(): List<E> {
    return selectAll()
}

inline fun <reified E : Any, reified S : StatusSelectTable<E>> IFmpDao<E, S>.selectWhere(expression: String): List<E> {
    val selectAllQuery = "SELECT * FROM $tableName WHERE $expression"
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
    return selectWhere(expression)
}

inline fun <reified E : Any, reified S : StatusSelectTable<E>> IFmpDao<E, S>.removeWhere(expression: String): S {
    val deleteQuery = "DELETE FROM $tableName WHERE $expression"
    return executeStatus(
        dao = this,
        query = deleteQuery,
        errorCode = ERROR_CODE_REMOVE_WHERE,
        methodName = "removeWhere"
    )
}

suspend inline fun <reified E : Any, reified S : StatusSelectTable<E>> IFmpDao<E, S>.removeWhereAsync(
    expression: String
): S {
    return removeWhere(expression)
}

///------Update Section

inline fun <reified E : Any, reified S : StatusSelectTable<E>> IFmpDao<E, S>.update(
    params: ScalarMap? = null,
    resourceName: String? = null
): BaseStatus {
    val localResourceName = resourceName ?: nameResource
    val request = newRequest(params, localResourceName)
    return request.streamCallAuto().execute()
}

suspend inline fun <reified E : Any, reified S : StatusSelectTable<E>> IFmpDao<E, S>.updateAsync(
    params: ScalarMap? = null,
    resourceName: String? = null
): BaseStatus {
    return update(params, resourceName)
}

inline fun <reified E : Any, reified S : StatusSelectTable<E>> IFmpDao<E, S>.newRequest(
    params: ScalarMap? = null,
    resourceName: String
): RequestBuilder<CustomParameter, ScalarParameter<*>> {
    val builder: RequestBuilder<CustomParameter, ScalarParameter<*>> =
        RequestBuilder(hyperHiveDatabase.provideHyperHive(), resourceName, isCached)
    if (params?.isNotEmpty() == true) {
        params.forEach {
            builder.addScalar(LimitedScalarParameter(name = it.key, value = it.value))
        }
    }
    return builder
}