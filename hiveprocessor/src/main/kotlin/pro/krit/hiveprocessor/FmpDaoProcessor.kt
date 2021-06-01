package pro.krit.hiveprocessor

import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import pro.krit.hiveprocessor.annotations.FmpDao
import pro.krit.hiveprocessor.annotations.FmpDatabase
import pro.krit.hiveprocessor.annotations.FmpLocalDao
import pro.krit.hiveprocessor.annotations.FmpQuery
import pro.krit.hiveprocessor.common.LocalDaoFields
import pro.krit.hiveprocessor.data.BindData
import pro.krit.hiveprocessor.data.TypeData
import pro.krit.hiveprocessor.provider.IHyperHiveDatabase
import java.io.IOException
import java.util.*
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.*
import javax.lang.model.type.TypeMirror
import javax.lang.model.util.Elements
import javax.lang.model.util.Types
import javax.tools.Diagnostic
import kotlin.properties.Delegates


@AutoService(Processor::class)
class FmpDaoProcessor : AbstractProcessor() {

    companion object {
        private const val CLASS_POSTFIX = "Impl"

        private const val DAO_PACKAGE_NAME = "pro.krit.generated.dao"
        private const val DATABASE_PACKAGE_NAME = "pro.krit.generated.database"

        private const val INIT_FIELDS_PATH = "pro.krit.hiveprocessor.extensions"
        private const val QUERY_EXECUTER_PATH = "pro.krit.hiveprocessor.common"
        private const val QUERY_EXECUTER_NAME = "QueryExecuter"

        private const val HYPER_HIVE_BASE_CLASSE_NAME = "HyperHiveDatabase"

        private const val INIT_FIELDS_NAME = "initFields"

        private const val FUNC_MEMBER_STATEMENT = "this.%M()"

        private const val FIELD_PROVIDER = "hyperHiveDatabase"
        private const val FIELD_RESOURCE = "nameResource"
        private const val FIELD_PARAMETER = "nameParameter"
        private const val FIELD_CACHED = "isCached"
        private const val FIELD_DAO_FIELDS = "localDaoFields"

        private const val LIST_RETURN_TYPE = "java.util.List"
        private const val SUSPEND_QUALIFIER = "kotlin.coroutines.Continuation"

        private const val KOTLIN_PATH = "kotlin"
        private const val KOTLIN_LIST_PATH = "kotlin.collections"
        private const val KOTLIN_LIST_NAME = "List"

        private const val NULL_INITIALIZER = "null"
        private const val TAG_CLASS_NAME = "%T"

        private const val QUERY_VALUE = "val query: String = "
        private const val QUERY_RETURN = "return %T.executeQuery(this, query)"

        private const val FILE_COMMENT =
            "This file was generated by HyperHiveProcessor. Do not modify!"


        /** Element Utilities, obtained from the processing environment */
        private var ELEMENT_UTILS: Elements by Delegates.notNull()

        /** Type Utilities, obtained from the processing environment */
        private var TYPE_UTILS: Types by Delegates.notNull()
    }

    /* Processing Environment helpers */
    private var filer: Filer by Delegates.notNull()

    /* message helper */
    private var messager: Messager by Delegates.notNull()

