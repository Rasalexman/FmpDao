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