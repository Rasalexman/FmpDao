package pro.krit.fmpdaoexample

import pro.krit.hiveprocessor.annotations.FmpLocalDao
import pro.krit.hiveprocessor.base.ILocalFmpDao

@FmpLocalDao(
    resourceName = "ZSR_TORO_PM_DATA",
    parameterName = "ET_DATA"
)
interface IPmDataLocalDao : ILocalFmpDao<PmEtDataLocalEntity, PmLocalStatus>