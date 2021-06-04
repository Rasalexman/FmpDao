package pro.krit.hiveprocessor.extensions

import java.lang.reflect.Field

fun Field.isInteger(): Boolean {
    return this.type.simpleName == "Integer"
}