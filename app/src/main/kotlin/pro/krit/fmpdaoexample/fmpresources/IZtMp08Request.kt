package pro.krit.fmpdaoexample.fmpresources

import pro.krit.core.annotations.FmpRestRequest
import pro.krit.core.annotations.FmpTable
import pro.krit.core.base.IRequest

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
            Fields.RETCODE,
            Fields.ERROR_TEXT
        ]
    )
    private interface EtData
}