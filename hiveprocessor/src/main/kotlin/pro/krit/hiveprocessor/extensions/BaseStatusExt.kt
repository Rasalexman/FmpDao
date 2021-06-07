package pro.krit.hiveprocessor.extensions

import com.mobrun.plugin.models.BaseStatus
import com.mobrun.plugin.models.Error
import pro.krit.hiveprocessor.errors.RequestException

fun BaseStatus.getFailure(): RequestException {
    //TODO need improve detection failure
    this.errors?.forEach {
        when (it.source) {
            "Core" -> return it.getServerFailure()
            "cURL" -> return RequestException.NetworkConnection
            "Server" -> return it.getServerFailure()
            "SQLite" -> return it.getDbFailure()
        }
    }

    return RequestException.ActivityNullError(this.errors?.firstOrNull()?.descriptions?.firstOrNull()?.takeIf { it != "unexpected http status" }.orEmpty())
}

fun Error.getServerFailure(): RequestException {
    if (this.code != null && this.code == 401) {
        return RequestException.AuthError
    }
    return RequestException.ServerError(this.descriptions?.firstOrNull().orEmpty())
}

fun Error.getDbFailure(): RequestException {
    return RequestException.DbError("${this.descriptions}")
}

fun BaseStatus.isNotBad(): Boolean {
    return this.isOk || this.httpStatus?.status == 304
}

fun BaseStatus.toStringSafety(): String {
    return "BaseStatusV08{status=" + this.status + ", httpStatus=" + this.httpStatus + ", errors=" + this.errors?.map { it.toStringSafety() } + ", retryCount=" + this.retryCount + '}'.toString()
}

fun Error.toStringSafety(): String {
    return "Error{code=" + this.code + ", source='" + this.source + '\''.toString() + ", description='" + this.description + '\''.toString() + ", descriptions=" + this.descriptions + '}'.toString()
}