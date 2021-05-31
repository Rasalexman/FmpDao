package pro.krit.hiveprocessor.extensions

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.mobrun.plugin.api.request_assistant.PrimaryKey
import com.mobrun.plugin.models.StatusSelectTable
import pro.krit.hiveprocessor.base.ILocalFmpDao
import pro.krit.hiveprocessor.common.LocalDaoFields
import java.lang.reflect.Field
import java.util.*

const val ERROR_CODE_CREATE = 10001
const val ERROR_CODE_INSERT = 10002
const val ERROR_CODE_DELETE = 10003

val <E : Any, S : StatusSelectTable<E>> ILocalFmpDao<E, S>.createTableQuery: String
    get(): String {
        var prefix = ""
        val localFieldNames = localDaoFields?.fieldsNames.orEmpty()
        return buildString {
            append("CREATE TABLE IF NOT EXISTS ")
            append(tableName)
            append(" (")

            localDaoFields?.primaryKeyName?.let {
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

fun <E : Any, S : StatusSelectTable<E>> ILocalFmpDao<E, S>.createInsertOrReplaceQuery(item: E): String {
    return "INSERT OR REPLACE INTO '" +
            tableName +
            "' " +
            localDaoFields?.fieldsForQuery +
            " VALUES " +
            getValues(item)
}

fun <E : Any, S : StatusSelectTable<E>> ILocalFmpDao<E, S>.createInsertOrReplaceQuery(items: List<E>): String {
    val stringBuilder = StringBuilder("BEGIN TRANSACTION; ")
    for (item in items) {
        stringBuilder.append(createInsertOrReplaceQuery(item))
            .append("; ")
    }
    stringBuilder.append("COMMIT;")
    return stringBuilder.toString()
}

fun <E : Any, S : StatusSelectTable<E>> ILocalFmpDao<E, S>.createDeleteQuery(item: E): String {
    if (localDaoFields?.primaryKeyField == null || localDaoFields?.primaryKeyName == null) {
        throw UnsupportedOperationException("No primary key for operation")
    }
    val keyValue: Any = try {
        localDaoFields?.primaryKeyField!![item]
    } catch (e: IllegalAccessException) {
        e.printStackTrace()
        throw UnsupportedOperationException(e.message)
    }
    return deleteQuery +
            " WHERE " +
            localDaoFields?.primaryKeyName +
            " = '" +
            keyValue +
            "'"
}

fun <E : Any, S : StatusSelectTable<E>> ILocalFmpDao<E, S>.createDeleteQuery(items: List<E>): String {
    val stringBuilder = StringBuilder("BEGIN TRANSACTION; ")
    for (item in items) {
        stringBuilder.append(createDeleteQuery(item))
            .append("; ")
    }
    stringBuilder.append("COMMIT;")
    return stringBuilder.toString()
}

inline fun <reified E : Any, reified S : StatusSelectTable<E>> ILocalFmpDao<E, S>.createTable(): S {
    return executeStatus(createTableQuery, ERROR_CODE_CREATE, "createTable")
}

suspend inline fun <reified E : Any, reified S : StatusSelectTable<E>> ILocalFmpDao<E, S>.createTableAsync(): S {
    return createTable()
}

inline fun <reified E : Any, reified S : StatusSelectTable<E>> ILocalFmpDao<E, S>.insertOrReplace(
    item: E
): S {
    val query = createInsertOrReplaceQuery(item)
    return executeStatus(query, ERROR_CODE_INSERT, "insertOrReplace")
}

suspend inline fun <reified E : Any, reified S : StatusSelectTable<E>> ILocalFmpDao<E, S>.insertOrReplaceAsync(
    item: E
): S {
    return insertOrReplace(item)
}

inline fun <reified E : Any, reified S : StatusSelectTable<E>> ILocalFmpDao<E, S>.insertOrReplace(
    items: List<E>
): S {
    val query = createInsertOrReplaceQuery(items)
    return executeStatus(query, ERROR_CODE_INSERT, "insertOrReplaceList")
}

suspend inline fun <reified E : Any, reified S : StatusSelectTable<E>> ILocalFmpDao<E, S>.insertOrReplaceAsync(
    items: List<E>
): S {
    return insertOrReplace(items)
}

inline fun <reified E : Any, reified S : StatusSelectTable<E>> ILocalFmpDao<E, S>.delete(item: E): S {
    val query = createDeleteQuery(item)
    return executeStatus(query, ERROR_CODE_DELETE, "delete")
}

suspend inline fun <reified E : Any, reified S : StatusSelectTable<E>> ILocalFmpDao<E, S>.deleteAsync(item: E): S {
    return delete(item)
}

inline fun <reified E : Any, reified S : StatusSelectTable<E>> ILocalFmpDao<E, S>.delete(items: List<E>): S {
    val query = createDeleteQuery(items)
    return executeStatus(query, ERROR_CODE_DELETE, "deleteList")
}

suspend inline fun <reified E : Any, reified S : StatusSelectTable<E>> ILocalFmpDao<E, S>.deleteAsync(items: List<E>): S {
    return delete(items)
}

fun <E : Any, S : StatusSelectTable<E>> ILocalFmpDao<E, S>.getValues(item: E): String {
    if (localDaoFields?.primaryKeyField == null || localDaoFields?.fields == null) {
        throw UnsupportedOperationException("No primary key for operation")
    }
    
    val stringBuilderRes = StringBuilder("(")
    var prefix = ""
    localDaoFields?.primaryKeyField?.let {
        try {
            val value = it[item]
            if (value == null) {
                stringBuilderRes.append("NULL")
            } else {
                stringBuilderRes.append("'")
                    .append(value)
                    .append("'")
            }
            prefix = ", "
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
    }

    val localFields = localDaoFields?.fields.orEmpty()
    for (field in localFields) {
        try {
            val value = field[item]
            stringBuilderRes.append(prefix)
            if (value == null) {
                stringBuilderRes.append("NULL")
            } else {
                stringBuilderRes.append("'")
                    .append(value)
                    .append("'")
            }
            prefix = ", "
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
    }
    stringBuilderRes.append(")")
    return stringBuilderRes.toString()
}

fun <E : Any, S : StatusSelectTable<E>> ILocalFmpDao<E, S>.getFields(fieldsNames: ArrayList<String>): String {
    var res = "("
    var prefix = ""
    localDaoFields?.primaryKeyName?.let {
        res += it
        prefix = ", "
    }

    for (fieldName in fieldsNames) {
        res += prefix + fieldName
        prefix = ", "
    }
    res += ")"
    return res
}

inline fun <reified E : Any, reified S : StatusSelectTable<E>> ILocalFmpDao<E, S>.initFields() {
    var localPrimaryKey: Field? = null
    var localPrimaryKeyName: String? = null
    val localFields = ArrayList<Field>()
    val localFieldsNames = ArrayList<String>()
    var localFieldsForQuery: String? = null

    val fieldsClass = E::class.java.fields
    for (field in fieldsClass) {
        val skipExposeAnnotation = field.getAnnotation(Expose::class.java) != null
        if (skipExposeAnnotation) {
            continue
        }

        val primaryKeyAnnotation = field.getAnnotation(
            PrimaryKey::class.java
        )
        var isPrimary = false
        if (primaryKeyAnnotation != null) {
            if (localPrimaryKey != null) {
                throw UnsupportedOperationException("Not support multiple PrimaryKey")
            }
            isPrimary = true
        }
        val annotationSerializedName = field.getAnnotation(
            SerializedName::class.java
        )

        val name: String = annotationSerializedName?.value ?: field.name
        if (isPrimary) {
            localPrimaryKeyName = name
            localPrimaryKey = field
        } else {
            localFields.add(field)
            localFieldsNames.add(name)
        }
        localFieldsForQuery = getFields(localFieldsNames)
    }

    localDaoFields = LocalDaoFields(
        fields = localFields,
        fieldsNames = localFieldsNames,
        primaryKeyField = localPrimaryKey,
        primaryKeyName = localPrimaryKeyName,
        fieldsForQuery = localFieldsForQuery
    )
}