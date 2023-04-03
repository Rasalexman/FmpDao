package com.rasalexman.fmpdaoexample.database

import com.rasalexman.hhivecore.annotations.FmpDao
import com.rasalexman.hhivecore.base.IDao

// Перечень симптомов
@FmpDao(
    resourceName = "ZFM_PM_SYMPTOM_LIST",
    isDelta = true
)
interface ISymptomList : IDao.IFieldsDao