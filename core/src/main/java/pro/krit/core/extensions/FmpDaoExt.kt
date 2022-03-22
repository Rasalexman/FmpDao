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

package pro.krit.core.extensions
import com.mobrun.plugin.models.StatusSelectTable
import pro.krit.core.base.IDao
import pro.krit.core.common.FlowableConfig

/**
 * Создает Flow, который эмитит данные при подписке
 *
 * @param builderBlock - конструктор запроса
 *
 * @return - Flow<List<E>> поток данных из базы данных
 */
inline fun <reified E : Any, reified S : StatusSelectTable<E>> IDao.IFmpDao<E, S>.flowable(
    builderBlock: FlowableConfig.() -> Unit = {}
) = DaoInstance.flowable<E, S>(dao = this, config = FlowableConfig().apply(builderBlock))

/**
 * Создает SQL-запрос на поиск данных в таблице справочника, и возвращает список результатов
 *
 * @param where - тело запроса для SELECT, если пустой то выбирает все данные (SELECT *)
 * @param limit - лимитированное количество данных
 * @param offset - отступ в получении данных
 * @param orderBy - сортировка результатов запроса, необходимо так же указывать ASC|DESC
 * @param fields - возвращаеммые поля, если не заполнен то возвращаются все поля
 */
inline fun <reified E : Any, reified S : StatusSelectTable<E>> IDao.IFmpDao<E, S>.select(
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
 */
inline fun <reified E : Any, reified S : StatusSelectTable<E>> IDao.IFmpDao<E, S>.selectResult(
    where: String = "",
    limit: Int = 0,
    offset: Int = 0,
    orderBy: String = "",
    fields: List<String>? = null
): Result<List<E>> {
    return DaoInstance.selectResult<E, S>(this, where, limit, offset, orderBy, fields)
}

///------- CREATE TABLE
/**
 * Создание таблицы данных
 */
inline fun <reified E : Any> IDao.createTable(): StatusSelectTable<E> {
    return DaoInstance.createTable(this)
}
