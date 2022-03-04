package pro.krit.hiveksp

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.*
import pro.krit.hiveksp.base.BaseSymbolVisitor

class FmpAnnotationVisitor(
    logger: KSPLogger,
    codeGenerator: CodeGenerator
) : BaseSymbolVisitor(logger, codeGenerator) {

    override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
        logger.warn("---> class name = ${classDeclaration.simpleName.asString()}")
        val kspData = getKspDataFromAnnotation(classDeclaration)
    }

    override fun visitAnnotated(annotated: KSAnnotated, data: Unit) {
        logger.warn("---> annotation name = ${annotated}")
    }

    override fun visitDeclaration(declaration: KSDeclaration, data: Unit) {
        logger.warn("---> declaration name = ${declaration.simpleName.asString()}")
    }

    override fun visitDeclarationContainer(
        declarationContainer: KSDeclarationContainer,
        data: Unit
    ) {
        logger.warn("---> declaration container name = ${declarationContainer.declarations.toList()}")
    }

}