package pro.krit.fmpdaoexample.fmpresources

object Fields {
    // Идентификатор справочника
    const val LOCAL_ID = "ID"
    const val LOCAL_ID_KEY = "LOCAL_ID_primary"
    // Текст кода статуса задания
    const val STAT_TEXT = "STAT_TEXT"
    const val MARKER = "MARKER"
    const val AUART = "AUART"
    // Код следующего статуса
    const val TASK_STAT_NEXT = "TASK_STAT_NEXT"
    // Код следующего статуса Int
    const val TASK_STAT_NEXT_Int = "TASK_STAT_NEXT_Int"
    // Код статуса задания Int
    const val TASK_STAT_Int = "TASK_STAT_Int"
    // Код статуса задания String
    const val TASK_STAT = "TASK_STAT"
    //Текст кода следующего статуса задания
    const val STAT_TEXT_NEXT = "STAT_TEXT_NEXT"
    // Количество заданий на приемку
    const val TASKQNT = "TASKQNT"
    // Количество заданий на приемку
    const val TASKQNT_Int = "TASKQNT_Int"
    // Код ошибки
    const val RETCODE = "RETCODE"
    // Текст ошибки
    const val ERROR_TEXT = "ERROR_TEXT"
    // Номер ТТН
    const val TTN_NUM = "TTN_NUM"
    // Номер BE
    const val BE_NUM = "BE_NUM"
    // Наименование поставщика
    const val SUP_NAME = "SUP_NAME"
    // Номер транспортного средства
    const val CAR_NUM = "CAR_NUM"
    // Номер задания
    const val TASK_NUM = "TASK_NUM"
    // Статус блокировки Int:
    // 1 - Заблокировано текущим логином
    // 2 - Заблокировано не текущим логином
    const val STAT_BLOCK_Int = "STAT_BLOCK_Int"
    // Статус блокировки String:
    const val STAT_BLOCK = "STAT_BLOCK"
    // Логин сотрудника
    const val USER = "USER"
    // Логин блокирующий задание
    const val USER_BLOCK = "USER_BLOCK"
    // Дата поставки DATETIME
    const val DATE_DELIV = "DATE_DELIV"
    // Дата изменения статуса DATETIME
    const val DATE_CHANGE = "DATE_CHANGE"
    // Сотрудник изменивший статус
    const val USER_CHANGE = "USER_CHANGE"
    //
    const val LOCAL_DATE_MILLIS = "DATE_MILLIS"
    //
    const val LOCAL_DATE_DELIV = "LOCAL_DATE_DELIV"
    // SAP-код товара
    const val MATNR = "MATNR"
    // Наименование товара
    const val MATNR_NAME = "MATNR_NAME"
    // Базисная единица измерения товара
    const val BUOM = "BUOM"
    // Плановые количества
    const val PLAN_QNT = "PLAN_QNT"
    // ЕИЗ
    const val MEINS = "MEINS"
    const val INDEX = "INDEX"
    const val TYPE = "TYPE"
    const val IS_LOCAL = "IS_LOCAL"
    // Код причины дефекта
    const val SYMPTOM_CODE = "SYMPTOM_CODE"
    // Код группы симптома
    const val SYMPT_GRP_CODE = "SYMPT_GRP_CODE"
    // Наименование причины дефекта
    const val SYMPTOM_TEXT = "SYMPTOM_TEXT"
    const val RBNR_Int = "RBNR_Int"
    const val RBNR = "RBNR"
    // Складское место
    const val STOR_SPACE = "STOR_SPACE"
    // Номер ЕО
    const val EXIDV = "EXIDV"
    // Общий СГ
    const val MHDHB = "MHDHB"
    // Остаточный СГ
    const val MHDRZ = "MHDRZ"
    // Характеристики товара
    const val CHARACT = "CHARACT"
    // ШК товара
    const val EAN = "EAN"
    // Единица измерения
    const val UOM = "UOM"
    // Коэффициент пересчета в БЕИ
    const val CONV_QNT = "CONV_QNT"
    // Название параметра
    const val PARAMNAME = "PARAMNAME"
    // Значение параметра
    const val PARAMVALUE = "PARAMVALUE"
    // Идентификатор справочника
    const val TID = "TID"
    // Код значения справочника
    const val CODE = "CODE"
    // Порядок сортировки
    const val SORDER = "SORDER"
    // Краткое описание
    const val TEXT = "TEXT"
    const val TAB_NUM = "TAB_NUM"
}