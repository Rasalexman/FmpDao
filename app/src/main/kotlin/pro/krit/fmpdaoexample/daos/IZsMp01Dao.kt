package pro.krit.fmpdaoexample.daos

import pro.krit.core.annotations.FmpDao
import pro.krit.core.base.IDao

//Загрузка справочника товаров
@FmpDao(
    resourceName = "zs_mp_01",
    tableName = "output_table",
    fields = [
        "MATNR", "MATNR_NAME", "BUOM"
    ],
    isDelta = false
)
interface IZsMp01Dao : IDao.IFieldsDao