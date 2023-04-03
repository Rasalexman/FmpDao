package com.rasalexman.fmpdaoexample.daos

import com.rasalexman.hhivecore.annotations.FmpLocalDao
import com.rasalexman.hhivecore.base.IDao

@FmpLocalDao(
    resourceName = "ZSR_TORO_PM_DATA",
    tableName = "ET_DATA_FIELDS",
    fields = ["ID_Primary", "TASK_NUM_Int", "TPLNR"],
    createTableOnInit = false
)
interface IPmDataFieldsDao : IDao.IFieldsDao