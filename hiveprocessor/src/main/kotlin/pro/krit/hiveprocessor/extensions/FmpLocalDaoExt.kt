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

inline fun <reified E : Any, reified S : StatusSelectTable<E>> IDao.IFmpLocalDao<E, S>.flowable(
    where: String = "",
    limit: Int = 0,
    withStart: Boolean = true,
    emitDelay: Long = 100L,
    withDistinct: Boolean = false
): Flow<List<E>> {
    return flowable<E,S>(this, where, limit, withStart, emitDelay, withDistinct)
}

///---- SELECT QUERIES
inline fun <reified E : Any, reified S : StatusSelectTable<E>> IDao.IFmpLocalDao<E, S>.select(
    where: String = "",
    limit: Int = 0
): List<E> = select<E, S>(this, where, limit)

suspend inline fun <reified E : Any, reified S : StatusSelectTable<E>> IDao.IFmpLocalDao<E, S>.selectAsync(
    where: String = "",
    limit: Int = 0
): List<E> {
    return selectAsync<E, S>(this, where, limit)
}

inline fun <reified E : Any, reified S : StatusSelectTable<E>> IDao.IFmpLocalDao<E, S>.selectResult(
    where: String = "",
    limit: Int = 0
): Result<List<E>> {
    return selectResult<E, S>(this, where, limit)
}

suspend inline fun <reified E : Any, reified S : StatusSelectTable<E>> IDao.IFmpLocalDao<E, S>.selectResultAsync(
    where: String = "",
    limit: Int = 0
): Result<List<E>> {
    return selectResultAsync<E, S>(this, where, limit)
}


////------- CREATE TABLES
inline fun <reified E : Any, reified S : StatusSelectTable<E>> IDao.IFmpLocalDao<E, S>.createTable(): S {
    return DaoInstance.createTable<E, S>(this)
}

suspend inline fun <reified E : Any, reified S : StatusSelectTable<E>> IDao.IFmpLocalDao<E, S>.createTableAsync(): S {
    return DaoInstance.createTableAsync<E, S>(this)
}

/////---------- DELETE QUERIES
inline fun <reified E : Any, reified S : StatusSelectTable<E>> IDao.IFmpLocalDao<E, S>.delete(
    where: String = "",
    notifyAll: Boolean = true
): S {
    return DaoInstance.delete(this, where, notifyAll)
}

suspend inline fun <reified E : Any, reified S : StatusSelectTable<E>> IDao.IFmpLocalDao<E, S>.deleteAsync(
    where: String = "",
    notifyAll: Boolean = true
): S {
    return DaoInstance.deleteAsync(this, where, notifyAll)
}

inline fun <reified E : Any, reified S : StatusSelectTable<E>> IDao.IFmpLocalDao<E, S>.delete(
    item: E,
    notifyAll: Boolean = false
): S {
    return DaoInstance.delete(this, item, notifyAll)
}

suspend inline fun <reified E : Any, reified S : StatusSelectTable<E>> IDao.IFmpLocalDao<E, S>.deleteAsync(
    item: E,
    notifyAll: Boolean = false
): S {
    return DaoInstance.deleteAsync(this, item, notifyAll)
}

inline fun <reified E : Any, reified S : StatusSelectTable<E>> IDao.IFmpLocalDao<E, S>.delete(
    items: List<E>,
    notifyAll: Boolean = false
): S {
    return DaoInstance.delete(this, items, notifyAll)
}

suspend inline fun <reified E : Any, reified S : StatusSelectTable<E>> IDao.IFmpLocalDao<E, S>.deleteAsync(
    items: List<E>,
    notifyAll: Boolean = false
): S {
    return DaoInstance.deleteAsync(this, items, notifyAll)
}

////--------- INSERT QUERIES
inline fun <reified E : Any, reified S : StatusSelectTable<E>> IDao.IFmpLocalDao<E, S>.insertOrReplace(
    item: E,
    notifyAll: Boolean = false
): S {
    return DaoInstance.insertOrReplace(this, item, notifyAll)
}

suspend inline fun <reified E : Any, reified S : StatusSelectTable<E>> IDao.IFmpLocalDao<E, S>.insertOrReplaceAsync(
    item: E,
    notifyAll: Boolean = false
): S {
    return DaoInstance.insertOrReplaceAsync(this, item, notifyAll)
}

inline fun <reified E : Any, reified S : StatusSelectTable<E>> IDao.IFmpLocalDao<E, S>.insertOrReplace(
    items: List<E>,
    notifyAll: Boolean = false
): S {
    return DaoInstance.insertOrReplace(this, items, notifyAll)
}

suspend inline fun <reified E : Any, reified S : StatusSelectTable<E>> IDao.IFmpLocalDao<E, S>.insertOrReplaceAsync(
    items: List<E>,
    notifyAll: Boolean = false
): S {
    return DaoInstance.insertOrReplaceAsync(this,items, notifyAll)
}