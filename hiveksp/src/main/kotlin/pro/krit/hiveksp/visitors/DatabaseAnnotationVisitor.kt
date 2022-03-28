package pro.krit.hiveksp.visitors

import com.google.devtools.ksp.closestClassDeclaration
import com.google.devtools.ksp.getDeclaredFunctions
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSValueArgument
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ksp.KotlinPoetKspPreview
import pro.krit.hhivecore.extensions.createFileName
import pro.krit.hiveksp.base.BaseCodeGenerator
import pro.krit.hiveksp.common.Consts.FIELD_DEFAULT_HEADERS
import pro.krit.hiveksp.common.Consts.FIELD_HYPER_HIVE
import pro.krit.hiveksp.common.Consts.FIELD_PROVIDER

@KotlinPoetKspPreview
class DatabaseAnnotationVisitor(
    logger: KSPLogger,
    codeGenerator: CodeGenerator
) : BaseCodeGenerator(logger, codeGenerator) {

    companion object {
        private const val ARG_AS_SINGLETON = "asSingleton"
        private const val ARG_AS_DAO_PROVIDER = "asDaoProvider"

        private const val DAO_PACKAGE_NAME = "pro.krit.generated.dao"
        private const val DATABASE_PACKAGE_NAME = "pro.krit.generated.database"
        private const val REQUEST_PACKAGE_NAME = "pro.krit.generated.request"

        private const val BASE_FMP_DATABASE_NAME = "AbstractFmpDatabase"
        private const val FMP_BASE_NAME = "IFmpDatabase"
        private const val FMP_REST_NAME = "FmpRestRequest"
        private const val FMP_WEB_NAME = "FmpWebRequest"

        private const val NULL_INITIALIZER = "null"
        private const val RETURN_STATEMENT = "return"
        private const val TAG_CLASS_NAME = "%T"
    }

    override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
        //logger.warn("---> class name = ${classDeclaration.simpleName.asString()}")

        val firstAnnotation = classDeclaration.annotations.firstOrNull()
        val annotationArgs: List<KSValueArgument> = firstAnnotation?.arguments.orEmpty()
        val isSingleInstance: Boolean = annotationArgs.getArgumentValue(ARG_AS_SINGLETON) ?: false
        val asDaoProvider: Boolean = annotationArgs.getArgumentValue(ARG_AS_DAO_PROVIDER) ?: false
        val className = classDeclaration.simpleName.asString()
        val packName = classDeclaration.packageName.asString()
        val fileName = className.createFileName()

        val superClassName = ClassName(packName, className)
        val classTypeSpec = if (isSingleInstance) {
            TypeSpec.objectBuilder(fileName)
        } else {
            TypeSpec.classBuilder(fileName)
        }
        classTypeSpec.superclass(superClassName)

        // Extended classes only for Interfaces
        createDatabaseExtendedFunction(
            classTypeSpec,
            classDeclaration,
            asDaoProvider
        )

        saveFiles(DATABASE_PACKAGE_NAME, fileName, listOf(classTypeSpec))
    }

    private fun createDatabaseExtendedFunction(
        classTypeSpec: TypeSpec.Builder,
        element: KSDeclaration,
        asProvider: Boolean
    ) {
        val extendedTypeMirrors = element.closestClassDeclaration()?.superTypes?.toList()?.map {
            it.resolve().declaration
        }.orEmpty()
        //logger.warn("-------> extended = ${extendedTypeMirrors}")
        if (extendedTypeMirrors.isNotEmpty()) {
            val extendedElements =
                extendedTypeMirrors.mapToInterfaceElements().filter { extElement ->
                    !extElement.simpleName.asString().contains(FMP_BASE_NAME)
                }
            extendedElements.forEach { extendedElement ->
                createFunctions(classTypeSpec, extendedElement, asProvider)
                //logger.warn("checkExtendedInterface $extendedElement - extendedElements $functions")
                createDatabaseExtendedFunction(classTypeSpec, extendedElement, asProvider)
            }
        }
    }

    private fun createFunctions(
        classTypeSpec: TypeSpec.Builder,
        element: KSDeclaration,
        asProvide: Boolean = false
    ): List<String> {
        val methods: List<KSFunctionDeclaration> =
            element.closestClassDeclaration()?.getDeclaredFunctions()?.toList().orEmpty()
        val allPropNames = mutableListOf<String>()
        //logger.warn("----------------> createFunctions")
        //logger.warn("------> element = $element | methods = $methods")

        methods.forEach { enclose ->

            if (enclose.isAbstract) {
                val returnType = enclose.returnType
                val returnDeclaration = returnType?.resolve()?.declaration
                val returnPack = returnDeclaration?.packageName?.asString().orEmpty()
                val returnClass = returnDeclaration?.simpleName?.asString().orEmpty()
                val funcName = enclose.simpleName.asString()
                val returnedClass = ClassName(returnPack, returnClass)

                val annotations = returnDeclaration?.annotations?.toList().orEmpty()
                val restAnnotation = annotations.findAnnotation(FMP_REST_NAME)
                val webAnnotation = annotations.findAnnotation(FMP_WEB_NAME)
                val isRequest = restAnnotation != null || webAnnotation != null
                val instancePackName = if (isRequest) {
                    REQUEST_PACKAGE_NAME
                } else {
                    DAO_PACKAGE_NAME
                }

                //logger.warn("------> enclose = $enclose")
                //logger.warn("--------------> | returnClass = $returnClass")

                val returnedClassName = ClassName(instancePackName, returnClass.createFileName())
                val instanceStatement = if (isRequest) {
                    buildString {
                        append("$TAG_CLASS_NAME(")
                        appendLine()
                        append("$FIELD_HYPER_HIVE = this.provideHyperHive(),")
                        appendLine()
                        append("$FIELD_DEFAULT_HEADERS = this.getDefaultHeaders()")
                        appendLine()
                        append(")")
                    }
                } else {
                    "$TAG_CLASS_NAME($FIELD_PROVIDER = this)"
                }
                val funcSpec = FunSpec.builder(funcName)
                    .addModifiers(KModifier.OVERRIDE)
                    .returns(returnedClass)
                    .apply {
                        if (asProvide) {
                            addStatement(
                                "$RETURN_STATEMENT $instanceStatement",
                                returnedClassName
                            )
                        } else {
                            val propName = funcName.createFileName()
                            allPropNames.add(propName)
                            val prop =
                                PropertySpec.builder(
                                    propName,
                                    returnedClassName.copy(nullable = true)
                                )
                                    .mutable()
                                    .addModifiers(KModifier.PRIVATE)
                                    .initializer(NULL_INITIALIZER)
                                    .build()

                            classTypeSpec.addProperty(prop)

                            val statementIf = "if($propName == $NULL_INITIALIZER) "
                            val statementCreate = "$propName = $instanceStatement"
                            val statementReturn = "$RETURN_STATEMENT $propName!!"
                            beginControlFlow(statementIf)
                            addStatement(statementCreate, returnedClassName)
                            endControlFlow()
                            addStatement(statementReturn)
                        }
                    }.build()

                //logger.warn("-----> func spec = ${funcSpec}")
                classTypeSpec.addFunction(funcSpec)
            }
        }

        return allPropNames
    }

    // Take extended interfaces for implement abstract methods
    private fun List<KSDeclaration>.mapToInterfaceElements(): List<KSDeclaration> {
        return this.mapNotNull { typeMirror ->
            typeMirror.takeIf {
                val currentClassName = it.simpleName.asString()
                !currentClassName.contains(BASE_FMP_DATABASE_NAME)
            }
        }
    }
}