package com.rasalexman.fmpdaoexample.fmpresources

import com.rasalexman.hhivecore.annotations.FmpParam
import com.rasalexman.hhivecore.annotations.FmpRestRequest
import com.rasalexman.hhivecore.annotations.FmpTable
import com.rasalexman.hhivecore.base.IRequest


@FmpRestRequest(
    resourceName = "zt_mp_02",
    parameters = ["IV_LGORT", "IV_WERKS"]
)
interface SecondZtMp01Request : IRequest.IBaseRequest {


    @FmpParam(
        name = "IV_LGORT",
        fields = [
            "IV_WERKS",  // Предприятие INT
            "IV_LGORT",  // Склад INT
            "IV_USER"   // Логин пользователя STRING
        ],
        isList = true
    )
    private interface IvLgort

    @FmpTable(
        name = "ET_DATA",
        fields = [
            "IV_WERKS",  // Предприятие INT
            "IV_LGORT",  // Склад INT
            "IV_USER"   // Логин пользователя STRING
        ]
    )
    private interface EtData

    @FmpTable(
        name = "ET_TASK_LIST",
        fields = [
            "TASK_NUM", "TASK_STAT", "USER_BLOCK", "STAT_TEXT",
            "TTN_NUM", "BE_NUM", "STAT_BLOCK", "SUP_NAME", "CAR_NUM"
        ],
        isList = true,
        isNumeric = true
    )
    private interface EtTaskList
}