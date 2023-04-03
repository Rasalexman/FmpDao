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

package com.rasalexman.hhivecore.common

import com.rasalexman.hhivecore.base.IDao
import com.rasalexman.hhivecore.extensions.fullTableName
import com.rasalexman.hhivecore.extensions.withoutInt

object QueryBuilder {

    const val COUNT_KEY = "count(%s)"
    const val SELECT_QUERY = "SELECT %s FROM "
    const val COUNT_QUERY = "SELECT count(%s) FROM "
    const val DELETE_QUERY = "DELETE FROM "
    private const val SELECT_ALL_MAKER = "*"
    private const val UPDATE_QUERY = "UPDATE %s SET "
    private const val ALTER_TABLE_ADD_QUERY = "ALTER TABLE %s ADD "

    //private const val BEGIN_TRANSACTION_QUERY = "BEGIN TRANSACTION; "
    //private const val COMMIT_TRANSACTION_QUERY = "COMMIT; "

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
    private const val FINISH_MARK = "; "
    private const val DELIM_MARK = ", "

    fun createQuery(
        dao: IDao,
        prefix: String = SELECT_QUERY,
        where: String = "",
        limit: Int = 0,
        offset: Int = 0,
        orderBy: String = "",
        fields: List<String>? = null
    ): String {
        val tableName = dao.fullTableName
        val limitQuery = limit.takeIf { it > 0 }?.run { "$LIMIT$limit" }.orEmpty()
        val offsetQuery = offset.takeIf { it > 0 }?.run { "$OFFSET$offset" }.orEmpty()
        val orderByQuery =
            orderBy.takeIf { it.isNotEmpty() }?.run { "$ORDER${orderBy.withoutInt()}" }.orEmpty()
        val prefixQuery = createWithFields(prefix, fields)

        val query = buildString {
            append(prefixQuery)
            append(tableName)
            if (where.isNotEmpty()) {
                append(WHERE)
                append(where.withoutInt())
            }
            append(limitQuery)
            append(offsetQuery)
            append(orderByQuery)
            append(FINISH_MARK)
        }
        return query
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
        return buildString {
            append(CREATE_TABLE_QUERY)
            append(dao.fullTableName)
            append(" (")

            primaryKeyName?.let {
                append(it)
                append(TEXT_PRIMARY_KEY)
                prefix = DELIM_MARK
            }

            for (entry in localFieldNames) {
                val fieldName = entry.key
                val fieldType = entry.value

                append(prefix)
                append(fieldName.withoutInt())
                append(getFieldType(fieldType))
                prefix = DELIM_MARK
            }
            append(")")
            append(FINISH_MARK)
        }
    }

    fun alterTableQueryAddColumn(dao: IDao, columnName: String): String {
        val alterTableAddQuery = ALTER_TABLE_ADD_QUERY.format(dao.fullTableName)
        return buildString {
            append(alterTableAddQuery)
            append(columnName)
            append(TEXT_FIELD_TYPE)
            append(FINISH_MARK)
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
            append(FINISH_MARK)
        }
    }

    fun <E : Any> createInsertOrReplaceQuery(
        dao: IDao,
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
            append(FINISH_MARK)
        }
    }

    fun <E : Any> createInsertOrReplaceQuery(
        dao: IDao,
        items: List<E>,
        withoutPrimaryKey: Boolean = false
    ): String {
        val fieldsForQuery = dao.fieldsData?.fieldsForQuery
            ?: throw UnsupportedOperationException("No 'fieldsForQuery' key for operation 'createInsertOrReplaceQuery'")

        var prefix = ""
        return buildString {
            if(items.isNotEmpty()) {
                //append(BEGIN_TRANSACTION_QUERY)
                append(INSERT_OR_REPLACE)
                append(dao.fullTableName)
                append(" ")
                append("$fieldsForQuery$VALUES")
                for (item in items) {
                    append(prefix)
                    val fieldsValues = FieldsBuilder.getValues(dao, item, withoutPrimaryKey)
                    append(fieldsValues)
                    prefix = DELIM_MARK
                }
                append(FINISH_MARK)
                //append(COMMIT_TRANSACTION_QUERY)
            }
        }
    }

    fun <E : Any> createDeleteQuery(dao: IDao, item: E): String {
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
            append(FINISH_MARK)
        }
    }

    fun <E : Any> createDeleteQuery(dao: IDao, items: List<E>): String {
        val tableName = dao.fullTableName
        val localDaoFields = dao.fieldsData
        val primaryKeyField = localDaoFields?.primaryKeyField
        val primaryKeyName = localDaoFields?.primaryKeyName

        if (primaryKeyField == null || primaryKeyName.isNullOrEmpty()) {
            throw UnsupportedOperationException("No 'primaryKeyField' for operation 'createDeleteListQuery'")
        }

        var prefix = ""
        return buildString {
            if(items.isNotEmpty()) {
                //append(BEGIN_TRANSACTION_QUERY)
                append(DELETE_QUERY)
                append(tableName)
                append(WHERE)
                append("$primaryKeyName IN ")
                append("(")
                for (item in items) {
                    append(prefix)
                    val keyValue: Any = primaryKeyField[item]
                    append("'$keyValue'")
                    prefix = DELIM_MARK
                }
                append(")")
                append(FINISH_MARK)
                //append(COMMIT_TRANSACTION_QUERY)
            }
        }
    }

    fun createWithFields(prefix: String, fields: List<String>? = null): String {
        val prefixQuery = fields.takeIf { !it.isNullOrEmpty() }?.let {
            val fieldsStatement = it.joinToString(DELIM_MARK)
            fieldsStatement.withoutInt()
        } ?: SELECT_ALL_MAKER
        return try {
            prefix.format(prefixQuery)
        } catch (e: Throwable) {
            e.printStackTrace()
            prefix
        }
    }

    private fun getFieldType(fieldType: String): String {
        return if (fieldType == FieldsBuilder.FIELD_TYPE_INTEGER) INTEGER_FIELD_TYPE else TEXT_FIELD_TYPE
    }
}