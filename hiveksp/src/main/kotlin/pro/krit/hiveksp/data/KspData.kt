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

package pro.krit.hiveksp.data

import com.google.devtools.ksp.symbol.KSDeclaration
import pro.krit.core.data.TypeData

data class KspData(
    val element: KSDeclaration,
    val fileName: String,
    val mainData: TypeData,
    val createTableOnInit: Boolean,
    val parameters: List<String> = emptyList(),
    val fields: List<String> = emptyList(),
    val resourceName: String = "",  // current resource name
    val tableName: String = "",  // current table name
    val isDelta: Boolean = false, // using deltaStream
    val isLocal: Boolean = false, // is this a local dao
    val isRequest: Boolean = false, // is this a request interface with @FmpWebRequest or @FmpRestRequest,
    val isWebRequest: Boolean = false
)