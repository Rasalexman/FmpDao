package pro.krit.hiveprocessor

import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.*
import pro.krit.hiveprocessor.annotations.FmpDao
import pro.krit.hiveprocessor.annotations.FmpDatabase
import pro.krit.hiveprocessor.annotations.FmpLocalDao
import pro.krit.hiveprocessor.common.LocalDaoFields
import pro.krit.hiveprocessor.data.BindData
import pro.krit.hiveprocessor.data.TypeData
import pro.krit.hiveprocessor.provider.IHyperHiveDatabase
import java.io.IOException
import java.util.*
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic
import kotlin.properties.Delegates


@AutoService(Processor::class)
class FmpDaoProcessor : AbstractProcessor() {

    companion object {
        private const val CLASS_POSTFIX = "Impl"

        private const val DAO_PACKAGE_NAME = "pro.krit.generated.dao"
        private const val DATABASE_PACKAGE_NAME = "pro.krit.generated.database"

        private const val INIT_FIELDS_PATH = "pro.krit.hiveprocessor.extensions"

        private const val INIT_FIELDS_NAME = "initFields"

        private const val FIELD_PROVIDER = "hyperHiveDatabase"
        private const val FIELD_RESOURCE = "nameResource"
        private const val FIELD_PARAMETER = "nameParameter"
        private const val FIELD_CACHED = "isCached"
        private const val FIELD_DAO_FIELDS = "localDaoFields"

        private const val FILE_COMMENT =
            "This file was generated by HyperHiveProcessor. Do not modify!"
    }

    /* Processing Environment helpers */
    private var filer: Filer by Delegates.notNull()

    /* message helper */
    private var messager: Messager by Delegates.notNull()

