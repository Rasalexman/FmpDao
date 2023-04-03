package com.rasalexman.hiveksp.base

import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSValueArgument
import com.google.devtools.ksp.symbol.KSVisitorVoid

abstract class BaseVisitor : KSVisitorVoid() {

    protected fun List<KSAnnotation>.hasAnnotation(name: String): Boolean {
        return findAnnotation(name) != null
    }

    protected fun List<KSAnnotation>.findAnnotation(name: String): KSAnnotation? {
        return find { it.annotationType.resolve().declaration.simpleName.asString() == name }
    }

    @Suppress("UNCHECKED_CAST")
    protected fun <T : Any> List<KSValueArgument>.getArgumentValue(name: String): T? {
        return firstOrNull { it.name?.asString() == name }?.value as? T
    }
}