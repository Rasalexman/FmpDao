package pro.krit.fmpdaoexample.daos

import pro.krit.hhivecore.annotations.FmpLocalDao
import pro.krit.hhivecore.base.IDao

@FmpLocalDao(
    resourceName = "ZSR_TORO_PM_DATA",
    tableName = "ET_DATA_FIELDS",
    fields = ["ID_Primary", "TASK_NUM_Int", "TPLNR"],
    createTableOnInit = false
)
interface IPmDataFieldsDao : IDao.IFieldsDao