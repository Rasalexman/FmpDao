package pro.krit.fmpdaoexample

import com.google.gson.annotations.SerializedName
import com.mobrun.plugin.api.request_assistant.PrimaryKey

data class PmEtDataLocalEntity(
    @JvmField
    @PrimaryKey
    @SerializedName("ID")
    var id: String? = null,

    //  type: TEXT, source: {'name': 'SAP', 'type': 'C'}
    @JvmField
    @SerializedName("MARKER")
    var marker: String? = null,

    //  type: TEXT, source: {'name': 'SAP', 'type': 'C'}
    @JvmField
    @SerializedName("AUART")
    var auart: String? = null,

    @JvmField
    @SerializedName("QWERTY")
    var index: Int? = null,

    @JvmField
    @SerializedName("TYPE")
    var type: PmType? = null
)

enum class PmType {
    USER, ADMIN
}
