package pro.krit.hiveprocessor.annotations

import com.mobrun.plugin.models.StatusSelectTable
import pro.krit.hiveprocessor.base.*
import com.mobrun.plugin.api.request_assistant.PrimaryKey
import com.google.gson.annotations.Expose

/**
 * This annotation is used for create Foresight HyperHive Table Resource
 * for work with local fmp database only. You should annotate create blank interface file extended from
 * [IFmpLocalDao] with marks two generics:
 * <p>
 * 1) The first generic is database table structure model with serialized annotations
 * 2) Fmp Status class extended from [StatusSelectTable] to tell the hyperHive databaseApi
 * which kind of data should be converted from SQL statement
 * </p>
 *
 * Also you should define
 * @param resourceName - The name of resource from remote FMP-server
 * @param parameterName - The name of remote table to operate with
 *
 * For database structure models you should define all fields with [PrimaryKey] [Expose] annotation
 *
 * Example:
 * <p>
 * <pre>{@code
 * &#64FmpLocalDao(
 *      resourceName = "ZSR_TORO_PM_DATA",
 *      parameterName = "ET_DATA_LOCAL"
 * )
 * interface IPmDataDao : IFmpLocalDao<PmEtDataLocalEntity, PmLocalStatus>
 *
 * class PmLocalStatus : StatusSelectTable<PmEtDataLocalEntity>()
 *
 * data class PmEtDataLocalEntity(
 *      &#64PrimaryKey
 *      &#64Expose
 *      var id: Long? = null,
 *
 *      //  type: TEXT, source: {'name': 'SAP', 'type': 'C'}
 *      &#64JvmField
 *      &#64Expose
 *      &#64SerializedName("MARKER")
 *      var marker: String? = null,
 *
 *      //  type: TEXT, source: {'name': 'SAP', 'type': 'C'}
 *      &#64JvmField
 *      &#64Expose
 *      &#64SerializedName("AUART")
 *      var auart: String? = null
 * )
 * }</pre></p>
 *
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class FmpLocalDao(
    val resourceName: String,
    val parameterName: String = ""
)
