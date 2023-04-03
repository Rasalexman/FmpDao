package com.rasalexman.fmpdaoexample.daos

import com.mobrun.plugin.models.StatusSelectTable
import com.rasalexman.fmpdaoexample.models.ZtMp08Entity
import com.rasalexman.generated.request.ZtMp08RequestEtDataModel
import com.rasalexman.hhivecore.annotations.FmpLocalDao
import com.rasalexman.hhivecore.base.IDao

@FmpLocalDao(
    resourceName = "zs_mp_08",
    tableName = "LOCAL"
)
interface IZtMp08Dao : IDao.IFmpLocalDao<ZtMp08Entity, IZtMp08Dao.ZtMp08Status> {
    class ZtMp08Status : StatusSelectTable<ZtMp08Entity>()
}