package com.rasalexman.hiveksp.base

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSValueArgument
import com.rasalexman.hhivecore.data.TypeData
import com.rasalexman.hhivecore.extensions.createFileName
import com.rasalexman.hiveksp.common.Params.CREATE_TABLE_ON_INIT
import com.rasalexman.hiveksp.common.Params.FIELDS
import com.rasalexman.hiveksp.common.Params.IS_DELTA
import com.rasalexman.hiveksp.common.Params.PARAMS
import com.rasalexman.hiveksp.common.Params.RESOURCE_NAME
import com.rasalexman.hiveksp.common.Params.TABLE_NAME
import com.rasalexman.hiveksp.data.KspData

abstract class BaseSymbolVisitor(
    logger: KSPLogger,
    codeGenerator: CodeGenerator
) : BaseCodeGenerator(logger, codeGenerator) {

    protected fun getKspDataFromAnnotation(element: KSDeclaration): KspData {
        val firstAnnotation = element.annotations.firstOrNull()
        val annotationArgs: List<KSValueArgument> = firstAnnotation?.arguments.orEmpty()
        val resourceName: String = annotationArgs.getArgumentValue<String>(RESOURCE_NAME).orEmpty()
        val tableName: String = annotationArgs.getArgumentValue<String>(TABLE_NAME).orEmpty()
        val fields: List<String> = annotationArgs.getArgumentValue<List<String>>(FIELDS).orEmpty()
        val params: List<String> = annotationArgs.getArgumentValue<List<String>>(PARAMS).orEmpty()
        val createTableOnInit: Boolean = annotationArgs.getArgumentValue(CREATE_TABLE_ON_INIT) ?: false
        val isDelta: Boolean = annotationArgs.getArgumentValue(IS_DELTA) ?: false
        val className = element.simpleName.asString()
        val fileName = className.createFileName()
        
        val shortName = firstAnnotation?.shortName?.asString().orEmpty()
        val isLocal = shortName.contains("local", ignoreCase = true)
        val isRequest = shortName.contains("request", ignoreCase = true)
        val isWebRequest = shortName.contains("web", ignoreCase = true)

        return KspData(
            element = element,
            fileName = fileName,
            mainData = TypeData(
                packName = element.packageName.asString(),
                className = className
            ),
            createTableOnInit = createTableOnInit,
            parameters = params,
            fields = fields,
            resourceName = resourceName,
            tableName = tableName,
            isDelta = isDelta,
            isLocal = isLocal,
            isRequest = isRequest,
            isWebRequest = isWebRequest
        )/*.also {
            logger.logging("----> KSP DATA = $it")
        }*/
    }

    abstract fun processSaveKspData(kspData: KspData)
}