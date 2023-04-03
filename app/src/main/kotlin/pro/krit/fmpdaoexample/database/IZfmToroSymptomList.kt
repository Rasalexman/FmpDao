package com.rasalexman.fmpdaoexample.database

import com.rasalexman.fmpdaoexample.fmpresources.Fields
import com.rasalexman.hhivecore.annotations.FmpDao
import com.rasalexman.hhivecore.base.IDao

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