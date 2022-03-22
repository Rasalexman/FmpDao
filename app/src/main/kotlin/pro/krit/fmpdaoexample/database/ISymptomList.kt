package pro.krit.fmpdaoexample.database

import pro.krit.core.annotations.FmpDao
import pro.krit.core.base.IDao

// Перечень симптомов
@FmpDao(
    resourceName = "ZFM_PM_SYMPTOM_LIST",
    isDelta = true
)
interface ISymptomList : IDao.IFieldsDao