package pro.krit.hhivecore.extensions

import pro.krit.hhivecore.base.status.StatusSelectTable
import pro.krit.hhivecore.base.IDao
import pro.krit.hhivecore.common.QueryConfig

/**
 * Создает Flow, который эмитит данные при подписке
 *
 * @param builderBlock - конструктор запроса
 *
 * @return - Flow<List<E>> поток данных из базы данных
 */
inline fun <reified E : Any, reified S : StatusSelectTable<E>> IDao.IFieldsDao.flowable(
    builderBlock: QueryConfig.() -> Unit = {}
) = DaoInstance.flowable<E, S>(dao = this, config = QueryConfig().apply(builderBlock))

/**
 * Создает Observable, который эмитит данные при триггере
 */
inline fun <reified E : Any, reified S : StatusSelectTable<E>> IDao.IFieldsDao.observable(
    builderBlock: QueryConfig.() -> Unit = {}
) = DaoInstance.observable<E, S>(dao = this, config = QueryConfig().apply(builderBlock))

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
    builderBlock: QueryConfig.() -> Unit = {}
): List<E> = QueryConfig().apply(builderBlock).run {
    DaoInstance.select<E, S>(this@select, where, limit, offset, orderBy, fields)
}

/**
 * Создает SQL-запрос на поиск данных в таблице справочника, и возвращает [Result]
 *
 * @param where - тело запроса для SELECT
 * @param offset - отступ в получении данных
 * @param orderBy - сортировка результатов запроса, необходимо так же указывать ASC|DESC
 * @param fields - возвращаеммые поля, если не заполнен то возвращаются все поля
 *
 * @return - E? с данными, либо пустой список
 */
inline fun <reified E : Any, reified S : StatusSelectTable<E>> IDao.IFieldsDao.selectFirst(
    builderBlock: QueryConfig.() -> Unit = {}
): E? = QueryConfig().apply(builderBlock).run {
    DaoInstance.select<E, S>(this@selectFirst, where, 1, offset, orderBy, fields)
}.firstOrNull()

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
    builderBlock: QueryConfig.() -> Unit = {}
): Result<List<E>> {
    return QueryConfig().apply(builderBlock).run {
        DaoInstance.selectResult<E, S>(this@selectResult, where, limit, offset, orderBy, fields)
    }
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