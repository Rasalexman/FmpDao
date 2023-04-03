package com.rasalexman.fmpdaoexample.fmpresources

import com.rasalexman.hhivecore.annotations.FmpRestRequest
import com.rasalexman.hhivecore.annotations.FmpTable
import com.rasalexman.hhivecore.base.IRequest

// Проводка поставки
@FmpRestRequest(
    resourceName = "zt_mp_088",
    parameters = [
        Parameters.IV_TASK_NUM,
        Parameters.IV_USER,
        Parameters.IV_VENDOR_LIST
    ]
)
interface IZtMp08Request : IRequest.IBaseRequest { //IRestRequest<ZtMp05RequestResultModel, ZtMp05RequestRespondStatus> {

    @FmpTable(
        name = "ET_DATA",
        fields = [
            Fields.LOCAL_ID_KEY,
            Fields.RETCODE,
            Fields.ERROR_TEXT
        ],
        isNumeric = true
    )
    private interface EtData
}