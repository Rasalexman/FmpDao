package pro.krit.fmpdaoexample.daos

import pro.krit.fmpdaoexample.models.PmEtDataEntity
import pro.krit.fmpdaoexample.statuses.PmStatus
import pro.krit.hiveprocessor.annotations.FmpDao
import pro.krit.hiveprocessor.annotations.FmpQuery
import pro.krit.hiveprocessor.base.IDao

@FmpDao(
    resourceName = "ZSR_TORO_PM_DATA",
    parameters = ["IV_WERKS", "IV_LGORT"]
)
interface IPmDataDao : IDao.IFmpDao<PmEtDataEntity, PmStatus> {

    @FmpQuery("SELECT * FROM :PmEtDataEntity WHERE MARKER = :id AND AUFPL = :count LIMIT :limit")
    suspend fun selectOnlyAvailable(id: String, count: String, limit: Long): List<PmEtDataEntity>
}