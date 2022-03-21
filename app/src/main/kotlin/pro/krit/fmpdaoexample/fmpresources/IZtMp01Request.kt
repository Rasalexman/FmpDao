package pro.krit.fmpdaoexample.fmpresources

import pro.krit.core.annotations.FmpRestRequest
import pro.krit.core.annotations.FmpTable
import pro.krit.core.base.IRequest


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