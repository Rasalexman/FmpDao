package pro.krit.fmpdaoexample.fmpresources

object Parameters {
    private const val IV = "IV_"

    // Номер задания
    const val IV_TASK_NUM = IV + Fields.TASK_NUM
    // Логин сотрудника
    const val IV_USER = "IV_USER"
    // Дата поставки DATETIME
    const val IV_DATE_DELIV = IV + Fields.DATE_DELIV
    // Предприятие INT
    const val IV_WERKS = "IV_WERKS"
    // Номер СФ
    const val IV_INVOICE_NUM = "IV_INVOICE_NUM"
    // Номер БЕ
    const val IV_BE_NUM = IV + Fields.BE_NUM
    // Наименование поставщика
    const val IV_VENDOR = "IV_VENDOR"
    // лист объектов вендор List<Any>
    const val IV_VENDOR_LIST = "IV_VENDOR_LIST"
    // Номер ТТН
    const val IV_TTN_NUM = IV + Fields.TTN_NUM
    //Номер транспортного средства
    const val IV_CAR_NUM = IV + Fields.CAR_NUM
    // Номер МТР
    const val IV_MATNR = IV + Fields.MATNR
    // Дата начала периода DATETIME
    const val IV_DATE_FROM = "IV_DATE_FROM"
    // Дата окончания периода
    const val IV_DATE_TO = "IV_DATE_TO"
    // Статус задания INT
    const val IV_TASK_STAT = IV + Fields.TASK_STAT
    // Склад INT
    const val IV_LGORT = "IV_LGORT"
}