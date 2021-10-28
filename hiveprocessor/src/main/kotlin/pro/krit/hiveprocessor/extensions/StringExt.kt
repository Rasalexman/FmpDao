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

package pro.krit.hiveprocessor.extensions

import com.google.gson.annotations.SerializedName
import com.mobrun.plugin.api.request_assistant.CountFields
import com.mobrun.plugin.api.request_assistant.ParameterField
import com.squareup.kotlinpoet.AnnotationSpec
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
const val PARAMS_POSTFIX = "Params"


const val PREFIX_UPPER = 'I'
const val PREFIX_LOWER = 'i'

const val MODEL_FIELD_TYPE_INT = "int"
const val MODEL_FIELD_TYPE_LIST = "list"
const val MODEL_FIELD_PRIMARY_KEY = "primary"
private const val TAG_STRING_FULL_TYPE = "%S"

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
    //
    val ifArray = withoutBraces.contains(LIST_RETURN_TYPE)
    val replaced = if (ifArray) {
        withoutBraces.split("<")[1].replace(">", "")
    } else {
        withoutBraces
    }

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

internal fun String?.splitGenericsArray(): List<String> {
    val genericsList = mutableListOf<String>()
    //println("------> splitGenericsArray = $this")
    this?.let { currentSupertype ->
        val splittedTypes = currentSupertype.split("<")
        if(splittedTypes.size > 1) {
            val secondPart = splittedTypes.getOrNull(1).orEmpty()
            if(secondPart.isNotEmpty()) {
                val secondSplitted = secondPart.replace(">","").split(",")
                if(secondSplitted.isNotEmpty()) {
                    val firstGeneric = secondSplitted.first()
                    val secondGeneric = secondSplitted.getOrNull(1).orEmpty()
                    if(firstGeneric.isNotEmpty()) {
                        genericsList.add(firstGeneric)
                    }
                    if(secondGeneric.isNotEmpty()) {
                        genericsList.add(secondGeneric)
                    }
                }
            }
        }
    }
    return genericsList
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

    // for list change input vararg to Any?
    /*val indexOfList = propSplit.indexOf(MODEL_FIELD_TYPE_LIST)
    if (indexOfInt > -1) {
        type = List::class
        propSplit.removeAt(indexOfList)
        println("-----> LIST FOUNT")
    }*/

    val indexOfPrimaryKey = propSplit.indexOf(MODEL_FIELD_PRIMARY_KEY)
    val isPrimaryKey = indexOfPrimaryKey > -1

    if (isPrimaryKey) {
        propSplit.removeAt(indexOfPrimaryKey)
    }
    //
    val fieldSmallName = buildString {
        propSplit.forEachIndexed { index, s ->
            if (index > 0) append(s.capitalizeFirst())
            else append(s)
        }
    }
    //
    val size = propSplit.size - 1
    val fieldNameBig = buildString {
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

fun String.createSerializedAnnotation(): AnnotationSpec {
    return AnnotationSpec.builder(SerializedName::class)
        .addMember(TAG_STRING_FULL_TYPE, this)
        .build()
}

fun createCountFieldsAnnotation(count: Int): AnnotationSpec {
    return AnnotationSpec.builder(CountFields::class)
        .addMember("count = %L", count)
        .build()
}

fun createParameterFieldAnnotation(position: Int): AnnotationSpec {
    return AnnotationSpec.builder(ParameterField::class)
        .addMember("position = %L", position)
        .build()
}

fun createJavaFieldAnnotation(): AnnotationSpec {
    return AnnotationSpec.builder(JvmField::class)
        .build()
}