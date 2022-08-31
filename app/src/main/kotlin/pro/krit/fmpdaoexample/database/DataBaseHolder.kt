package pro.krit.fmpdaoexample.database

import android.content.Context
import android.provider.Settings
import com.rasalexman.sresult.common.extensions.errorResult
import com.rasalexman.sresult.common.extensions.successResult
import com.rasalexman.sresult.data.dto.SResult
import pro.krit.fmpdaoexample.BuildConfig
import pro.krit.fmpdaoexample.IMainDatabase
import pro.krit.generated.database.MainDatabaseImpl
import pro.krit.hhivecore.provider.DatabaseConfig
import pro.krit.hhivecore.provider.DatabaseState

object DataBaseHolder {

    lateinit var mainDb: IMainDatabase

    var isDbOpened: Boolean = false

    fun createAndOpenDatabase(dbKey: String, context: Context): DatabaseState {
        val serverAddress = "http://10.100.205.123"
        val environment = "MTORO"
        val project = "project_sap"

        val fmpDbName = serverAddress
            .replace("/", "")
            .replace(".", "_")
            .replace(":", "_") +
                "_" + environment +
                "_" + dbKey +
                "_" + project

        val device_id: String = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
        val storage: String = context.filesDir.absolutePath
        val headers = mapOf(
            "X-SUP-DOMAIN" to "DM-MAIN",
            "Content-Type" to "application/json",
            "X-Version-App" to "2.0",
            "X-Phone" to "X-OS",
            "Device-Id" to device_id
        )

        val hyperHiveConfig = DatabaseConfig(
            serverAddress = serverAddress,
            environment = environment,
            project = project,
            retryCount = 6,
            retryInterval = 10,
            deviceId = device_id,
            storage = storage,
            headers = headers,
            disableEncryption = BuildConfig.DEBUG
        )

        mainDb = MainDatabaseImpl.initialize(
            config = hyperHiveConfig
        )
        val dbState = mainDb.openDatabase(fmpDbName)
        return dbState.also {
            isDbOpened = it.isOpened
        }
    }


    fun authUser(login: String, password: String): SResult<String> {
        //
        val (user, authResult) = mainDb.auth(login, password)
        return if(authResult.result) {
             successResult(user.token)
        } else {
            errorResult(authResult.error)
        }
    }
}