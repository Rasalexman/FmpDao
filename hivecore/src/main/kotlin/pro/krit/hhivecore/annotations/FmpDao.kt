// Copyright (c) 2021 Aleksandr Minkin aka Rasalexman (sphc@yandex.ru)
//
// Permission is hereby granted, free of charge, to any person obtaining a copy of this software
// and associated documentation files (the "Software"), to deal in the Software without restriction,
// including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
// and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so,
// subject to the following conditions:
// The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
// WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
// IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
// WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH
// THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

package pro.krit.hhivecore.annotations

/**
 * This annotation is used for create Foresight HyperHive Table Resource
 * for work with remote fmp-server. You should annotate create blank interface file extended from
 * [pro.krit.hhivecore.base.IDao.IFmpDao] with marks two generics:
 * <p>
 * 1) The first generic is database table structure model with serialized annotations
 * 2) Fmp Status class extended from [com.mobrun.plugin.models.StatusSelectTable] to tell the hyperHive databaseApi
 * which kind of data should be converted from SQL statement
 * </p>
 *
 * Also you should define
 * @param resourceName - The name of resource from remote FMP-server
 * @param tableName - The name of remote table to operate with
 * @param isDelta - Does it need to use {@link com.mobrun.plugin.api.HyperHive#deltaStream()}
 * instead of {@link com.mobrun.plugin.api.HyperHive#deltaStream()} to operate with data updates
 * @param parameters - list of input request parameters names. Default: emptyArray()
 * @param fields - list of strings for autogenerate data models. Default: emptyArray()
 *
 * Example:
 * <p><pre>{@code
 * &#64FmpDao(
 *      resourceName = "ZSR_TORO_PM_DATA",
 *      parameterName = "ET_DATA",
 *      isDelta = true
 * )
 * interface IPmDataDao : IFmpDao<PmEtDataEntity, PmStatus>
 *
 * class PmStatus : StatusSelectTable<PmEtDataEntity>()
 *
 * data class PmEtDataEntity(
 *
 *      &#64JvmField
 *      &#64SerializedName("MARKER")
 *      val marker: String? = null,
 *
 *      &#64JvmField
 *      &#64SerializedName("AUART")
 *      val auart: String? = null
 * )
 * }</pre></p>
 *
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class FmpDao(
    val resourceName: String,
    val tableName: String = "",
    val parameters: Array<String> = [],
    val fields: Array<String> = [],
    val isDelta: Boolean = false
)
