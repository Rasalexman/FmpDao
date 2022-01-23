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

import pro.krit.hiveprocessor.base.IDao
import pro.krit.hiveprocessor.base.IDao.IFieldsDao
import pro.krit.hiveprocessor.extensions.fullTableName
import pro.krit.hiveprocessor.extensions.withoutInt

object QueryBuilder {

    const val COUNT_KEY = "count(*)"
    const val SELECT_QUERY = "SELECT * FROM "
    const val COUNT_QUERY = "SELECT count(*) FROM "
    const val DELETE_QUERY = "DELETE FROM "
    private const val UPDATE_QUERY = "UPDATE %s SET "

    const val BEGIN_TRANSACTION_QUERY = "BEGIN TRANSACTION;"
    const val END_TRANSACTION_QUERY = "END TRANSACTION;"

    private const val WHERE = " WHERE "
    private const val LIMIT = " LIMIT "
    private const val OFFSET = " OFFSET "
    private const val ORDER = " ORDER BY "

    private const val CREATE_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS "
    private const val TEXT_PRIMARY_KEY = " TEXT PRIMARY KEY NOT NULL"
    private const val TEXT_FIELD_TYPE = " TEXT"
    private const val INTEGER_FIELD_TYPE = " INTEGER"

    private const val INSERT_OR_REPLACE = "INSERT OR REPLACE INTO "
    private const val VALUES = " VALUES "

    fun createQuery(
        dao: IDao,
        prefix: String = SELECT_QUERY,
        where: String = "",
        limit: Int = 0,
        offset: Int = 0,
        orderBy: String = ""
    ): String {
        val tableName = dao.fullTableName
        val limitQuery = limit.takeIf { it > 0 }?.run { "$LIMIT$limit" }.orEmpty()
        val offsetQuery = offset.takeIf { it > 0 }?.run { "$OFFSET$offset" }.orEmpty()
        val orderByQuery =
            orderBy.takeIf { it.isNotEmpty() }?.run { "$ORDER${orderBy.withoutInt()}" }.orEmpty()

        return buildString {
            append(prefix)
            append(tableName)
            if(where.isNotEmpty()) {
                append(WHERE)
                append(where.withoutInt())
            }
            append(limitQuery)
            append(offsetQuery)
            append(orderByQuery)
        }
    }

    fun createTableQuery(dao: IDao): String {
        val localDaoFields = dao.fieldsData
        val fieldsNames = localDaoFields?.fieldsNamesWithTypes
        if (fieldsNames.isNullOrEmpty()) {
            throw UnsupportedOperationException("No 'fieldsNames' key for operation 'createTableQuery'")
        }
        val primaryKeyName = localDaoFields.primaryKeyName

        var prefix = ""
        val localFieldNames = fieldsNames.orEmpty()
        val localFieldsSize = localFieldNames.size
        return buildString {
            append(CREATE_TABLE_QUERY)
            append(dao.fullTableName)
            append(" (")

            primaryKeyName?.let {
                append(it)
                append(TEXT_PRIMARY_KEY)
                prefix = ", "
            }

            var counter = 0
            for (entry in localFieldNames) {
                counter++
                val fieldName = entry.key
                val fieldType = entry.value

                append(prefix)
                append(fieldName.withoutInt())
                append(getFieldType(fieldType))
                prefix = if(counter < localFieldsSize) {
                    ", "
                } else {
                    ""
                }
            }
            append(")")
        }
    }

    fun createUpdateQuery(
        dao: IDao,
        setQuery: String,
        from: String = "",
        where: String = ""
    ): String {
        val updateTableQuery = UPDATE_QUERY.format(dao.fullTableName)
        return buildString {
            append(updateTableQuery)
            append(setQuery)
            if (from.isNotEmpty()) {
                append(" ")
                append(from.withoutInt())
            }
            if (where.isNotEmpty()) {
                append(WHERE)
                append(where.withoutInt())
            }
        }
    }

    fun <E : Any> createInsertOrReplaceQuery(
        dao: IFieldsDao,
        item: E
    ): String {

        val fieldsForQuery = dao.fieldsData?.fieldsForQuery
            ?: throw UnsupportedOperationException("No 'fieldsForQuery' key for operation 'createInsertOrReplaceQuery'")

        val fieldsValues = FieldsBuilder.getValues(dao, item)
        return buildString {
            append(INSERT_OR_REPLACE)
            append(dao.fullTableName)
            append(" ")
            append("$fieldsForQuery$VALUES$fieldsValues")
        }
    }

    fun <E : Any> createInsertOrReplaceQuery(
        dao: IFieldsDao,
        items: List<E>
    ): String {
        return buildString {
            for (item in items) {
                append(createInsertOrReplaceQuery(dao, item))
                append("; ")
            }
        }
    }

    fun <E : Any> createDeleteQuery(
        dao: IFieldsDao,
        item: E
    ): String {
        val tableName = dao.fullTableName
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
        return buildString {
            append(DELETE_QUERY)
            append(tableName)
            append(WHERE)
            append("$primaryKeyName = '$keyValue'")
        }
    }

    fun <E : Any> createDeleteQuery(dao: IFieldsDao, items: List<E>): String {
        return buildString {
            for (item in items) {
                append(createDeleteQuery(dao, item))
                append("; ")
            }
        }
    }

    private fun getFieldType(fieldType: String): String {
        return if (fieldType == FieldsBuilder.FIELD_TYPE_INTEGER) INTEGER_FIELD_TYPE else TEXT_FIELD_TYPE
    }
}