package pro.krit.fmpdaoexample.daos

import pro.krit.processor.annotations.FmpLocalDao
import pro.krit.processor.base.IDao

@FmpLocalDao(
    resourceName = "ZSR_TORO_PM_DATA",
    tableName = "ET_DATA_FIELDS",
    fields = ["ID_Primary", "TASK_NUM_Int", "TPLNR"],
    createTableOnInit = false
)
interface IPmDataFieldsDao : IDao.IFieldsDao