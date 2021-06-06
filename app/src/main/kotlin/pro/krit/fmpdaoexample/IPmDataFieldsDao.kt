package pro.krit.fmpdaoexample

import pro.krit.hiveprocessor.annotations.FmpDao
import pro.krit.hiveprocessor.base.IDao

@FmpDao(
    resourceName = "ZSR_TORO_PM_DATA",
    tableName = "ET_DATA_FIELDS",
    parameters = ["IV_WERKS", "IV_LGORT"],
    fields = ["ID_Primary", "TASK_NUM_Int", "TPLNR"]
)
interface IPmDataFieldsDao : IDao.IFieldsDao {

}