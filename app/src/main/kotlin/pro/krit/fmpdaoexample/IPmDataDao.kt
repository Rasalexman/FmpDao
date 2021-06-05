package pro.krit.fmpdaoexample

import pro.krit.hiveprocessor.annotations.FmpDao
import pro.krit.hiveprocessor.annotations.FmpQuery
import pro.krit.hiveprocessor.base.IFmpDao

@FmpDao(
    resourceName = "ZSR_TORO_PM_DATA",
    tableName = "ET_DATA",
    parameters = ["IV_WERKS", "IV_LGORT"]
)
interface IPmDataDao : IFmpDao<PmEtDataEntity, PmStatus> {

    @FmpQuery("SELECT * FROM :PmEtDataEntity WHERE MARKER = :id AND AUFPL = :count LIMIT :limit")
    suspend fun selectOnlyAvailable(id: String, count: Int, limit: Long): List<PmEtDataEntity>
}