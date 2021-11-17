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

package pro.krit.hiveprocessor.extensions

import com.mobrun.plugin.api.request_assistant.CustomParameter
import com.mobrun.plugin.models.BaseStatus
import com.mobrun.plugin.models.StatusRawDataListTable
import pro.krit.hiveprocessor.base.IRequest
import pro.krit.hiveprocessor.base.RawStatus
import pro.krit.hiveprocessor.common.RequestBuilder
import pro.krit.hiveprocessor.common.RequestExecuter

/**
 * Простой запрос данных для FMP
 *
 * @param requestData - данные для запроса
 *
 * @return - сгенерированный процессором статус запроса [RawStatus]
 */
inline fun <reified S : Any, reified T : RawStatus<S>> IRequest.request(
    requestData: Any? = null
): S? {
    val params = RequestBuilder.createParams(this, requestData)
    println("------> Start request '${this.resourceName}' with params: $params")
    val resultStatus = RequestExecuter.executeStatus<S, T>(this, params)
    val resultError = resultStatus?.errors
    println("------> Finish request '${this.resourceName}' with result: ${resultStatus?.raw} and errors: $resultError")
    return resultStatus?.result?.raw
}

/*
suspend inline fun <reified S : Any, reified T : RawStatus<S>> IRequest.requestAsync(
    requestData: Any? = null
): S? {
    val currentRequest = this
    return withContext(Dispatchers.IO) {
        val params = withContext(Dispatchers.Default) {
            RequestBuilder.createParams(
                currentRequest,
                requestData
            )
        }
        RequestExecuter.executeStatus<S, T>(currentRequest, params)?.result?.raw
    }
}
*/

/**
 * Простой запрос данных для FMP
 *
 * @param requestData - данные для запроса
 *
 * @return - статус запроса [BaseStatus]
 */
inline fun <reified S : Any, reified T : RawStatus<S>> IRequest.requestStatus(
    requestData: Any? = null
): BaseStatus {
    val params = RequestBuilder.createParams(this, requestData)
    println("------> Start requestStatus '${this.resourceName}' with params: $params")
    val resultStatus = RequestExecuter.executeBaseStatus(this, params, T::class.java)
    println("------> Finish requestStatus '${this.resourceName}' with status: ${resultStatus.raw}")
    return resultStatus
}

/*suspend inline fun <reified S : Any, reified T : RawStatus<S>> IRequest.requestStatusAsync(
    requestData: Any? = null
): BaseStatus {
    val currentRequest = this
    return withContext(Dispatchers.IO) {
        val params = RequestBuilder.createParams(
            currentRequest,
            requestData
        )
        RequestExecuter.executeBaseStatus(currentRequest, params, T::class.java)
    }
}*/

/**
 * Простой запрос данных для FMP
 *
 * @param requestData - данные для запроса
 *
 * @return - статус запроса обернутый в [Result]
 */
inline fun <reified S : Any, reified T : RawStatus<S>> IRequest.requestResult(
    requestData: Any? = null
): Result<S> {
    val params = RequestBuilder.createParams(this, requestData)
    println("------> Start requestResult '${this.resourceName}' with params: $params")
    return RequestExecuter.executeResult<S, T>(this, params).also {
        println("------> Finish requestResult '${this.resourceName}' with result: ${it.getOrNull()}")
    }
}

/*suspend inline fun <reified S : Any, reified T : RawStatus<S>> IRequest.requestResultAsync(
    requestData: Any? = null
): Result<S> {
    val currentRequest = this
    return withContext(Dispatchers.IO) {
        val params = RequestBuilder.createParams(currentRequest, requestData)
        RequestExecuter.executeResult<S, T>(currentRequest, params)
    }
}*/

/**
 * Простой запрос данных для FMP
 *
 * @param requestData - данные для запроса
 *
 * @return - статус запроса [StatusRawDataListTable]
 */
fun IRequest.requestListStatus(requestData: Any? = null): StatusRawDataListTable {
    val params = RequestBuilder.createParams(this, requestData)
    println("------> Start requestListStatus '${this.resourceName}' with params: $params")
    return RequestExecuter.executeBaseStatus(
        this,
        params,
        StatusRawDataListTable::class.java
    ).also {
        println("------> Finish requestListStatus '${this.resourceName}' with result: ${it.raw} and errors: ${it.errors}")
    }
}

/**
 * Простой запрос данных для FMP
 *
 * @param requestData - [List] данные для запроса
 *
 * @return - статус запроса [StatusRawDataListTable]
 */
fun IRequest.tableListStatus(requestData: List<CustomParameter>? = null): StatusRawDataListTable {
    val params = RequestBuilder.createTableParams(requestData)
    println("------> Start tableListStatus '${this.resourceName}' with params: ${params.data}")
    return RequestExecuter.executeBaseTableStatus(
        this,
        params
    ).also {
        println("------> Finish tableListStatus '${this.resourceName}' with result: ${it.raw} and errors: ${it.errors}")
    }
}