package pro.krit.fmpdaoexample

import pro.krit.hiveprocessor.annotations.FmpRestRequest
import pro.krit.hiveprocessor.annotations.FmpTable
import pro.krit.hiveprocessor.base.IRequest


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