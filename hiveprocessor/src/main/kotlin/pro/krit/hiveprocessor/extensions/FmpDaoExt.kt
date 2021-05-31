package pro.krit.hiveprocessor.extensions

import com.mobrun.plugin.api.HyperHive
import com.mobrun.plugin.api.request_assistant.CustomParameter
import com.mobrun.plugin.api.request_assistant.RequestBuilder
import com.mobrun.plugin.api.request_assistant.ScalarParameter
import com.mobrun.plugin.models.BaseStatus
import com.mobrun.plugin.models.Error
import com.mobrun.plugin.models.StatusSelectTable
import pro.krit.hiveprocessor.base.IFmpDao
import pro.krit.hiveprocessor.common.LimitedScalarParameter

val <E : Any, S : StatusSelectTable<E>> IFmpDao<E, S>.tableName: String
    get() = nameResource + "_" + nameParameter

val <E : Any, S : StatusSelectTable<E>> IFmpDao<E, S>.selectAllQuery: String
    get() = "SELECT * FROM $tableName"

val <E : Any, S : StatusSelectTable<E>> IFmpDao<E, S>.deleteQuery: String
    get() = "DELETE FROM $tableName"

val <E : Any, S : StatusSelectTable<E>> IFmpDao<E, S>.hyperHive: HyperHive
    get() = hyperHiveDatabase.provideHyperHive()

inline fun <reified E : Any, reified S : StatusSelectTable<E>> IFmpDao<E, S>.selectAll(): List<E> {
    return executeQuery(selectAllQuery)
}

suspend inline fun <reified E : Any, reified S : StatusSelectTable<E>> IFmpDao<E, S>.selectAllAsync(): List<E> {
    return selectAll()
}

inline fun <reified E : Any, reified S : StatusSelectTable<E>> IFmpDao<E, S>.update(params: Map<String, Any>?, resourceName: String?): BaseStatus {
    val localResourceName = resourceName ?: nameResource
    val request = newRequest(params, localResourceName)
    return request.streamCallAuto().execute()
}

suspend inline fun <reified E : Any, reified S : StatusSelectTable<E>> IFmpDao<E, S>.updateAsync(params: Map<String, Any>?, resourceName: String?): BaseStatus {
    return update(params, resourceName)
}

inline fun <reified E : Any, reified S : StatusSelectTable<E>> IFmpDao<E, S>.newRequest(params: Map<String, Any>? = null, resourceName: String): RequestBuilder<CustomParameter, ScalarParameter<*>> {
    val builder: RequestBuilder<CustomParameter, ScalarParameter<*>> = RequestBuilder(hyperHive, resourceName, isCached)
    if(params?.isNotEmpty() == true) {
        params.forEach {
            builder.addScalar(LimitedScalarParameter(name = it.key, value = it.value))
        }
    }
    return builder
}

inline fun <reified E : Any, reified S : StatusSelectTable<E>> IFmpDao<E, S>.getWhere(expression: String): List<E> {
    return executeQuery("$selectAllQuery WHERE $expression")
}

inline fun <reified E : Any, reified S : StatusSelectTable<E>> IFmpDao<E, S>.executeQuery(
    query: String,
    errorCode: Int = 1001,
    methodName: String = ""
): List<E> {
    val result = try {
        executeStatus(query, errorCode, methodName).result.database.records
    } catch (e: Exception) {
        null
    }
    return result.orEmpty()
}

inline fun <reified E : Any, reified S : StatusSelectTable<E>> IFmpDao<E, S>.executeStatus(
    query: String,
    errorCode: Int = 1001,
    methodName: String = ""
): S {
    return try {
        hyperHive.databaseAPI.query(query, S::class.java).execute()!!
    } catch (e: Exception) {
        createErrorStatus<E>(
            ex = e,
            codeType = errorCode,
            method = methodName
        ) as S
    }
}

inline fun <reified E : Any> createErrorStatus(ex: Exception, codeType: Int, method: String): StatusSelectTable<E> {
    val error = Error()
    error.code = codeType
    error.description = ex.message ?: "$method HyperHive Error with $ex"
    return StatusSelectTable<E>().apply {
        errors.add(error)
    }
}

suspend inline fun <reified E : Any, reified S : StatusSelectTable<E>> IFmpDao<E, S>.getWhereAsync(expression: String): List<E> {
    return getWhere(expression)
}

inline fun <reified E : Any, reified S : StatusSelectTable<E>> IFmpDao<E, S>.removeWhere(expression: String): S {
    return hyperHive.databaseAPI.query("$deleteQuery WHERE $expression", S::class.java).execute()
}

suspend inline fun <reified E : Any, reified S : StatusSelectTable<E>> IFmpDao<E, S>.removeWhereAsync(expression: String): S {
    return removeWhere(expression)
}
