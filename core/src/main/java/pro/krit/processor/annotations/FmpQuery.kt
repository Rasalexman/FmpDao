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

package pro.krit.processor.annotations

/**
 * Simple annotation to use into IFmpLocalDao and IFmpDao child interfaces
 *  Example:
 * <p><pre>
 *
 * @FmpDao(
 *      resourceName = "ZSR_TORO_PM_DATA",
 *      tableName = "ET_DATA"
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
