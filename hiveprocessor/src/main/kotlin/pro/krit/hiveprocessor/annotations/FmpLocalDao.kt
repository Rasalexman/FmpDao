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

package pro.krit.hiveprocessor.annotations

/**
 * This annotation is used for create Foresight HyperHive Table Resource
 * for work with local fmp database only. You should annotate create blank interface file extended from
 * [pro.krit.hiveprocessor.base.IFmpLocalDao] with marks two generics:
 * <p>
 * 1) The first generic is database table structure model with serialized annotations
 * 2) Fmp Status class extended from [com.mobrun.plugin.models.StatusSelectTable]
 * to tell the hyperHive databaseApi which kind of data should be converted from SQL statement
 * </p>
 *
 * Also you should define
 * @param resourceName - The name of resource from remote FMP-server
 * @param tableName - The name of remote table to operate with
 *
 * For FmpLocalDao Entities mark field [com.mobrun.plugin.api.request_assistant.PrimaryKey] field
 *
 * If you don't want to put structure model field into database
 * you should define that field with [com.google.gson.annotations.Expose] annotation
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
 *      &#64SerializedName("ID")
 *      var id: Long? = null,
 *
 *      //  type: TEXT, source: {'name': 'SAP', 'type': 'C'}
 *      &#64JvmField
 *      &#64SerializedName("MARKER")
 *      var marker: String? = null,
 *
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
annotation class FmpLocalDao(
    val resourceName: String,
    val tableName: String = "",
    val createTableOnInit: Boolean = true
)
