package pro.krit.hhivecore.extensions

import com.mobrun.plugin.models.StatusSelectTable
import io.reactivex.rxjava3.core.Observable
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import pro.krit.hhivecore.base.IDao
import pro.krit.hhivecore.common.QueryBuilder
import pro.krit.hhivecore.common.QueryConfig
import pro.krit.hhivecore.common.QueryExecuter
import java.util.concurrent.TimeUnit

object DaoInstance {

    inline fun <reified E : Any, reified S : StatusSelectTable<E>> flowable(
        dao: IDao,
        config: QueryConfig
    ): Flow<List<E>> {
        val trigger = dao.getFlowTrigger().run {
            if (config.isDevastate) this.filter { it.isNotEmpty() }
            else this
        }
        //println("------> onCreate trigger devastate = ${config.isDevastate}")
        return flow {
            trigger.collect {
                if (config.emitDelay > 0) {
                    delay(config.emitDelay)
                }
                //println("------> onCollect trigger value: $it")
                val result = config.run {
                    select<E, S>(dao, where, limit, offset, orderBy, fields)
                }
                emit(result)
                if (config.isDevastate) {
                    dao.dropTrigger()
                }
            }
        }.onStart {
            val isTriggerEmpty = dao.getFlowTrigger().firstOrNull().isNullOrEmpty()
            if (config.withStart && isTriggerEmpty) {
                //println("------> onStart")
                dao.triggerFlow()
            }
        }.catch {
            //println("[ERROR]: database table '${dao.fullTableName}' error $it")
            emit(emptyList())
        }.apply {
            if (config.withDistinct) {
                distinctUntilChanged()
            }
        }
    }

    inline fun <reified E : Any, reified S : StatusSelectTable<E>> observable(
        dao: IDao,
        config: QueryConfig
    ): Observable<List<E>> {
        val trigger = dao.getRxTrigger().run {
            if (config.isDevastate) this.filter { it.isNotEmpty() }
            else this
        }
        val requestDelay = config.emitDelay
        return trigger.delay(requestDelay, TimeUnit.MILLISECONDS).flatMap { trig ->
            if(trig.isNotEmpty()) {
                Observable.fromCallable {
                    config.run {
                        select<E, S>(dao, where, limit, offset, orderBy, fields)
                    }
                }
            } else {
                Observable.empty()
            }
        }.doOnSubscribe {
            val isTriggerEmpty = dao.getRxTrigger().blockingFirst().isEmpty()
            if (config.withStart && isTriggerEmpty) {
                //println("------> onStart")
                dao.triggerRx()
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
        dao: IDao,
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
        dao: IDao,
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
        dao: IDao,
        items: List<E>,
        notifyAll: Boolean = false
    ): StatusSelectTable<E> {
        val query = QueryBuilder.createDeleteQuery(dao, items)
        return QueryExecuter.executeStatus(
            dao = dao,
            query = query,
            errorCode = ERROR_CODE_DELETE,
            methodName = "deleteList",
            notifyAll = notifyAll
        )
    }

    ////--------- UPDATE QUERIES
    inline fun <reified E : Any> update(
        dao: IDao,
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
        dao: IDao,
        item: E,
        notifyAll: Boolean = false
    ): StatusSelectTable<E> {
        val query = QueryBuilder.createInsertOrReplaceQuery(dao, item)
        return QueryExecuter.executeStatus(
            dao = dao,
            query = query,
            errorCode = ERROR_CODE_INSERT,
            methodName = "insertOrReplaceItem",
            notifyAll
        )
    }

    inline fun <reified E : Any, reified S : StatusSelectTable<E>> insertOrReplace(
        dao: IDao,
        items: List<E>,
        notifyAll: Boolean = false,
        withoutPrimaryKey: Boolean = false
    ): StatusSelectTable<E> {
        val query = QueryBuilder.createInsertOrReplaceQuery(dao, items, withoutPrimaryKey)
        return QueryExecuter.executeStatus<E, S>(
            dao = dao,
            query = query,
            errorCode = ERROR_CODE_INSERT,
            methodName = "insertOrReplaceList",
            notifyAll = notifyAll
        )
    }
}