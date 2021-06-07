package pro.krit.hiveprocessor.extensions

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeName
import pro.krit.hiveprocessor.data.BindData
import pro.krit.hiveprocessor.data.FieldData
import java.util.*
import kotlin.reflect.KClass

const val CLASS_POSTFIX = "Impl"
const val MODEL_POSTFIX = "Model"
const val STATUS_POSTFIX = "Status"
const val RESULT_MODEL_POSTFIX = "ResultModel"
const val RESPOND_STATUS_POSTFIX = "RespondStatus"
const val RAW_MODEL_POSTFIX = "RawModel"


const val PREFIX_UPPER = 'I'
const val PREFIX_LOWER = 'i'

const val MODEL_FIELD_TYPE_INT = "int"
const val MODEL_FIELD_PRIMARY_KEY = "primary"

const val LIST_RETURN_TYPE = "java.util.List"
const val SUSPEND_QUALIFIER = "kotlin.coroutines.Continuation"

const val KOTLIN_LIST_NAME = "List"
const val KOTLIN_STRING_TYPE_NAME = "String"

const val KOTLIN_PATH = "kotlin"
const val KOTLIN_COLLECTION_PATH = "kotlin.collections"
const val KOTLIN_MAP_OF_NAME = "mapOf"

fun String.capitalizeFirst(): String {
    return replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
}

internal fun String.createFileName(postFix: String = CLASS_POSTFIX): String {
    var className = this
    val classNameFirstChar = className.first()
    if (classNameFirstChar == PREFIX_UPPER || classNameFirstChar == PREFIX_LOWER) {
        className = className.substring(1)
    }
    return className + postFix
}

internal fun String.createReturnType(returnPack: String, returnClass: String): TypeName {
    val isList = this.contains(LIST_RETURN_TYPE)
    return if (isList) {
        val list = ClassName(KOTLIN_COLLECTION_PATH, KOTLIN_LIST_NAME)
        list.parameterizedBy(ClassName(returnPack, returnClass))
    } else {
        ClassName(returnPack, returnClass)
    }
}

internal fun String.getPackAndClass(): Pair<String, String> {
    val withoutSuspend = this.withoutSuspend()
    val withoutBraces = withoutSuspend.replace("()", "")
    val replaced = withoutBraces.splitArray()

    val splitted = replaced.split(".")
    val className = splitted.last()
    val packName = splitted.subList(0, splitted.size - 1).joinToString(".")
    return packName to className
}

internal fun String.withoutSuspend(): String {
    val isSuspend = this.contains(SUSPEND_QUALIFIER)
    return if (isSuspend) {
        val indexStart = this.indexOf(LIST_RETURN_TYPE)
        val lastIndex = this.lastIndexOf(">")
        this.substring(indexStart, lastIndex)
    } else {
        this
    }
}

internal fun String.replaceTablePattern(returnClass: String, bindData: BindData): String {
    val tablePattern = ":$returnClass"
    return if (this.contains(tablePattern)) {
        val tableName = "${bindData.resourceName}_${bindData.tableName}"
        this.replace(tablePattern, tableName)
    } else this
}

internal fun String.splitArray(): String {
    val ifArray = this.contains(LIST_RETURN_TYPE)
    return if (ifArray) {
        this.split("<")[1].replace(">", "")
    } else {
        this
    }
}

internal fun String.screenParameter(className: String): String {
    return if (className == KOTLIN_STRING_TYPE_NAME) {
        "\'$$this\'"
    } else {
        "$$this"
    }
}

internal fun String.asModelFieldData(): FieldData {
    val propSplit = this.split("_").map { it.lowercase() }.toMutableList()
    var type: KClass<*> = String::class
    val indexOfInt = propSplit.indexOf(MODEL_FIELD_TYPE_INT)
    if (indexOfInt > -1) {
        type = Int::class
        propSplit.removeAt(indexOfInt)
    }

    val indexOfPrimaryKey = propSplit.indexOf(MODEL_FIELD_PRIMARY_KEY)
    val isPrimaryKey = indexOfPrimaryKey > -1

    if (isPrimaryKey) {
        propSplit.removeAt(indexOfPrimaryKey)
    }

    val fieldSmallName = buildString {
        propSplit.forEachIndexed { index, s ->
            if (index > 0) append(s.capitalizeFirst())
            else append(s)
        }
    }
    val fieldNameBig = buildString {
        val size = propSplit.size - 1
        propSplit.forEachIndexed { index, s ->
            append(s.uppercase())
            if (index < size) append("_")
        }
    }

    return FieldData(
        name = fieldSmallName,
        type = type,
        annotate = fieldNameBig,
        isPrimaryKey = isPrimaryKey
    )
}