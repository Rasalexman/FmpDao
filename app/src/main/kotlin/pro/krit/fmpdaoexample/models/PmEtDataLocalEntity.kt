package com.rasalexman.fmpdaoexample.models

import com.google.gson.annotations.SerializedName
import com.mobrun.plugin.api.request_assistant.PrimaryKey
import com.rasalexman.sresult.common.extensions.orZero
import com.rasalexman.sresult.models.IConvertableTo
import com.rasalexman.fmpdaoexample.fmpresources.Fields.AUART
import com.rasalexman.fmpdaoexample.fmpresources.Fields.IS_LOCAL
import com.rasalexman.fmpdaoexample.fmpresources.Fields.LOCAL_ID
import com.rasalexman.fmpdaoexample.fmpresources.Fields.MARKER
import com.rasalexman.fmpdaoexample.fmpresources.Fields.TASK_NUM
import com.rasalexman.fmpdaoexample.fmpresources.Fields.TYPE

data class PmEtDataLocalEntity(
    @JvmField
    @PrimaryKey
    @SerializedName(LOCAL_ID)
    var id: String? = null,

    //  type: TEXT, source: {'name': 'SAP', 'type': 'C'}
    @JvmField
    @SerializedName(MARKER)
    var marker: String? = null,

    //  type: TEXT, source: {'name': 'SAP', 'type': 'C'}
    @JvmField
    @SerializedName(AUART)
    var auart: String? = null,

    @JvmField
    @SerializedName(TASK_NUM)
    var taskNum: Int? = null,

    @JvmField
    @SerializedName(IS_LOCAL)
    var isLocal: Boolean? = false,

    @JvmField
    @SerializedName(TYPE)
    var type: PmType? = null
) : IConvertableTo<PmDataUI> {
    override fun convertTo(): PmDataUI? {
        return id?.takeIf { it.isNotEmpty() }?.run {
            PmDataUI(
                id = id.orEmpty(),
                marker = marker.orEmpty(),
                auart = auart.orEmpty(),
                index = taskNum.orZero(),
                type = type ?: PmType.USER
            )
        }
    }

}

enum class PmType {
    USER, ADMIN
}
