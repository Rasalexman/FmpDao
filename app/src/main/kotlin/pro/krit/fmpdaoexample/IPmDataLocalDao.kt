package pro.krit.fmpdaoexample

import pro.krit.hiveprocessor.annotations.FmpFieldsDao
import pro.krit.hiveprocessor.base.IDao

@FmpFieldsDao(
    resourceName = "ZSR_TORO_PM_DATA",
    tableName = "ET_DATA_LOCAL"
)
interface IPmDataLocalDao : IDao.IFmpFieldsDao<PmEtDataLocalEntity, PmLocalStatus>