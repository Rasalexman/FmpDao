package pro.krit.fmpdaoexample

import pro.krit.hiveprocessor.annotations.FmpDatabase
import pro.krit.hiveprocessor.provider.HyperHiveDatabase

@FmpDatabase
abstract class MainDatabase : HyperHiveDatabase(), IMainDatabase {
    // You can use it if you want
    //abstract fun providePmDao(): IPmDataDao
    //abstract fun providePmLocalDao(): IPmDataLocalDao
}

interface IMainDatabase {
    fun providePmDao(): IPmDataDao
    fun providePmLocalDao(): IPmDataLocalDao
}