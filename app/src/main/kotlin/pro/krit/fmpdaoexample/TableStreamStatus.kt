package pro.krit.fmpdaoexample

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.mobrun.plugin.models.BaseStatus

data class TableStreamStatus(
    @Expose
    @SerializedName("request")
    var request: Request? = null
) : BaseStatus()

data class Request (
    @Expose
    @SerializedName("source")
    var source: Source? = null
)

data class Source (
    @Expose
    @SerializedName("database")
    var database: Database? = null
)

data class Database(
    @Expose
    @SerializedName("name")
    var name: String? = null,
    @Expose
    @SerializedName("statements")
    var statements: List<String>
)