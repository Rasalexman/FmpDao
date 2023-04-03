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

package com.rasalexman.hhivecore.provider

import com.mobrun.plugin.api.DatabaseAPI
import com.mobrun.plugin.api.HyperHive
import com.mobrun.plugin.api.HyperHiveState
import io.reactivex.rxjava3.subjects.Subject
import kotlinx.coroutines.flow.Flow
import com.rasalexman.hhivecore.base.IDao

interface IFmpDatabase {
    val isDbCreated: Boolean
    val databasePath: String
    val databaseApi: DatabaseAPI
    fun provideHyperHive(): HyperHive
    fun provideHyperHiveState(): HyperHiveState

    fun getFlowTrigger(dao: IDao): Flow<String>
    fun getRxTrigger(dao: IDao): Subject<String>

    fun openDatabase(dbKey: String = "", pathBase: String = ""): DatabaseState
    fun closeDatabase(pathBase: String = ""): DatabaseState
    fun closeAndClearProviders(pathBase: String = ""): DatabaseState

    fun setDefaultHeaders(headers: Map<String, String>?)
    fun getDefaultHeaders(): Map<String, String>?
    fun clearProviders()
}