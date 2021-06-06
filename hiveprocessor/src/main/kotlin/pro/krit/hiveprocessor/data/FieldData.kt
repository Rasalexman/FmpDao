package pro.krit.hiveprocessor.data

import kotlin.reflect.KClass

data class FieldData(
    val name: String,
    val type: KClass<*>,
    val annotate: String,
    val isPrimaryKey: Boolean
)