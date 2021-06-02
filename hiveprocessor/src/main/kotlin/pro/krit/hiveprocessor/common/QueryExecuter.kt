package pro.krit.hiveprocessor.common

import com.mobrun.plugin.models.Error
import com.mobrun.plugin.models.StatusSelectTable
import pro.krit.hiveprocessor.base.IFmpDao
import pro.krit.hiveprocessor.base.IFmpLocalDao
import pro.krit.hiveprocessor.extensions.createTable
import pro.krit.hiveprocessor.extensions.tableName

object QueryExecuter {

    const val NO_SUCH_TABLE_ERROR = "no_such_table"

    inline fun <reified E : Any, reified S : StatusSelectTable<E>> executeQuery(
        dao: IFmpDao<E, S>,
        query: String,
        errorCode: Int = 1001,
        methodName: String = ""
    ): List<E> {
        return try {
            val status = executeStatus(dao, query, errorCode, methodName)
            status.result.database.records
        } catch (e: Exception) {
            e.printStackTrace()
            println("[ERROR]: ${dao.tableName} ERROR WITH QUERY $query")
            emptyList()
        }
    }

    inline fun <reified E : Any, reified S : StatusSelectTable<E>> checkStatusForTable(
        dao: IFmpLocalDao<E, S>,
        status: S
    ): S? {
        var localStatus: S? = null
        var tableIsNotCreated = status.checkForTableError()
        if (tableIsNotCreated) {
            val statusForCreateTable = dao.createTable()
            tableIsNotCreated = statusForCreateTable.checkForTableError()
            if (tableIsNotCreated) {
                localStatus = statusForCreateTable
            }
        }
        return localStatus
    }

    inline fun <reified E : Any> StatusSelectTable<E>.checkForTableError(): Boolean {
        val errors = this.errors.orEmpty()
        return if (errors.isNotEmpty()) {
            errors.any {
                it.descriptions.any { error ->
                    error.replace(" ", "_").contains(NO_SUCH_TABLE_ERROR)
                }
            }
        } else false
    }

    inline fun <reified E : Any, reified S : StatusSelectTable<E>> executeStatus(
        dao: IFmpDao<E, S>,
        query: String,
        errorCode: Int = 1001,
        methodName: String = ""
    ): S {
        return try {
            val hyperHiveDatabaseApi = dao.hyperHiveDatabase.databaseApi
            hyperHiveDatabaseApi.query(query, S::class.java).execute()!!
        } catch (e: Exception) {
            createErrorStatus<E>(
                ex = e,
                codeType = errorCode,
                method = methodName
            ) as S
        }
    }

    inline fun <reified E : Any, reified S : StatusSelectTable<E>> executeTransactionStatus(
        dao: IFmpDao<E, S>,
        query: String,
        errorCode: Int = 1001,
        methodName: String = ""
    ): S {
        var status = executeStatus(dao, QueryBuilder.BEGIN_TRANSACTION_QUERY)
        if(status.isOk) {
            status = executeStatus(dao, query, errorCode, methodName)
        }
        val endStatus = executeStatus(dao, QueryBuilder.END_TRANSACTION_QUERY)
        if(!endStatus.isOk) {
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