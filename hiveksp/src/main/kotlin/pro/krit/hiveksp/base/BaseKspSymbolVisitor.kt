package pro.krit.hiveksp.base

import com.google.devtools.ksp.symbol.*
import pro.krit.hiveksp.data.KspData

abstract class BaseKspSymbolVisitor : BaseVisitor(), KSVisitor<Unit, KspData?> {
    override fun visitAnnotated(annotated: KSAnnotated, data: Unit): KspData? = null

    override fun visitAnnotation(annotation: KSAnnotation, data: Unit): KspData? = null

    override fun visitCallableReference(reference: KSCallableReference, data: Unit): KspData? = null

    override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit): KspData? = null

    override fun visitClassifierReference(reference: KSClassifierReference, data: Unit): KspData? = null

    override fun visitDeclaration(declaration: KSDeclaration, data: Unit): KspData? = null

    override fun visitDeclarationContainer(
        declarationContainer: KSDeclarationContainer,
        data: Unit
    ): KspData? = null

    override fun visitDynamicReference(reference: KSDynamicReference, data: Unit): KspData? = null

    override fun visitFile(file: KSFile, data: Unit): KspData? = null

    override fun visitFunctionDeclaration(function: KSFunctionDeclaration, data: Unit): KspData? = null

    override fun visitModifierListOwner(
        modifierListOwner: KSModifierListOwner,
        data: Unit
    ): KspData? = null

    override fun visitNode(node: KSNode, data: Unit): KspData? = null

    override fun visitParenthesizedReference(
        reference: KSParenthesizedReference,
        data: Unit
    ): KspData? = null

    override fun visitPropertyAccessor(accessor: KSPropertyAccessor, data: Unit): KspData? = null

    override fun visitPropertyDeclaration(property: KSPropertyDeclaration, data: Unit): KspData? = null

    override fun visitPropertyGetter(getter: KSPropertyGetter, data: Unit): KspData? = null

    override fun visitPropertySetter(setter: KSPropertySetter, data: Unit): KspData? = null

    override fun visitReferenceElement(element: KSReferenceElement, data: Unit): KspData? = null

    override fun visitTypeAlias(typeAlias: KSTypeAlias, data: Unit): KspData? = null

    override fun visitTypeArgument(typeArgument: KSTypeArgument, data: Unit): KspData? = null

    override fun visitTypeParameter(typeParameter: KSTypeParameter, data: Unit): KspData? = null

    override fun visitTypeReference(typeReference: KSTypeReference, data: Unit): KspData? = null

    override fun visitValueArgument(valueArgument: KSValueArgument, data: Unit): KspData? = null

    override fun visitValueParameter(valueParameter: KSValueParameter, data: Unit): KspData? = null
}