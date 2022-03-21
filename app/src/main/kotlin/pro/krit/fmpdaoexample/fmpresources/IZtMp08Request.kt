package pro.krit.fmpdaoexample.fmpresources

import com.google.gson.annotations.SerializedName
import pro.krit.generated.request.ZtMp05RequestRespondStatus
import pro.krit.generated.request.ZtMp05RequestResultModel
import pro.krit.core.annotations.FmpRestRequest
import pro.krit.core.base.IRequest
import pro.krit.core.request.ObjectRawStatus

// Проводка поставки
@FmpRestRequest(
    resourceName = "zt_mp_088",
    parameters = [
        Parameters.IV_TASK_NUM,
        Parameters.IV_USER,
        Parameters.IV_VENDOR_LIST
    ]
)
interface IZtMp08Request : IRequest.IRestRequest<ZtMp05RequestResultModel, ZtMp05RequestRespondStatus> { //IRestRequest<ZtMp05RequestResultModel, ZtMp05RequestRespondStatus> {

    /*@FmpTable(
        name = "ET_DATA",
        fields = [
            Fields.RETCODE,
            Fields.ERROR_TEXT
        ]
    )
    private interface EtData*/
}

public data class ZtMp088RequestParams(
    @SerializedName("IV_TASK_NUM")
    public val ivTaskNum: String? = null,
    @SerializedName("IV_DATE_DELIV")
    public val ivDateDeliv: String? = null,
    @SerializedName("IV_USER")
    public val ivUser: String? = null
)

public data class ZtMp088RequestEtDataModel(
    @SerializedName("TASK_STAT")
    public val taskStat: Int? = null,
    @SerializedName("STAT_TEXT")
    public val statText: String? = null,
    @SerializedName("TASK_STAT_NEXT")
    public val taskStatNext: Int? = null,
    @SerializedName("STAT_TEXT_NEXT")
    public val statTextNext: String? = null,
    @SerializedName("TTN_NUM")
    public val ttnNum: String? = null,
    @SerializedName("DATE_DELIV")
    public val dateDeliv: String? = null,
    @SerializedName("BE_NUM")
    public val beNum: String? = null,
    @SerializedName("SUP_NAME")
    public val supName: String? = null,
    @SerializedName("CAR_NUM")
    public val carNum: String? = null,
    @SerializedName("RETCODE")
    public val retcode: String? = null,
    @SerializedName("ERROR_TEXT")
    public val errorText: String? = null,
    @SerializedName("USER_BLOCK")
    public val userBlock: String? = null
)

public data class ZtMp088RequestEtMatnrListModel(
    @SerializedName("MATNR")
    public val matnr: String? = null,
    @SerializedName("PLAN_QNT")
    public val planQnt: String? = null,
    @SerializedName("MEINS")
    public val meins: String? = null
)

public data class ZtMp088RequestEtLogModel(
    @SerializedName("TASK_STAT")
    public val taskStat: Int? = null,
    @SerializedName("STAT_TEXT")
    public val statText: String? = null,
    @SerializedName("DATE_CHANGE")
    public val dateChange: String? = null,
    @SerializedName("USER_CHANGE")
    public val userChange: String? = null
)

public data class ZtMp088RequestResultModel(
    @SerializedName("ET_DATA")
    public val etData: ZtMp088RequestEtDataModel? = null,
    @SerializedName("ET_MATNR_LIST")
    public val etMatnrList: List<ZtMp088RequestEtMatnrListModel>? = null,
    @SerializedName("ET_LOG")
    public val etLog: List<ZtMp088RequestEtLogModel>? = null
)

public class ZtMp088RequestRespondStatus : ObjectRawStatus<ZtMp088RequestResultModel>()
