package com.rasalexman.fmpdaoexample.daos

import com.rasalexman.hhivecore.annotations.FmpDao
import com.rasalexman.hhivecore.base.IDao
import com.rasalexman.fmpdaoexample.models.PmEtDataEntity
import com.rasalexman.fmpdaoexample.statuses.PmStatus

@FmpDao(
    resourceName = "ZSR_TORO_PM_DATA",
    parameters = ["IV_WERKS", "IV_LGORT"]
)
interface IPmDataDao : IDao.IFmpDao<PmEtDataEntity, PmStatus> {

    /*@FmpQuery("SELECT * FROM :PmEtDataEntity WHERE MARKER = :id AND AUFPL = :count LIMIT :limit")
    suspend fun selectOnlyAvailable(id: String, count: String, limit: Long): List<PmEtDataEntity>*/
}