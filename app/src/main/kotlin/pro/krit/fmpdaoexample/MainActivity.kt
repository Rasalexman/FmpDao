package pro.krit.fmpdaoexample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mobrun.plugin.api.HyperHiveState
import pro.krit.generated.database.MainDatabaseImpl
import pro.krit.hiveprocessor.extensions.selectAll
import pro.krit.hiveprocessor.provider.HyperHiveConfig

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val hyperHiveConfig: HyperHiveConfig = HyperHiveConfig(
            dbKey = "",
            serverAddress = "",
            environment = "",
            project = "",
            dbPath = ""
        )
        val mainDb: MainDatabase = MainDatabaseImpl()
        mainDb.initialize(HyperHiveState(this.applicationContext), hyperHiveConfig)
        val pmDao = mainDb.providePmDao()
        pmDao.selectAll(limit = 50L)
    }
}