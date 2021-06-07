package pro.krit.hiveprocessor.request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.mobrun.plugin.models.BaseStatus

open class ObjectRawStatus<T> : BaseStatus() {
    @Expose
    @SerializedName("result")
    val result: ResultObject<T>? = null

    override fun toString(): String {
        return "${super.toString()}\n${result?.raw}"
    }
}

open class ResultObject<T>(
    val raw: T?
)