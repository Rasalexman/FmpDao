package pro.krit.hiveprocessor.request

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.mobrun.plugin.api.HyperHive
import com.mobrun.plugin.api.callparams.RequestCallParams
import com.mobrun.plugin.api.callparams.WebCallParams
import com.mobrun.plugin.models.BaseStatus
import pro.krit.hiveprocessor.errors.RequestException
import pro.krit.hiveprocessor.extensions.isNotBad
import java.io.File

internal class FmpRequestsHelper(
    private val hyperHive: HyperHive,
    private val fmpToken: String,
    private val deviceId: String
) : IFmpRequestHelper {
    private val gson: Gson = GsonBuilder().create()

    private val defaultHeaders: Map<String, String> get() = mapOf(
        "X-SUP-DOMAIN" to "DM-MAIN",
        "Content-Type" to "application/json",
        "Web-Authorization" to "Bearer $fmpToken",
        "X-Version-App" to "2.0",
        "X-Phone" to "X-OS",
        "Device-Id" to deviceId
    )

    override suspend fun <T : ObjectRawStatus<S>, S : Any> restRequest(
        resourceName: String,
        data: Any?,
        args: Map<String, String>,
        clazz: Class<T>,
        isWeb: Boolean
    ): S? {

        val statusString = if (isWeb) {
            val webCallParams = WebCallParams().apply {
                if (data != null) {
                    this.data = if (data is String) data else gson.toJson(data)
                }
                headers = defaultHeaders
            }
            hyperHive.requestAPI.web(resourceName, webCallParams).execute()
        } else {
            val callParams = RequestCallParams().apply {
                if (data != null) {
                    this.data = if (data is String) data else gson.toJson(data)
                }

                if (args.isNotEmpty()) {
                    this.args = args
                }
                headers = defaultHeaders
            }

            println( "requestCallParams.data: ${callParams.data}")
            hyperHive.requestAPI.request(resourceName, callParams).execute()
        }
        return try {
            println( "status: $statusString")
            val status = gson.fromJson(statusString, clazz)
            if (status.isNotBad()) {
                val resultData = status.result?.raw
                resultData?.let {
                    it
                } ?: throw RequestException.ServerError()
            } else {
                status.result?.raw
            }
        } catch (e: Exception) {
            val status = gson.fromJson(statusString, BaseStatus::class.java)
            println("status: $status")
            //status
            null
        }
    }

    override suspend fun <T : ObjectRawStatus<out BaseFmpRawModel<S>>, S : Any> restDataRequest(
        resourceName: String,
        data: Any?,
        args: Map<String, String>?,
        clazz: Class<T>
    ): S? {

        val callParams = RequestCallParams().apply {
            headers = defaultHeaders
            if (data != null) {
                this.data = if (data is String) data else gson.toJson(data)
            }

            if (args?.isNotEmpty() == true) {
                this.args = args
            }
        }
        println( "Start POST call on resource = $resourceName with Params: ${callParams.data}" )
        val statusString =
            hyperHive.requestAPI.request(resourceName, callParams).execute()
        return statusString.getStatusResultWithData(resourceName, clazz)
    }

    /*override suspend fun updateResources(resources: List<BaseFmpResource>): ISResult<Boolean> {
        resources.forEach { resource ->
            val request = if (resource.isDelta) {
                resource.newRequest().streamCallDelta()
            } else {
                resource.newRequest().streamCallTable()
            }

            val response = request.execute()
            if (!response.isOk && response.httpStatus.status != HTTP_STATUS_NO_DELTA) {
                val firstError = response.errors.firstOrNull()?.descriptions?.firstOrNull()
                return errorResult(firstError.orEmpty())
            }
        }
        return successResult(true)
    }*/

    override suspend fun getFileList(
        folderName: String,
        storageName: String
    ): List<String> {
        val status = hyperHive.filesApi.directoryGet(
            folderName,
            storageName,
            FmpFilesStatus::class.java
        ).execute()

        if (status.isNotBad()) {
            val files = status?.result?.raw?.files ?: emptyList()
            return files
        }
        return emptyList()
    }

    override suspend fun downloadFile(
        localFile: File,
        fileName: String,
        folderName: String,
        storageName: String
    ): Boolean {
        val serverPath: String = folderName + fileName
        val status = hyperHive.filesApi.fileGet(
            localFile.absolutePath,
            serverPath,
            storageName
        ).execute()

        return status.isNotBad()
    }

    override suspend fun uploadFile(
        remoteFilePath: String,
        storageName: String,
        file: File
    ): Boolean {
        val status = hyperHive.filesApi.filePut(
            file.absolutePath,
            remoteFilePath,
            storageName
        ).execute()
        return status.isNotBad()
    }

    private fun <T : ObjectRawStatus<out BaseFmpRawModel<S>>, S : Any> String.getStatusResultWithData(
        resourceName: String,
        clazz: Class<T>
    ): S? {
        return try {
            println("status: $this")
            val status = gson.fromJson(this, clazz)
            if (status.isNotBad()) {
                val result = status.result
                if (result != null) {
                    println( "result: $result" )
                    val raw = result.raw
                    if (raw != null) {
                        println( "raw: $raw" )
                        val errorMessage = raw.errorMessage
                        val errorDescription = raw.errorDescription
                        when {
                            errorMessage != null -> {
                                println( "errorMessage = $errorMessage | resourceName = '$resourceName'" )
                                throw RequestException.SapError(message = errorMessage)
                            }
                            errorDescription != null -> {
                                println( "errorDescription = $errorDescription | resourceName = '$resourceName'" )
                                throw RequestException.SapError(message = errorDescription)
                            }
                            else -> {
                                val rawData = raw.data
                                if (rawData != null) {
                                    rawData
                                } else {
                                    println( "raw.Data is null | resourceName = '$resourceName'" )
                                    throw RequestException.RawDataNullError
                                }
                            }
                        }
                    } else {
                        println( "raw result is null | resourceName = '$resourceName'" )
                        throw RequestException.ResultRawNullError
                    }
                } else {
                    println( "status result is null | resourceName = '$resourceName'" )
                    throw RequestException.StatusResultNullError
                }
            } else {
                val raw = status.result?.raw
                val errors = status.errors
                val firstError = errors.firstOrNull()
                val errorMessage = firstError?.source ?: raw?.errorMessage.orEmpty()
                val errorDescription = firstError?.description ?: raw?.errorDescription.orEmpty()
                println( "status is bad | errorMessage = $errorMessage " +
                        "| errorDescription = $errorDescription " +
                        "| resourceName = '$resourceName'" )
                throw RequestException.SapError(errorDescription)
            }
        } catch (e: Throwable) {
            throw RequestException.WebCallError
        }
    }

    companion object {
        const val HTTP_STATUS_NO_DELTA = 304
    }
}

interface IFmpRequestHelper {
    suspend fun <T : ObjectRawStatus<S>, S : Any> restRequest(
        resourceName: String,
        data: Any? = null,
        args: Map<String, String> = emptyMap(),
        clazz: Class<T>,
        isWeb: Boolean = true
    ): S?

    suspend fun <T : ObjectRawStatus<out BaseFmpRawModel<S>>, S : Any> restDataRequest(
        resourceName: String,
        data: Any? = null,
        args: Map<String, String>? = null,
        clazz: Class<T>
    ): S?

    //suspend fun updateResources(resources: List<BaseFmpResource>): ISResult<Boolean>

    suspend fun getFileList(folderName: String, storageName: String): List<String>

    suspend fun uploadFile(
        remoteFilePath: String,
        storageName: String,
        file: File
    ): Boolean

    suspend fun downloadFile(
        localFile: File,
        fileName: String,
        folderName: String,
        storageName: String
    ): Boolean
}