package pro.krit.fmpdaoexample.models

import com.google.gson.annotations.SerializedName
import com.mobrun.plugin.api.request_assistant.PrimaryKey

data class ZtMp08Entity(
    @SerializedName("LOCAL_ID")
    @JvmField
    @PrimaryKey var localId: String? = null,
    @SerializedName("RETCODE")
    @JvmField var retcode: String? = null,
    @SerializedName("ERROR_TEXT")
    @JvmField var errorText: String? = null,
)
