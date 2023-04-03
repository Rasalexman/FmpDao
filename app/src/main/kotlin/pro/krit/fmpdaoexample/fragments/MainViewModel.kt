package com.rasalexman.fmpdaoexample.fragments

import androidx.lifecycle.MutableLiveData
import com.rasalexman.sresult.common.extensions.*
import com.rasalexman.sresultpresentation.extensions.launchUITryCatch
import com.rasalexman.sresultpresentation.viewModels.BaseViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.rasalexman.fmpdaoexample.MainActivity
import com.rasalexman.fmpdaoexample.daos.IPmDataLocalDao
import com.rasalexman.fmpdaoexample.database.DataBaseHolder
import com.rasalexman.fmpdaoexample.database.IZfmToroSymptomList
import com.rasalexman.fmpdaoexample.fmpresources.Fields
import com.rasalexman.fmpdaoexample.models.PmEtDataLocalEntity
import com.rasalexman.fmpdaoexample.models.PmType
import com.rasalexman.generated.dao.ZfmToroSymptomListModel
import com.rasalexman.generated.dao.ZfmToroSymptomListStatus
import com.rasalexman.generated.request.ZfmPmGetSetRequestEtAuthGrModel
import com.rasalexman.generated.request.ZfmPmGetSetRequestEtAuthUserModel
import com.rasalexman.generated.request.ZfmPmGetSetRequestParams
import com.rasalexman.hhivecore.common.RequestExecuter.isNotBad
import com.rasalexman.hhivecore.extensions.*
import java.util.*
import kotlin.random.Random

class MainViewModel : BaseViewModel() {

    override val toolbarTitle: MutableLiveData<String> = MutableLiveData("Main")

    private val pmLocalDao: IPmDataLocalDao by lazy {
        DataBaseHolder.mainDb.providePmLocalDao()
    }
    private val zfmToroSymptomList: IZfmToroSymptomList by lazy {
        DataBaseHolder.mainDb.provideIZfmToroSymptomList()
    }

    val status: MutableLiveData<String> by unsafeLazy {
        MutableLiveData(if (DataBaseHolder.isDbOpened) "DB OPENED" else "DB NOT OPEN")
    }

    fun authOnline() = launchUITryCatch {
        processLoading(true)
        val result = doAsync {
            DataBaseHolder.authUser(
                MainActivity.DEBUG_LOGIN,
                MainActivity.DEBUG_PASSWORD
            )
        }
        result.applyIfSuccessSuspend {
            logg { "AUTH SUCCESS with token: $it" }
            loadGetSetUserData()
        }.applyIfSuccessSuspend {
            loadSymptoms()
        }.applyIfSuccessSuspend {
            status.postValue("USER AUTHORIZED")
        }
        processLoading(false)
    }

    private suspend fun loadGetSetUserData() = doAsync {
        val zmpGetSet = DataBaseHolder.mainDb.provideZmGetSet()
        val param = ZfmPmGetSetRequestParams(
            ivUser = MainActivity.DEBUG_LOGIN
        )
        val status = zmpGetSet.requestListStatus(param)
        println("-----> ZfmPmGetSetRequestParams result = $status")

        val userCredentials =
            status.parseTable<ZfmPmGetSetRequestEtAuthGrModel>("ET_AUTH_GR")
        val userData = status.parseTable<ZfmPmGetSetRequestEtAuthUserModel>("ET_AUTH_USER")
            .firstOrNull()

        println("-----> loadGetSetUserData userCredentials = $userCredentials")
        println("-----> loadGetSetUserData userData = $userData")
        if(status.isNotBad()) {
            anySuccess()
        } else {
            errorResult(message = status.getErrorMessage())
        }
    }

    private suspend fun loadSymptoms() = doAsync {
        val request = DataBaseHolder.mainDb.provideIZfmToroSymptomList().requestBuilder()
        val status = request.streamCallAuto().execute()
        println("-----> loadSymptoms ${status.isNotBad()}")
    }

    fun getSymptoms() = launchUITryCatch {
        processLoading(true)
        val localRbnr = 100
        val allData: List<ZfmToroSymptomListModel> = doAsync {
            zfmToroSymptomList.select<ZfmToroSymptomListModel, ZfmToroSymptomListStatus> {
                where = "${Fields.RBNR_Int} = $localRbnr"
            }
        }
        println("-----> getSymptoms ${allData.size}")
        processLoading(false)
    }

    fun authOffline() {

    }

    fun onShowUsersHandler() {
        navigationLiveData.value =
            MainFragmentDirections.actionMainFragmentToUsersFragment().toNavigateResult()
    }

    fun insertLocalList() {
        val scope = CoroutineScope(Dispatchers.IO)
        scope.launch {
            val selectedSize = selectLocalDaoAsync()
            if (selectedSize == 0) {
                val localListToInsert = mutableListOf<PmEtDataLocalEntity>()
                val randomCount = Random.nextInt(4100, 8000)
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
                val insertAllStatus = doAsync {
                    pmLocalDao.insertOrReplace(localListToInsert, notifyAll = true)
                }
                println("-----> insertList of ${localListToInsert.size} = ${insertAllStatus.isOk}")
            }
        }
    }

    private suspend fun selectLocalDaoAsync(): Int = doAsync {
        val fields = listOf(Fields.MARKER, Fields.TASK_NUM, Fields.LOCAL_ID)
        val allData = pmLocalDao.select()
        val allCount = pmLocalDao.count(byField = Fields.MARKER)
        println("----> all count = $allCount")
        val convertedSize = if (allData.isNotEmpty()) {
            //val first = allData.first()
            val resultList = pmLocalDao.select {
                this.where = "${Fields.IS_LOCAL} = 'false'"
                this.fields = fields
            }
            val converted = resultList.mapNotNull { it.convertTo() }
            converted.size
        } else 0

        val deleteStatus = pmLocalDao.delete() //where = "${Fields.IS_LOCAL} = 'false'"

        println("----> delete status = ${deleteStatus.isNotBad()}")
        println("----> converted count = $convertedSize")
        convertedSize
    }

    private fun processLoading(isLoading: Boolean) {
        supportLiveData.postValue(
            if (isLoading) {
                loadingResult()
            } else {
                emptyResult()
            }
        )
    }
}