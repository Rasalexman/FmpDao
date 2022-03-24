package pro.krit.fmpdaoexample.database

import pro.krit.fmpdaoexample.fmpresources.Fields
import pro.krit.hhivecore.annotations.FmpDao
import pro.krit.hhivecore.base.IDao

// Перечень симптомов
@FmpDao(
    resourceName = "ZFM_PM_SYMPTOM_LIST",
    tableName = "ET_SYMPT_LIST",
    fields = [
        Fields.SYMPTOM_CODE, Fields.SYMPT_GRP_CODE, Fields.SYMPTOM_TEXT, Fields.RBNR_Int
    ],
    isDelta = true
)
interface IZfmToroSymptomList : IDao.IFieldsDao