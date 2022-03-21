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

package pro.krit.core.provider

import com.mobrun.plugin.api.LoggingAPI

/**
 * Config for create [AbstractFmpDatabase] instance
 */
data class DatabaseConfig(
    val serverAddress: String,
    val environment: String,
    val project: String,
    val dbKey: String = "",
    val dbPath: String = "",
    val projectVersion: String = "app",
    val retryCount: Int = 5,
    val retryInterval: Int = 10,
    val isLoggingEnabled: Boolean = true,
    val logLevel: Int = LoggingAPI.Type.LOGCAT.type,
    val logOutputType: LoggingAPI.Type = LoggingAPI.Type.LOGCAT,
    val isSharedTrigger: Boolean = false
)
