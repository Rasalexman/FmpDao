package pro.krit.fmpdaoexample.fmpresources

import pro.krit.hhivecore.annotations.FmpDao
import pro.krit.hhivecore.base.IDao

//Загрузка справочника значений для выпадающих списков
@FmpDao(
    resourceName = "zs_mp_04",
    tableName = "output_table",
    fields = [
        "TID", "CODE", "SORDER", "TEXT"
    ],
    isDelta = false
)
interface IZsMp04Dao : IDao.IFieldsDao