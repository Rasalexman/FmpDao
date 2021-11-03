package pro.krit.fmpdaoexample

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.mobrun.plugin.api.HyperHiveState
import com.mobrun.plugin.api.request_assistant.NumeratedFields
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pro.krit.fmpdaoexample.fmpresources.IZtMp01Request
import pro.krit.generated.dao.*
import pro.krit.generated.database.MainDatabaseImpl
import pro.krit.generated.request.ZfmPmGetSetRequestEtDataModel
import pro.krit.generated.request.ZfmPmGetSetRequestParams
import pro.krit.generated.request.ZtMp01RequestRespondStatus
import pro.krit.generated.request.ZtMp01RequestResultModel
import pro.krit.hiveprocessor.extensions.*
import pro.krit.hiveprocessor.provider.DatabaseConfig
import java.util.*
import kotlin.random.Random
import kotlin.reflect.KClass

@Suppress("UNUSED_PARAMETER", "UNUSED_VARIABLE")
class MainActivity : AppCompatActivity() {

    companion object {
        const val DEBUG_LOGIN = "334455"
        const val DEBUG_PASSWORD = "334455"
    }

    lateinit var mainDb: IMainDatabase
    lateinit var pmFieldDao: IPmDataFieldsDao
    lateinit var pmLocalDao: IPmDataLocalDao
    lateinit var pmRemoteDao: IPmDataDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        this.findViewById<Button>(R.id.request01Button)?.setOnClickListener {
            makeRequest01Map()
        }
        this.findViewById<Button>(R.id.request02Button)?.setOnClickListener {
            insertFieldsList(pmFieldDao)
        }

        this.findViewById<Button>(R.id.request03Button)?.setOnClickListener {
            insertLocalList(pmLocalDao)
        }


        val serverAddress = "http://10.100.205.123"
        val environment = "MTORO"
        val project = "project"

        val fmpDbName = serverAddress
            .replace("/", "")
            .replace(".", "_")
            .replace(":", "_") +
                "_" + environment +
                "_" + project + ".sqlite"

        val dbPath = this.applicationContext.getDatabasePath(fmpDbName).path

        val hyperHiveConfig = DatabaseConfig(
            serverAddress = serverAddress,
            environment = environment,
            project = project,
            dbPath = dbPath
        )


        mainDb = MainDatabaseImpl.initialize(
            hState = HyperHiveState(this).setHandler(Handler(Looper.getMainLooper())),
            config = hyperHiveConfig
        )
        val dbState = mainDb.openDatabase()

        println("-----> dbState = $dbState")
        val headers = mapOf(
            "X-SUP-DOMAIN" to "DM-MAIN",
            "Content-Type" to "application/json",
            "X-Version-App" to "2.0",
            "X-Phone" to "X-OS",
            "Device-Id" to "sasasdw9272sadsadsda"
        )
        mainDb.setDefaultHeaders(headers)

        val dbApi = mainDb.databaseApi
        val hyperHive = mainDb.provideHyperHive()
        pmLocalDao = mainDb.providePmLocalDao()
        val status = hyperHive.authAPI.auth(DEBUG_LOGIN, DEBUG_PASSWORD, true).execute()
        /*try {
            val resultSchema = hyperHive.authAPI.resourcesBaseStatus().execute()
            println("-----> Base schema status = $resultSchema")
        } catch (e: Exception) {
            println("-----> Cannot load schema = ${e.message}")
        }*/

