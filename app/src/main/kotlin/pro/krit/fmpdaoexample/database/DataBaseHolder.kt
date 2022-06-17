package pro.krit.fmpdaoexample.database

import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName
import com.mobrun.plugin.api.HyperHiveState
import com.rasalexman.sresult.common.extensions.errorResult
import com.rasalexman.sresult.common.extensions.logg
import com.rasalexman.sresult.common.extensions.loggE
import com.rasalexman.sresult.common.extensions.toSuccessResult
import com.rasalexman.sresult.data.dto.SResult
import pro.krit.fmpdaoexample.IMainDatabase
import pro.krit.generated.database.MainDatabaseImpl
import pro.krit.hhivecore.provider.DatabaseConfig
import pro.krit.hhivecore.provider.DatabaseState
import java.util.*

object DataBaseHolder {

    private val gson: Gson = GsonBuilder().create()

    lateinit var mainDb: IMainDatabase

    var isDbOpened: Boolean = false

    fun createAndOpenDatabase(login: String, dbKey: String, context: Context): DatabaseState {
        val serverAddress = "http://10.100.205.123"
        val environment = "MTORO"
        val project = "project"

        val fmpDbName = serverAddress
            .replace("/", "")
            .replace(".", "_")
            .replace(":", "_") +
                "_" + environment +
                "_" + dbKey +
                "_" + project + ".sqlite"

        val dbPath = context.getDatabasePath(fmpDbName).toString()

        val hyperHiveConfig = DatabaseConfig(
            dbPath = dbPath,
            dbKey = dbKey,
            serverAddress = serverAddress,
            environment = environment,
            project = project,
            retryCount = 6,
            retryInterval = 10
        )

        val handler = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            Handler.createAsync(Looper.getMainLooper())
        } else {
            Handler(Looper.getMainLooper())
        }
        val hyperHiveState = HyperHiveState(context).setHandler(handler)
        mainDb = MainDatabaseImpl.initialize(
            hState = hyperHiveState,
            config = hyperHiveConfig
        )
        val dbState = mainDb.openDatabase(login)
        return dbState.also {
            isDbOpened = it.isOpened
        }
    }


    fun authUser(login: String, password: String): SResult<String> {
        val headers = mapOf(
            "X-SUP-DOMAIN" to "DM-MAIN",
            "Content-Type" to "application/json",
            "X-Version-App" to "2.0",
            "X-Phone" to "X-OS",
            "Device-Id" to "sasasdw9272sadsadsda"
        )
        mainDb.setDefaultHeaders(headers)
        //
        val hyperHive = mainDb.provideHyperHive()
        val result = hyperHive.authAPI.auth(
            login,
            password, true).execute()

        val status = result.status
        logg { "Auth Status = $status" }
        return try {
            val authData = gson.fromJson(result.raw, AuthModel::class.java)
            val raw = authData.result?.raw
            val description = raw?.description.orEmpty()
            val code = raw?.code ?: 0
            val authStatus = authData?.status?.uppercase().orEmpty()

            if (status.name == KEY_OK) {
                val token = raw?.token ?: UUID.randomUUID().toString()
                if (authStatus == KEY_OK && token.isNotEmpty()) {
                    token.toSuccessResult()
                } else {
                    val message = "error"
                    errorResult(
                        message = message,
                        exception = IllegalAccessException(),
                        code = code
                    )
                }
            } else {
                errorResult(
                    message = description,
                    exception = IllegalAccessException(),
                    code = code
                )
            }
        } catch (e: Exception) {
            loggE(e, "${e.message}")
            errorResult(exception = e)
        }
    }

    data class AuthModel(
        @SerializedName("status")
        val status: String? = "",
        @SerializedName("result")
        val result: RawTokenModel?,
        @SerializedName("request")
        val request: Request,
    )

    private const val KEY_OK = "OK"

}