package pro.krit.hiveprocessor.common

import com.mobrun.plugin.models.Error
import com.mobrun.plugin.models.StatusSelectTable
import pro.krit.hiveprocessor.base.IFmpDao

object QueryExecuter {

    inline fun <reified E : Any, reified S : StatusSelectTable<E>> executeQuery(
        dao: IFmpDao<E, S>,
        query: String,
        errorCode: Int = 1001,
        methodName: String = ""
    ): List<E> {
        val result = try {
            executeStatus(dao, query, errorCode, methodName).result.database.records
        } catch (e: Exception) {
            null
        }
        return result.orEmpty()
    }

    inline fun <reified E : Any, reified S : StatusSelectTable<E>> executeStatus(
        dao: IFmpDao<E, S>,
        query: String,
        errorCode: Int = 1001,
        methodName: String = ""
    ): S {
        return try {
            dao.hyperHiveDatabase.provideHyperHive().databaseAPI.query(query, S::class.java).execute()!!
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
}