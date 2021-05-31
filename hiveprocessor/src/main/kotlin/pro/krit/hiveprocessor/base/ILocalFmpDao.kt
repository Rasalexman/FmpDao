package pro.krit.hiveprocessor.base

import com.mobrun.plugin.models.StatusSelectTable
import pro.krit.hiveprocessor.common.LocalDaoFields

interface ILocalFmpDao<E : Any, T : StatusSelectTable<E>> : IFmpDao<E, T> {
    var localDaoFields: LocalDaoFields?
}