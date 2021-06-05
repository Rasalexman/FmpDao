package pro.krit.fmpdaoexample

import pro.krit.hiveprocessor.annotations.FmpLocalDao
import pro.krit.hiveprocessor.base.IFmpLocalDao

@FmpLocalDao(
    resourceName = "ZSR_TORO_PM_DATA",
    tableName = "ET_DATA_LOCAL"
)
interface IPmDataLocalDao : IFmpLocalDao<PmEtDataLocalEntity, PmLocalStatus>