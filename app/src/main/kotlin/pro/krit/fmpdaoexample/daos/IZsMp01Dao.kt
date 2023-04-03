package com.rasalexman.fmpdaoexample.daos

import com.rasalexman.hhivecore.annotations.FmpDao
import com.rasalexman.hhivecore.base.IDao

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