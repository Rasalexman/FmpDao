package pro.krit.fmpdaoexample

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import com.rasalexman.sresult.common.extensions.applyIfSuccessSuspend
import com.rasalexman.sresult.common.extensions.doAsync
import com.rasalexman.sresult.common.extensions.logg
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import pro.krit.fmpdaoexample.database.CreateDataBaseUseCase
import pro.krit.fmpdaoexample.database.IZfmToroSymptomList
import pro.krit.fmpdaoexample.database.parseTable
import pro.krit.fmpdaoexample.databinding.ActivityMainBinding
import pro.krit.fmpdaoexample.fmpresources.Fields
import pro.krit.generated.dao.PmDataFieldsDaoModel
import pro.krit.generated.dao.PmDataFieldsDaoStatus
import pro.krit.generated.dao.ZfmToroSymptomListModel
import pro.krit.generated.dao.ZfmToroSymptomListStatus
import pro.krit.generated.request.ZfmPmGetSetRequestEtAuthGrModel
import pro.krit.generated.request.ZfmPmGetSetRequestEtAuthUserModel
import pro.krit.generated.request.ZfmPmGetSetRequestParams
import pro.krit.hiveprocessor.common.RequestExecuter.isNotBad
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

    private val pmLocalDao: IPmDataLocalDao by lazy {
        mainDb.providePmLocalDao()
    }
    private val zfmToroSymptomList: IZfmToroSymptomList by lazy {
        mainDb.provideIZfmToroSymptomList()
    }
    lateinit var pmRemoteDao: IPmDataDao

    val showLoading: MutableLiveData<Int> = MutableLiveData(View.GONE)
    val status: MutableLiveData<String> = MutableLiveData("DB NOT OPEN")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: ActivityMainBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.main = this
        binding.lifecycleOwner = this

        binding.request02Button.setOnClickListener {
            insertFieldsList(pmFieldDao)
        }

        openDataBase()
    }

    private fun openDataBase() {
        processLoading(true)
        val appContext = this.applicationContext
        val scope = CoroutineScope(Dispatchers.Main)
        scope.launch {
            delay(1000L)
            val state =
                doAsync { dataBaseHolder.createAndOpenDatabase(DEBUG_LOGIN, "omk", appContext) }
            if (state.isOpened) {
                mainDb = dataBaseHolder.mainDb
                status.value = "DB OPENED"
            } else {
                status.value = "DB NOT OPENED"
            }
            processLoading(false)
        }
    }

    fun authOnline() {
        processLoading(true)
        val scope = CoroutineScope(Dispatchers.Main)
        scope.launch {
            val result = doAsync { dataBaseHolder.authUser(DEBUG_LOGIN, DEBUG_PASSWORD) }
            result.applyIfSuccessSuspend {
                logg { "AUTH SUCCESS with token: $it" }
                loadGetSetUserData()
            }.applyIfSuccessSuspend {
                loadSymptoms()
            }
            processLoading(false)
        }
    }

    private suspend fun loadGetSetUserData() = doAsync {
        val zmpGetSet = mainDb.provideZmGetSet()
        val param = ZfmPmGetSetRequestParams(
            ivUser = DEBUG_LOGIN
        )
        val status = zmpGetSet.requestListStatus(param)
        println("-----> ZfmPmGetSetRequestParams result = $status")

        val userCredentials =
            status.parseTable<ZfmPmGetSetRequestEtAuthGrModel>("ET_AUTH_GR")
        val userData = status.parseTable<ZfmPmGetSetRequestEtAuthUserModel>("ET_AUTH_USER")
            .firstOrNull()

        println("-----> loadGetSetUserData $userCredentials")
    }

    private suspend fun loadSymptoms() = doAsync {
        val request = zfmToroSymptomList.requestBuilder()
        val status = request.streamCallAuto().execute()
        println("-----> loadSymptoms ${status.isNotBad()}")
    }

    fun getSymptoms() {
        processLoading(true)
        val scope = CoroutineScope(Dispatchers.Main)
        val localRbnr = 100
        scope.launch {
            val allData: List<ZfmToroSymptomListModel> = doAsync {
                zfmToroSymptomList.select<ZfmToroSymptomListModel, ZfmToroSymptomListStatus>(
                    where = "${Fields.RBNR_Int} = $localRbnr"
                )
            }
            println("-----> getSymptoms ${allData.size}")
            processLoading(false)
        }
    }

    fun authOffline() {

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

    fun insertLocalList() {
        val scope = CoroutineScope(Dispatchers.IO)
        scope.launch {
            val selectedSize = selectLocalDaoAsync()
            if(selectedSize == 0) {
                val localListToInsert = mutableListOf<PmEtDataLocalEntity>()
                val randomCount = Random.nextInt(21, 50)
                repeat(randomCount) {
                    val isLocal = it % 2 == 0
                    val type = if (isLocal) PmType.USER else PmType.ADMIN

                    localListToInsert.add(
                        PmEtDataLocalEntity(
                            id = UUID.randomUUID().toString(),
                            marker = UUID.randomUUID().toString().take(18),
                            auart = UUID.randomUUID().toString().take(12),
                            taskNum = Random.nextInt(10, 10000000),
                            type = type,
                            isLocal = isLocal
                        )
                    )
                }
                val insertAllStatus = pmLocalDao.insertOrReplace(localListToInsert, notifyAll = true)
                println("-----> insertList of ${localListToInsert.size} = ${insertAllStatus.isOk}")
            }
        }
    }

    private suspend fun selectLocalDaoAsync(): Int = doAsync {
        val fields = listOf(Fields.MARKER, Fields.TASK_NUM, Fields.LOCAL_ID)
        val allData = pmLocalDao.select()
        val allCount = pmLocalDao.count(byField = Fields.MARKER)
        println("----> all count = $allCount")
        val convertedSize = if(allData.isNotEmpty()) {
            val first = allData.first()
            val resultList = pmLocalDao.select(
                where = "${Fields.IS_LOCAL} = 'false'",
                fields = fields
            )
            val converted = resultList.mapNotNull { it.convertTo() }
            converted.size
        } else 0

        val deleteStatus = pmLocalDao.delete(where = "${Fields.IS_LOCAL} = 'false'")

        println("----> delete status = ${deleteStatus.isNotBad()}")
        println("----> converted count = $convertedSize")
        convertedSize
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
                taskNum = Random.nextInt(10, 10000000),
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
                taskNum = Random.nextInt(10, 10000000),
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
        val insertAllStatus = dao.insertOrReplace(
            localListToInsert,
            notifyAll = true
        )
        println("-----> insertList of ${localListToInsert.size} = ${insertAllStatus.isOk}")
    }

    private fun processLoading(isLoading: Boolean) {
        showLoading.postValue(
            if(isLoading) {
                View.VISIBLE
            } else {
                View.GONE
            }
        )
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