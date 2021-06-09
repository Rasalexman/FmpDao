// Copyright (c) 2021 Aleksandr Minkin aka Rasalexman (sphc@yandex.ru)
//
// Permission is hereby granted, free of charge, to any person obtaining a copy of this software
// and associated documentation files (the "Software"), to deal in the Software without restriction,
// including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
// and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so,
// subject to the following conditions:
// The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
// WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
// IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
// WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH
// THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

package pro.krit.hiveprocessor.common

import com.google.gson.GsonBuilder
import com.mobrun.plugin.api.callparams.RequestCallParams
import com.mobrun.plugin.api.callparams.WebCallParams
import com.mobrun.plugin.models.BaseStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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

    suspend fun executeAsync(
        request: IRequest,
        params: Any? = null
    ): String {
        return withContext(Dispatchers.IO) { execute(request, params) }
    }

    fun <T : BaseStatus> executeBaseStatus(
        request: IRequest,
        params: Any? = null,
        clazz: Class<T>
    ): BaseStatus {
        val requestApi = request.hyperHive.requestAPI
        val resourceName = request.resourceName
        return when (params) {
            is WebCallParams -> requestApi.web(resourceName, params, clazz).execute()
            is RequestCallParams -> requestApi.request(resourceName, params, clazz).execute()
            else -> requestApi.web(resourceName).execute().let { respond ->
                BaseStatus().apply {
                    raw = respond
                }
            }
        }
    }

    suspend fun <T : BaseStatus> executeBaseStatusAsync(
        request: IRequest,
        params: Any? = null,
        clazz: Class<T>
    ): BaseStatus {
        return withContext(Dispatchers.IO) { executeBaseStatus(request, params, clazz) }
    }

    inline fun <reified S : Any, reified T : ObjectRawStatus<out BaseFmpRawModel<S>>> executeStatus(
        request: IRequest,
        params: Any? = null
    ): BaseFmpRawModel<S> {
        val statusString = execute(request, params)
        return statusString.tryParseRawStatus<S, T>(T::class.java)
    }

    suspend inline fun <reified S : Any, reified T : ObjectRawStatus<out BaseFmpRawModel<S>>> executeStatusAsync(
        request: IRequest,
        params: Any? = null
    ): BaseFmpRawModel<S> {
        return withContext(Dispatchers.IO) { executeStatus(request, params) }
    }

    inline fun <reified S : Any, reified T : ObjectRawStatus<out BaseFmpRawModel<S>>> executeResult(
        request: IRequest,
        params: Any? = null
    ): Result<S> {
        val statusString = execute(request, params)
        return statusString.getStatusResultWithData(request.resourceName, T::class.java)
    }

    suspend inline fun <reified S : Any, reified T : ObjectRawStatus<out BaseFmpRawModel<S>>> executeResultAsync(
        request: IRequest,
        params: Any? = null
    ): Result<S> {
        return withContext(Dispatchers.IO) { executeResult(request, params) }
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
                    println("result: $result")
                    val raw = result.raw
                    if (raw != null) {
                        println("raw: $raw")
                        val errorMessage = raw.errorMessage
                        val errorDescription = raw.errorDescription
                        when {
                            errorMessage != null -> {
                                println("errorMessage = $errorMessage | resourceName = '$resourceName'")
                                Result.failure(RequestException.SapError(message = errorMessage))
                            }
                            errorDescription != null -> {
                                println("errorDescription = $errorDescription | resourceName = '$resourceName'")
                                Result.failure(RequestException.SapError(message = errorDescription))
                            }
                            else -> {
                                val rawData = raw.data
                                if (rawData != null) {
                                    Result.success(rawData)
                                } else {
                                    println("raw.Data is null | resourceName = '$resourceName'")
                                    Result.failure(RequestException.RawDataNullError)
                                }
                            }
                        }
                    } else {
                        println("raw result is null | resourceName = '$resourceName'")
                        Result.failure(RequestException.ResultRawNullError)
                    }
                } else {
                    println("status result is null | resourceName = '$resourceName'")
                    Result.failure(RequestException.StatusResultNullError)
                }
            } else {
                val raw = status.result?.raw
                val errors = status.errors
                val firstError = errors.firstOrNull()
                val firstDescription = firstError?.description ?: raw?.errorMessage
                val errorMessage = firstError?.source ?: firstDescription
                val errorFirsDescription = firstError?.descriptions?.firstOrNull()
                val errorDetails = raw?.detail ?: raw?.errorDescription
                val errorDescription = errorDetails ?: errorFirsDescription

                val errorText = "status is bad | errorMessage = '$errorMessage' " +
                        "| errorDescription = '$errorDescription' " +
                        "| description = '$firstDescription' " +
                        "| resourceName = '$resourceName'" +
                        "| errorDetails = '$errorDetails'"
                Result.failure(RequestException.SapError(errorText))
            }
        } catch (e: Throwable) {
            e.printStackTrace()
            Result.failure(RequestException.WebCallError)
        }
    }

    inline fun <reified S : Any, reified T : ObjectRawStatus<out BaseFmpRawModel<S>>> String.tryParseRawStatus(
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

    inline fun <reified S : Any> BaseStatus.getErrorRawModel(): BaseFmpRawModel<S> {
        val errorStatus = BaseFmpRawModel<S>()
        errorStatus.errorDescription =
            this.errors.firstOrNull()?.descriptions?.firstOrNull().orEmpty()
        errorStatus.errorMessage = this.errors.firstOrNull()?.description.orEmpty()
        errorStatus.statusCode = this.httpStatus?.status ?: -1
        errorStatus.version = ""
        return errorStatus
    }

}