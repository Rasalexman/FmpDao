package pro.krit.fmpdaoexample.daos

import com.mobrun.plugin.models.StatusSelectTable
import pro.krit.fmpdaoexample.models.ZtMp08Entity
import pro.krit.generated.request.ZtMp08RequestEtDataModel
import pro.krit.hhivecore.annotations.FmpLocalDao
import pro.krit.hhivecore.base.IDao

@FmpLocalDao(
    resourceName = "zs_mp_08",
    tableName = "LOCAL"
)
interface IZtMp08Dao : IDao.IFmpLocalDao<ZtMp08Entity, IZtMp08Dao.ZtMp08Status> {
    class ZtMp08Status : StatusSelectTable<ZtMp08Entity>()
}