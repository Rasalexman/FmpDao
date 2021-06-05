package pro.krit.fmpdaoexample

import pro.krit.hiveprocessor.annotations.FmpDatabase
import pro.krit.hiveprocessor.provider.AbstractFmpDatabase
import pro.krit.hiveprocessor.provider.IFmpDatabase

@FmpDatabase
abstract class MainDatabase : AbstractFmpDatabase(), IMainDatabase {
    // You can use it if you want
    //abstract fun providePmDao(): IPmDataDao
    //abstract fun providePmLocalDao(): IPmDataLocalDao
}

interface IMainDatabase : IMainLocalDatabase, IMainRemoteDatabase, IFmpDatabase

interface IMainLocalDatabase {
    fun providePmLocalDao(): IPmDataLocalDao
}

interface IMainRemoteDatabase {
    fun providePmDao(): IPmDataDao
}