package pro.krit.hiveprocessor.common

import com.google.gson.GsonBuilder
import com.mobrun.plugin.api.callparams.RequestCallParams
import com.mobrun.plugin.api.callparams.WebCallParams
import com.mobrun.plugin.models.BaseStatus
import pro.krit.hiveprocessor.base.IRequest
import pro.krit.hiveprocessor.errors.RequestException
import pro.krit.hiveprocessor.request.BaseFmpRawModel
import pro.krit.hiveprocessor.request.ObjectRawStatus

object RequestExecuter {

    fun execute(
        request: IRequest,
        params: Any? = null
    ): String {
        val requestApi = request.hyperHive.requestAPI
        val resourceName = request.resourceName
        return when (params) {
            is WebCallParams -> requestApi.web(resourceName, params).execute()
            is RequestCallParams -> requestApi.request(resourceName, params).execute()
            else -> requestApi.web(resourceName).execute()
        }
    }

    inline fun<reified S : Any, reified T : ObjectRawStatus<out BaseFmpRawModel<S>>> executeStatus(
        request: IRequest,
        params: Any? = null
    ): BaseFmpRawModel<S> {
        val statusString = execute(request, params)
        return statusString.tryParseRawStatus<S, T>(T::class.java)
    }

    inline fun<reified S : Any, reified T : ObjectRawStatus<out BaseFmpRawModel<S>>> executeResult(
        request: IRequest,
        params: Any? = null
    ): Result<S> {
        val statusString = execute(request, params)
        return statusString.getStatusResultWithData(request.resourceName, T::class.java)
    }

    fun BaseStatus.isNotBad(): Boolean {
        return this.isOk || this.httpStatus?.status == 304
    }

    inline fun <reified S : Any, reified T : ObjectRawStatus<out BaseFmpRawModel<S>>> String.getStatusResultWithData(
        resourceName: String,
        clazz: Class<T>
    ): Result<S> {
        return try {
            println("status: $this")
            val gson = GsonBuilder().create()
            val status = gson.fromJson(this, clazz)
            if (status.isNotBad()) {
                val result = status.result
                if (result != null) {
                    println( "result: $result" )
                    val raw = result.raw
                    if (raw != null) {
                        println( "raw: $raw" )
                        val errorMessage = raw.errorMessage
                        val errorDescription = raw.errorDescription
                        when {
                            errorMessage != null -> {
                                println( "errorMessage = $errorMessage | resourceName = '$resourceName'" )
                                Result.failure(RequestException.SapError(message = errorMessage))
                            }
                            errorDescription != null -> {
                                println( "errorDescription = $errorDescription | resourceName = '$resourceName'" )
                                Result.failure(RequestException.SapError(message = errorDescription))
                            }
                            else -> {
                                val rawData = raw.data
                                if (rawData != null) {
                                    Result.success(rawData)
                                } else {
                                    println( "raw.Data is null | resourceName = '$resourceName'" )
                                    Result.failure(RequestException.RawDataNullError)
                                }
                            }
                        }
                    } else {
                        println( "raw result is null | resourceName = '$resourceName'" )
                        Result.failure(RequestException.ResultRawNullError)
                    }
                } else {
                    println( "status result is null | resourceName = '$resourceName'" )
                    Result.failure(RequestException.StatusResultNullError)
                }
            } else {
                val raw = status.result?.raw
                val errors = status.errors
                val firstError = errors.firstOrNull()
                val errorMessage = firstError?.source ?: raw?.errorMessage.orEmpty()
                val errorDescription = firstError?.description ?: raw?.errorDescription.orEmpty()
                println( "status is bad | errorMessage = $errorMessage " +
                        "| errorDescription = $errorDescription " +
                        "| resourceName = '$resourceName'" )
                Result.failure(RequestException.SapError(errorDescription))
            }
        } catch (e: Throwable) {
            e.printStackTrace()
            Result.failure(RequestException.WebCallError)
        }
    }

    inline fun<reified S : Any, reified T : ObjectRawStatus<out BaseFmpRawModel<S>>> String.tryParseRawStatus(
        clazz: Class<T>
    ): BaseFmpRawModel<S> {
         return try {
            val gson = GsonBuilder().create()
            val status = gson.fromJson(this, clazz)
            if (status.isNotBad()) {
                val resultData = status.result?.raw
                resultData ?: status.getErrorRawModel()
            } else {
                status.getErrorRawModel()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            BaseFmpRawModel<S>().apply {
                errorDescription = e.localizedMessage
                errorMessage = e.message.orEmpty()
                statusCode = -1
            }
        }
    }
    inline fun<reified S : Any> BaseStatus.getErrorRawModel(): BaseFmpRawModel<S> {
        val errorStatus = BaseFmpRawModel<S>()
        errorStatus.errorDescription = this.errors.firstOrNull()?.descriptions?.firstOrNull().orEmpty()
        errorStatus.errorMessage = this.errors.firstOrNull()?.description.orEmpty()
        errorStatus.statusCode = this.httpStatus?.status ?: -1
        errorStatus.version = ""
        return errorStatus
    }

}