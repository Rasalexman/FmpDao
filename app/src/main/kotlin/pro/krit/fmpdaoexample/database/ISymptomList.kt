package pro.krit.fmpdaoexample.database

import pro.krit.processor.annotations.FmpDao
import pro.krit.processor.base.IDao

// Перечень симптомов
@FmpDao(
    resourceName = "ZFM_PM_SYMPTOM_LIST",
    isDelta = true
)
interface ISymptomList : IDao.IFieldsDao