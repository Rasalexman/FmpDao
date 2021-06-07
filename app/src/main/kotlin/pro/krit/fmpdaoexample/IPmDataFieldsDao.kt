package pro.krit.fmpdaoexample

import pro.krit.hiveprocessor.annotations.FmpLocalDao
import pro.krit.hiveprocessor.base.IDao

@FmpLocalDao(
    resourceName = "ZSR_TORO_PM_DATA",
    tableName = "ET_DATA_FIELDS",
    fields = ["ID_Primary", "TASK_NUM_Int", "TPLNR"]
)
interface IPmDataFieldsDao : IDao.IFieldsDao