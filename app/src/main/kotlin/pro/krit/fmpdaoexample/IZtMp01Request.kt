package pro.krit.fmpdaoexample

import pro.krit.hiveprocessor.annotations.FmpTable
import pro.krit.hiveprocessor.annotations.FmpWebRequest
import pro.krit.hiveprocessor.base.IRequest


@FmpWebRequest(
    resourceName = "zt_mp_01"
)
interface IZtMp01Request : IRequest.IBaseRequest {

    @FmpTable(
        name = "ET_DATA",
        fields = [
            "IV_WERKS",  // Предприятие INT
            "IV_LGORT_Int",  // Склад INT
            "IV_USER"   // Логин пользователя STRING
        ]
    )
    private interface EtData

    @FmpTable(
        name = "ET_TASK_LIST",
        fields = [
            "TASK_NUM", "TASK_STAT", "USER_BLOCK", "STAT_TEXT",
            "TTN_NUM", "BE_NUM", "STAT_BLOCK", "SUP_NAME", "CAR_NUM"
        ]
    )
    private interface EtTaskList
}