package com.rasalexman.fmpdaoexample.fmpresources

import com.rasalexman.hhivecore.annotations.FmpRestRequest
import com.rasalexman.hhivecore.annotations.FmpTable
import com.rasalexman.hhivecore.base.IRequest


@FmpRestRequest(
    resourceName = "zt_mp_01",
    parameters = ["IV_LGORT", "IV_WERKS"]
)
interface IZtMp01Request : IRequest.IBaseRequest {

    @FmpTable(
        name = "ET_DATA",
        fields = [
            "TASKQNT",
            "RETCODE",
            "ERROR_TEXT"
        ]
    )
    private interface EtData
}