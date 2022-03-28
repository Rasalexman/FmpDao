package pro.krit.fmpdaoexample.daos

import com.mobrun.plugin.models.StatusSelectTable
import pro.krit.generated.request.ZtMp08RequestEtDataModel
import pro.krit.hhivecore.annotations.FmpLocalDao
import pro.krit.hhivecore.base.IDao

@FmpLocalDao(
    resourceName = "ZtMp08Status",
    tableName = "LOCAL"
)
interface IZtMp08Dao : IDao.IFmpLocalDao<ZtMp08RequestEtDataModel, IZtMp08Dao.ZtMp08Status> {
    class ZtMp08Status : StatusSelectTable<ZtMp08RequestEtDataModel>()
}