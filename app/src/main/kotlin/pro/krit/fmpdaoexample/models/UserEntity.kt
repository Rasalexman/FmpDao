package com.rasalexman.fmpdaoexample.models

import com.google.gson.annotations.SerializedName
import com.mobrun.plugin.api.request_assistant.PrimaryKey
import com.rasalexman.sresult.common.extensions.orZero
import com.rasalexman.sresult.common.extensions.toStringOrEmpty
import com.rasalexman.sresult.models.IConvertableTo
import com.rasalexman.fmpdaoexample.fmpresources.Fields

data class UserEntity(
    @JvmField
    @PrimaryKey
    @SerializedName(Fields.LOCAL_ID)
    var id: String? = null,

    //  type: TEXT, source: {'name': 'SAP', 'type': 'C'}
    @JvmField
    @SerializedName(Fields.MARKER)
    var marker: PmType? = null,

    //  type: TEXT, source: {'name': 'SAP', 'type': 'C'}
    @JvmField
    @SerializedName(Fields.USER)
    var userName: String? = null,

    @JvmField
    @SerializedName(Fields.TAB_NUM)
    var tabNumber: Int? = null,

    @JvmField
    @SerializedName(Fields.SYMPTOM_CODE)
    var symptomCode: String? = null,

    @JvmField
    @SerializedName(Fields.RBNR)
    var rbnr: Int? = null,
) : IConvertableTo<UserItemUI> {
    override fun convertTo(): UserItemUI {
        return UserItemUI(
            id = id.orEmpty(),
            marker = marker ?: PmType.ADMIN,
            userName = userName.orEmpty(),
            tabNumber = tabNumber.orZero().toStringOrEmpty(),
            rbnr = rbnr.orZero().toStringOrEmpty(),
            symptomCode = symptomCode.orEmpty()
        )
    }

}
