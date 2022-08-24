package pro.krit.hiveksp.base

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.ksp.writeTo
import java.io.IOException

abstract class BaseCodeGenerator(
    protected val logger: KSPLogger,
    private val codeGenerator: CodeGenerator
) : BaseVisitor() {

    companion object {
        private const val FILE_COMMENT =
            "This file was generated by  Do not modify!"
    }

    protected fun saveFiles(
        packageName: String,
        classFileName: String,
        builders: List<TypeSpec.Builder>
    ) {
        val file = FileSpec.builder(packageName, classFileName)
            .addFileComment(FILE_COMMENT)
            .apply {
                builders.forEach { builder ->
                    addType(builder.build())
                }
            }
            .build()
        try {
            //file.writeTo(codeGenerator = codeGenerator, aggregating = false)
            file.writeTo(codeGenerator = codeGenerator, dependencies = Dependencies.ALL_FILES)
        } catch (e: IOException) {
            val message = java.lang.String.format("Unable to write file: %s", e.message)
            logger.error(message)
        }
    }
}