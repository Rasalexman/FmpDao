package pro.krit.hhivecore.base.status

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

open class BaseStatus {
    @Expose
    @SerializedName("status")
    var status: StatusRequest? = null

    @Expose
    @SerializedName("http")
    var httpStatus: HttpStatus? = null

    @Expose
    @SerializedName("errors")
    var errors: ArrayList<Error> = ArrayList()

    @Expose
    @SerializedName("retryCount")
    var retryCount: Int? = null
    var raw: String? = null

    fun setBaseStatus(baseStatus: BaseStatus) {
        status = baseStatus.status
        httpStatus = baseStatus.httpStatus
        retryCount = baseStatus.retryCount
        raw = baseStatus.raw
    }

    val isOk: Boolean
        get() = status != null && status == StatusRequest.OK
}