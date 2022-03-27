package pro.krit.fmpdaoexample.fmpresources

import pro.krit.hhivecore.annotations.FmpRestRequest
import pro.krit.hhivecore.annotations.FmpTable
import pro.krit.hhivecore.base.IRequest

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