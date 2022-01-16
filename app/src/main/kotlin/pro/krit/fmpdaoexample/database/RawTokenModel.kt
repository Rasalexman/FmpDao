package pro.krit.fmpdaoexample.database

import com.google.gson.annotations.SerializedName

data class RawTokenModel(
    @SerializedName("raw")
    val raw: TokenModel?
)