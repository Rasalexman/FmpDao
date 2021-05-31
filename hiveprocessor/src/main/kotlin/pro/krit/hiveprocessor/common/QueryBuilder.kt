package pro.krit.hiveprocessor.common

import com.mobrun.plugin.models.StatusSelectTable
import pro.krit.hiveprocessor.base.IFmpLocalDao
import pro.krit.hiveprocessor.extensions.tableName

object QueryBuilder {

    const val SELECT_QUERY = "SELECT * FROM"
    const val DELETE_QUERY = "DELETE FROM"

    const val WHERE = "WHERE"
    const val LIMIT = "LIMIT"

    fun <E : Any, S : StatusSelectTable<E>> createTableQuery(dao: IFmpLocalDao<E, S>): String {
        val localDaoFields = dao.localDaoFields
        if (localDaoFields?.fieldsNames == null) {
            throw UnsupportedOperationException("No 'fieldsNames' key for operation 'createTableQuery'")
        }

        var prefix = ""
        val localFieldNames = localDaoFields.fieldsNames.orEmpty()
        return buildString {
            append("CREATE TABLE IF NOT EXISTS ")
            append(dao.tableName)
            append(" (")

            localDaoFields.primaryKeyName?.let {
                append(it)
                append(" TEXT PRIMARY KEY NOT NULL")
                prefix = ", "
            }

            for (fieldName in localFieldNames) {
                append(prefix)
                append(fieldName)
                append(" TEXT")
                prefix = ", "
            }
            append(")")
        }
    }

    fun <E : Any, S : StatusSelectTable<E>> createInsertOrReplaceQuery(
        dao: IFmpLocalDao<E, S>,
        item: E
    ): String {
        if (dao.localDaoFields?.fieldsForQuery == null) {
            throw UnsupportedOperationException("No 'fieldsForQuery' key for operation 'createInsertOrReplaceQuery'")
        }

        return "INSERT OR REPLACE INTO '" +
                dao.tableName +
                "' " +
                dao.localDaoFields?.fieldsForQuery +
                " VALUES " +
                FieldsBuilder.getValues(dao, item)
    }

    fun <E : Any, S : StatusSelectTable<E>> createInsertOrReplaceQuery(dao: IFmpLocalDao<E, S>, items: List<E>): String {
        val stringBuilder = StringBuilder("BEGIN TRANSACTION; ")
        for (item in items) {
            stringBuilder.append(createInsertOrReplaceQuery(dao, item))
                .append("; ")
        }
        stringBuilder.append("COMMIT;")
        return stringBuilder.toString()
    }

    fun <E : Any, S : StatusSelectTable<E>> createDeleteQuery(dao: IFmpLocalDao<E, S>, item: E): String {
        val localDaoFields = dao.localDaoFields
        if (localDaoFields?.primaryKeyField == null || localDaoFields.primaryKeyName == null) {
            throw UnsupportedOperationException("No 'primaryKeyField' for operation 'createDeleteQuery'")
        }
        val keyValue: Any = try {
            localDaoFields.primaryKeyField!![item]
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
            throw UnsupportedOperationException(e.message)
        }
        val deleteQuery = "DELETE FROM ${dao.tableName}"
        return deleteQuery +
                " WHERE " +
                localDaoFields.primaryKeyName +
                " = '" +
                keyValue +
                "'"
    }

    fun <E : Any, S : StatusSelectTable<E>> createDeleteQuery(dao: IFmpLocalDao<E, S>, items: List<E>): String {
        val stringBuilder = StringBuilder("BEGIN TRANSACTION; ")
        for (item in items) {
            stringBuilder.append(createDeleteQuery(dao, item))
                .append("; ")
        }
        stringBuilder.append("COMMIT;")
        return stringBuilder.toString()
    }
}