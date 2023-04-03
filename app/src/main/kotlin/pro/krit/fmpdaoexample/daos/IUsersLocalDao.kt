package com.rasalexman.fmpdaoexample.daos

import com.mobrun.plugin.models.StatusSelectTable
import com.rasalexman.fmpdaoexample.models.UserEntity
import com.rasalexman.hhivecore.annotations.FmpLocalDao
import com.rasalexman.hhivecore.base.IDao

@FmpLocalDao(
    resourceName = "USERS",
    tableName = "LOCAL"
)
interface IUsersLocalDao : IDao.IFmpLocalDao<UserEntity, IUsersLocalDao.UserEntityStatus> {

    class UserEntityStatus : StatusSelectTable<UserEntity>()
}