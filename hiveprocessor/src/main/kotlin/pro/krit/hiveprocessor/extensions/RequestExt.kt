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

import com.mobrun.plugin.models.BaseStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pro.krit.hiveprocessor.base.IRequest
import pro.krit.hiveprocessor.base.RawStatus
import pro.krit.hiveprocessor.common.RequestBuilder
import pro.krit.hiveprocessor.common.RequestExecuter

inline fun <reified S : Any, reified T : RawStatus<S>> IRequest.request(
    requestData: Any? = null
): S? {
    val params = RequestBuilder.createParams(this, requestData)
    val resultStatus = RequestExecuter.executeStatus<S, T>(this, params)
    return resultStatus?.result?.raw
}

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

inline fun <reified S : Any, reified T : RawStatus<S>> IRequest.requestStatus(
    requestData: Any? = null
): BaseStatus {
    val params = RequestBuilder.createParams(this, requestData)
    return RequestExecuter.executeBaseStatus<T>(this, params, T::class.java)
}

suspend inline fun <reified S : Any, reified T : RawStatus<S>> IRequest.requestStatusAsync(
    requestData: Any? = null
): BaseStatus {
    val currentRequest = this
    return withContext(Dispatchers.IO) {
        val params = RequestBuilder.createParams(
            currentRequest,
            requestData
        )
        RequestExecuter.executeBaseStatus<T>(currentRequest, params, T::class.java)
    }
}

inline fun <reified S : Any, reified T : RawStatus<S>> IRequest.requestResult(
    requestData: Any? = null
): Result<S> {
    val params = RequestBuilder.createParams(this, requestData)
    return RequestExecuter.executeResult<S, T>(this, params)
}

suspend inline fun <reified S : Any, reified T : RawStatus<S>> IRequest.requestResultAsync(
    requestData: Any? = null
): Result<S> {
    val currentRequest = this
    return withContext(Dispatchers.IO) {
        val params = RequestBuilder.createParams(currentRequest, requestData)
        RequestExecuter.executeResult<S, T>(currentRequest, params)
    }

}