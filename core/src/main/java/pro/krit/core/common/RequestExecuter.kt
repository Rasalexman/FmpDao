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

package pro.krit.core.common

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.mobrun.plugin.api.callparams.RequestCallParams
import com.mobrun.plugin.api.callparams.TableCallParams
import com.mobrun.plugin.api.callparams.WebCallParams
import com.mobrun.plugin.models.BaseStatus
import com.mobrun.plugin.models.Error
import com.mobrun.plugin.models.StatusRawDataListTable
import pro.krit.core.base.IRequest
import pro.krit.core.base.RawStatus
import pro.krit.core.errors.RequestException
import pro.krit.core.request.ObjectRawStatus

object RequestExecuter {

    val gson: Gson by lazy {
        GsonBuilder().create()
    }

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

    fun <T : BaseStatus> executeBaseStatus(
        request: IRequest,
        params: Any? = null,
        clazz: Class<T>
    ): T {
        val requestApi = request.hyperHive.requestAPI
        val resourceName = request.resourceName
        return when (params) {
            is WebCallParams -> requestApi.web(resourceName, params, clazz).execute()
            is RequestCallParams -> requestApi.request(resourceName, params, clazz).execute()
            else -> requestApi.web(resourceName, WebCallParams(), clazz).execute()
        }
    }

    fun executeBaseTableStatus(
        request: IRequest,
        params: TableCallParams? = null
    ): StatusRawDataListTable {
        val requestApi = request.hyperHive.requestAPI
        val resourceName = request.resourceName
        return requestApi.table(resourceName, params, StatusRawDataListTable::class.java).execute()
    }

    inline fun <reified S : Any, reified T : RawStatus<S>> executeStatus(
        request: IRequest,
        params: Any? = null
    ): T? {
        val statusString = execute(request, params)
        return statusString.tryParseRawStatus<S, T>()
    }

    inline fun <reified S : Any, reified T : RawStatus<S>> executeResult(
        request: IRequest,
        params: Any? = null
    ): Result<S> {
        val statusString = execute(request, params)
        return statusString.getStatusResultWithData<S, T>(request.resourceName)
    }

    inline fun <reified S : Any, reified T : RawStatus<S>> String.getStatusResultWithData(
        resourceName: String
    ): Result<S> {
        val result = try {
            //println("[RequestExecuter] request status: $this")
            val typeToken = object : TypeToken<T>() {}.type
            val status: T = gson.fromJson<T>(this, typeToken)
            if (status.isNotBad()) {
                val result = status.result
                if (result != null) {
                    //println("result: $result")
                    val raw = result.raw
                    raw?.let { currentResult ->
                        Result.success(currentResult)
                    } ?: Result.failure(RequestException.ResultRawNullError)
                } else {
                    //println("status result is null | resourceName = '$resourceName'")
                    Result.failure(RequestException.StatusResultNullError)
                }
            } else {
                val errors = status.errors
                val firstError = errors.firstOrNull()
                val firstDescription = firstError?.description.orEmpty() //?: raw?.errorMessage
                val errorMessage = firstError?.source ?: firstDescription
                val errorFirsDescription = firstError?.descriptions?.firstOrNull().orEmpty()

                val errorText = "status is bad | errorMessage = '$errorMessage' " +
                        "| errorDescription = '$errorFirsDescription' " +
                        "| description = '$firstDescription' " +
                        "| resourceName = '$resourceName'"
                Result.failure(RequestException.SapError(errorText))
            }
        } catch (e: Throwable) {
            e.printStackTrace()
            Result.failure(RequestException.WebCallError)
        }
        //println("final result = $result")
        return result
    }

    inline fun <reified S : Any, reified T : RawStatus<S>> String.tryParseRawStatus(): T? {
        return try {
            //println("status: $this")
            val status: T = gson.fromJson<T>(this, T::class.java)
            status
        } catch (ex: Exception) {
            ex.printStackTrace()
            ObjectRawStatus<S>().apply {
                val error = Error()
                error.code = 0
                error.description = ex.message ?: "tryParseRawStatus HyperHive Error with $ex"
                errors.add(error)
            } as? T
        }
    }

    fun BaseStatus.isNotBad(): Boolean {
        return this.isOk || this.httpStatus?.status == 304
    }

}