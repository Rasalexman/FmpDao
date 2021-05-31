package pro.krit.hiveprocessor.common

import com.mobrun.plugin.models.StatusSelectTable
import pro.krit.hiveprocessor.base.IFmpLocalDao
import java.util.*

object FieldsBuilder {

    fun <E : Any, S : StatusSelectTable<E>> getValues(dao: IFmpLocalDao<E, S>, item: E): String {
        val localDaoFields = dao.localDaoFields
        if (localDaoFields?.primaryKeyField == null || localDaoFields.fields == null) {
            throw UnsupportedOperationException("No 'primaryKeyField' for operation $dao 'getValues'")
        }

        val stringBuilderRes = StringBuilder("(")
        var prefix = ""
        localDaoFields.primaryKeyField?.let {
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

        val localFields = localDaoFields.fields.orEmpty()
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

    fun <E : Any, S : StatusSelectTable<E>> getFields(dao: IFmpLocalDao<E, S>, fieldsNames: ArrayList<String>): String {
        var res = "("
        var prefix = ""
        dao.localDaoFields?.primaryKeyName?.let {
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