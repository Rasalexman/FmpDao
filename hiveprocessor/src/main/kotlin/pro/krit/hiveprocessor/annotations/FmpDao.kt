package pro.krit.hiveprocessor.annotations

import com.mobrun.plugin.models.StatusSelectTable
import pro.krit.hiveprocessor.base.IFmpDao

/**
 * This annotation is used for create Foresight HyperHive Table Resource
 * for work with remote fmp-server. You should annotate create blank interface file extended from
 * [IFmpDao] with marks two generics:
 * <p>
 * 1) The first generic is database table structure model with serialized annotations
 * 2) Fmp Status class extended from [StatusSelectTable] to tell the hyperHive databaseApi
 * which kind of data should be converted from SQL statement
 * </p>
 *
 * Also you should define
 * @param resourceName - The name of resource from remote FMP-server
 * @param parameterName - The name of remote table to operate with
 * @param isCached - Does it need to use {@link com.mobrun.plugin.api.HyperHive#deltaStream()}
 * instead of {@link com.mobrun.plugin.api.HyperHive#deltaStream()} to operate with data updates
 *
 * Example:
 * <p><pre>{@code
 * &#64FmpDao(
 *      resourceName = "ZSR_TORO_PM_DATA",
 *      parameterName = "ET_DATA",
 *      isCached = true
 * )
 * interface IPmDataDao : IFmpDao<PmEtDataEntity, PmStatus>
 *
 * class PmStatus : StatusSelectTable<PmEtDataEntity>()
 *
 * data class PmEtDataEntity(
 *      //  type: TEXT, source: {'name': 'SAP', 'type': 'C'}
 *      &#64JvmField
 *      &#64SerializedName("MARKER")
 *      var marker: String? = null,

 *      //  type: TEXT, source: {'name': 'SAP', 'type': 'C'}
 *      &#64JvmField
 *      &#64SerializedName("AUART")
 *      var auart: String? = null
 * )
 * }</pre></p>
 *
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class FmpDao(
    val resourceName: String,
    val parameterName: String = "",
    val isCached: Boolean = false
)
