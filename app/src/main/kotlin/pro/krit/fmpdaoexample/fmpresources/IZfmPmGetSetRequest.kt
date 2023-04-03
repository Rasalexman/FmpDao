package com.rasalexman.fmpdaoexample.fmpresources

import com.rasalexman.hhivecore.annotations.FmpRestRequest
import com.rasalexman.hhivecore.annotations.FmpTable
import com.rasalexman.hhivecore.base.IRequest

//Получение настроек пользователя
@FmpRestRequest(
    resourceName = "ZFM_PM_GET_SET",
    parameters = [
        Parameters.IV_USER
    ]
)
interface IZfmPmGetSetRequest : IRequest.IBaseRequest {

    @FmpTable(
        name = "ET_AUTH_GR",
        fields = [
            "BEGRU"
        ],
        isNumeric = true
    )
    private interface EtAuthGr

    @FmpTable(
        name = "ET_ERROR",
        fields = [
            "RETCODE",
            "ERROR_TEXT"
        ],
        isNumeric = true
    )
    private interface EtError

    @FmpTable(
        name = "ET_AUTH_USER",
        fields = [
            "PERNR", "FIO", "WERKS", "PLANS_TEXT"
        ],
        isNumeric = true
    )
    private interface EtAuthUser

    @FmpTable(
        name = "ET_PARAM",
        fields = [
            "PARAM_NAME", "PARAM_VALUE"
        ],
        isList = true,
        isNumeric = true
    )
    private interface EtParams

}