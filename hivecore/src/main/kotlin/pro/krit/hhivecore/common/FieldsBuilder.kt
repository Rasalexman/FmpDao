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

package pro.krit.hhivecore.common

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.mobrun.plugin.api.request_assistant.PrimaryKey
import pro.krit.hhivecore.base.IDao
import pro.krit.hhivecore.extensions.withoutInt
import java.lang.reflect.Field
import java.security.MessageDigest
import java.util.*

object FieldsBuilder {

    private val md by lazy { MessageDigest.getInstance("SHA-256") }
    private const val PRIMARY_KEY_JAVA_TYPE = "String"
    private const val VALUE_NULL = "NULL"
    const val FIELD_TYPE_INTEGER = "Integer"

    fun initFields(dao: IDao, fields: Array<Field>) {
        if(dao.fieldsData != null) return

        var localPrimaryKey: Field? = null
        var localPrimaryKeyName: String? = null
        val localFields = ArrayList<Field>()
        val localFieldsNames = mutableMapOf<String, String>()
        var localFieldsForQuery: String? = null

        for (field in fields) {
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
                localFieldsNames[name] = field.type.simpleName
            }
            localFieldsForQuery = getFields(localPrimaryKeyName, localFieldsNames.keys.toList())
        }

        dao.fieldsData = DaoFieldsData(
            fields = localFields,
            fieldsNamesWithTypes = localFieldsNames,
            primaryKeyField = localPrimaryKey,
            primaryKeyName = localPrimaryKeyName,
            fieldsForQuery = localFieldsForQuery
        )
    }

    fun <E : Any> getValues(dao: IDao, item: E, withoutPrimaryKey: Boolean = false): String {
        val localDaoFields = dao.fieldsData
        val primaryKeyField = localDaoFields?.primaryKeyField
        val fields = localDaoFields?.fields

        if(withoutPrimaryKey && fields.isNullOrEmpty()) {
            throw UnsupportedOperationException("Table `fields` for resource ${dao.tableName} not initialized 'getValues'")
        } else if (!withoutPrimaryKey && (primaryKeyField == null || fields.isNullOrEmpty())) {
            throw UnsupportedOperationException("No 'primaryKeyField' for table ${dao.tableName} 'getValues'")
        }

        val stringBuilderRes = StringBuilder("(")
        var prefix = ""
        if(!withoutPrimaryKey && primaryKeyField != null) {
            try {
                val primaryKeyValue = primaryKeyField[item]
                    ?: if (primaryKeyField.type.simpleName == PRIMARY_KEY_JAVA_TYPE) {
                        generatePrimaryKeyValue()
                    } else VALUE_NULL

                stringBuilderRes.append("'")
                    .append(primaryKeyValue)
                    .append("'")
                prefix = ", "
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            }
        }


        val localFields = fields.orEmpty()
        for (field in localFields) {
            try {
                val value = field[item]
                stringBuilderRes.append(prefix)
                if (value == null) {
                    stringBuilderRes.append(VALUE_NULL)
                } else {
                    val fieldType = field.type.simpleName
                    if(fieldType != FIELD_TYPE_INTEGER) {
                        stringBuilderRes.append("'")
                            .append(value)
                            .append("'")
                    } else {
                        stringBuilderRes.append(value)
                    }
                }
                prefix = ", "
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            }
        }
        stringBuilderRes.append(")")
        return stringBuilderRes.toString()
    }

    private fun generatePrimaryKeyValue(): String {
        val key = UUID.randomUUID().toString()
        val bytes = key.toByteArray()
        val digest = md.digest(bytes)
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }

    private fun getFields(
        primaryKeyName: String?,
        fieldsNames: List<String>
    ): String {
        var res = "("
        var prefix = ""
        primaryKeyName?.let {
            res += it
            prefix = ", "
        }

        for (fieldName in fieldsNames) {
            res += prefix + fieldName.withoutInt()
            prefix = ", "
        }
        res += ")"
        return res
    }
}