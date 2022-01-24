package pro.krit.hiveprocessor.extensions

import com.mobrun.plugin.models.StatusSelectTable
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import pro.krit.hiveprocessor.base.IDao
import pro.krit.hiveprocessor.common.QueryBuilder
import pro.krit.hiveprocessor.common.QueryExecuter

object DaoInstance {

    inline fun <reified E : Any, reified S : StatusSelectTable<E>> flowable(
        dao: IDao,
        where: String = "",
        limit: Int = 0,
        offset: Int = 0,
        orderBy: String = "",
        withStart: Boolean = true,
        emitDelay: Long = 0L,
        withDistinct: Boolean = false,
        fields: List<String>? = null
    ): Flow<List<E>> {
        val trigger = dao.getStartedTrigger(withStart)
        println("------> onCreate trigger flow")
        return flow {
            trigger.collect {
                if (emitDelay > 0) {
                    delay(emitDelay)
                }
                println("------> onCollect trigger value: $it")
                val result = select<E, S>(dao, where, limit, offset, orderBy, fields)
                emit(result)
                if(withStart) {
                    dao.dropTrigger()
                }
            }
        }.apply {
            if (withDistinct) {
                distinctUntilChanged()
            }
        }
    }

    inline fun <reified E : Any, reified S : StatusSelectTable<E>> select(
        dao: IDao,
        where: String = "",
        limit: Int = 0,
        offset: Int = 0,
        orderBy: String = "",
        fields: List<String>? = null,
    ): List<E> {
        val selectQuery = QueryBuilder.createQuery(
            dao = dao,
            prefix = QueryBuilder.SELECT_QUERY,
            where = where,
            limit = limit,
            offset = offset,
            orderBy = orderBy,
            fields = fields
        )
        //println("------> selectQuery = $selectQuery")
        return QueryExecuter.executeQuery<E, S>(
            dao = dao,
            query = selectQuery,
            errorCode = ERROR_CODE_SELECT_WHERE,
            methodName = "selectWhere"
        )
    }

    inline fun <reified E : Any, reified S : StatusSelectTable<E>> selectResult(
        dao: IDao,
        where: String = "",
        limit: Int = 0,
        offset: Int = 0,
        orderBy: String = "",
        fields: List<String>? = null
    ): Result<List<E>> {
        val selectQuery = QueryBuilder.createQuery(
            dao = dao,
            where = where,
            limit = limit,
            offset = offset,
            orderBy = orderBy,
            fields = fields
        )
        return QueryExecuter.executeResultQuery<E, S>(
            dao = dao,
            query = selectQuery,
            errorCode = ERROR_CODE_SELECT_WHERE,
            methodName = "selectWhere"
        )
    }


    ///// Create tables
    inline fun <reified E : Any> createTable(
        dao: IDao
    ): StatusSelectTable<E> {
        dao.initFields<E>()
        val query = QueryBuilder.createTableQuery(dao)
        return QueryExecuter.executeStatus(
            dao = dao,
            query = query,
            errorCode = ERROR_CODE_CREATE,
            methodName = "createTable"
        )
    }

    /////---------- DELETE QUERIES
    inline fun <reified E : Any> delete(
        dao: IDao.IFieldsDao,
        where: String = "",
        notifyAll: Boolean = false
    ): StatusSelectTable<E> {
        val deleteQuery = QueryBuilder.createQuery(dao, QueryBuilder.DELETE_QUERY, where)
        return QueryExecuter.executeStatus(
            dao = dao,
            query = deleteQuery,
            errorCode = ERROR_CODE_REMOVE_WHERE,
            methodName = "delete",
            notifyAll = notifyAll
        )
    }

    inline fun <reified E : Any> delete(
        dao: IDao.IFieldsDao,
        item: E,
        notifyAll: Boolean = false
    ): StatusSelectTable<E> {
        val query = QueryBuilder.createDeleteQuery(dao, item)
        return QueryExecuter.executeStatus(
            dao = dao,
            query = query,
            errorCode = ERROR_CODE_DELETE,
            methodName = "delete",
            notifyAll = notifyAll
        )
    }

    inline fun <reified E : Any> delete(
        dao: IDao.IFieldsDao,
        items: List<E>,
        notifyAll: Boolean = false
    ): StatusSelectTable<E> {
        val query = QueryBuilder.createDeleteQuery(dao, items)
        return QueryExecuter.executeTransactionStatus(
            dao = dao,
            query = query,
            errorCode = ERROR_CODE_DELETE,
            methodName = "deleteList",
            notifyAll = notifyAll
        )
    }

    ////--------- UPDATE QUERIES
    inline fun <reified E : Any> update(
        dao: IDao.IFieldsDao,
        setQuery: String,
        from: String = "",
        where: String = "",
        notifyAll: Boolean = false
    ): StatusSelectTable<E> {
        val updateQuery = QueryBuilder.createUpdateQuery(dao, setQuery, from, where)
        return QueryExecuter.executeStatus(
            dao = dao,
            query = updateQuery,
            errorCode = ERROR_CODE_UPDATE,
            methodName = "update",
            notifyAll = notifyAll
        )
    }

    ////--------- INSERT QUERIES
    inline fun <reified E : Any> insertOrReplace(
        dao: IDao.IFieldsDao,
        item: E,
        notifyAll: Boolean = false
    ): StatusSelectTable<E> {
        val query = QueryBuilder.createInsertOrReplaceQuery(dao, item)
        return QueryExecuter.executeStatus(
            dao = dao,
            query = query,
            errorCode = ERROR_CODE_INSERT,
            methodName = "insertOrReplace",
            notifyAll
        )
    }

    inline fun <reified E : Any, reified S : StatusSelectTable<E>> insertOrReplace(
        dao: IDao.IFieldsDao,
        items: List<E>,
        notifyAll: Boolean = false
    ): StatusSelectTable<E> {
        val query = QueryBuilder.createInsertOrReplaceQuery(dao, items)
        return QueryExecuter.executeTransactionStatus<E, S>(
            dao = dao,
            query = query,
            errorCode = ERROR_CODE_INSERT,
            methodName = "insertOrReplaceList",
            notifyAll = notifyAll
        )
    }
}