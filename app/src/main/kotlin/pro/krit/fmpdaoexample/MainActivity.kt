package pro.krit.fmpdaoexample

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.mobrun.plugin.api.HyperHiveState
import pro.krit.generated.database.MainDatabaseImpl
import pro.krit.hiveprocessor.extensions.*
import pro.krit.hiveprocessor.provider.HyperHiveConfig
import java.util.*
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val serverAddress = "http://your.server.address"
        val environment = "android_environment"
        val project = "project"

        val fmpDbName = serverAddress
            .replace("/", "")
            .replace(".", "_")
            .replace(":", "_") +
                "_" + environment +
                "_" + project + ".sqlite"

        val dbPath = this.applicationContext.getDatabasePath(fmpDbName).path

        val hyperHiveConfig = HyperHiveConfig(
            dbKey = "Main",
            serverAddress = serverAddress,
            environment = environment,
            project = project,
            dbPath = dbPath
        )

        val mainDb: IMainDatabase = MainDatabaseImpl.initialize(
            hState = HyperHiveState(this).setHandler(Handler(Looper.getMainLooper())),
            config = hyperHiveConfig
        )
        val dbState = mainDb.openDatabase()
        println("-----> dbState = $dbState")
        val pmLocalDao = mainDb.providePmLocalDao()
        val resultList = pmLocalDao.selectAll()
        println("----> resultList = ${resultList.size}")

        insertList(pmLocalDao, resultList.size)
        insertSingle(pmLocalDao)

        val updateLocalStatus = pmLocalDao.update()
        println("----> updateStatus = ${updateLocalStatus.isOk}")

        val requestLocal = pmLocalDao.newRequest()
        println("----> requestLocal = $requestLocal")

        val resultLimitLocal = pmLocalDao.selectAll(limit = 50)
        println("----> resultLimitLocal = ${resultLimitLocal.size}")
        val first = resultLimitLocal.lastOrNull()
        first?.let {
            deleteSingle(pmLocalDao, it)
        }
        val listToDelete = resultLimitLocal.subList(resultLimitLocal.size-10, resultLimitLocal.size)
        deleteList(pmLocalDao, listToDelete)

        val resultAfterDeleteLocal = pmLocalDao.selectAll()
        println("----> resultAfterDeleteLocal = ${resultAfterDeleteLocal.size}")
    }

    private fun deleteList(dao: IPmDataLocalDao, list: List<PmEtDataLocalEntity>) {
        val deleteListStatus = dao.delete(list)
        println("----> deleteListStatus = ${deleteListStatus.isOk}")
    }

    private fun deleteSingle(dao: IPmDataLocalDao, single: PmEtDataLocalEntity) {
        val deleteSingleStatus = dao.delete(single)
        println("----> deleteSingleStatus = ${deleteSingleStatus.isOk}")
    }

    private fun insertSingle(dao: IPmDataLocalDao) {
        val statusInsert = dao.insertOrReplace(
            PmEtDataLocalEntity(
                id = Random.nextInt(),
                marker = "Hello",
                auart = "World"
            )
        )
        println("-----> insertStatus = ${statusInsert.isOk}")
    }

    private fun insertList(dao: IPmDataLocalDao, lastCount: Int) {
        val localListToInsert = mutableListOf<PmEtDataLocalEntity>()
        repeat(40) {
            localListToInsert.add(PmEtDataLocalEntity(
                id = lastCount+it+1,
                marker = UUID.randomUUID().toString(),
                auart = UUID.randomUUID().toString()
            ))
        }
        val insertAllStatus = dao.insertOrReplace(localListToInsert)
        println("-----> insertList = ${insertAllStatus.isOk}")
    }
}