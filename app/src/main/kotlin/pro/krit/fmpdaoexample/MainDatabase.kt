package pro.krit.fmpdaoexample

import pro.krit.hiveprocessor.annotations.FmpDatabase
import pro.krit.hiveprocessor.provider.HyperHiveDatabase

@FmpDatabase
abstract class MainDatabase : HyperHiveDatabase() {

    abstract fun providePmDao(): IPmDataDao
    abstract fun providePmLocalDao(): IPmDataLocalDao
}