package pro.krit.fmpdaoexample.daos

import com.mobrun.plugin.models.StatusSelectTable
import pro.krit.fmpdaoexample.models.UserEntity
import pro.krit.processor.annotations.FmpLocalDao
import pro.krit.processor.base.IDao

@FmpLocalDao(
    resourceName = "USERS",
    tableName = "LOCAL"
)
interface IUsersLocalDao : IDao.IFmpLocalDao<UserEntity, IUsersLocalDao.UserEntityStatus> {

    class UserEntityStatus : StatusSelectTable<UserEntity>()
}