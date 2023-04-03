package com.rasalexman.fmpdaoexample.daos

import com.rasalexman.fmpdaoexample.models.PmEtDataLocalEntity
import com.rasalexman.fmpdaoexample.statuses.PmLocalStatus
import com.rasalexman.hhivecore.annotations.FmpLocalDao
import com.rasalexman.hhivecore.base.IDao

@FmpLocalDao(
    resourceName = "ZSR_TORO_PM_DATA",
    tableName = "ET_DATA_LOCAL"
)
interface IPmDataLocalDao : IDao.IFmpLocalDao<PmEtDataLocalEntity, PmLocalStatus>