package pro.krit.fmpdaoexample

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.mobrun.plugin.api.HyperHiveState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
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
        println("----> all count = ${resultList.size}")

        val scope = CoroutineScope(Dispatchers.Main)
        scope.launch {
            pmLocalDao.flowable().flowOn(Dispatchers.IO).collect {
                println("----> flowable count = ${it.size}")
            }
        }

        val whereScope = CoroutineScope(Dispatchers.Main)
        whereScope.launch {
            pmLocalDao.flowableWhere("TYPE = \'${PmType.USER}\'").flowOn(Dispatchers.IO).collect {
                println("----> flowable PmType.USER count = ${it.size}")
            }
        }


        /*val insertScope = CoroutineScope(Dispatchers.Main)
        insertScope.launch {
            val random = Random.nextInt(5, 10)
            repeat(random) {
                insertSingleAsync(pmLocalDao)
                delay(100L)
            }

            val selectFirst = pmLocalDao.selectAllAsync(5)
            pmLocalDao.deleteAsync(selectFirst)
        }*/

        insertList(pmLocalDao)
        insertSingle(pmLocalDao)

        /*val updateLocalStatus = pmLocalDao.update()
        println("----> updateStatus = ${updateLocalStatus.isOk}")

        val requestLocal = pmLocalDao.newRequest()
        println("----> requestLocal = $requestLocal")*/

        val resultLimitLocal = pmLocalDao.selectAll(limit = 50)
        /*val first = resultLimitLocal.lastOrNull()
        first?.let {
            deleteSingle(pmLocalDao, it)
        }*/
        val count = 10
        if(resultLimitLocal.size > count) {
            val listToDelete = resultLimitLocal.subList(resultLimitLocal.size-count, resultLimitLocal.size)
            deleteList(pmLocalDao, listToDelete)
        }

        val resultAfterDeleteLocal = pmLocalDao.selectAll()
        println("----> resultAfterDeleteLocal = ${resultAfterDeleteLocal.size}")
    }

    private fun deleteList(dao: IPmDataLocalDao, list: List<PmEtDataLocalEntity>) {
        val deleteListStatus = dao.delete(list)
        println("----> deleteListStatus of ${list.size} = ${deleteListStatus.isOk}")
    }

    private fun deleteSingle(dao: IPmDataLocalDao, single: PmEtDataLocalEntity) {
        val deleteSingleStatus = dao.delete(single)
        println("----> deleteSingleStatus = ${deleteSingleStatus.isOk}")
    }

    private fun insertSingle(dao: IPmDataLocalDao) {
        val statusInsert = dao.insertOrReplace(
            PmEtDataLocalEntity(
                id = Random.nextInt(),
                marker = UUID.randomUUID().toString().take(18),
                auart = UUID.randomUUID().toString().take(12)
            )
        )
        println("-----> insertSingle = ${statusInsert.isOk}")
    }

    private suspend fun insertSingleAsync(dao: IPmDataLocalDao) {
        val statusInsert = dao.insertOrReplaceAsync(
            PmEtDataLocalEntity(
                id = Random.nextInt(),
                marker = UUID.randomUUID().toString().take(18),
                auart = UUID.randomUUID().toString().take(12)
            )
        )
        println("-----> insertStatus = ${statusInsert.isOk}")
    }

    private fun insertList(dao: IPmDataLocalDao) {
        val localListToInsert = mutableListOf<PmEtDataLocalEntity>()
        val randomCount = Random.nextInt(21, 50)
        repeat(randomCount) {
            val type = if(it%2 == 0) PmType.USER else PmType.ADMIN

            localListToInsert.add(PmEtDataLocalEntity(
                id = Random.nextInt(),
                marker = UUID.randomUUID().toString().take(18),
                auart = UUID.randomUUID().toString().take(12),
                type = type
            ))
        }
        val insertAllStatus = dao.insertOrReplace(localListToInsert)
        println("-----> insertList of ${localListToInsert.size} = ${insertAllStatus.isOk}")
    }
}