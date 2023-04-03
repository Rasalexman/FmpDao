package com.rasalexman.fmpdaoexample.fmpresources

import com.rasalexman.hhivecore.annotations.FmpDao
import com.rasalexman.hhivecore.base.IDao

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