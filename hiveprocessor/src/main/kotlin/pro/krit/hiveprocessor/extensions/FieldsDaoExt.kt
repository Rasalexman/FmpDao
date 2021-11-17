package pro.krit.hiveprocessor.extensions

import com.mobrun.plugin.models.StatusSelectTable
import kotlinx.coroutines.flow.Flow
import pro.krit.hiveprocessor.base.IDao
import pro.krit.hiveprocessor.common.FieldsBuilder

inline fun <reified E : Any> IDao.IFieldsDao.initFields() {
    FieldsBuilder.initFields(this, E::class.java.fields)
}

/*suspend inline fun <reified E : Any> IDao.IFieldsDao.initFieldsAsync() {
    withContext(Dispatchers.IO) {
        FieldsBuilder.initFields(
            this@initFieldsAsync,
            E::class.java.fields
        )
    }
}*/

/**
 * Создает Flow, который эмитит данные при подписке либо при старте
 *
 * @param where - тело запроса для SELECT, если пустой то выбирает все данные (SELECT *)
 * @param limit - лимитированное количество данных
 * @param offset - отступ в получении данных
 * @param orderBy - сортировка результатов запроса, необходимо так же указывать ASC|DESC
 * @param withStart - начать эмитить данные при создании потока
 * @param emitDelay - задержка при эмитинге данных
 * @param withDistinct - использовать эмитинг только уникальных данных
 */
inline fun <reified E : Any, reified S : StatusSelectTable<E>> IDao.IFieldsDao.flowable(
    where: String = "",
    limit: Int = 0,
    offset: Int = 0,
    orderBy: String = "",
    withStart: Boolean = true,
    emitDelay: Long = 100L,
    withDistinct: Boolean = false
): Flow<List<E>> {
    return DaoInstance.flowable<E,S>(this, where, limit, offset, orderBy, withStart, emitDelay, withDistinct)
}

/**
 * Создает SQL-запрос на поиск данных в таблице справочника, и возвращает [Result]
 *
 * @param where - тело запроса для SELECT
 * @param limit - лимитированное количество данных
 * @param offset - отступ в получении данных
 * @param orderBy - сортировка результатов запроса, необходимо так же указывать ASC|DESC
 *
 * @return - [List] с данными, либо пустой список
 */
inline fun <reified E : Any, reified S : StatusSelectTable<E>> IDao.IFieldsDao.select(
    where: String = "",
    limit: Int = 0,
    offset: Int = 0,
    orderBy: String = ""
): List<E> = DaoInstance.select<E, S>(this, where, limit, offset, orderBy)

/*suspend inline fun <reified E : Any, reified S : StatusSelectTable<E>> IDao.IFieldsDao.selectAsync(
    where: String = "",
    limit: Int = 0
): List<E> {
    return DaoInstance.selectAsync<E, S>(this, where, limit)
}*/

/**
 * Создает SQL-запрос на поиск данных в таблице справочника, и возвращает [Result]
 *
 * @param where - тело запроса для SELECT
 * @param limit - лимитированное количество данных
 * @param offset - отступ в получении данных
 * @param orderBy - сортировка результатов запроса, необходимо так же указывать ASC|DESC
 *
 * @return - список результатов запроса обернутый в [Result]
 */
inline fun <reified E : Any, reified S : StatusSelectTable<E>> IDao.IFieldsDao.selectResult(
    where: String = "",
    limit: Int = 0,
    offset: Int = 0,
    orderBy: String = ""
): Result<List<E>> {
    return DaoInstance.selectResult<E, S>(this, where, limit, offset, orderBy)
}

///------- CREATE TABLE
/**
 * Создание таблицы данных
 */
inline fun <reified E : Any, reified S : StatusSelectTable<E>> IDao.IFieldsDao.createTable(): S {
    return DaoInstance.createTable<E, S>(this)
}

/*suspend inline fun <reified E : Any, reified S : StatusSelectTable<E>> IDao.IFieldsDao.createTableAsync(): S {
    return DaoInstance.createTableAsync<E, S>(this)
}*/

///------ INSERT DATA
/**
 * Удаление данных из таблицы по запросу
 *
 * @param item - тело запроса для SELECT
 * @param notifyAll - тригер на одновление всех значений
 *
 * @return - возвращает статус удаленной записи из таблицы
 */
inline fun <reified E : Any, reified S : StatusSelectTable<E>> IDao.IFieldsDao.insertOrReplace(
    item: E,
    notifyAll: Boolean = false
): S {
    return DaoInstance.insertOrReplace<E, S>(this, item, notifyAll)
}

/*suspend inline fun <reified E : Any, reified S : StatusSelectTable<E>> IDao.IFieldsDao.insertOrReplaceAsync(
    item: E,
    notifyAll: Boolean = false
): S {
    return DaoInstance.insertOrReplaceAsync<E, S>(this, item, notifyAll)
}*/

inline fun <reified E : Any, reified S : StatusSelectTable<E>> IDao.IFieldsDao.insertOrReplace(
    items: List<E>,
    notifyAll: Boolean = false
): S {
    return DaoInstance.insertOrReplace<E, S>(this, items, notifyAll)
}

/*suspend inline fun <reified E : Any, reified S : StatusSelectTable<E>> IDao.IFieldsDao.insertOrReplaceAsync(
    items: List<E>,
    notifyAll: Boolean = false
): S {
    return DaoInstance.insertOrReplaceAsync<E, S>(this, items, notifyAll)
}*/

///---- DELETE
inline fun <reified E : Any, reified S : StatusSelectTable<E>> IDao.IFieldsDao.delete(
    where: String = "",
    notifyAll: Boolean = false
): S {
    return DaoInstance.delete<E, S>(this, where, notifyAll)
}

/*suspend inline fun <reified E : Any, reified S : StatusSelectTable<E>> IDao.IFieldsDao.deleteAsync(
    where: String = "",
    notifyAll: Boolean = true
): S {
    return DaoInstance.deleteAsync<E, S>(this, where, notifyAll)
}*/

inline fun <reified E : Any, reified S : StatusSelectTable<E>> IDao.IFieldsDao.delete(
    item: E,
    notifyAll: Boolean = false
): S {
    return DaoInstance.delete<E, S>(this, item, notifyAll)
}

/*suspend inline fun <reified E : Any, reified S : StatusSelectTable<E>> IDao.IFieldsDao.deleteAsync(
    item: E,
    notifyAll: Boolean = false
): S {
    return DaoInstance.deleteAsync<E, S>(this, item, notifyAll)
}*/

inline fun <reified E : Any, reified S : StatusSelectTable<E>> IDao.IFieldsDao.delete(
    items: List<E>,
    notifyAll: Boolean = false
): S {
    return DaoInstance.delete<E, S>(this, items, notifyAll)
}

/*suspend inline fun <reified E : Any, reified S : StatusSelectTable<E>> IDao.IFieldsDao.deleteAsync(
    items: List<E>,
    notifyAll: Boolean = false
): S {
    return DaoInstance.deleteAsync<E, S>(this, items, notifyAll)
}*/
