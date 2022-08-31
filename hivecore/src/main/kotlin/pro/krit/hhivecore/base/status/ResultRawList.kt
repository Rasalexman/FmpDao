package pro.krit.hhivecore.base.status

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

open class ResultRawList<RawType> {
    @SerializedName("raw")
    @Expose
    var raw = emptyList<RawType>()

    @SerializedName("file")
    @Expose
    var file: String? = null

    override fun toString(): String {
        return "Result{raw=$raw, file='$file'}"
    }
}