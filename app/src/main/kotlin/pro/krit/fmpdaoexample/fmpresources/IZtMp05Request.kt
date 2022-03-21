package pro.krit.fmpdaoexample.fmpresources

import pro.krit.core.annotations.FmpRestRequest
import pro.krit.core.annotations.FmpTable
import pro.krit.core.base.IRequest

// Фиксация прибытия
@FmpRestRequest(
    resourceName = "zt_mp_05",
    parameters = [
        Parameters.IV_TASK_NUM,  // Предприятие
        Parameters.IV_DATE_DELIV,  // Дата поставки DATETIME
        Parameters.IV_USER   // Логин сотрудника
    ]
)
interface IZtMp05Request : IRequest.IBaseRequest {

    // Таблица для объединения скалярных значений
    @FmpTable(
        name = "ET_DATA",
        fields = [
            Fields.TASK_STAT_Int, Fields.STAT_TEXT, Fields.TASK_STAT_NEXT_Int, Fields.STAT_TEXT_NEXT,
            Fields.TTN_NUM, Fields.DATE_DELIV, Fields.BE_NUM, Fields.SUP_NAME,
            Fields.CAR_NUM, Fields.RETCODE, Fields.ERROR_TEXT, Fields.USER_BLOCK
        ]
    )
    private interface EtData

    // Список МТР
    @FmpTable(
        name = "ET_MATNR_LIST",
        fields = [
            Fields.MATNR, Fields.PLAN_QNT, Fields.MEINS
        ],
        isList = true
    )
    private interface EtMatnrList

    @FmpTable(
        name = "ET_LOG",
        fields = [
            Fields.TASK_STAT_Int, Fields.STAT_TEXT,
            Fields.DATE_CHANGE, Fields.USER_CHANGE
        ],
        isList = true
    )
    private interface EtLog
}