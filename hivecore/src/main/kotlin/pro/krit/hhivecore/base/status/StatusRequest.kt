package pro.krit.hhivecore.base.status

import com.google.gson.annotations.SerializedName

enum class StatusRequest {
    @SerializedName("ok")
    OK,

    @SerializedName("cancel")
    CANCEL,

    @SerializedName("error")
    ERROR
}