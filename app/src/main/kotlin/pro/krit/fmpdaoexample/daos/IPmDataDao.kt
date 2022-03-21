package pro.krit.fmpdaoexample.daos

import pro.krit.fmpdaoexample.models.PmEtDataEntity
import pro.krit.fmpdaoexample.statuses.PmStatus
import pro.krit.core.annotations.FmpDao
import pro.krit.core.annotations.FmpQuery
import pro.krit.core.base.IDao

@FmpDao(
    resourceName = "ZSR_TORO_PM_DATA",
    parameters = ["IV_WERKS", "IV_LGORT"]
)
interface IPmDataDao : IDao.IFmpDao<PmEtDataEntity, PmStatus> {

    /*@FmpQuery("SELECT * FROM :PmEtDataEntity WHERE MARKER = :id AND AUFPL = :count LIMIT :limit")
    suspend fun selectOnlyAvailable(id: String, count: String, limit: Long): List<PmEtDataEntity>*/
}