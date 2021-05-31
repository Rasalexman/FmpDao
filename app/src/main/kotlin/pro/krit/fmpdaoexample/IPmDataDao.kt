package pro.krit.fmpdaoexample

import pro.krit.hiveprocessor.annotations.FmpDao
import pro.krit.hiveprocessor.base.IFmpDao

@FmpDao(
    resourceName = "ZSR_TORO_PM_DATA",
    parameterName = "ET_DATA"
)
interface IPmDataDao : IFmpDao<PmEtDataEntity, PmStatus>