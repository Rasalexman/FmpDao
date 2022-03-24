package pro.krit.fmpdaoexample.database

import pro.krit.hhivecore.annotations.FmpDao
import pro.krit.hhivecore.base.IDao

// Перечень симптомов
@FmpDao(
    resourceName = "ZFM_PM_SYMPTOM_LIST",
    isDelta = true
)
interface ISymptomList : IDao.IFieldsDao