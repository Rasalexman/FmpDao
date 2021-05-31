package pro.krit.hiveprocessor.extensions

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.mobrun.plugin.api.request_assistant.PrimaryKey
import com.mobrun.plugin.models.StatusSelectTable
import pro.krit.hiveprocessor.base.ILocalFmpDao
import pro.krit.hiveprocessor.common.LocalDaoFields
import pro.krit.hiveprocessor.common.FieldsBuilder.getFields
import pro.krit.hiveprocessor.common.QueryBuilder
import pro.krit.hiveprocessor.common.QueryExecuter.executeStatus
import java.lang.reflect.Field
import java.util.*

const val ERROR_CODE_CREATE = 10004
const val ERROR_CODE_INSERT = 10005
const val ERROR_CODE_DELETE = 10006

inline fun <reified E : Any, reified S : StatusSelectTable<E>> ILocalFmpDao<E, S>.createTable(): S {
    return executeStatus(
        dao = this,
        query = QueryBuilder.createTableQuery(this),
        errorCode = ERROR_CODE_CREATE,
        methodName = "createTable"
    )
}

suspend inline fun <reified E : Any, reified S : StatusSelectTable<E>> ILocalFmpDao<E, S>.createTableAsync(): S {
    return createTable()
}

inline fun <reified E : Any, reified S : StatusSelectTable<E>> ILocalFmpDao<E, S>.insertOrReplace(
    item: E
): S {
    val query = QueryBuilder.createInsertOrReplaceQuery(this, item)
    return executeStatus(
        dao = this,
        query = query,
        errorCode = ERROR_CODE_INSERT,
        methodName = "insertOrReplace"
    )
}

suspend inline fun <reified E : Any, reified S : StatusSelectTable<E>> ILocalFmpDao<E, S>.insertOrReplaceAsync(
    item: E
): S {
    return insertOrReplace(item)
}

inline fun <reified E : Any, reified S : StatusSelectTable<E>> ILocalFmpDao<E, S>.insertOrReplace(
    items: List<E>
): S {
    val query = QueryBuilder.createInsertOrReplaceQuery(this, items)
    return executeStatus(
        dao = this,
        query = query,
        errorCode = ERROR_CODE_INSERT,
        methodName = "insertOrReplaceList"
    )
}

suspend inline fun <reified E : Any, reified S : StatusSelectTable<E>> ILocalFmpDao<E, S>.insertOrReplaceAsync(
    items: List<E>
): S {
    return insertOrReplace(items)
}

inline fun <reified E : Any, reified S : StatusSelectTable<E>> ILocalFmpDao<E, S>.delete(item: E): S {
    val query = QueryBuilder.createDeleteQuery(this, item)
    return executeStatus(
        dao = this,
        query = query,
        errorCode = ERROR_CODE_DELETE,
        methodName = "delete"
    )
}

suspend inline fun <reified E : Any, reified S : StatusSelectTable<E>> ILocalFmpDao<E, S>.deleteAsync(
    item: E
): S {
    return delete(item)
}

inline fun <reified E : Any, reified S : StatusSelectTable<E>> ILocalFmpDao<E, S>.delete(items: List<E>): S {
    val query = QueryBuilder.createDeleteQuery(this, items)
    return executeStatus(
        dao = this,
        query = query,
        errorCode = ERROR_CODE_DELETE,
        methodName = "deleteList"
    )
}

suspend inline fun <reified E : Any, reified S : StatusSelectTable<E>> ILocalFmpDao<E, S>.deleteAsync(
    items: List<E>
): S {
    return delete(items)
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
        localFieldsForQuery = getFields(this, localFieldsNames)
    }

    localDaoFields = LocalDaoFields(
        fields = localFields,
        fieldsNames = localFieldsNames,
        primaryKeyField = localPrimaryKey,
        primaryKeyName = localPrimaryKeyName,
        fieldsForQuery = localFieldsForQuery
    )
}