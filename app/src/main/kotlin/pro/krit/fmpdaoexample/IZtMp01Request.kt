package pro.krit.fmpdaoexample

import pro.krit.hiveprocessor.annotations.FmpTable
import pro.krit.hiveprocessor.annotations.FmpWebRequest
import pro.krit.hiveprocessor.base.IRequest


@FmpWebRequest(
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