    override fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)
        filer = processingEnv.filer
        messager = processingEnv.messager
        ELEMENT_UTILS = processingEnv.elementUtils
        TYPE_UTILS = processingEnv.typeUtils
    }

    override fun process(
        annotations: MutableSet<out TypeElement>,
        roundEnv: RoundEnvironment
    ): Boolean {
        val startTime = System.currentTimeMillis()
        println("HyperHiveProcessor started")
        val modulesMap = mutableListOf<BindData>()
        // Create files for FmpDao annotation
        val fmpResult = collectAnnotationData(
            roundEnv.getElementsAnnotatedWith(FmpDao::class.java),
            modulesMap,
            ::getDataFromFmpDao
        )
        // Create files for FmpLocalDao annotation
        val fmpLocalResult = collectAnnotationData(
            roundEnv.getElementsAnnotatedWith(FmpLocalDao::class.java),
            modulesMap,
            ::getDataFromFmpLocalDao
        )
        // If we has generated files for database without errors
        if (!fmpResult && !fmpLocalResult) {
            processDaos(modulesMap)

            val databases = roundEnv.getElementsAnnotatedWith(FmpDatabase::class.java)
            databases?.forEach { currentDatabase ->
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
        val classTypeSpec = TypeSpec.objectBuilder(fileName)
        classTypeSpec.superclass(superClassName)

        // Extended classes only for Interfaces
        val extendedTypeMirrors = TYPE_UTILS.directSupertypes(databaseElement.asType())
        if (extendedTypeMirrors != null && extendedTypeMirrors.size > 1) {
            val extendedElements = extendedTypeMirrors.mapToInterfaceElements()
            extendedElements.forEach {
                createFunctions(classTypeSpec, it, daoList)
            }
        }
        // than create function for database
        createFunctions(classTypeSpec, databaseElement, daoList)

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

    private fun createFunctions(
        classTypeSpec: TypeSpec.Builder,
        element: Element,
        daoList: List<BindData>
    ) {
        val methods = element.enclosedElements
        methods.forEach { enclose ->
            if (enclose.isAbstractMethod()) {
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
                            .initializer(NULL_INITIALIZER)
                            .build()

                    classTypeSpec.addProperty(prop)

                    val statementIf = "if($propName == $NULL_INITIALIZER) "
                    val statementCreate = "$propName = $TAG_CLASS_NAME($FIELD_PROVIDER = this) "
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
    }

    private fun processDaos(moduleElements: List<BindData>) {
        moduleElements.forEach { bindData ->
            val classFileName = bindData.fileName
            val mainClassName = ClassName(bindData.mainData.packName, bindData.mainData.className)
            val classTypeSpec =
                TypeSpec.classBuilder(classFileName).addSuperinterface(mainClassName)
                    .primaryConstructor(constructorFunSpec(bindData))
                    .addProperties(createProperties(bindData))

            val functs = bindData.element.enclosedElements
            functs.forEach { enclose ->
                val queryAnnotation = enclose.getAnnotation(FmpQuery::class.java)
                if (queryAnnotation != null && enclose.isAbstractMethod()) {
                    val returnType = enclose.asType().toString()
                    val isSuspend = returnType.contains(SUSPEND_QUALIFIER)
                    val (returnPack, returnClass) = returnType.getPackAndClass()
                    val funcName = enclose.simpleName.toString()
                    val returnedClass = returnType.createReturnType(returnPack, returnClass)

                    val funcSpec = FunSpec.builder(funcName)
                    var query = queryAnnotation.query.replaceTablePattern(returnClass, bindData)

                    val parameters = enclose.takeParameters(isSuspend)
                    parameters.forEach { property ->
                        val propName = property.toString()
                        val propertyClassName =
                            property.asType().toString().getPackAndClass().second

                        val kotlinPropClass = propertyClassName.capitalizeFirst()
                        val propertyClass = ClassName(KOTLIN_PATH, kotlinPropClass)
                        val queryProperty = propName.screenParameter(propertyClassName)
                        val replacedProperty = ":$propName"

                        query = query.replace(replacedProperty, queryProperty)

                        val parameterSpec = ParameterSpec.builder(propName, propertyClass).build()
                        funcSpec.addParameter(parameterSpec)
                    }

                    val queryExecuter = ClassName(QUERY_EXECUTER_PATH, QUERY_EXECUTER_NAME)

                    val statementQuery = "\"${query}\""

                    funcSpec.apply {
                        if (isSuspend) {
                            addModifiers(KModifier.OVERRIDE, KModifier.SUSPEND)
                        } else {
                            addModifiers(KModifier.OVERRIDE)
                        }
                    }.returns(returnedClass).apply {
                        addStatement(QUERY_VALUE)
                        addStatement(statementQuery)
                        addStatement(QUERY_RETURN, queryExecuter)
                    }

                    classTypeSpec.addFunction(funcSpec.build())
                }
            }

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
            ).mutable()
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
                        .defaultValue(NULL_INITIALIZER)
                        .build()
                    addParameter(isLocalProp)
                    addStatement(
                        FUNC_MEMBER_STATEMENT,
                        MemberName(INIT_FIELDS_PATH, INIT_FIELDS_NAME)
                    )
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
            element = element,
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

    // Take extended interfaces for implement abstract methods
    private fun List<TypeMirror>.mapToInterfaceElements(): List<Element> {
        return this.mapNotNull { typeMirror ->
            typeMirror.takeIf {
                val currentClassName = it.toString().getPackAndClass().second
                currentClassName != HYPER_HIVE_BASE_CLASSE_NAME
            }?.run {
                val typeElement = TYPE_UTILS.asElement(this)
                // Only for Interfaces
                typeElement.takeIf { it.kind == ElementKind.INTERFACE }
            }
        }
    }

    private fun Element.takeParameters(isSuspend: Boolean): List<Element> {
        val allParams = (this as? ExecutableElement)?.parameters.orEmpty()
        return if (isSuspend) {
            if (allParams.isNotEmpty() && allParams.size > 1) {
                allParams.subList(0, allParams.size - 1)
            } else emptyList()
        } else {
            allParams
        }
    }

    private fun Element.isAbstractMethod(): Boolean {
        return this.kind == ElementKind.METHOD && this.modifiers.contains(Modifier.ABSTRACT)
    }

    private fun String.createReturnType(returnPack: String, returnClass: String): TypeName {
        val isList = this.contains(LIST_RETURN_TYPE)
        return if (isList) {
            val list = ClassName(KOTLIN_LIST_PATH, KOTLIN_LIST_NAME)
            list.parameterizedBy(ClassName(returnPack, returnClass))
        } else {
            ClassName(returnPack, returnClass)
        }
    }

    private fun String.getPackAndClass(): Pair<String, String> {
        val withoutSuspend = this.withoutSuspend()
        val withoutBraces = withoutSuspend.replace("()", "")
        val replaced = withoutBraces.splitArray()

        val splitted = replaced.split(".")
        val className = splitted.last()
        val packName = splitted.subList(0, splitted.size - 1).joinToString(".")
        return packName to className
    }

    private fun String.withoutSuspend(): String {
        val isSuspend = this.contains(SUSPEND_QUALIFIER)
        return if (isSuspend) {
            val indexStart = this.indexOf(LIST_RETURN_TYPE)
            val lastIndex = this.lastIndexOf(">")
            this.substring(indexStart, lastIndex)
        } else {
            this
        }
    }

    private fun String.replaceTablePattern(returnClass: String, bindData: BindData): String {
        val tablePattern = ":$returnClass"
        return if (this.contains(tablePattern)) {
            val tableName = "${bindData.resourceName}_${bindData.parameterName}"
            this.replace(tablePattern, tableName)
        } else this
    }

    private fun String.splitArray(): String {
        val ifArray = this.contains(LIST_RETURN_TYPE)
        return if (ifArray) {
            this.split("<")[1].replace(">", "")
        } else {
            this
        }
    }

    private fun String.screenParameter(className: String): String {
        return if (className == "String") {
            "\'$$this\'"
        } else {
            "$$this"
        }
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
                    "Only classes and interfaces can be annotated as @FmpDao or @FmpDatabase"
                )
                return true
            }
            val bindingData = elementDataHandler(element)
            items.add(bindingData)
        }
        return false
    }

    private fun String.capitalizeFirst(): String {
        return replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
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