package pro.krit.hiveprocessor.request

import com.google.gson.annotations.SerializedName

open class BaseFmpRawModel<T> {
    @SerializedName("StatusCode")
    var statusCode: Int? = null
    @SerializedName("Version")
    var version: String? = null
    @SerializedName("error")
    var errorMessage: String? = null
    @SerializedName("error_description")
    var errorDescription: String? = null
    @SerializedName("detail")
    var detail: String? = null
    @SerializedName("Data")
    val data: T? = null
}