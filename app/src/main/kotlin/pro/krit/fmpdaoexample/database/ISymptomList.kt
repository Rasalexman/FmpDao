package pro.krit.fmpdaoexample.database

import pro.krit.hiveprocessor.annotations.FmpDao
import pro.krit.hiveprocessor.base.IDao

// Перечень симптомов
@FmpDao(
    resourceName = "ZFM_PM_SYMPTOM_LIST",
    isDelta = true
)
interface ISymptomList : IDao.IFieldsDao