package pro.krit.fmpdaoexample.daos

import com.mobrun.plugin.models.StatusSelectTable
import pro.krit.fmpdaoexample.models.UserEntity
import pro.krit.core.annotations.FmpLocalDao
import pro.krit.core.base.IDao

@FmpLocalDao(
    resourceName = "USERS",
    tableName = "LOCAL"
)
interface IUsersLocalDao : IDao.IFmpLocalDao<UserEntity, IUsersLocalDao.UserEntityStatus> {

    class UserEntityStatus : StatusSelectTable<UserEntity>()
}