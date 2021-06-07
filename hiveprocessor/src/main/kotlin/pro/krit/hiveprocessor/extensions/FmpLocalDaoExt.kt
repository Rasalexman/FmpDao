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
import pro.krit.hiveprocessor.base.IDao.IFmpLocalDao

inline fun <reified E : Any, reified S : StatusSelectTable<E>> IFmpLocalDao<E, S>.createTable(): StatusSelectTable<E> {
    return createTable<E>()
}

suspend inline fun <reified E : Any, reified S : StatusSelectTable<E>> IFmpLocalDao<E, S>.createTableAsync(): StatusSelectTable<E> {
    return createTableAsync<E>()
}

inline fun <reified E : Any, reified S : StatusSelectTable<E>> IFmpLocalDao<E, S>.insertOrReplace(
    item: E,
    notifyAll: Boolean = false
): StatusSelectTable<E> {
    return insertOrReplace<E>(item, notifyAll)
}

suspend inline fun <reified E : Any, reified S : StatusSelectTable<E>> IFmpLocalDao<E, S>.insertOrReplaceAsync(
    item: E,
    notifyAll: Boolean = false
): StatusSelectTable<E> {
    return insertOrReplaceAsync<E>(item, notifyAll)
}

inline fun <reified E : Any, reified S : StatusSelectTable<E>> IFmpLocalDao<E, S>.insertOrReplace(
    items: List<E>,
    notifyAll: Boolean = false
): StatusSelectTable<E> {
    return insertOrReplace<E>(items, notifyAll)
}

suspend inline fun <reified E : Any, reified S : StatusSelectTable<E>> IFmpLocalDao<E, S>.insertOrReplaceAsync(
    items: List<E>,
    notifyAll: Boolean = false
): StatusSelectTable<E> {
    return insertOrReplaceAsync<E>(items, notifyAll)
}

inline fun <reified E : Any, reified S : StatusSelectTable<E>> IFmpLocalDao<E, S>.delete(
    item: E,
    notifyAll: Boolean = true
): StatusSelectTable<E> {
    return delete<E>(item, notifyAll)
}

suspend inline fun <reified E : Any, reified S : StatusSelectTable<E>> IFmpLocalDao<E, S>.deleteAsync(
    item: E,
    notifyAll: Boolean = true
): StatusSelectTable<E> {
    return deleteAsync<E>(item, notifyAll)
}

inline fun <reified E : Any, reified S : StatusSelectTable<E>> IFmpLocalDao<E, S>.delete(
    items: List<E>,
    notifyAll: Boolean = true
): StatusSelectTable<E> {
    return delete<E>(items, notifyAll)
}

suspend inline fun <reified E : Any, reified S : StatusSelectTable<E>> IFmpLocalDao<E, S>.deleteAsync(
    items: List<E>,
    notifyAll: Boolean = true
): StatusSelectTable<E> {
    return deleteAsync<E>(items, notifyAll)
}