        val zmpGetSet = mainDb.provideZmGetSet()
        val param = ZfmPmGetSetRequestParams(
            ivUser = "bolonin_dn@vsw.ru"
        )
        val mp = mutableMapOf<String, Class<out NumeratedFields>>()
        mp.put("ET_DATA", ZfmPmGetSetRequestEtDataModel::class.java)
        val result = zmpGetSet.requestListStatus(param)
        println("-----> ZfmPmGetSetRequestParams result = $result")
    }

    private fun makeRequest01Map() {
        val request: IZtMp01Request = mainDb.provideIZtMp01Request()
        val paramsMap = request.createParamsMap("0001", "0001")
        val params = request.createParams("0001", "0001")
        makeRequest01(params)
    }

    private fun makeRequest01(params: Any?) {
        val scope = CoroutineScope(Dispatchers.Main)
        scope.launch {
            val request: IZtMp01Request = mainDb.provideIZtMp01Request()
            /*val result1 = request.requestResultAsync<ZtMp01RequestResultModel, ZtMp01RequestRespondStatus>(
                params
            )
            result1.fold(onSuccess = {
                Toast.makeText(this@MainActivity, "SUCCESS", Toast.LENGTH_SHORT).show()
            }, onFailure = {
                it.printStackTrace()
                Toast.makeText(this@MainActivity, "FAIL REQUEST", Toast.LENGTH_SHORT).show()
            })*/

            val request2 = request.requestStatusAsync<ZtMp01RequestResultModel, ZtMp01RequestRespondStatus>(
                params
            )
            println("------> request2 = $request2")

            val request3 = request.requestAsync<ZtMp01RequestResultModel, ZtMp01RequestRespondStatus>(
                params
            )
            println("------> request3 = $request3")
        }

    }

    private fun exampleWithLocalDao(pmLocalDao: IPmDataLocalDao) {
        val scope = CoroutineScope(Dispatchers.Main)
        scope.launch {
            withContext(Dispatchers.IO) { selectLocalDaoAsync() }

            val resultList = pmLocalDao.selectAsync()
            println("----> all count = ${resultList.mapNotNull { it.convertTo() }}")

            pmLocalDao.flowable(withDistinct = true).flowOn(Dispatchers.IO).collect {
                println("----> pmLocalDao count = ${it.size}")
            }
        }

        scope.launch {
            pmLocalDao.flowable("TYPE = \'${PmType.USER}\' ORDER BY QWERTY ASC").flowOn(Dispatchers.IO).collect {
                println("----> pmLocalDao PmType.USER count = ${it.size}")
            }
        }
    }

    private fun insertLocalList(dao: IPmDataLocalDao) {
        val scope = CoroutineScope(Dispatchers.IO)
        scope.launch {
            selectLocalDaoAsync()
        }

        val localListToInsert = mutableListOf<PmEtDataLocalEntity>()
        val randomCount = Random.nextInt(21, 50)
        repeat(randomCount) {
            val type = if(it%2 == 0) PmType.USER else PmType.ADMIN

            localListToInsert.add(PmEtDataLocalEntity(
                //id = Random.nextInt(),
                marker = UUID.randomUUID().toString().take(18),
                auart = UUID.randomUUID().toString().take(12),
                index = Random.nextInt(10, 10000000),
                type = type,
            ))
        }
        val insertAllStatus = dao.insertOrReplace(localListToInsert, notifyAll = true)
        println("-----> insertList of ${localListToInsert.size} = ${insertAllStatus.isOk}")
    }

    private suspend fun selectLocalDaoAsync() {
        //pmLocalDao.dele

        val resultList = pmLocalDao.selectAsync<PmEtDataLocalEntity, PmLocalStatus>()
        //pmLocalDao.selectResultAsync()
        println("----> all count = ${resultList.mapNotNull { it.convertTo() }}")
    }

    private fun exampleWithFieldsDao(pmFieldDao: IPmDataFieldsDao) {
        val resultList = pmFieldDao.select<PmDataFieldsDaoModel, PmDataFieldsDaoStatus>()
        println("----> IPmDataFieldsDao select all count = ${resultList.size}")

        val scope = CoroutineScope(Dispatchers.Main)
        scope.launch {
            val resultListAsync = pmFieldDao.selectAsync<PmDataFieldsDaoModel, PmDataFieldsDaoStatus>()
            println("----> IPmDataFieldsDao selectAsync all count = ${resultListAsync.size}")

            pmFieldDao.flowable<PmDataFieldsDaoModel, PmDataFieldsDaoStatus>(withDistinct = true).flowOn(Dispatchers.IO).collect {
                println("----> pmFieldDao CHANGED")
            }
        }

        scope.launch {
            pmFieldDao.flowableCount(withDistinct = true).flowOn(Dispatchers.IO).collect {
                println("----> pmFieldDao COUNT = $it")
            }
        }
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
                marker = UUID.randomUUID().toString().take(18),
                auart = UUID.randomUUID().toString().take(12),
                index = Random.nextInt(10, 10000000),
                type = PmType.USER
            ), notifyAll = true
        )
        println("-----> insertSingle = ${statusInsert.isOk}")
    }

    private suspend fun insertSingleAsync(dao: IPmDataLocalDao) {
        val randomvalue = Random.nextInt(1, 10)
        val type = if(randomvalue%2 == 0) PmType.USER else PmType.ADMIN
        val statusInsert = dao.insertOrReplaceAsync(
            PmEtDataLocalEntity(
                //id = Random.nextInt(),
                marker = UUID.randomUUID().toString().take(18),
                auart = UUID.randomUUID().toString().take(12),
                index = Random.nextInt(10, 10000000),
                type = type
            )
        )
        println("-----> insertStatus = ${statusInsert.isOk}")
    }

    private fun insertFieldsList(dao: IPmDataFieldsDao) {
        val localListToInsert = mutableListOf<PmDataFieldsDaoModel>()
        val randomCount = Random.nextInt(21, 50)
        repeat(randomCount) {
            val tplnr = if(it%2 == 0) PmType.USER else PmType.ADMIN
            localListToInsert.add(PmDataFieldsDaoModel(
                tplnr = UUID.randomUUID().toString().take(18),
                taskNum = Random.nextInt(10, 10000000)
            ))
        }
        val insertAllStatus = dao.insertOrReplace(localListToInsert, notifyAll = true)
        println("-----> insertList of ${localListToInsert.size} = ${insertAllStatus.isOk}")
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

    //insertLocalList(pmLocalDao)
    //insertSingle(pmLocalDao)

    /*val updateLocalStatus = pmLocalDao.update()
    println("----> updateStatus = ${updateLocalStatus.isOk}")

    val requestLocal = pmLocalDao.newRequest()
    println("----> requestLocal = $requestLocal")*/
    //val resultLimitLocal = pmLocalDao.select(limit = 50)
    /*val first = resultLimitLocal.lastOrNull()
    first?.let {
        deleteSingle(pmLocalDao, it)
    }*/
    /*val count = 10
    if(resultLimitLocal.size > count) {
        val listToDelete = resultLimitLocal.subList(resultLimitLocal.size-count, resultLimitLocal.size)
        //deleteList(pmLocalDao, listToDelete)
    }*/

    //val resultAfterDeleteLocal = pmLocalDao.selectAll()
    //println("----> resultAfterDeleteLocal = ${resultAfterDeleteLocal.size}")

}