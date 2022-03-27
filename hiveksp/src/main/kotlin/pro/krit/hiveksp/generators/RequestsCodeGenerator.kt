package pro.krit.hiveksp.generators

import com.google.devtools.ksp.closestClassDeclaration
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.ksp.KotlinPoetKspPreview
import pro.krit.hhivecore.base.IRequest
import pro.krit.hhivecore.data.FieldData
import pro.krit.hhivecore.extensions.*
import pro.krit.hiveksp.base.BaseCodeGenerator
import pro.krit.hiveksp.common.Params
import pro.krit.hiveksp.data.KspData

@DelicateKotlinPoetApi("Use it with your own responsibility")
@KotlinPoetKspPreview
class RequestsCodeGenerator(
    logger: KSPLogger,
    codeGenerator: CodeGenerator
) : BaseCodeGenerator(logger, codeGenerator) {

    companion object {
        private const val REQUEST_PACKAGE_NAME = "pro.krit.generated.request"

        private const val FUNC_CREATE_PARAMS_MAP_NAME = "createParamsMap"
        private const val FUNC_GET_PARAMETER_NAME = "getParameterName"

        private const val TAG_MEMBER_FULL = "%M()"
        private const val TAG_MEMBER_HALF = "%M("

        private const val API_MODEL_PATH = "com.mobrun.plugin.api"
        private const val OBJ_RAW_STATUS_PATH = "pro.krit.hhivecore.request"
        private const val CLASS_OBJ_RAW_STATUS = "ObjectRawStatus"
        private const val CLASS_HYPER_HIVE = "HyperHive"
        private const val FIELD_HYPER_HIVE = "hyperHive"
        private const val FIELD_DEFAULT_HEADERS = "defaultHeaders"
        private const val FIELD_RESOURCE_NAME = "resourceName"
        private const val FIELD_PARAMS = "params"
        private const val FIELD_PARAMS_GET = "params.getOrNull(%s).orEmpty()"

        private const val NULL_INITIALIZER = "null"
        private const val RETURN_STATEMENT = "return"

        private const val PARAMS_COMMENT_FIRST = "This function was autogenerated."
        private const val PARAMS_COMMENT_SECOND =
            " Use annotation field - 'parameters' to add request params"
        private const val PARAMS_COMMENT_THIRD = " Please specify %s vararg param: "
    }


    fun processRequest(moduleElements: List<KspData>) {
        moduleElements.forEach { data ->
            val element = data.element
            val parameters = data.parameters
            val mainPackName = data.mainData.packName
            val mainClassName = data.mainData.className
            val requestInterface = if (data.isWebRequest) {
                IRequest.IRestRequest::class
            } else {
                IRequest.IWebRequest::class
            }.asTypeName()

            val resourceName = data.resourceName
            val typeClassName = mainClassName.createFileName()

            //------ ALL ELEMENT CLASSES
            val elementsFiles = mutableListOf<TypeSpec.Builder>()
            // типы параметров запроса
            val paramsAnnotationClasses = mutableMapOf<String, TypeName>()

            //------ Main Request Class
            val mainSuperInterface = ClassName(mainPackName, mainClassName)
            val mainClassTypeSpec = TypeSpec.classBuilder(typeClassName)
                .addSuperinterface(mainSuperInterface)

            val mainClassPropSpec = FunSpec.constructorBuilder()
            createMainRequestClass(
                resourceName,
                parameters,
                mainClassPropSpec,
                mainClassTypeSpec
            )
            elementsFiles.add(mainClassTypeSpec)

            //logger.warn("-----> element = $element")
            val elementClass = element.closestClassDeclaration()
            val elementEnclosed = elementClass?.declarations?.toList().orEmpty()

            //logger.warn("-----> elementEnclosed = $elementEnclosed")
            if (elementEnclosed.isNotEmpty()) {

                //------ Result models
                val resultModelClassName = ClassName(
                    REQUEST_PACKAGE_NAME,
                    mainClassName.createFileName(RESULT_MODEL_POSTFIX)
                )
                val resultModelTypeSpec =
                    TypeSpec.classBuilder(resultModelClassName)
                resultModelTypeSpec.addModifiers(KModifier.DATA)
                val resultConstructorSpec = FunSpec.constructorBuilder()

                elementEnclosed.forEach { inter ->
                    val annotations = inter.annotations.toList()
                    val tableAnnotation = annotations.findAnnotation("FmpTable")
                    val paramAnnotation = annotations.findAnnotation("FmpParam")

                    //val annotationName = tableAnnotation?.shortName?.asString() ?: paramAnnotation?.shortName?.asString().orEmpty()

                    //logger.warn("-----> annotationName = $annotationName")

                    val isTableAnnotation = tableAnnotation != null
                    val isParamAnnotation = paramAnnotation != null
                    val arguments =
                        tableAnnotation?.arguments ?: paramAnnotation?.arguments.orEmpty()
                    val annotationInnerName: String =
                        arguments.getArgumentValue<String>(Params.NAME).orEmpty()
                    val propClassFields =
                        arguments.getArgumentValue<List<String>>(Params.FIELDS).orEmpty()

                    //logger.warn("-----> annotationInnerName = $annotationInnerName")

                    // название таблицы
                    val postFixName = if (isTableAnnotation) MODEL_POSTFIX else PARAMS_POSTFIX
                    val propName = mainClassName + inter.simpleName.asString().capitalizeFirst()
                    val propModelData = annotationInnerName.asModelFieldData()
                    val propTypeClassName = propName.createFileName(postFixName).capitalizeFirst()
                    val propClassName = ClassName(REQUEST_PACKAGE_NAME, propTypeClassName)
                    val propClassSpec =
                        TypeSpec.classBuilder(propTypeClassName).addModifiers(KModifier.DATA)
                    val isList: Boolean = arguments.getArgumentValue(Params.IS_LIST) ?: false

                    // добавляем в маппу для параметров запроса
                    if (isParamAnnotation) {
                        // выбираем правильный тип данных
                        val typeName = createListTypeName(propClassName, isList)
                        paramsAnnotationClasses[annotationInnerName] = typeName
                    }

                    // table constructor properties
                    val constructorPropSpec = FunSpec.constructorBuilder()
                    // does table have numeric parameter in annotation
                    val isNumericModel: Boolean =
                        arguments.getArgumentValue(Params.IS_NUMERIC) ?: false

                    var currentIndex = -1
                    propClassFields.forEachIndexed { _, name ->
                        val modelData = name.asModelFieldData()
                        if(!modelData.isPrimaryKey) {
                            currentIndex++
                        }
                        addTableModelProperty(
                            modelData,
                            constructorPropSpec,
                            propClassSpec,
                            null,
                            isNumericModel,
                            currentIndex
                        )
                    }

                    propClassSpec.primaryConstructor(constructorPropSpec.build()).apply {
                        if (isNumericModel) {
                            val fullCount = currentIndex + 1
                            addAnnotation(createCountFieldsAnnotation(fullCount))
                            if (isTableAnnotation) {
                                val numeratedFieldsClassName =
                                    ClassName(ASSISTANT_MODEL_PATH, CLASS_NUMERATED_FIELDS)
                                addSuperinterface(numeratedFieldsClassName)
                            } else {
                                val customClassName =
                                    ClassName(ASSISTANT_MODEL_PATH, CLASS_CUSTOM_PARAMETER)
                                addSuperinterface(customClassName)
                                val returnMapStatement = buildString {
                                    append("$RETURN_STATEMENT \"$annotationInnerName\"")
                                }
                                val stringTypeName = String::class.asTypeName()
                                val getParamsMapFunSpec = FunSpec.builder(FUNC_GET_PARAMETER_NAME)
                                    .addModifiers(KModifier.PUBLIC, KModifier.OVERRIDE)
                                    .addStatement(returnMapStatement)
                                    .returns(stringTypeName)
                                    .build()

                                addFunction(getParamsMapFunSpec)
                            }
                        }
                    }
                    elementsFiles.add(propClassSpec)

                    if (isTableAnnotation) {
                        // result model property
                        createResultModelProperty(
                            propModelData.name,
                            annotationInnerName,
                            propClassName,
                            resultConstructorSpec,
                            resultModelTypeSpec,
                            isList
                        )
                    }
                }

                //------ Request Params Class
                if (parameters.isNotEmpty()) {
                    val paramsClass =
                        createRequestParams(mainClassName, parameters, paramsAnnotationClasses)
                    elementsFiles.add(1, paramsClass)
                }

                // add result model class
                resultModelTypeSpec.primaryConstructor(resultConstructorSpec.build())
                elementsFiles.add(resultModelTypeSpec)

                // Raw Status Class
                val respondStatusClassName = ClassName(
                    REQUEST_PACKAGE_NAME,
                    mainClassName.createFileName(RESPOND_STATUS_POSTFIX)
                )
                val rawStatusTypeSpec =
                    createRawStatusClass(respondStatusClassName, resultModelClassName)
                elementsFiles.add(rawStatusTypeSpec)

                val parametrizedInterface =
                    requestInterface.parameterizedBy(resultModelClassName, respondStatusClassName)
                mainClassTypeSpec.addSuperinterface(parametrizedInterface)
            }

            // save all classes in one file
            saveFiles(REQUEST_PACKAGE_NAME, typeClassName, elementsFiles)
        }
    }

    private fun createMainRequestClass(
        resourceName: String,
        parameters: List<String>,
        constructorSpec: FunSpec.Builder,
        classTypeSpec: TypeSpec.Builder
    ) {
        val classType = ClassName(API_MODEL_PATH, CLASS_HYPER_HIVE)
        val hyperHivePropName = FIELD_HYPER_HIVE
        val propHyperSpec =
            PropertySpec.builder(hyperHivePropName, classType, KModifier.PUBLIC, KModifier.OVERRIDE)
                .initializer(hyperHivePropName)
                .build()
        val paramHyperSpec = ParameterSpec.builder(hyperHivePropName, classType)
            .build()

        val stringTypeName = String::class.asTypeName()
        val mapTypeName = Map::class.asTypeName().parameterizedBy(stringTypeName, stringTypeName)
        val nullableMapType = mapTypeName.copy(nullable = true)
        val propHeadersSpec = PropertySpec.builder(
            FIELD_DEFAULT_HEADERS,
            nullableMapType,
            KModifier.PUBLIC,
            KModifier.OVERRIDE
        )
            .mutable()
            .initializer(FIELD_DEFAULT_HEADERS)
            .build()

        val paramHeadersSpec = ParameterSpec.builder(FIELD_DEFAULT_HEADERS, nullableMapType)
            .defaultValue(NULL_INITIALIZER)
            .build()

        val propResourceSpec = PropertySpec.builder(
            FIELD_RESOURCE_NAME,
            stringTypeName,
            KModifier.PUBLIC,
            KModifier.OVERRIDE
        )
            .initializer(FIELD_RESOURCE_NAME)
            .build()

        val paramResourceSpec = ParameterSpec.builder(FIELD_RESOURCE_NAME, stringTypeName)
            .defaultValue("\"${resourceName}\"")
            .build()

        val fullParamsSize = parameters.size
        val mapMemberName = MemberName(KOTLIN_COLLECTION_PATH, KOTLIN_MAP_OF_NAME)
        var commentOfParams = PARAMS_COMMENT_FIRST
        val returnMapStatement = buildString {
            if (parameters.isEmpty()) {
                commentOfParams += PARAMS_COMMENT_SECOND
                append("$RETURN_STATEMENT $TAG_MEMBER_FULL")
            } else {
                commentOfParams += PARAMS_COMMENT_THIRD.format("$fullParamsSize")

                val paramSize = fullParamsSize - 1
                parameters.forEachIndexed { index, param ->
                    commentOfParams += "'$param'"
                    if (index < paramSize) {
                        commentOfParams += ", "
                    }
                }

                append(createValueParamsStatement(parameters))
                val returnedMap = createMapStatement(parameters)
                append("$RETURN_STATEMENT $returnedMap")
            }
        }

        val createParamsMapFunSpec = FunSpec.builder(FUNC_CREATE_PARAMS_MAP_NAME)
            .addModifiers(KModifier.PUBLIC, KModifier.OVERRIDE)
            .addParameter(FIELD_PARAMS, stringTypeName, KModifier.VARARG)
            .addStatement(returnMapStatement, mapMemberName)
            .returns(mapTypeName)
            .addKdoc(commentOfParams)
            .build()

        constructorSpec.addParameter(paramHyperSpec)
        constructorSpec.addParameter(paramHeadersSpec)
        constructorSpec.addParameter(paramResourceSpec)

        classTypeSpec.addProperty(propHyperSpec)
        classTypeSpec.addProperty(propHeadersSpec)
        classTypeSpec.addProperty(propResourceSpec)

        classTypeSpec.primaryConstructor(constructorSpec.build())
        classTypeSpec.addFunction(createParamsMapFunSpec)
    }

    private fun createRawStatusClass(
        respondStatusClassName: ClassName,
        rawModelClassName: ClassName
    ): TypeSpec.Builder {
        val respondStatusTypeSpec = TypeSpec.classBuilder(respondStatusClassName)
        val baseRespondStatusClassName = ClassName(OBJ_RAW_STATUS_PATH, CLASS_OBJ_RAW_STATUS)
            .parameterizedBy(rawModelClassName)
        return respondStatusTypeSpec.superclass(baseRespondStatusClassName)
    }

    private fun createValueParamsStatement(parameters: List<String>): String {
        return buildString {
            parameters.forEachIndexed { index, _ ->
                val fieldGet = FIELD_PARAMS_GET.format("$index")
                append("val param${index + 1}: String = $fieldGet")
                appendLine()
            }
        }
    }

    private fun createListTypeName(
        originalClassName: ClassName,
        fieldReturnList: Boolean
    ): TypeName {
        return if (fieldReturnList) {
            val list = ClassName(KOTLIN_COLLECTION_PATH, KOTLIN_LIST_NAME)
            list.parameterizedBy(originalClassName).copy(nullable = true)
        } else {
            originalClassName.copy(nullable = true)
        }
    }

    private fun createMapStatement(
        parameters: List<String>,
        needLowerCase: Boolean = false
    ): String {
        return buildString {
            append(TAG_MEMBER_HALF)
            val paramSize = parameters.size - 1
            parameters.forEachIndexed { index, param ->
                val paramName = if (needLowerCase) {
                    param.asModelFieldData().name
                } else {
                    param
                }
                append("\"$paramName\" to param${index + 1}")
                if (index < paramSize) {
                    append(", ")
                }
            }
            append(")")
        }
    }

    private fun createResultModelProperty(
        name: String,
        annotationSerializedName: String,
        parameterClassName: ClassName,
        constructorSpec: FunSpec.Builder,
        classTypeSpec: TypeSpec.Builder,
        fieldReturnList: Boolean = false
    ) {
        //logger.warn("-----> annotation serialized name = $annotationSerializedName")

        val annotationSerialize = annotationSerializedName.createSerializedAnnotation()
        val returnClassName = createListTypeName(parameterClassName, fieldReturnList)
        val propBuilder = PropertySpec.builder(name, returnClassName, KModifier.PUBLIC)
            .initializer(name)
            .addAnnotation(annotationSerialize)
            .build()

        val paramBuilder =
            ParameterSpec.builder(name, returnClassName)
                .defaultValue(NULL_INITIALIZER)
                .build()

        constructorSpec.addParameter(paramBuilder)
        classTypeSpec.addProperty(propBuilder)
    }

    private fun createRequestParams(
        mainClassName: String,
        properties: List<String>,
        annotationParams: Map<String, TypeName> = emptyMap()
    ): TypeSpec.Builder {
        val paramsClassName = mainClassName.createFileName(PARAMS_POSTFIX)
        val paramsClassSpec = TypeSpec.classBuilder(paramsClassName)
            .addModifiers(KModifier.DATA)

        val constructorPropSpec = FunSpec.constructorBuilder()
        properties.forEach { name ->
            val modelTypeName: TypeName? = annotationParams[name]
            val modelData = name.asModelFieldData()
            addTableModelProperty(modelData, constructorPropSpec, paramsClassSpec, modelTypeName)
        }
        paramsClassSpec.primaryConstructor(constructorPropSpec.build())
        return paramsClassSpec
    }

    private fun addTableModelProperty(
        modelData: FieldData,
        constructorSpec: FunSpec.Builder,
        classTypeSpec: TypeSpec.Builder,
        modelTypeName: TypeName? = null,
        isNumericModel: Boolean = false,
        numIndex: Int = -1
    ) {

        val propName = modelData.name
        val annotationName = modelData.annotate

        //logger.warn("-----> propName = $propName")
        //logger.warn("-----> annotationName = ${annotationName}")

        val annotationSerialize = annotationName.createSerializedAnnotation()
        val type = modelTypeName ?: modelData.type.asTypeName().copy(nullable = true)
        val propSpec =
            PropertySpec.builder(propName, type, KModifier.PUBLIC)
                .initializer(propName)
                .mutable(true)
                .addAnnotation(annotationSerialize).apply {
                    if (isNumericModel) {
                        addAnnotation(createJavaFieldAnnotation())
                        if(!modelData.isPrimaryKey && numIndex >= 0) {
                            addAnnotation(createParameterFieldAnnotation(numIndex))
                        }
                    }
                    if(modelData.isPrimaryKey) {
                        addAnnotation(createPrimaryKeyAnnotation())
                    }
                }
                .build()

        val paramSpec =
            ParameterSpec.builder(propName, type)
                .defaultValue(NULL_INITIALIZER)
                .build()
        constructorSpec.addParameter(paramSpec)
        classTypeSpec.addProperty(propSpec)
    }
}