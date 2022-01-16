package pro.krit.fmpdaoexample

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.rasalexman.sresult.common.extensions.applyIfSuccessSuspend
import com.rasalexman.sresult.common.extensions.doAsync
import com.rasalexman.sresult.common.extensions.logg
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pro.krit.fmpdaoexample.database.CreateDataBaseUseCase
import pro.krit.fmpdaoexample.database.parseTable
import pro.krit.fmpdaoexample.fmpresources.IZtMp01Request
import pro.krit.generated.dao.PmDataFieldsDaoModel
import pro.krit.generated.dao.PmDataFieldsDaoStatus
import pro.krit.generated.request.ZfmPmGetSetRequestEtAuthGrModel
import pro.krit.generated.request.ZfmPmGetSetRequestEtAuthUserModel
import pro.krit.generated.request.ZfmPmGetSetRequestParams
import pro.krit.hiveprocessor.extensions.*
import java.util.*
import kotlin.random.Random

@Suppress("UNUSED_PARAMETER", "UNUSED_VARIABLE")
class MainActivity : AppCompatActivity(R.layout.activity_main) {

    companion object {
        const val DEBUG_LOGIN = "35069"
        const val DEBUG_PASSWORD = "35069"
    }

    private val dataBaseHolder = CreateDataBaseUseCase()

    lateinit var mainDb: IMainDatabase
    lateinit var pmFieldDao: IPmDataFieldsDao
    lateinit var pmLocalDao: IPmDataLocalDao
    lateinit var pmRemoteDao: IPmDataDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.findViewById<Button>(R.id.request01Button)?.setOnClickListener {
            makeRequest01Map()
        }
        this.findViewById<Button>(R.id.request02Button)?.setOnClickListener {
            insertFieldsList(pmFieldDao)
        }

        this.findViewById<Button>(R.id.request03Button)?.setOnClickListener {
            insertLocalList(pmLocalDao)
        }

        this.findViewById<Button>(R.id.authOnlineDB)?.setOnClickListener {
            authOnline()
        }

        this.findViewById<Button>(R.id.authOfflineDB)?.setOnClickListener {
            authOffline()
        }
    }

    private fun loadGetSetUserData() {
        pmLocalDao = mainDb.providePmLocalDao()
        val zmpGetSet = mainDb.provideZmGetSet()
        val param = ZfmPmGetSetRequestParams(
            ivUser = "bolonin_dn@vsw.ru"
        )
        val status = zmpGetSet.requestListStatus(param)
        println("-----> ZfmPmGetSetRequestParams result = $status")

        val userCredentials =
            status.parseTable<ZfmPmGetSetRequestEtAuthGrModel>("ET_AUTH_GR")
        val userData = status.parseTable<ZfmPmGetSetRequestEtAuthUserModel>("ET_AUTH_USER")
            .firstOrNull()
    }

    private fun authOnline() {
        val state =
            dataBaseHolder.createAndOpenDatabase(DEBUG_LOGIN, "omk", this.applicationContext)
        if (state.isOpened) {
            mainDb = dataBaseHolder.mainDb
            val scope = CoroutineScope(Dispatchers.Main)
            scope.launch {
                val result = doAsync { dataBaseHolder.authUser(DEBUG_LOGIN, DEBUG_PASSWORD) }
                result.applyIfSuccessSuspend {
                    logg { "AUTH SUCCESS with token: $it" }

                    loadGetSetUserData()
                }
            }
        }
    }

    private fun authOffline() {

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

            /*val request2 = request.requestStatusAsync<ZtMp01RequestResultModel, ZtMp01RequestRespondStatus>(
                params
            )
            println("------> request2 = $request2")*/

            /*val request3 = request.requestAsync<ZtMp01RequestResultModel, ZtMp01RequestRespondStatus>(
                params
            )
            println("------> request3 = $request3")*/
        }

    }

    private fun exampleWithLocalDao(pmLocalDao: IPmDataLocalDao) {
        val scope = CoroutineScope(Dispatchers.Main)
        scope.launch {
            withContext(Dispatchers.IO) { selectLocalDaoAsync() }

            /*val resultList = pmLocalDao.select()
            println("----> all count = ${resultList.mapNotNull { it.convertTo() }}")*/

            pmLocalDao.flowable(withDistinct = true).flowOn(Dispatchers.IO).collect {
                println("----> pmLocalDao count = ${it.size}")
            }
        }

        scope.launch {
            pmLocalDao.flowable("TYPE = \'${PmType.USER}\' ORDER BY QWERTY ASC")
                .flowOn(Dispatchers.IO).collect {
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
            val type = if (it % 2 == 0) PmType.USER else PmType.ADMIN

            localListToInsert.add(
                PmEtDataLocalEntity(
                    //id = Random.nextInt(),
                    marker = UUID.randomUUID().toString().take(18),
                    auart = UUID.randomUUID().toString().take(12),
                    index = Random.nextInt(10, 10000000),
                    type = type,
                )
            )
        }
        val insertAllStatus = dao.insertOrReplace(localListToInsert, notifyAll = true)
        println("-----> insertList of ${localListToInsert.size} = ${insertAllStatus.isOk}")
    }

    private suspend fun selectLocalDaoAsync() {
        //pmLocalDao.dele

        val resultList = pmLocalDao.select<PmEtDataLocalEntity, PmLocalStatus>()
        //pmLocalDao.selectResultAsync()
        println("----> all count = ${resultList.mapNotNull { it.convertTo() }}")
    }

    private fun exampleWithFieldsDao(pmFieldDao: IPmDataFieldsDao) {
        val resultList = pmFieldDao.select<PmDataFieldsDaoModel, PmDataFieldsDaoStatus>()
        println("----> IPmDataFieldsDao select all count = ${resultList.size}")

        val scope = CoroutineScope(Dispatchers.Main)
        scope.launch {
            val resultListAsync = pmFieldDao.select<PmDataFieldsDaoModel, PmDataFieldsDaoStatus>()
            println("----> IPmDataFieldsDao selectAsync all count = ${resultList.size}")

            pmFieldDao.flowable<PmDataFieldsDaoModel, PmDataFieldsDaoStatus>(withDistinct = true)
                .flowOn(Dispatchers.IO).collect {
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
        val type = if (randomvalue % 2 == 0) PmType.USER else PmType.ADMIN
        val statusInsert = dao.insertOrReplace(
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
            val tplnr = if (it % 2 == 0) PmType.USER else PmType.ADMIN
            localListToInsert.add(
                PmDataFieldsDaoModel(
                    tplnr = UUID.randomUUID().toString().take(18),
                    taskNum = Random.nextInt(10, 10000000)
                )
            )
        }
        val insertAllStatus = dao.insertOrReplace<PmDataFieldsDaoModel, PmDataFieldsDaoStatus>(
            localListToInsert,
            notifyAll = true
        )
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