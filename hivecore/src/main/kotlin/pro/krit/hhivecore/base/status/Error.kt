package pro.krit.hhivecore.base.status

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Error {
    @SerializedName("code")
    @Expose
    var code: Int? = null

    @SerializedName("source")
    @Expose
    var source: String? = null

    @SerializedName("description")
    @Expose
    var description: String = ""

    @SerializedName("descriptions")
    @Expose
    var descriptions: List<String> = emptyList()

    override fun toString(): String {
        return "Error{code=" + this.code + ", source='" + source + '\'' + ", description='" + description + '\'' + ", descriptions=" + descriptions + '}'
    }
}