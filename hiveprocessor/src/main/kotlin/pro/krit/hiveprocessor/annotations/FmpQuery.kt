package pro.krit.hiveprocessor.annotations

/**
 * Simple annotation to use into IFmpLocalDao and IFmpDao child interfaces
 *  Example:
 * <p><pre>
 *
 * @FmpDao(
 *      resourceName = "ZSR_TORO_PM_DATA",
 *      parameterName = "ET_DATA"
 * )
 * interface IPmDataDao : IFmpDao<PmEtDataEntity, PmStatus> {
 *      // sql query with imported parameters and table name. Return only List<E : Any>
 *      @FmpQuery("SELECT * FROM :PmEtDataEntity WHERE MARKER = :id AND AUFPL = :count LIMIT :limit")
 *      suspend fun selectOnlyAvailable(id: String, count: Int, limit: Long): List<PmEtDataEntity>
 * }
 </p></pre>
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class FmpQuery(
    val query: String
)
