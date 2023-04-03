package com.rasalexman.fmpdaoexample

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import timber.log.Timber

@Suppress("UNUSED_PARAMETER", "UNUSED_VARIABLE")
class MainActivity : AppCompatActivity(R.layout.activity_main) {

    companion object {
        const val DEBUG_LOGIN = "35069"
        const val DEBUG_PASSWORD = "35069"
    }

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        Timber.plant(object : Timber.Tree() {
            override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
                println("$tag - $message")
            }
        })
    }

    /*lateinit var pmFieldDao: IPmDataFieldsDao

    private val pmLocalDao: IPmDataLocalDao by lazy {
        mainDb.providePmLocalDao()
    }*/

   /* private fun exampleWithLocalDao(pmLocalDao: IPmDataLocalDao) {
        val scope = CoroutineScope(Dispatchers.Main)
        scope.launch {
            withContext(Dispatchers.IO) { selectLocalDaoAsync() }

            *//*val resultList = pmLocalDao.select()
            println("----> all count = ${resultList.mapNotNull { it.convertTo() }}")*//*

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
    }*/

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