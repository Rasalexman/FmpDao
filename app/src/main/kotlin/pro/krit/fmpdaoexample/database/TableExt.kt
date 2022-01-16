package pro.krit.fmpdaoexample.database

import com.mobrun.plugin.api.request_assistant.NumeratedFields
import com.mobrun.plugin.api.request_assistant.RequestAdapter
import com.mobrun.plugin.models.StatusRawDataListTable
import com.mobrun.plugin.models.TableRawData
import com.rasalexman.sresult.common.extensions.tryOrNull

inline fun <reified T : NumeratedFields> StatusRawDataListTable.parseTable(tableName: String): List<T> {
    return result?.raw?.find { it.name == tableName }.parseValues()
}

inline fun <reified T : NumeratedFields> TableRawData?.parseValues(): List<T> {
    val requestAdapter = RequestAdapter.REQUEST_ADAPTER_INSTANCE
    return this?.data?.mapNotNull {
        tryOrNull { requestAdapter.getNumeratedFieldsFromList(it, T::class.java) }
    }.orEmpty()
}
