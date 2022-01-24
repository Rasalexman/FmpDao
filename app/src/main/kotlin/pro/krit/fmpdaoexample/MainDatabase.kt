package pro.krit.fmpdaoexample

import pro.krit.fmpdaoexample.daos.*
import pro.krit.fmpdaoexample.database.IZfmToroSymptomList
import pro.krit.fmpdaoexample.fmpresources.*
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
    fun provideUsersLocalDao(): IUsersLocalDao
}

interface IMainRemoteDatabase {
    fun providePmDao(): IPmDataDao
    fun provideFieldsDao(): IPmDataFieldsDao

    fun provideZsMp01Dao(): IZsMp01Dao
    fun provideZsMp04Dao(): IZsMp04Dao

    fun provideIZfmToroSymptomList(): IZfmToroSymptomList
}

interface IMainRequest {
    fun provideIZtMp01Request(): IZtMp01Request
    fun provideIZtMp05Request(): IZtMp05Request
    fun provideIZtMp08Request(): IZtMp08Request
    fun provideSecondRequest(): SecondZtMp01Request
    fun provideZmGetSet(): IZfmPmGetSetRequest

}