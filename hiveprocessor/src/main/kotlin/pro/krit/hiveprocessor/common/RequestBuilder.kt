package pro.krit.hiveprocessor.common

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.mobrun.plugin.api.callparams.RequestCallParams
import com.mobrun.plugin.api.callparams.WebCallParams
import pro.krit.hiveprocessor.base.IRequest

object RequestBuilder {

    private val gson: Gson by lazy { GsonBuilder().create() }

    @Suppress("UNCHECKED_CAST")
    fun createParams(
        request: IRequest,
        requestData: Any? = null
    ): Any {
        val isWeb = request is IRequest.IWebRequest<*,*>
        return if (isWeb) {
            WebCallParams().apply {
                requestData.tryParseParams()?.let {
                    this.data = it
                }
                headers = request.defaultHeaders
            }
        } else {
            RequestCallParams().apply {
                headers = request.defaultHeaders
                (requestData as? Map<String, String>)?.takeIf { it.isNotEmpty() }?.let {
                    this.args = it
                } ?:
                requestData.tryParseParams()?.let {
                    this.data = it
                }
            }
        }
    }

    private fun Any?.tryParseParams(): String? {
        return this?.let { params ->
            if (params is String) {
                params
            } else {
                try {
                    gson.toJson(params)
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
            }
        }
    }
}