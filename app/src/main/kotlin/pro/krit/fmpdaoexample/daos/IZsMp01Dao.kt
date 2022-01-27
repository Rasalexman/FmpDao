package pro.krit.fmpdaoexample.daos

import pro.krit.hiveprocessor.annotations.FmpDao
import pro.krit.hiveprocessor.base.IDao

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