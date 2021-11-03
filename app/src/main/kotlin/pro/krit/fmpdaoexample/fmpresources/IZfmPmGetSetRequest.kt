package pro.krit.fmpdaoexample.fmpresources

import pro.krit.hiveprocessor.annotations.FmpParam
import pro.krit.hiveprocessor.annotations.FmpRestRequest
import pro.krit.hiveprocessor.annotations.FmpTable
import pro.krit.hiveprocessor.base.IRequest

//Получение настроек пользователя
@FmpRestRequest(
    resourceName = "ZFM_PM_GET_SET",
    parameters = [
        Parameters.IV_USER,  // Login
        Parameters.IV_MATNR
    ]
)
interface IZfmPmGetSetRequest : IRequest.IBaseRequest {

    @FmpParam(
        name = Parameters.IV_MATNR,
        fields = [
            "ET_AUTH_GR", "ET_ERROR", "ET_AUTH_USER"
        ],
        isNumeric = true
    )
    private interface IvMatnr

    @FmpTable(
        name = "ET_DATA",
        fields = [
            "ET_AUTH_GR", "ET_ERROR", "ET_AUTH_USER"
        ],
        isNumeric = true
    )
    private interface EtData

}