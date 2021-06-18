package pro.krit.hiveprocessor.extensions

import com.mobrun.plugin.models.StatusSelectTable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import pro.krit.hiveprocessor.base.IDao
import pro.krit.hiveprocessor.common.FieldsBuilder

inline fun <reified E : Any> IDao.IFieldsDao.initFields() {
    FieldsBuilder.initFields(this, E::class.java.fields)
}

suspend inline fun <reified E : Any> IDao.IFieldsDao.initFieldsAsync() {
    withContext(Dispatchers.IO) {
        FieldsBuilder.initFields(
            this@initFieldsAsync,
            E::class.java.fields
        )
    }
}

///----
inline fun <reified E : Any, reified S : StatusSelectTable<E>> IDao.IFieldsDao.flowable(
    where: String = "",
    limit: Int = 0,
    withStart: Boolean = true,
    emitDelay: Long = 100L,
    withDistinct: Boolean = false
): Flow<List<E>> {
    return DaoInstance.flowable<E,S>(this, where, limit, withStart, emitDelay, withDistinct)
}

///---- SELECT QUERIES
inline fun <reified E : Any, reified S : StatusSelectTable<E>> IDao.IFieldsDao.select(
    where: String = "",
    limit: Int = 0
): List<E> = DaoInstance.select<E, S>(this, where, limit)

suspend inline fun <reified E : Any, reified S : StatusSelectTable<E>> IDao.IFieldsDao.selectAsync(
    where: String = "",
    limit: Int = 0
): List<E> {
    return DaoInstance.selectAsync<E, S>(this, where, limit)
}

inline fun <reified E : Any, reified S : StatusSelectTable<E>> IDao.IFieldsDao.selectResult(
    where: String = "",
    limit: Int = 0
): Result<List<E>> {
    return DaoInstance.selectResult<E, S>(this, where, limit)
}

///------- CREATE TABLE
inline fun <reified E : Any, reified S : StatusSelectTable<E>> IDao.IFieldsDao.createTable(): S {
    return DaoInstance.createTable<E, S>(this)
}

suspend inline fun <reified E : Any, reified S : StatusSelectTable<E>> IDao.IFieldsDao.createTableAsync(): S {
    return DaoInstance.createTableAsync<E, S>(this)
}

///------ INSERT DATA
inline fun <reified E : Any, reified S : StatusSelectTable<E>> IDao.IFieldsDao.insertOrReplace(
    item: E,
    notifyAll: Boolean = false
): S {
    return DaoInstance.insertOrReplace<E, S>(this, item, notifyAll)
}

suspend inline fun <reified E : Any, reified S : StatusSelectTable<E>> IDao.IFieldsDao.insertOrReplaceAsync(
    item: E,
    notifyAll: Boolean = false
): S {
    return DaoInstance.insertOrReplaceAsync<E, S>(this, item, notifyAll)
}

inline fun <reified E : Any, reified S : StatusSelectTable<E>> IDao.IFieldsDao.insertOrReplace(
    items: List<E>,
    notifyAll: Boolean = false
): S {
    return DaoInstance.insertOrReplace<E, S>(this, items, notifyAll)
}

suspend inline fun <reified E : Any, reified S : StatusSelectTable<E>> IDao.IFieldsDao.insertOrReplaceAsync(
    items: List<E>,
    notifyAll: Boolean = false
): S {
    return DaoInstance.insertOrReplaceAsync<E, S>(this, items, notifyAll)
}

///---- DELETE
inline fun <reified E : Any, reified S : StatusSelectTable<E>> IDao.IFieldsDao.delete(
    where: String = "",
    notifyAll: Boolean = true
): S {
    return DaoInstance.delete<E, S>(this, where, notifyAll)
}

suspend inline fun <reified E : Any, reified S : StatusSelectTable<E>> IDao.IFieldsDao.deleteAsync(
    where: String = "",
    notifyAll: Boolean = true
): S {
    return DaoInstance.deleteAsync<E, S>(this, where, notifyAll)
}

inline fun <reified E : Any, reified S : StatusSelectTable<E>> IDao.IFieldsDao.delete(
    item: E,
    notifyAll: Boolean = false
): S {
    return DaoInstance.delete<E, S>(this, item, notifyAll)
}

suspend inline fun <reified E : Any, reified S : StatusSelectTable<E>> IDao.IFieldsDao.deleteAsync(
    item: E,
    notifyAll: Boolean = false
): S {
    return DaoInstance.deleteAsync<E, S>(this, item, notifyAll)
}

inline fun <reified E : Any, reified S : StatusSelectTable<E>> IDao.IFieldsDao.delete(
    items: List<E>,
    notifyAll: Boolean = false
): S {
    return DaoInstance.delete<E, S>(this, items, notifyAll)
}

suspend inline fun <reified E : Any, reified S : StatusSelectTable<E>> IDao.IFieldsDao.deleteAsync(
    items: List<E>,
    notifyAll: Boolean = false
): S {
    return DaoInstance.deleteAsync<E, S>(this, items, notifyAll)
}
