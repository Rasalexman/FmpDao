package pro.krit.fmpdaoexample.daos

import pro.krit.processor.annotations.FmpDao
import pro.krit.processor.base.IDao

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