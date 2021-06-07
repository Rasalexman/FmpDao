package pro.krit.hiveprocessor.errors

sealed class RequestException(message: String = "") : Exception(message) {
    class ActivityNullError(message: String = "") : RequestException(message)
    class DbError(message: String = "") : RequestException(message)
    class SapError(message: String) : RequestException(message)

    class ServerError(message: String = "") : RequestException(message)

    object StatusResultNullError : RequestException("StatusResultNullError")
    object ResultRawNullError : RequestException("ResultRawNullError")
    object RawDataNullError : RequestException("RawDataNullError")
    object WebCallError : RequestException("WebCallError")

    object NoInternetConnectionException : RequestException("NoInternetConnectionException")
    object NetworkConnection : RequestException("NetworkConnection")
    object AuthError : RequestException("AuthError")
}
