package pro.krit.hiveprocessor.base

import com.mobrun.plugin.models.StatusSelectTable
import pro.krit.hiveprocessor.common.LocalDaoFields


/**
 * Base abstraction for annotation processing works with local fmp database structures
 */
interface IFmpLocalDao<E : Any, T : StatusSelectTable<E>> : IFmpDao<E, T> {
    var localDaoFields: LocalDaoFields?
}