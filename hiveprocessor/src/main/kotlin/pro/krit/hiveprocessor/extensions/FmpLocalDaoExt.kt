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

package pro.krit.hiveprocessor.extensions

import com.mobrun.plugin.models.StatusSelectTable
import kotlinx.coroutines.flow.Flow
import pro.krit.hiveprocessor.base.IDao
import pro.krit.hiveprocessor.common.QueryExecuter

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
inline fun <reified E : Any, reified S : StatusSelectTable<E>> IDao.IFmpLocalDao<E, S>.flowable(
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
 * Простой запрос в таблицу базы данных
 *
 * @param body - тело запроса
 */
inline fun <reified E : Any, reified S : StatusSelectTable<E>> IDao.IFmpLocalDao<E, S>.query(
    body: String = ""
): List<E> {
    return try {
        QueryExecuter.executeQuery<E, S>(
            dao = this,
            query = body,
            errorCode = ERROR_CODE_QUERY,
            methodName = "query"
        )
    } catch (e: Throwable) {
        e.printStackTrace()
        emptyList<E>()
    }
}

///---- SELECT QUERIES
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
inline fun <reified E : Any, reified S : StatusSelectTable<E>> IDao.IFmpLocalDao<E, S>.select(
    where: String = "",
    limit: Int = 0,
    offset: Int = 0,
    orderBy: String = ""
): List<E> = DaoInstance.select<E, S>(this, where, limit, offset, orderBy)

/*suspend inline fun <reified E : Any, reified S : StatusSelectTable<E>> IDao.IFmpLocalDao<E, S>.selectAsync(
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
inline fun <reified E : Any, reified S : StatusSelectTable<E>> IDao.IFmpLocalDao<E, S>.selectResult(
    where: String = "",
    limit: Int = 0,
    offset: Int = 0,
    orderBy: String = ""
): Result<List<E>> {
    return DaoInstance.selectResult<E, S>(this, where, limit, offset, orderBy)
}

/**
 * Создает SQL-запрос на обновление данных в таблице справочника, и возвращает [StatusSelectTable]
 *
 * @param setQuery - тело запроса для SET
 * @param from - тело запроса если данные обновляются из другой таблицы
 * @param where - тело запроса поиска элементов обновления
 *
 * @return - [StatusSelectTable] статус обновления таблицы
 */
inline fun <reified E : Any, reified S : StatusSelectTable<E>> IDao.IFmpLocalDao<E, S>.update(
    setQuery: String,
    from: String = "",
    where: String = ""
): S = DaoInstance.update(this, setQuery, from, where)



////------- CREATE TABLES
/**
 * Создание таблицы данных
 */
inline fun <reified E : Any, reified S : StatusSelectTable<E>> IDao.IFmpLocalDao<E, S>.createTable(): S {
    return DaoInstance.createTable(this)
}

/*suspend inline fun <reified E : Any, reified S : StatusSelectTable<E>> IDao.IFmpLocalDao<E, S>.createTableAsync(): S {
    return DaoInstance.createTableAsync<E, S>(this)
}*/

/////---------- DELETE QUERIES
/**
 * Удаление данных из таблицы по запросу
 *
 * @param where - тело запроса для SELECT
 * @param notifyAll - тригер на одновление всех значений
 *
 * @return - возвращает статус удаленной записи из таблицы
 */
inline fun <reified E : Any, reified S : StatusSelectTable<E>> IDao.IFmpLocalDao<E, S>.delete(
    where: String = "",
    notifyAll: Boolean = false
): S {
    return DaoInstance.delete(this, where, notifyAll)
}

/*suspend inline fun <reified E : Any, reified S : StatusSelectTable<E>> IDao.IFmpLocalDao<E, S>.deleteAsync(
    where: String = "",
    notifyAll: Boolean = true
): S {
    return DaoInstance.deleteAsync<E, S>(this, where, notifyAll)
}*/

/**
 * Удаление данных из таблицы по запросу
 *
 * @param item - экземпляр данных для удаления
 * @param notifyAll - тригер на одновление всех значений
 *
 * @return - возвращает статус удаленной записи из таблицы
 */
inline fun <reified E : Any, reified S : StatusSelectTable<E>> IDao.IFmpLocalDao<E, S>.delete(
    item: E,
    notifyAll: Boolean = false
): S {
    return DaoInstance.delete(this, item, notifyAll)
}

/*suspend inline fun <reified E : Any, reified S : StatusSelectTable<E>> IDao.IFmpLocalDao<E, S>.deleteAsync(
    item: E,
    notifyAll: Boolean = false
): S {
    return DaoInstance.deleteAsync<E, S>(this, item, notifyAll)
}*/

/**
 * Удаление данных из таблицы по запросу
 *
 * @param items - список [List] экземпляров данных для удаления
 * @param notifyAll - тригер на одновление всех значений
 *
 * @return - возвращает статус удаленной записи из таблицы
 */
inline fun <reified E : Any, reified S : StatusSelectTable<E>> IDao.IFmpLocalDao<E, S>.delete(
    items: List<E>,
    notifyAll: Boolean = false
): S {
    return DaoInstance.delete(this, items, notifyAll)
}

/*suspend inline fun <reified E : Any, reified S : StatusSelectTable<E>> IDao.IFmpLocalDao<E, S>.deleteAsync(
    items: List<E>,
    notifyAll: Boolean = false
): S {
    return DaoInstance.deleteAsync<E, S>(this, items, notifyAll)
}*/

////--------- INSERT QUERIES
/**
 * Вставка или замена данных из таблицы по уникальному ключу [PrimaryKey]
 *
 * @param item - экземпляров данных
 * @param notifyAll - тригер на одновление всех значений
 *
 * @return - возвращает статус вставленной записи
 */
inline fun <reified E : Any, reified S : StatusSelectTable<E>> IDao.IFmpLocalDao<E, S>.insertOrReplace(
    item: E,
    notifyAll: Boolean = false
): S {
    return DaoInstance.insertOrReplace(this, item, notifyAll)
}

/*suspend inline fun <reified E : Any, reified S : StatusSelectTable<E>> IDao.IFmpLocalDao<E, S>.insertOrReplaceAsync(
    item: E,
    notifyAll: Boolean = false
): S {
    return DaoInstance.insertOrReplaceAsync<E, S>(this, item, notifyAll)
}*/

/**
 * Вставка или замена списка данных по уникальному ключу [PrimaryKey]
 *
 * @param items - [List] экземпляров данных
 * @param notifyAll - тригер на одновление всех значений
 *
 * @return - возвращает статус вставленной записи
 */
inline fun <reified E : Any, reified S : StatusSelectTable<E>> IDao.IFmpLocalDao<E, S>.insertOrReplace(
    items: List<E>,
    notifyAll: Boolean = false
): S {
    return DaoInstance.insertOrReplace<E, S>(this, items, notifyAll)
}

/*
suspend inline fun <reified E : Any, reified S : StatusSelectTable<E>> IDao.IFmpLocalDao<E, S>.insertOrReplaceAsync(
    items: List<E>,
    notifyAll: Boolean = false
): S {
    return DaoInstance.insertOrReplaceAsync<E, S>(this,items, notifyAll)
}*/
