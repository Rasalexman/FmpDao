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

package com.rasalexman.hhivecore.extensions

import com.mobrun.plugin.models.StatusSelectTable
import com.rasalexman.hhivecore.base.IDao
import com.rasalexman.hhivecore.common.QueryBuilder
import com.rasalexman.hhivecore.common.QueryConfig
import com.rasalexman.hhivecore.common.QueryExecuter

/**
 * Создает Flow, который эмитит данные при подписке
 *
 * @param builderBlock - конструктор запроса
 *
 * @return - Flow<List<E>> поток данных из базы данных
 */
inline fun <reified E : Any, reified S : StatusSelectTable<E>> IDao.IFmpLocalDao<E, S>.flowable(
    builderBlock: QueryConfig.() -> Unit = {}
) = DaoInstance.flowable<E, S>(dao = this, config = QueryConfig().apply(builderBlock))

/**
 * Создает Observable, который эмитит данные при триггере
 */
inline fun <reified E : Any, reified S : StatusSelectTable<E>> IDao.IFmpLocalDao<E, S>.observable(
    builderBlock: QueryConfig.() -> Unit = {}
) = DaoInstance.observable<E, S>(dao = this, config = QueryConfig().apply(builderBlock))

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
 * @param builderBlock - конструктор тела запроса [QueryConfig]
 *
 * @return - [List] с данными, либо пустой список
 */
inline fun <reified E : Any, reified S : StatusSelectTable<E>> IDao.IFmpLocalDao<E, S>.select(
    builderBlock: QueryConfig.() -> Unit = {}
): List<E> = QueryConfig().apply(builderBlock).run {
    DaoInstance.select<E, S>(this@select, where, limit, offset, orderBy, fields)
}

/**
 * Создает SQL-запрос на поиск данных в таблице справочника, и возвращает [Result]
 *
 * @param builderBlock - конструктор тела запроса [QueryConfig]
 *
 * @return - E? с данными, либо пустой список
 */
inline fun <reified E : Any, reified S : StatusSelectTable<E>> IDao.IFmpLocalDao<E, S>.selectFirst(
    builderBlock: QueryConfig.() -> Unit = {}
): E? = QueryConfig().apply(builderBlock).run {
    DaoInstance.select<E, S>(this@selectFirst, where, 1, offset, orderBy, fields)
}.firstOrNull()

/**
 * Создает SQL-запрос на поиск данных в таблице справочника, и возвращает [Result]
 *
 * @param builderBlock - конструктор тела запроса [QueryConfig]
 *
 * @return - список результатов запроса обернутый в [Result]
 */
inline fun <reified E : Any, reified S : StatusSelectTable<E>> IDao.IFmpLocalDao<E, S>.selectResult(
    builderBlock: QueryConfig.() -> Unit = {}
): Result<List<E>> {
    return QueryConfig().apply(builderBlock).run {
        DaoInstance.selectResult<E, S>(this@selectResult, where, limit, offset, orderBy, fields)
    }
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

/**
 * Запрос на добавление поля в таблицу
 */
inline fun <reified E : Any, reified S : StatusSelectTable<E>> IDao.IFmpLocalDao<E, S>.addColumn(columnName: String): StatusSelectTable<E> {
    val addColumnQuery = QueryBuilder.alterTableQueryAddColumn(this, columnName)
    val addColumnStatus = QueryExecuter.executeStatus<E, S>(
        dao = this,
        query = addColumnQuery
    )
    return addColumnStatus
}
