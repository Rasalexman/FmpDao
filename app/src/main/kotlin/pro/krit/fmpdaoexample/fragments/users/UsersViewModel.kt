package pro.krit.fmpdaoexample.fragments.users

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import com.rasalexman.sresult.common.extensions.*
import com.rasalexman.sresult.common.typealiases.ResultList
import com.rasalexman.sresultpresentation.extensions.asyncLiveData
import com.rasalexman.sresultpresentation.extensions.mutableMap
import com.rasalexman.sresultpresentation.viewModels.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import pro.krit.hhivecore.extensions.count
import pro.krit.hhivecore.extensions.flowable
import pro.krit.hhivecore.extensions.insertOrReplace
import pro.krit.fmpdaoexample.daos.IUsersLocalDao
import pro.krit.fmpdaoexample.database.DataBaseHolder
import pro.krit.fmpdaoexample.fmpresources.Fields
import pro.krit.fmpdaoexample.models.PmType
import pro.krit.fmpdaoexample.models.UserEntity
import pro.krit.fmpdaoexample.models.UserItemUI
import java.util.*
import kotlin.random.Random

class UsersViewModel : BaseViewModel() {

    override val toolbarTitle: MutableLiveData<String> = MutableLiveData("Users")

    val items: MutableLiveData<List<UserItemUI>> by lazy {
        resultLiveData.mutableMap {
            it.data.orEmpty()
        }
    }

    private val usersDao: IUsersLocalDao by lazy {
        DataBaseHolder.mainDb.provideUsersLocalDao()
    }

    private val usersFlowable: Flow<List<UserEntity>>
        get() = usersDao.flowable {
            orderBy = "${Fields.TAB_NUM} ASC"
            isDevastate = false
        }.flowOn(Dispatchers.IO)

    override val resultLiveData: LiveData<ResultList<UserItemUI>> by unsafeLazy {
        usersFlowable.asLiveData(timeoutInMs = 0L).switchMap { usersList ->
            asyncLiveData {
                emit(loadingResult())
                val currentUsers = if (usersList.size < 200) {
                    createUsers()
                } else {
                    usersList
                }
                val convertedUsers: ResultList<UserItemUI> =
                    currentUsers.toSuccessListResult().mapListTo()
                emit(convertedUsers)
            }
        }
    }

    private suspend fun createUsers(): List<UserEntity> = doAsync {
        val randomCount = Random.nextInt(10, 200)
        val freshUsers = mutableListOf<UserEntity>()
        repeat(randomCount) {
            val isLocal = it % 2 == 0
            val type = if (isLocal) PmType.USER else PmType.ADMIN
            freshUsers.add(
                UserEntity(
                    id = UUID.randomUUID().toString(),
                    marker = type,
                    userName = createRandomName(),
                    tabNumber = Random.nextInt(892232, 21312334),
                    rbnr = Random.nextInt(100, 104)
                )
            )
        }
        /*
        val deleteStatus = usersDao.delete(existUsers)
        println("deleteStatus is OK - ${deleteStatus.isOk}")
        */
        val usersAll = usersDao.count()
        println("all users count - $usersAll")
        val insertStatus = usersDao.insertOrReplace(freshUsers)
        println("insertStatus is OK - ${insertStatus.isOk}")
        freshUsers
    }

    fun onUserClicked(item: UserItemUI) {
        navigationLiveData.value = UsersFragmentDirections.actionUsersFragmentToSymptomsFragment(
            user = item
        ).toNavigateResult()
    }

    companion object {
        private val userNames =
            listOf("Alexander", "Vladimir", "Daniil", "Nikolay", "Nail", "Dmitriy", "Sergey", "Anton")
        private val userSurn =
            listOf("Minkin", "Assadulin", "Michailov", "Voichek", "Trishin", "Smirnov", "Jukov", "Makarov")

        private fun createRandomName(): String {
            val szNames = userNames.size - 1
            val szSurns = userSurn.size - 1
            val randName = Random.nextInt(0, szNames)
            val randSurn = Random.nextInt(0, szSurns)
            return buildString {
                append(userNames[randName])
                append(" ")
                append(userSurn[randSurn])
            }
        }
    }
}