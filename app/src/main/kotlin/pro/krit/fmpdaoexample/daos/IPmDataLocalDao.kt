package pro.krit.fmpdaoexample.daos

import pro.krit.fmpdaoexample.models.PmEtDataLocalEntity
import pro.krit.fmpdaoexample.statuses.PmLocalStatus
import pro.krit.core.annotations.FmpLocalDao
import pro.krit.core.base.IDao

@FmpLocalDao(
    resourceName = "ZSR_TORO_PM_DATA",
    tableName = "ET_DATA_LOCAL"
)
interface IPmDataLocalDao : IDao.IFmpLocalDao<PmEtDataLocalEntity, PmLocalStatus>