    override fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)
        filer = processingEnv.filer
        messager = processingEnv.messager
    }

    override fun process(
        annotations: MutableSet<out TypeElement>,
        roundEnv: RoundEnvironment
    ): Boolean {
        val startTime = System.currentTimeMillis()
        println("HyperHiveProcessor started")
        val modulesMap = mutableListOf<BindData>()
        val fmpResult = collectAnnotationData(
            roundEnv.getElementsAnnotatedWith(FmpDao::class.java),
            modulesMap,
            ::getDataFromFmpDao
        )
        val fmpLocalResult = collectAnnotationData(
            roundEnv.getElementsAnnotatedWith(FmpLocalDao::class.java),
            modulesMap,
            ::getDataFromFmpLocalDao
        )
        if (!fmpResult && !fmpLocalResult) {
            processModules(modulesMap)

            val firstDatabase =
                roundEnv.getElementsAnnotatedWith(FmpDatabase::class.java).firstOrNull()
            firstDatabase?.let { currentDatabase ->
                processDatabase(currentDatabase, modulesMap)
            }
        }

        println("HyperHiveProcessor finished in `${System.currentTimeMillis() - startTime}` ms")
        return false
    }

    private fun processDatabase(databaseElement: Element, daoList: List<BindData>) {
        val annotationType = databaseElement.asType()
        val databaseData = ClassName.bestGuess(annotationType.toString())

        val className = databaseData.simpleName
        val packName = databaseData.packageName
        val fileName = className.createFileName()

        val superClassName = ClassName(packName, className)
        val classTypeSpec = TypeSpec.classBuilder(fileName)
        classTypeSpec.superclass(superClassName)

        val functs = databaseElement.enclosedElements
        functs.forEach { enclose ->
            if (enclose.kind == ElementKind.METHOD && enclose.modifiers.contains(Modifier.ABSTRACT)) {
                val (returnPack, returnClass) = enclose.asType().toString().getPackAndClass()
                val funcName = enclose.simpleName.toString()
                val returnedClass = ClassName(returnPack, returnClass)
                val returnElementData = daoList.find { it.mainData.className == returnClass }

                returnElementData?.let {
                    val returnedClassName = ClassName(DAO_PACKAGE_NAME, it.fileName)

                    val propName = funcName + CLASS_POSTFIX
                    val prop =
                        PropertySpec.builder(propName, returnedClassName.copy(nullable = true))
                            .mutable()
                            .addModifiers(KModifier.PRIVATE)
                            .initializer("null")
                            .build()

                    classTypeSpec.addProperty(prop)

                    val statementIf = "if($propName == null) "
                    val statementCreate = "$propName = %T($FIELD_PROVIDER = this) "
                    val statementReturn = "return $propName!!"

                    val funcSpec = FunSpec.builder(funcName)
                        .addModifiers(KModifier.OVERRIDE)
                        .returns(returnedClass)
                        .beginControlFlow(statementIf)
                        .addStatement(statementCreate, returnedClassName)
                        .endControlFlow()
                        .addStatement(statementReturn)
                        .build()

                    classTypeSpec.addFunction(funcSpec)
                }
            }
        }

        val fileBuilder = FileSpec.builder(DATABASE_PACKAGE_NAME, fileName)
            .addComment(FILE_COMMENT)
            .addType(classTypeSpec.build())

        val file = fileBuilder.build()
        try {
            file.writeTo(filer)
        } catch (e: IOException) {
            val message = java.lang.String.format("Unable to write file: %s", e.message)
            messager.printMessage(Diagnostic.Kind.ERROR, message)
        }
    }

    private fun processModules(moduleElements: List<BindData>) {
        moduleElements.forEach { bindData ->
            val classFileName = bindData.fileName
            val mainClassName = ClassName(bindData.mainData.packName, bindData.mainData.className)
            val classTypeSpec =
                TypeSpec.classBuilder(classFileName).addSuperinterface(mainClassName)
                    .primaryConstructor(constructorFunSpec(bindData))
                    .addProperties(createProperties(bindData))

            val file = FileSpec.builder(DAO_PACKAGE_NAME, classFileName)
                .addComment(FILE_COMMENT)
                .addType(classTypeSpec.build())
                .build()
            try {
                file.writeTo(filer)
            } catch (e: IOException) {
                val message = java.lang.String.format("Unable to write file: %s", e.message)
                messager.printMessage(Diagnostic.Kind.ERROR, message)
            }
        }
    }

    private fun createProperties(bindData: BindData): List<PropertySpec> {
        val hyperHiveProviderProp =
            PropertySpec.builder(FIELD_PROVIDER, IHyperHiveDatabase::class)
                .initializer(FIELD_PROVIDER)
                .addModifiers(KModifier.PUBLIC, KModifier.OVERRIDE)
                .build()

        val nameResourceProp = PropertySpec.builder(FIELD_RESOURCE, String::class)
            .initializer(FIELD_RESOURCE)
            .addModifiers(KModifier.PUBLIC, KModifier.OVERRIDE)
            .build()

        val parameterNameProp = PropertySpec.builder(FIELD_PARAMETER, String::class)
            .initializer(FIELD_PARAMETER)
            .addModifiers(KModifier.PUBLIC, KModifier.OVERRIDE)
            .build()

        val isCachedProp = PropertySpec.builder(FIELD_CACHED, Boolean::class)
            .initializer(FIELD_CACHED)
            .addModifiers(KModifier.PUBLIC, KModifier.OVERRIDE)
            .build()

        return if (bindData.isLocal) {
            val isLocalProp = PropertySpec.builder(
                FIELD_DAO_FIELDS,
                LocalDaoFields::class.asTypeName().copy(nullable = true)
            )
                .mutable()
                .initializer(FIELD_DAO_FIELDS)
                .addModifiers(KModifier.PUBLIC, KModifier.OVERRIDE)
                .build()
            listOf(
                hyperHiveProviderProp,
                nameResourceProp,
                parameterNameProp,
                isCachedProp,
                isLocalProp
            )
        } else {
            listOf(hyperHiveProviderProp, nameResourceProp, parameterNameProp, isCachedProp)
        }

    }

    private fun constructorFunSpec(bindData: BindData): FunSpec {
        val nameResource = ParameterSpec.builder(FIELD_RESOURCE, String::class).apply {
            if (bindData.resourceName.isNotEmpty()) {
                defaultValue("\"${bindData.resourceName}\"")
            }
        }.build()

        val parameterName = ParameterSpec.builder(FIELD_PARAMETER, String::class).apply {
            if (bindData.parameterName.isNotEmpty()) {
                defaultValue("\"${bindData.parameterName}\"")
            }
        }.build()

        val isCached = ParameterSpec.builder(FIELD_CACHED, Boolean::class)
            .defaultValue("${bindData.isCached}")
            .build()

        val hyperHiveProvider =
            ParameterSpec.builder(FIELD_PROVIDER, IHyperHiveDatabase::class)
                .build()

        return FunSpec.constructorBuilder()
            .addParameter(hyperHiveProvider)
            .addParameter(nameResource)
            .addParameter(parameterName)
            .addParameter(isCached)
            .apply {
                if (bindData.isLocal) {
                    val isLocalProp = ParameterSpec.builder(
                        FIELD_DAO_FIELDS,
                        LocalDaoFields::class.asTypeName().copy(nullable = true)
                    )
                        .defaultValue("null")
                        .build()
                    addParameter(isLocalProp)
                    addStatement("this.%M()", MemberName(INIT_FIELDS_PATH, INIT_FIELDS_NAME))
                }
            }
            .build()
    }

    private fun getDataFromFmpDao(element: Element): BindData {
        val annotation = element.getAnnotation(FmpDao::class.java)
        return createAnnotationData(
            element = element,
            resourceName = annotation.resourceName,
            parameterName = annotation.parameterName,
            isCached = annotation.isCached,
            isLocal = false
        )
    }

    private fun getDataFromFmpLocalDao(element: Element): BindData {
        val annotation = element.getAnnotation(FmpLocalDao::class.java)
        return createAnnotationData(
            element = element,
            resourceName = annotation.resourceName,
            parameterName = annotation.parameterName,
            isCached = false,
            isLocal = true
        )
    }

    private fun createAnnotationData(
        element: Element,
        resourceName: String,
        parameterName: String,
        isCached: Boolean = false,
        isLocal: Boolean = false
    ): BindData {
        val annotationType = element.asType()
        val elementClassName = ClassName.bestGuess(annotationType.toString())

        val className = elementClassName.simpleName
        val fileName = className.createFileName()

        return BindData(
            fileName = fileName,
            mainData = TypeData(
                packName = elementClassName.packageName,
                className = elementClassName.simpleName
            ),
            resourceName = resourceName,
            parameterName = parameterName,
            isCached = isCached,
            isLocal = isLocal
        )
    }

    private fun String.getPackAndClass(): Pair<String, String> {
        val splitted = this.replace("()", "").split(".")
        val className = splitted.last()
        val packName = splitted.subList(0, splitted.size - 1).joinToString(".")
        return packName to className
    }

    override fun getSupportedAnnotationTypes(): Set<String> {
        return setOf(
            FmpDao::class.java.canonicalName,
            FmpLocalDao::class.java.canonicalName,
            FmpDatabase::class.java.canonicalName
        )
    }

    override fun getSupportedOptions(): Set<String?>? {
        return Collections.singleton("org.gradle.annotation.processing.aggregating")
        //return Collections.singleton("org.gradle.annotation.processing.isolating")
    }

    override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latestSupported()

    private fun collectAnnotationData(
        elementsSet: Set<Element>,
        items: MutableList<BindData>,
        elementDataHandler: (Element) -> BindData
    ): Boolean {
        elementsSet.forEach { element ->
            val kind = element.kind
            if (kind == ElementKind.METHOD && kind != ElementKind.CLASS && kind != ElementKind.INTERFACE) {
                messager.printMessage(
                    Diagnostic.Kind.ERROR,
                    "Only classes and methods can be annotated as @FmpDao or @FmpDatabase"
                )
                return true
            }
            val bindingData = elementDataHandler(element)
            items.add(bindingData)
        }
        return false
    }

    private fun String.createFileName(): String {
        var className = this
        val classNameFirstChar = className.first()
        if (classNameFirstChar == 'I' || classNameFirstChar == 'i') {
            className = className.substring(1)
        }
        return className + CLASS_POSTFIX
    }

}