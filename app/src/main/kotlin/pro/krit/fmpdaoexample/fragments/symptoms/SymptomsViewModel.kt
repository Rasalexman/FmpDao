package pro.krit.fmpdaoexample.fragments.symptoms

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.switchMap
import com.rasalexman.sresult.common.extensions.*
import com.rasalexman.sresult.common.typealiases.AnyResult
import com.rasalexman.sresult.common.typealiases.ResultList
import com.rasalexman.sresultpresentation.extensions.asyncLiveData
import com.rasalexman.sresultpresentation.extensions.launchUITryCatch
import com.rasalexman.sresultpresentation.extensions.mutableMap
import com.rasalexman.sresultpresentation.viewModels.BaseViewModel
import kotlinx.coroutines.Dispatchers
import pro.krit.fmpdaoexample.constants.ArgNames.SELECTED_SYMPTOM
import pro.krit.fmpdaoexample.constants.ArgNames.SELECTED_USER
import pro.krit.fmpdaoexample.daos.IUsersLocalDao
import pro.krit.fmpdaoexample.database.DataBaseHolder
import pro.krit.fmpdaoexample.database.IZfmToroSymptomList
import pro.krit.fmpdaoexample.fmpresources.Fields
import pro.krit.fmpdaoexample.models.SymptomItemUI
import pro.krit.fmpdaoexample.models.UserItemUI
import pro.krit.generated.dao.ZfmToroSymptomListModel
import pro.krit.generated.dao.ZfmToroSymptomListStatus
import pro.krit.hiveprocessor.extensions.createTable
import pro.krit.hiveprocessor.extensions.insertOrReplace
import pro.krit.hiveprocessor.extensions.select
import pro.krit.hiveprocessor.extensions.update
import kotlin.random.Random

class SymptomsViewModel(
    savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    private val selectedUser: LiveData<UserItemUI> = savedStateHandle.getLiveData(SELECTED_USER)
    private var lastSelectedSymptom: SymptomItemUI? = null

    override val toolbarTitle: MutableLiveData<String> = selectedUser.mutableMap {
        "Symptoms with rbnr: '${it.rbnr}'"
    }

    private val zfmToroSymptomList: IZfmToroSymptomList by lazy {
        DataBaseHolder.mainDb.provideIZfmToroSymptomList()
    }

    private val usersDao: IUsersLocalDao by lazy {
        DataBaseHolder.mainDb.provideUsersLocalDao()
    }

    val items: MutableLiveData<List<SymptomItemUI>> by lazy {
        resultLiveData.mutableMap {
            it.getList()
        }
    }

    override val resultLiveData: LiveData<ResultList<SymptomItemUI>> by unsafeLazy {
        selectedUser.switchMap { user ->
            asyncLiveData(Dispatchers.IO) {
                emit(loadingResult())

                val query = "${Fields.RBNR_Int} = ${user.rbnr}"
                val items =
                    zfmToroSymptomList.select<ZfmToroSymptomListModel, ZfmToroSymptomListStatus>(
                        where = query
                    )
                val converted = convertSymptoms(items, user.symptomCode)
                emit(converted)
            }
        }
    }

    private suspend fun convertSymptoms(
        input: List<ZfmToroSymptomListModel>,
        userSymptomCode: String
    ): ResultList<SymptomItemUI> = doAsync {
        //zfmToroSymptomList.delete<ZfmToroSymptomListModel>()
        val realInput = input.takeIf { it.isNotEmpty() } ?: createSymptoms()
        realInput.map { item ->
            val alreadySelected = userSymptomCode == item.symptomCode
            SymptomItemUI(
                symptomCode = item.symptomCode.orEmpty(),
                symptGrpCode = item.symptGrpCode.orEmpty(),
                symptomText = item.symptomText.orEmpty(),
                rbnr = item.rbnr.orZero().toStringOrEmpty()
            ).apply {
                if (alreadySelected) {
                    isSelected.set(alreadySelected)
                    lastSelectedSymptom = this
                }
            }
        }.toSuccessListResult()
    }

    private fun createSymptoms(): List<ZfmToroSymptomListModel> {
        val groups = Random.nextInt(10, 20)
        val codes = Random.nextInt(10, 30)
        val localSymptoms = mutableListOf<ZfmToroSymptomListModel>()
        repeat(groups) { group ->
            repeat(codes) { code ->
                val cGrp = group + 1
                val cCd = code + 1
                val realCode = "$cGrp$cCd"
                localSymptoms.add(
                    ZfmToroSymptomListModel(
                        symptomCode = realCode,
                        symptGrpCode = "$cGrp",
                        symptomText = "Симптом с кодом $realCode",
                        rbnr = Random.nextInt(100, 104)
                    )
                )
            }
        }
        zfmToroSymptomList.createTable<ZfmToroSymptomListModel>()
        zfmToroSymptomList.insertOrReplace(localSymptoms, true, withoutPrimaryKey = true)
        return localSymptoms
    }

    override fun onBackClicked() = launchUITryCatch {
        val isLastSelected = lastSelectedSymptom?.isSelected?.get().orFalse()
        supportLiveData.value = if (isLastSelected) {
            lastSelectedSymptom?.let { currentSymptom ->
                val saveResult = saveSymptomToUser(currentSymptom.symptomCode)
                saveResult.flatMapIfSuccessSuspend {
                    navigatePop(mapOf(SELECTED_SYMPTOM to currentSymptom))
                }
            }.orIfNull {
                navigatePop()
            }
        } else {
            val saveResult = saveSymptomToUser()
            saveResult.flatMapIfSuccessSuspend {
                navigatePop()
            }
        }
    }

    fun onSymptomClicked(item: SymptomItemUI) {
        if (lastSelectedSymptom != item) {
            lastSelectedSymptom?.dropSelection()
        }
        item.revertSelection()
        lastSelectedSymptom = item
    }

    private suspend fun saveSymptomToUser(symptomCode: String = ""): AnyResult = doAsync {
        val user = takeCurrentUser()
        if(user == null) {
            supportLiveData.postValue(errorResult("Юзер не выбран"))
            emptyResult()
        } else {
            val lastSymptomCode = user.symptomCode
            val isUpdateOk = if (lastSymptomCode != symptomCode) {
                val setQuery = "${Fields.SYMPTOM_CODE} = '$symptomCode'"
                val whereQuery = "${Fields.LOCAL_ID} = '${user.id}'"
                val updateStatus = usersDao.update(
                    setQuery = setQuery,
                    where = whereQuery,
                    notifyAll = true
                )
                updateStatus.isOk
            } else {
                true
            }
            if (isUpdateOk) {
                anySuccess()
            } else {
                emptyResult()
            }
        }
    }

    private fun takeCurrentUser(): UserItemUI? {
        return selectedUser.value
    }
}