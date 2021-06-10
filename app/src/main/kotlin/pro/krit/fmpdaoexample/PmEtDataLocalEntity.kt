package pro.krit.fmpdaoexample

import com.google.gson.annotations.SerializedName
import com.mobrun.plugin.api.request_assistant.PrimaryKey
import com.rasalexman.sresult.common.extensions.orZero
import com.rasalexman.sresult.models.IConvertableTo

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
) : IConvertableTo<PmDataUI> {
    override fun convertTo(): PmDataUI? {
        return id?.takeIf { it.isNotEmpty() }?.run {
            PmDataUI(
                id = id.orEmpty(),
                marker = marker.orEmpty(),
                auart = auart.orEmpty(),
                index = index.orZero(),
                type = type ?: PmType.USER
            )
        }
    }

}

enum class PmType {
    USER, ADMIN
}
