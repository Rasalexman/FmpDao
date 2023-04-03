package com.rasalexman.fmpdaoexample.database

import com.google.gson.annotations.SerializedName

data class TokenModel(
    @SerializedName("token")
    val token: String?,
    @SerializedName("code")
    val code: Int?,
    @SerializedName("description")
    val description: String?
)