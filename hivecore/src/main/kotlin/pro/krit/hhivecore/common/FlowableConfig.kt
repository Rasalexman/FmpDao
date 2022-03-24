package pro.krit.hhivecore.common


/**
 * @property where - тело запроса для SELECT, если пустой то выбирает все данные (SELECT *)
 * @property limit - лимитированное количество данных
 * @property offset - отступ в получении данных
 * @property orderBy - сортировка результатов запроса, необходимо так же указывать ASC|DESC
 * @property withStart - начать эмитить данные при создании потока
 * @property emitDelay - задержка при эмитинге данных
 * @property withDistinct - использовать эмитинг только уникальных данных
 * @property fields - возвращаеммые поля, ессли не заполнен то возвращаются все поля
 * @property isDevastate - тип триггера
 */
class FlowableConfig {
    var where: String = ""
    var limit: Int = 0
    var offset: Int = 0
    var orderBy: String = ""
    var withStart: Boolean = true
    var emitDelay: Long = 0L
    var withDistinct: Boolean = false
    var fields: List<String>? = null
    var isDevastate: Boolean = false
}