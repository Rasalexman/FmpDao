package pro.krit.fmpdaoexample

import pro.krit.hiveprocessor.annotations.FmpDatabase
import pro.krit.hiveprocessor.provider.HyperHiveDatabase
import pro.krit.hiveprocessor.provider.IHyperHiveDatabase

@FmpDatabase
abstract class MainDatabase : HyperHiveDatabase(), IMainDatabase {
    // You can use it if you want
    //abstract fun providePmDao(): IPmDataDao
    //abstract fun providePmLocalDao(): IPmDataLocalDao
}

interface IMainDatabase : IHyperHiveDatabase {
    fun providePmDao(): IPmDataDao
    fun providePmLocalDao(): IPmDataLocalDao
}