package pro.krit.hiveprocessor.extensions

import com.mobrun.plugin.models.StatusSelectTable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext
import pro.krit.hiveprocessor.base.IDao
import pro.krit.hiveprocessor.common.QueryBuilder
import pro.krit.hiveprocessor.common.QueryExecuter

object DaoInstance {

    inline fun <reified E : Any, reified S : StatusSelectTable<E>> flowable(
        dao: IDao,
        where: String = "",
        limit: Int = 0,
        withStart: Boolean = true,
        emitDelay: Long = 100L,
        withDistinct: Boolean = false
    ) = flow {
        dao.fmpDatabase.getTrigger(dao).collect {
            if(emitDelay > 0) {
                delay(emitDelay)
            }
            val result = selectAsync<E, S>(dao, where, limit)
            emit(result)
        }
    }.onStart {
        if (withStart) {
            if(emitDelay > 0) {
                delay(emitDelay)
            }
            val startResult = selectAsync<E, S>(dao, where, limit)
            emit(startResult)
        }
    }.apply {
        if (withDistinct) {
            distinctUntilChanged()
        }
    }

    inline fun <reified E : Any, reified S : StatusSelectTable<E>> select(
        dao: IDao,
        where: String = "",
        limit: Int = 0
    ): List<E> {
        val selectQuery = QueryBuilder.createQuery(dao, QueryBuilder.SELECT_QUERY, where, limit)
        return QueryExecuter.executeQuery<E, S>(
            dao = dao,
            query = selectQuery,
            errorCode = ERROR_CODE_SELECT_WHERE,
            methodName = "selectWhere"
        )
    }

    suspend inline fun <reified E : Any, reified S : StatusSelectTable<E>> selectAsync(
        dao: IDao,
        where: String = "",
        limit: Int = 0
    ): List<E> {
        return withContext(Dispatchers.IO) { select<E, S>(dao, where, limit) }
    }

    inline fun <reified E : Any, reified S : StatusSelectTable<E>> selectResult(
        dao: IDao,
        where: String = "",
        limit: Int = 0
    ): Result<List<E>> {
        val selectQuery = QueryBuilder.createQuery(dao, QueryBuilder.SELECT_QUERY, where, limit)
        return QueryExecuter.executeResultQuery<E, S>(
            dao = dao,
            query = selectQuery,
            errorCode = ERROR_CODE_SELECT_WHERE,
            methodName = "selectWhere"
        )
    }


    ///// Create tables
    inline fun <reified E : Any, reified S : StatusSelectTable<E>> createTable(
        dao: IDao.IFieldsDao
    ): S {
        dao.initFields<E>()
        val query = QueryBuilder.createTableQuery(dao)
        return QueryExecuter.executeStatus<E, S>(
            dao = dao,
            query = query,
            errorCode = ERROR_CODE_CREATE,
            methodName = "createTable"
        )
    }

    suspend inline fun <reified E : Any, reified S : StatusSelectTable<E>> createTableAsync(
        dao: IDao.IFieldsDao
    ): S {
        return withContext(Dispatchers.IO) {
            dao.initFieldsAsync<E>()
            createTable<E, S>(dao)
        }
    }

    /////---------- DELETE QUERIES
    inline fun <reified E : Any, reified S : StatusSelectTable<E>> delete(
        dao: IDao.IFieldsDao,
        where: String = "",
        notifyAll: Boolean = true
    ): S {
        val deleteQuery = QueryBuilder.createQuery(dao, QueryBuilder.DELETE_QUERY, where)
        return QueryExecuter.executeStatus<E, S>(
            dao = dao,
            query = deleteQuery,
            errorCode = ERROR_CODE_REMOVE_WHERE,
            methodName = "delete",
            notifyAll = notifyAll
        )
    }

    suspend inline fun <reified E : Any, reified S : StatusSelectTable<E>> deleteAsync(
        dao: IDao.IFieldsDao,
        where: String = "",
        notifyAll: Boolean = true
    ): S {
        return withContext(Dispatchers.IO) { delete<E, S>(dao, where, notifyAll) }
    }

    inline fun <reified E : Any, reified S : StatusSelectTable<E>> delete(
        dao: IDao.IFieldsDao,
        item: E,
        notifyAll: Boolean = false
    ): S {
        val query = QueryBuilder.createDeleteQuery(dao, item)
        return QueryExecuter.executeStatus<E, S>(
            dao = dao,
            query = query,
            errorCode = ERROR_CODE_DELETE,
            methodName = "delete",
            notifyAll = notifyAll
        )
    }

    suspend inline fun <reified E : Any, reified S : StatusSelectTable<E>> deleteAsync(
        dao: IDao.IFieldsDao,
        item: E,
        notifyAll: Boolean = false
    ): S {
        return withContext(Dispatchers.IO) { delete<E, S>(dao, item, notifyAll) }
    }

    inline fun <reified E : Any, reified S : StatusSelectTable<E>> delete(
        dao: IDao.IFieldsDao,
        items: List<E>,
        notifyAll: Boolean = false
    ): S {
        val query = QueryBuilder.createDeleteQuery(dao, items)
        return QueryExecuter.executeTransactionStatus<E, S>(
            dao = dao,
            query = query,
            errorCode = ERROR_CODE_DELETE,
            methodName = "deleteList",
            notifyAll = notifyAll
        )
    }

    suspend inline fun <reified E : Any, reified S : StatusSelectTable<E>> deleteAsync(
        dao: IDao.IFieldsDao,
        items: List<E>,
        notifyAll: Boolean = false
    ): S {
        return withContext(Dispatchers.IO) { delete<E, S>(dao, items, notifyAll) }
    }

    ////--------- INSERT QUERIES
    inline fun <reified E : Any, reified S : StatusSelectTable<E>> insertOrReplace(
        dao: IDao.IFieldsDao,
        item: E,
        notifyAll: Boolean = false
    ): S {
        val query = QueryBuilder.createInsertOrReplaceQuery(dao, item)
        return QueryExecuter.executeStatus<E, S>(
            dao = dao,
            query = query,
            errorCode = ERROR_CODE_INSERT,
            methodName = "insertOrReplace",
            notifyAll
        )
    }

    suspend inline fun <reified E : Any, reified S : StatusSelectTable<E>> insertOrReplaceAsync(
        dao: IDao.IFieldsDao,
        item: E,
        notifyAll: Boolean = false
    ): S {
        return withContext(Dispatchers.IO) { insertOrReplace<E, S>(dao, item, notifyAll) }
    }

    inline fun <reified E : Any, reified S : StatusSelectTable<E>> insertOrReplace(
        dao: IDao.IFieldsDao,
        items: List<E>,
        notifyAll: Boolean = false
    ): S {
        val query = QueryBuilder.createInsertOrReplaceQuery(dao, items)
        return QueryExecuter.executeTransactionStatus<E, S>(
            dao = dao,
            query = query,
            errorCode = ERROR_CODE_INSERT,
            methodName = "insertOrReplaceList",
            notifyAll = notifyAll
        )
    }

    suspend inline fun <reified E : Any, reified S : StatusSelectTable<E>> insertOrReplaceAsync(
        dao: IDao.IFieldsDao,
        items: List<E>,
        notifyAll: Boolean = false
    ): S {
        return withContext(Dispatchers.IO) { insertOrReplace<E, S>(dao, items, notifyAll) }
    }

}