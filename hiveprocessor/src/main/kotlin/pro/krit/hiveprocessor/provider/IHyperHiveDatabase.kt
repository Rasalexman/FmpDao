package pro.krit.hiveprocessor.provider

import com.mobrun.plugin.api.DatabaseAPI
import com.mobrun.plugin.api.HyperHive
import com.mobrun.plugin.api.HyperHiveState

interface IHyperHiveDatabase {
    val isDbCreated: Boolean
    val databasePath: String
    val databaseApi: DatabaseAPI
    fun provideHyperHive(): HyperHive
    fun provideHyperHiveState(): HyperHiveState

    fun openDatabase(dbKey: String = "", pathBase: String = ""): DatabaseState
    fun closeDatabase(pathBase: String = ""): DatabaseState
    fun closeAndClearProviders(pathBase: String = "")
}