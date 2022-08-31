package pro.krit.hhivecore.base.status

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class TableRawData {
    @SerializedName("name")
    @Expose
    var name: String? = null

    @SerializedName("data")
    @Expose
    var data: List<List<String>>? = null
}