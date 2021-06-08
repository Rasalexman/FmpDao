package pro.krit.fmpdaoexample

import pro.krit.hiveprocessor.annotations.FmpDatabase
import pro.krit.hiveprocessor.provider.AbstractFmpDatabase
import pro.krit.hiveprocessor.provider.IFmpDatabase

@FmpDatabase(asDaoProvider = false)
abstract class MainDatabase : AbstractFmpDatabase(), IMainDatabase {
    // You can use it if you want
    //abstract fun providePmDao(): IPmDataDao
    //abstract fun providePmLocalDao(): IPmDataLocalDao
    //abstract fun provideFieldsDao(): IPmDataFieldsDao
    //abstract fun provideIZtMp01Request(): IZtMp01Request
}

interface IMainDatabase : IMainLocalDatabase, IMainRemoteDatabase, IMainRequest, IFmpDatabase

interface IMainLocalDatabase {
    fun providePmLocalDao(): IPmDataLocalDao
}

interface IMainRemoteDatabase {
    fun providePmDao(): IPmDataDao
    fun provideFieldsDao(): IPmDataFieldsDao
}

interface IMainRequest {
    fun provideIZtMp01Request(): IZtMp01Request
    fun provideSecondRequest(): SecondZtMp01Request
}