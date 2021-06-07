package pro.krit.hiveprocessor.base

import com.mobrun.plugin.api.HyperHive
import pro.krit.hiveprocessor.request.BaseFmpRawModel
import pro.krit.hiveprocessor.request.ObjectRawStatus

typealias RawStatus<S> = ObjectRawStatus<out BaseFmpRawModel<S>>

sealed interface IRequest {
    val hyperHive: HyperHive
    val defaultHeaders: Map<String, String>
    val resourceName: String

    interface IBaseRequest : IRequest

    interface IWebRequest<S : Any, T : RawStatus<S>> : IRequest
    interface IRestRequest<S : Any, T : RawStatus<S>> : IRequest
}