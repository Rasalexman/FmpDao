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
inline fun <reified E : Any, reified S : StatusSelectTable<E>> IDao.IFieldsDao.flowable(
    builderBlock: FlowableConfig.() -> Unit = {}
) = DaoInstance.flowable<E, S>(dao = this, config = FlowableConfig().apply(builderBlock))

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
 *
 * @return - список результатов запроса обернутый в [Result]
 */
inline fun <reified E : Any, reified S : StatusSelectTable<E>> IDao.IFieldsDao.selectResult(
    where: String = "",
    limit: Int = 0,
    offset: Int = 0,
    orderBy: String = "",
    fields: List<String>? = null
): Result<List<E>> {
    return DaoInstance.selectResult<E, S>(this, where, limit, offset, orderBy, fields)
}

///------ INSERT DATA
/**
 * Удаление данных из таблицы по запросу
 *
 * @param item - тело запроса для SELECT
 * @param notifyAll - тригер на одновление всех значений
 *
 * @return - возвращает статус удаленной записи из таблицы
 */
inline fun <reified E : Any> IDao.IFieldsDao.insertOrReplace(
    item: E,
    notifyAll: Boolean = false
): StatusSelectTable<E> {
    return DaoInstance.insertOrReplace(this, item, notifyAll)
}

inline fun <reified E : Any> IDao.IFieldsDao.insertOrReplace(
    items: List<E>,
    notifyAll: Boolean = false,
    withoutPrimaryKey: Boolean = false
): StatusSelectTable<E> {
    return DaoInstance.insertOrReplace(this, items, notifyAll, withoutPrimaryKey)
}

///---- DELETE
inline fun <reified E : Any> IDao.IFieldsDao.delete(
    where: String = "",
    notifyAll: Boolean = false
): StatusSelectTable<E> {
    return DaoInstance.delete(this, where, notifyAll)
}

inline fun <reified E : Any> IDao.IFieldsDao.delete(
    item: E,
    notifyAll: Boolean = false
): StatusSelectTable<E> {
    return DaoInstance.delete(this, item, notifyAll)
}

inline fun <reified E : Any> IDao.IFieldsDao.delete(
    items: List<E>,
    notifyAll: Boolean = false
): StatusSelectTable<E> {
    return DaoInstance.delete(this, items, notifyAll)
}