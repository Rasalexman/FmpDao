package pro.krit.hiveksp.generators

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
import pro.krit.processor.extensions.createFileName
import pro.krit.hiveksp.base.BaseDatabaseVisitor
import pro.krit.hiveksp.data.KspData

@KotlinPoetKspPreview
class DatabaseCodeGenerator(
    logger: KSPLogger,
    codeGenerator: CodeGenerator
) : BaseDatabaseVisitor(logger, codeGenerator) {

    companion object {
        private const val ARG_AS_SINGLETON = "asSingleton"
        private const val ARG_AS_DAO_PROVIDER = "asDaoProvider"

        private const val DAO_PACKAGE_NAME = "pro.krit.generated.dao"
        private const val DATABASE_PACKAGE_NAME = "pro.krit.generated.database"
        private const val REQUEST_PACKAGE_NAME = "pro.krit.generated.request"

        private const val BASE_FMP_DATABASE_NAME = "AbstractFmpDatabase"
        private const val FMP_BASE_NAME = "IFmpDatabase"

        private const val FIELD_PROVIDER = "fmpDatabase"
        private const val FIELD_HYPER_HIVE = "hyperHive"
        private const val FIELD_DEFAULT_HEADERS = "defaultHeaders"

        private const val NULL_INITIALIZER = "null"
        private const val RETURN_STATEMENT = "return"
        private const val TAG_CLASS_NAME = "%T"
    }

    override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: List<KspData>) {
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
            data,
            asDaoProvider
        )

        saveFiles(DATABASE_PACKAGE_NAME, fileName, listOf(classTypeSpec))
    }

    private fun createDatabaseExtendedFunction(
        classTypeSpec: TypeSpec.Builder,
        element: KSDeclaration,
        kspList: List<KspData>,
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
                createFunctions(classTypeSpec, extendedElement, kspList, asProvider)
                //logger.warn("checkExtendedInterface $extendedElement - extendedElements $functions")
                createDatabaseExtendedFunction(classTypeSpec, extendedElement, kspList, asProvider)
            }
        }
    }

    private fun createFunctions(
        classTypeSpec: TypeSpec.Builder,
        element: KSDeclaration,
        daoList: List<KspData>,
        asProvide: Boolean = false
    ): List<String> {
        val methods: List<KSFunctionDeclaration> =
            element.closestClassDeclaration()?.getDeclaredFunctions()?.toList().orEmpty()
        val allPropNames = mutableListOf<String>()
        //logger.warn("----------------> createFunctions")
        //logger.warn("------> element = $element | enclosedElements = $methods")

        methods.forEach { enclose ->

            if (enclose.isAbstract) {
                val returnType = enclose.returnType
                val returnDeclaration = returnType?.resolve()?.declaration
                val returnPack = returnDeclaration?.packageName?.asString().orEmpty()
                val returnClass = returnDeclaration?.simpleName?.asString().orEmpty()
                val funcName = enclose.simpleName.asString()
                val returnedClass = ClassName(returnPack, returnClass)

                val returnElementData = daoList.find { it.mainData.className == returnClass }

                //logger.warn("------> enclose = $enclose | returnElementData = $returnElementData")

                returnElementData?.let {
                    val instancePackName = if (returnElementData.isRequest) {
                        REQUEST_PACKAGE_NAME
                    } else {
                        DAO_PACKAGE_NAME
                    }
                    val returnedClassName = ClassName(instancePackName, it.fileName)

                    //logger.warn("------> funcName = ${funcName} ")
                    //logger.warn("------> returnedClass = ${it.mainData.className} ")

                    val instanceStatement = if (returnElementData.isRequest) {
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
                        .returns(returnedClass).apply {
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
                        }
                        .build()

                    //logger.warn("-----> func spec = ${funcSpec}")
                    classTypeSpec.addFunction(funcSpec)
                }
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