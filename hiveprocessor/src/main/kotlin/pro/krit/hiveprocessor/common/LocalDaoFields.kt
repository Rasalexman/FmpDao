package pro.krit.hiveprocessor.common

import java.lang.reflect.Field

data class LocalDaoFields(
    var fields: MutableList<Field>? = null,
    var fieldsNames: MutableList<String>? = null,
    var primaryKeyField: Field? = null,
    var primaryKeyName: String? = null,
    var fieldsForQuery: String? = null,
)
