package pro.krit.hhivecore.base.status

import com.google.gson.annotations.SerializedName

class HttpStatus {
    @SerializedName("status")
    var status: Int? = null

    @SerializedName("bytesDownloaded")
    var bytesDownloaded: Int? = null

    @SerializedName("bytesUploaded")
    var bytesUploaded: Int? = null

    @SerializedName("headers")
    var headers: Map<String, String>? = null

    override fun toString(): String {
        return "HttpStatus{status=$status, bytesDownloaded=$bytesDownloaded, bytesUploaded=$bytesUploaded, headers=$headers}"
    }
}