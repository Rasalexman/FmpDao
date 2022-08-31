package pro.krit.hhivecore.base.status

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Database<RecordType> {
    @SerializedName("name")
    @Expose
    var name: String? = null

    @SerializedName("records")
    @Expose
    var records: List<RecordType> = ArrayList()

    override fun toString(): String {
        return "Database{name='$name', records=$records}"
    }
}