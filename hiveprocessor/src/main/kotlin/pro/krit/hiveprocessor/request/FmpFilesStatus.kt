package pro.krit.hiveprocessor.request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class FmpFilesStatus : ObjectRawStatus<FmpFilesRaw?>()

data class FmpFilesRaw(
        @Expose
        @SerializedName("files")
        val files: List<String>?
)