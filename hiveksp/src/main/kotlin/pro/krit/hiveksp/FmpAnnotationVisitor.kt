package pro.krit.hiveksp

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSDeclarationContainer
import com.squareup.kotlinpoet.ksp.KotlinPoetKspPreview
import pro.krit.hiveksp.base.BaseSymbolVisitor
import pro.krit.hiveksp.data.KspData

@KotlinPoetKspPreview
class FmpAnnotationVisitor(
    logger: KSPLogger
) : BaseSymbolVisitor(logger) {

    override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit): KspData {
        //logger.warn("---> class name = ${classDeclaration.simpleName.asString()}")
        val kspData = getKspDataFromAnnotation(classDeclaration)
        return kspData
    }
}