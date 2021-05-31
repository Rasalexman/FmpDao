package pro.krit.fmpdaoexample

import android.content.Context
import com.mobrun.plugin.api.HyperHiveState
import com.rasalexman.kodi.annotations.BindSingle
import com.rasalexman.sresult.common.extensions.logg
import pro.krit.generated.database.MainDatabaseImpl
import pro.krit.hiveprocessor.annotations.FmpDatabase
import pro.krit.hiveprocessor.provider.HyperHiveConfig
import pro.krit.hiveprocessor.provider.HyperHiveDatabase

@FmpDatabase
abstract class MainDatabase : HyperHiveDatabase() {

    abstract fun providePmDao(): IPmDataDao
    abstract fun providePmLocalDao(): IPmDataLocalDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: MainDatabase? = null

        fun getDatabase(context: Context, hiperHiveState: HyperHiveConfig): MainDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = MainDatabaseImpl()
                instance.initialize(HyperHiveState(context), hiperHiveState)
                INSTANCE = instance
                return instance
            }
        }
    }
}