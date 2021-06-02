package pro.krit.hiveprocessor.common

import com.mobrun.plugin.models.StatusSelectTable
import pro.krit.hiveprocessor.base.IFmpLocalDao
import java.util.*

object FieldsBuilder {

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
            val value = primaryKeyField[item]
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

    fun getFields(
        primaryKeyName: String?,
        fieldsNames: ArrayList<String>
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