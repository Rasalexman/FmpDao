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

package pro.krit.hiveprocessor.common

import com.mobrun.plugin.models.StatusSelectTable
import pro.krit.hiveprocessor.base.IDao
import pro.krit.hiveprocessor.base.IDao.IFieldsDao
import pro.krit.hiveprocessor.extensions.fullTableName

object QueryBuilder {

    const val COUNT_KEY = "count(*)"
    const val SELECT_QUERY = "SELECT * FROM"
    const val COUNT_QUERY = "SELECT count(*) FROM"
    const val DELETE_QUERY = "DELETE FROM"

    const val BEGIN_TRANSACTION_QUERY = "BEGIN TRANSACTION;"
    const val END_TRANSACTION_QUERY = "END TRANSACTION;"

    private const val WHERE = "WHERE"
    private const val LIMIT = "LIMIT"

    private const val CREATE_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS "
    private const val TEXT_PRIMARY_KEY = " TEXT PRIMARY KEY NOT NULL"
    private const val TEXT_FIELD_TYPE = " TEXT"
    private const val INTEGER_FIELD_TYPE = " INTEGER"

    private const val INSERT_OR_REPLACE = "INSERT OR REPLACE INTO "
    private const val VALUES = " VALUES "

    private const val FIELD_TYPE_INTEGER = "Integer"

    fun createQuery(
        dao: IDao,
        prefix: String = SELECT_QUERY,
        where: String = "",
        limit: Int = 0
    ): String {
        val tableName = dao.fullTableName
        val limitQuery = limit.takeIf { it > 0 }?.run { " $LIMIT $limit" }.orEmpty()
        return if (where.isNotEmpty()) {
            "$prefix $tableName $WHERE $where$limitQuery"
        } else {
            "$prefix $tableName$limitQuery"
        }
    }

    fun createTableQuery(dao: IFieldsDao): String {
        val localDaoFields = dao.fieldsData
        val fieldsNames = localDaoFields?.fieldsNamesWithTypes
        if (fieldsNames.isNullOrEmpty()) {
            throw UnsupportedOperationException("No 'fieldsNames' key for operation 'createTableQuery'")
        }
        val primaryKeyName = localDaoFields.primaryKeyName

        var prefix = ""
        val localFieldNames = fieldsNames.orEmpty()
        return buildString {
            append(CREATE_TABLE_QUERY)
            append(dao.fullTableName)
            append(" (")

            primaryKeyName?.let {
                append(it)
                append(TEXT_PRIMARY_KEY)
                prefix = ", "
            }

            for (entry in localFieldNames) {
                val fieldName = entry.key
                val fieldType = entry.value

                append(prefix)
                append(fieldName)
                append(getFieldType(fieldType))
                prefix = ", "
            }
            append(")")
        }
    }

    fun <E : Any> createInsertOrReplaceQuery(
        dao: IFieldsDao,
        item: E
    ): String {
        val fieldsForQuery = dao.fieldsData?.fieldsForQuery
            ?: throw UnsupportedOperationException("No 'fieldsForQuery' key for operation 'createInsertOrReplaceQuery'")

        val fieldsValues = FieldsBuilder.getValues(dao, item)
        return "$INSERT_OR_REPLACE ${dao.fullTableName} $fieldsForQuery$VALUES$fieldsValues"
    }

    fun <E : Any, S : StatusSelectTable<E>> createInsertOrReplaceQuery(
        dao: IFieldsDao,
        items: List<E>
    ): String {
        val stringBuilder = StringBuilder()
        for (item in items) {
            stringBuilder.append(createInsertOrReplaceQuery(dao, item))
                .append("; ")
        }
        return stringBuilder.toString()
    }

    fun <E : Any> createDeleteQuery(
        dao: IFieldsDao,
        item: E
    ): String {
        val localDaoFields = dao.fieldsData
        val primaryKeyField = localDaoFields?.primaryKeyField
        val primaryKeyName = localDaoFields?.primaryKeyName
        if (primaryKeyField == null || primaryKeyName.isNullOrEmpty()) {
            throw UnsupportedOperationException("No 'primaryKeyField' for operation 'createDeleteQuery'")
        }
        val keyValue: Any = try {
            primaryKeyField[item]
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
            throw UnsupportedOperationException(e.message)
        }
        return "$DELETE_QUERY ${dao.fullTableName} $WHERE $primaryKeyName = '$keyValue'"
    }

    fun <E : Any> createDeleteQuery(dao: IFieldsDao, items: List<E>): String {
        val stringBuilder = StringBuilder()
        for (item in items) {
            stringBuilder.append(createDeleteQuery(dao, item))
                .append("; ")
        }
        return stringBuilder.toString()
    }

    private fun getFieldType(fieldType: String): String {
        return if (fieldType == FIELD_TYPE_INTEGER) INTEGER_FIELD_TYPE else TEXT_FIELD_TYPE
    }
}