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

@file:Suppress("unused")

package pro.krit.hhivecore.extensions

import com.mobrun.plugin.models.StatusSelectTable
import pro.krit.hhivecore.base.IDao
import pro.krit.hhivecore.common.FlowableConfig
import pro.krit.hhivecore.common.QueryExecuter

/**
 * Создает Flow, который эмитит данные при подписке
 *
 * @param builderBlock - конструктор запроса
 *
 * @return - Flow<List<E>> поток данных из базы данных
 */
inline fun <reified E : Any, reified S : StatusSelectTable<E>> IDao.IFmpLocalDao<E, S>.flowable(
    builderBlock: FlowableConfig.() -> Unit = {}
) = DaoInstance.flowable<E, S>(dao = this, config = FlowableConfig().apply(builderBlock))

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
        emptyList()
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
 * @param fields - возвращаеммые поля, если не заполнен то возвращаются все поля
 *
 * @return - [List] с данными, либо пустой список
 */
inline fun <reified E : Any, reified S : StatusSelectTable<E>> IDao.IFmpLocalDao<E, S>.select(
    where: String = "",
    limit: Int = 0,
    offset: Int = 0,
    orderBy: String = "",
    fields: List<String>? = null
): List<E> = DaoInstance.select<E, S>(this, where, limit, offset, orderBy, fields)

/**
 * Создает SQL-запрос на поиск данных в таблице справочника, и возвращает [Result]
 *
 * @param where - тело запроса для SELECT
 * @param limit - лимитированное количество данных
 * @param offset - отступ в получении данных
 * @param orderBy - сортировка результатов запроса, необходимо так же указывать ASC|DESC
 * @param fields - возвращаеммые поля, если не заполнен то возвращаются все поля
 *
 * @return - список результатов запроса обернутый в [Result]
 */
inline fun <reified E : Any, reified S : StatusSelectTable<E>> IDao.IFmpLocalDao<E, S>.selectResult(
    where: String = "",
    limit: Int = 0,
    offset: Int = 0,
    orderBy: String = "",
    fields: List<String>? = null
): Result<List<E>> {
    return DaoInstance.selectResult<E, S>(this, where, limit, offset, orderBy, fields)
}

/**
 * Создает SQL-запрос на обновление данных в таблице справочника, и возвращает [StatusSelectTable]
 *
 * @param setQuery - тело запроса для SET
 * @param from - тело запроса если данные обновляются из другой таблицы
 * @param where - тело запроса поиска элементов обновления
 * @param notifyAll - тригер на одновление flowable
 *
 * @return - [StatusSelectTable] статус обновления таблицы
 */
inline fun <reified E : Any, reified S : StatusSelectTable<E>> IDao.IFmpLocalDao<E, S>.update(
    setQuery: String,
    from: String = "",
    where: String = "",
    notifyAll: Boolean = false
): StatusSelectTable<E> = DaoInstance.update(this, setQuery, from, where, notifyAll)

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
): StatusSelectTable<E> {
    return DaoInstance.delete(this, where, notifyAll)
}

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
): StatusSelectTable<E> {
    return DaoInstance.delete(this, item, notifyAll)
}

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
): StatusSelectTable<E> {
    return DaoInstance.delete(this, items, notifyAll)
}

////--------- INSERT QUERIES
/**
 * Вставка или замена данных из таблицы по уникальному ключу [com.mobrun.plugin.api.request_assistant.PrimaryKey]
 *
 * @param item - экземпляров данных
 * @param notifyAll - тригер на одновление всех значений
 *
 * @return - возвращает статус вставленной записи
 */
inline fun <reified E : Any, reified S : StatusSelectTable<E>> IDao.IFmpLocalDao<E, S>.insertOrReplace(
    item: E,
    notifyAll: Boolean = false
): StatusSelectTable<E> {
    return DaoInstance.insertOrReplace(this, item, notifyAll)
}

/**
 * Вставка или замена списка данных по уникальному ключу [com.mobrun.plugin.api.request_assistant.PrimaryKey]
 *
 * @param items - [List] экземпляров данных
 * @param notifyAll - тригер на одновление всех значений
 *
 * @return - возвращает статус вставленной записи
 */
inline fun <reified E : Any, reified S : StatusSelectTable<E>> IDao.IFmpLocalDao<E, S>.insertOrReplace(
    items: List<E>,
    notifyAll: Boolean = false
): StatusSelectTable<E> {
    return DaoInstance.insertOrReplace<E, S>(this, items, notifyAll)
}
