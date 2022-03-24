package pro.krit.fmpdaoexample.daos

import pro.krit.hhivecore.annotations.FmpDao
import pro.krit.hhivecore.base.IDao

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