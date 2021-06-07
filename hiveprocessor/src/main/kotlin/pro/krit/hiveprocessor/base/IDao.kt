package pro.krit.hiveprocessor.base

import com.mobrun.plugin.models.StatusSelectTable
import pro.krit.hiveprocessor.common.DaoFieldsData
import pro.krit.hiveprocessor.provider.IFmpDatabase

sealed interface IDao {
    val fmpDatabase: IFmpDatabase
    val resourceName: String
    val tableName: String
    val isDelta: Boolean

    interface IFieldsDao : IDao {
        var fieldsData: DaoFieldsData?
    }

    /**
     * Base abstraction for annotation processing works with remotely fmp database structures
     */
    interface IFmpDao<E : Any, T : StatusSelectTable<E>> : IDao

    /**
     * Base abstraction for annotation processing works with local fmp database structures
     */
    interface IFmpLocalDao<E : Any, T : StatusSelectTable<E>> : IFmpDao<E, T>, IFieldsDao
}