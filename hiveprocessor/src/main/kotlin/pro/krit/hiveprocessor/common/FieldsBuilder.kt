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
import pro.krit.hiveprocessor.base.IFmpLocalDao
import java.security.MessageDigest
import java.util.*

object FieldsBuilder {

    private val md by lazy { MessageDigest.getInstance("SHA-256") }
    private const val PRIMARY_KEY_JAVA_TYPE = "String"
    private const val VALUE_NULL = "NULL"

    fun <E : Any, S : StatusSelectTable<E>> getValues(dao: IFmpLocalDao<E, S>, item: E): String {
        val localDaoFields = dao.localDaoFields
        val primaryKeyField = localDaoFields?.primaryKeyField
        val fields = localDaoFields?.fields
        if (primaryKeyField == null || fields.isNullOrEmpty()) {
            throw UnsupportedOperationException("No 'primaryKeyField' for operation $dao 'getValues'")
        }

        val stringBuilderRes = StringBuilder("(")
        var prefix = ""
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

        val localFields = fields.orEmpty()
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

    private fun generatePrimaryKeyValue(): String {
        val key = UUID.randomUUID().toString()
        val bytes = key.toByteArray()
        val digest = md.digest(bytes)
        return digest.fold("", { str, it -> str + "%02x".format(it) })
    }

    fun getFields(
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
            res += prefix + fieldName
            prefix = ", "
        }
        res += ")"
        return res
    }
}