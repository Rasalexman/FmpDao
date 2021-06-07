package pro.krit.hiveprocessor.extensions

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pro.krit.hiveprocessor.base.IRequest
import pro.krit.hiveprocessor.common.RequestBuilder
import pro.krit.hiveprocessor.common.RequestExecuter
import pro.krit.hiveprocessor.request.BaseFmpRawModel
import pro.krit.hiveprocessor.request.ObjectRawStatus

inline fun <reified S : Any, reified T : ObjectRawStatus<out BaseFmpRawModel<S>>> IRequest.request(
    requestData: Any? = null
): S? {
    val params = RequestBuilder.createParams(this, requestData)
    val resultStatus = RequestExecuter.executeStatus<S, T>(this, params)
    return resultStatus.data
}

suspend inline fun <reified S : Any, reified T : ObjectRawStatus<out BaseFmpRawModel<S>>> IRequest.requestAsync(
    requestData: Any? = null
): S? {
    return withContext(Dispatchers.IO) { request<S, T>(requestData) }
}

inline fun <reified S : Any, reified T : ObjectRawStatus<out BaseFmpRawModel<S>>> IRequest.requestResult(
    requestData: Any? = null
): Result<S> {
    val params = RequestBuilder.createParams(this, requestData)
    return RequestExecuter.executeResult<S, T>(this, params)
}

suspend inline fun <reified S : Any, reified T : ObjectRawStatus<out BaseFmpRawModel<S>>> IRequest.requestResultAsync(
    requestData: Any? = null
): Result<S> {
    return withContext(Dispatchers.IO) { requestResult(requestData) }
}