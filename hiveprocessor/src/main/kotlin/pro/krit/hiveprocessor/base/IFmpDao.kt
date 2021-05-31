package pro.krit.hiveprocessor.base

import com.mobrun.plugin.models.StatusSelectTable
import pro.krit.hiveprocessor.provider.IHyperHiveDatabase

interface IFmpDao<E : Any, T : StatusSelectTable<E>>{
    val hyperHiveDatabase: IHyperHiveDatabase
    val nameResource: String
    val nameParameter: String
    val isCached: Boolean
}