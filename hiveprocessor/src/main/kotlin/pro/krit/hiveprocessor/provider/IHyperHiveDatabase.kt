package pro.krit.hiveprocessor.provider

import com.mobrun.plugin.api.HyperHive
import com.mobrun.plugin.api.HyperHiveState

interface IHyperHiveDatabase {
    val isDbCreated: Boolean
    val databasePath: String
    fun provideHyperHive(): HyperHive
    fun provideHyperHiveState(): HyperHiveState
}