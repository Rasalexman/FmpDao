package pro.krit.hiveksp.base

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.*
import com.squareup.kotlinpoet.ksp.KotlinPoetKspPreview
import pro.krit.hiveksp.data.KspData

@KotlinPoetKspPreview
abstract class BaseDatabaseVisitor(
    logger: KSPLogger,
    codeGenerator: CodeGenerator
) : BaseCodeGenerator(logger, codeGenerator), KSVisitor<List<KspData>, Unit> {
    override fun visitAnnotated(annotated: KSAnnotated, data: List<KspData>) = Unit
    override fun visitAnnotation(annotation: KSAnnotation, data: List<KspData>) = Unit
    override fun visitCallableReference(reference: KSCallableReference, data: List<KspData>) = Unit
    override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: List<KspData>) = Unit
    override fun visitClassifierReference(reference: KSClassifierReference, data: List<KspData>) = Unit
    override fun visitDeclaration(declaration: KSDeclaration, data: List<KspData>) = Unit
    override fun visitDeclarationContainer(
        declarationContainer: KSDeclarationContainer,
        data: List<KspData>
    ) = Unit

    override fun visitDynamicReference(reference: KSDynamicReference, data: List<KspData>) = Unit

    override fun visitFile(file: KSFile, data: List<KspData>) = Unit

    override fun visitFunctionDeclaration(function: KSFunctionDeclaration, data: List<KspData>) = Unit

    override fun visitModifierListOwner(
        modifierListOwner: KSModifierListOwner,
        data: List<KspData>
    ) = Unit

    override fun visitNode(node: KSNode, data: List<KspData>) = Unit

    override fun visitParenthesizedReference(
        reference: KSParenthesizedReference,
        data: List<KspData>
    ) = Unit

    override fun visitPropertyAccessor(accessor: KSPropertyAccessor, data: List<KspData>) = Unit
    override fun visitPropertyDeclaration(property: KSPropertyDeclaration, data: List<KspData>) = Unit
    override fun visitPropertyGetter(getter: KSPropertyGetter, data: List<KspData>) = Unit
    override fun visitPropertySetter(setter: KSPropertySetter, data: List<KspData>) = Unit
    override fun visitReferenceElement(element: KSReferenceElement, data: List<KspData>) = Unit
    override fun visitTypeAlias(typeAlias: KSTypeAlias, data: List<KspData>) = Unit
    override fun visitTypeArgument(typeArgument: KSTypeArgument, data: List<KspData>) = Unit
    override fun visitTypeParameter(typeParameter: KSTypeParameter, data: List<KspData>) = Unit
    override fun visitTypeReference(typeReference: KSTypeReference, data: List<KspData>) = Unit
    override fun visitValueArgument(valueArgument: KSValueArgument, data: List<KspData>) = Unit
    override fun visitValueParameter(valueParameter: KSValueParameter, data: List<KspData>) = Unit
}