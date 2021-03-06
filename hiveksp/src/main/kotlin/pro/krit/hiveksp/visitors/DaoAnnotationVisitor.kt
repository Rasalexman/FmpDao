package pro.krit.hiveksp.visitors

import com.google.devtools.ksp.closestClassDeclaration
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSTypeReference
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.ksp.KotlinPoetKspPreview
import com.squareup.kotlinpoet.ksp.toTypeName
import pro.krit.hhivecore.base.IDao
import pro.krit.hhivecore.common.DaoFieldsData
import pro.krit.hhivecore.extensions.*
import pro.krit.hhivecore.provider.IFmpDatabase
import pro.krit.hiveksp.base.BaseSymbolVisitor
import pro.krit.hiveksp.common.Consts.FIELD_PROVIDER
import pro.krit.hiveksp.common.Consts.FIELD_RESOURCE_NAME
import pro.krit.hiveksp.common.Consts.TAG_MEMBER_HALF
import pro.krit.hiveksp.common.Params.IS_DELTA
import pro.krit.hiveksp.common.Params.TABLE_NAME
import pro.krit.hiveksp.data.KspData

@KotlinPoetKspPreview
class DaoAnnotationVisitor(
    logger: KSPLogger,
    codeGenerator: CodeGenerator
) : BaseSymbolVisitor(logger, codeGenerator) {


    companion object {
        private const val DAO_PACKAGE_NAME = "pro.krit.generated.dao"
        private const val EXTENSIONS_PATH = "pro.krit.hhivecore.extensions"

        private const val INIT_CREATE_TABLE = "createTable"

        private const val REQUEST_STATEMENT = "request"
        private const val REQUEST_NAME = "requestWithParams"

        private const val FUNC_MEMBER_STATEMENT = "this.%M()"
        private const val FUNC_MEMBER_PARAMS_STATEMENT = "this.%M"
        private const val FUNC_MEMBER_STATEMENT_GENERIC = "this.%M<"
        private const val FUNC_MEMBER_STATEMENT_GENERIC_CLOSE = ">()"

        private const val FIELD_DAO_FIELDS = "fieldsData"
        private const val FIELD_PARAMS_REPLACE = "(params = %s)"

        private const val MOBRUN_MODEL_PATH = "com.mobrun.plugin.models"
        private const val MOBRUN_SELECTABLE_NAME = "StatusSelectTable"
        private const val MOBRUN_BASE_NAME = "BaseStatus"

        private const val NULL_INITIALIZER = "null"
        private const val RETURN_STATEMENT = "return"
    }

    override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
        //logger.logging("------> class name = ${classDeclaration.simpleName.asString()}")
        val kspData = getKspDataFromAnnotation(classDeclaration)
        processSaveKspData(kspData)
    }

    override fun processSaveKspData(kspData: KspData) {
        val classFileName = kspData.fileName
        val className = kspData.mainData.className
        val packageName = kspData.mainData.packName
        val mainClassName = ClassName(packageName, className)
        val classBuilders = mutableListOf<TypeSpec.Builder>()


        // generics type array for class type
        val genericsArray = mutableListOf<String>()

        val classTypeSpec =
            TypeSpec.classBuilder(classFileName)
                .addSuperinterface(mainClassName)
                .addProperties(createProperties())
        classBuilders.add(classTypeSpec)

        if (kspData.parameters.isNotEmpty()) {
            val localParams = kspData.parameters
            val requestFunc = createRequestFunction(localParams)
            classTypeSpec.addFunction(requestFunc.build())
        }

        if (kspData.fields.isNotEmpty()) {
            val fileModelName = className.createFileName(MODEL_POSTFIX)
            val fileStatusName = className.createFileName(STATUS_POSTFIX)

            val modelClass = ClassName(DAO_PACKAGE_NAME, fileModelName)
            val statusClass = ClassName(DAO_PACKAGE_NAME, fileStatusName)
            val statusParentClaas = ClassName(MOBRUN_MODEL_PATH, MOBRUN_SELECTABLE_NAME)
            val modelTypeSpec = TypeSpec.classBuilder(fileModelName)
            val statusTypeSpec = TypeSpec.classBuilder(fileStatusName)
                .superclass(statusParentClaas.parameterizedBy(modelClass))

            genericsArray.clear()
            genericsArray.add(modelClass.toString())
            genericsArray.add(statusClass.toString())

            val baseClassType = if (kspData.isLocal) {
                IDao.IFmpLocalDao::class.asClassName()
            } else {
                IDao.IFmpDao::class.asClassName()
            }

            val constructorSpec = FunSpec.constructorBuilder()
            val jvmFieldClassName = ClassName(KOTLIN_JVM_PATH, CLASS_JVM_FIELD)
            val primaryKeyClassName = ClassName(ASSISTANT_MODEL_PATH, CLASS_PRIMARY_FIELD)
            val annotationJvmField = AnnotationSpec.builder(jvmFieldClassName).build()
            val annotationPrimaryKey = AnnotationSpec.builder(primaryKeyClassName).build()

            kspData.fields.forEach { field ->
                val data = field.asModelFieldData()
                val annotationSerialize = data.annotate.createSerializedAnnotation()

                val currentType = data.type.asClassName().copy(nullable = true)
                val prop =
                    PropertySpec.builder(data.name, currentType)
                        .mutable(true)
                        .initializer(data.name)
                        .addAnnotation(annotationJvmField)
                        .apply {
                            if (data.isPrimaryKey) {
                                addAnnotation(annotationPrimaryKey)
                            }
                        }
                        .addAnnotation(annotationSerialize)
                        .build()


                constructorSpec.addParameter(
                    ParameterSpec.builder(data.name, currentType)
                        .defaultValue(NULL_INITIALIZER)
                        .build()
                )
                modelTypeSpec.addProperty(prop)
            }
            modelTypeSpec.primaryConstructor(constructorSpec.build())
            modelTypeSpec.addModifiers(KModifier.DATA)
            classBuilders.add(modelTypeSpec)
            classBuilders.add(statusTypeSpec)

            classTypeSpec.addSuperinterface(
                baseClassType.parameterizedBy(modelClass, statusClass)
            )
        } else {
            val element = kspData.element
            val superReference: KSTypeReference? =
                element.closestClassDeclaration()?.superTypes?.toList()?.firstOrNull()
            val elementParent = superReference?.element
            val superInterfaces = elementParent?.run {
                typeArguments.map { typeArg ->
                    typeArg.toTypeName().toString()
                }
            }.orEmpty()

            //logger.logging("------> superInterfaces $className = $superInterfaces")
            genericsArray.clear()
            genericsArray.addAll(superInterfaces)
        }

        //println("------> genericsArray = $genericsArray")
        // ???????????????? ?????????????????????? ?? ??????????????????????
        classTypeSpec.primaryConstructor(constructorFunSpec(kspData, genericsArray))

        saveFiles(DAO_PACKAGE_NAME, classFileName, builders = classBuilders)
    }

    // ?????????????? ?????????????????????????????? ???????? ?????? ???????????????????????? ?? ?????? ???? ?????????????? init {  }
    private fun constructorFunSpec(bindData: KspData, superTypeGenerics: List<String>): FunSpec {
        val resourceName = ParameterSpec.builder(FIELD_RESOURCE_NAME, String::class).apply {
            if (bindData.resourceName.isNotEmpty()) {
                defaultValue("\"${bindData.resourceName}\"")
            }
        }.build()

        val parameterName = ParameterSpec.builder(TABLE_NAME, String::class)
            .defaultValue("\"${bindData.tableName}\"")
            .build()

        val isCached = ParameterSpec.builder(IS_DELTA, Boolean::class)
            .defaultValue("${bindData.isDelta}")
            .build()

        val hyperHiveProvider =
            ParameterSpec.builder(FIELD_PROVIDER, IFmpDatabase::class)
                .build()

        return FunSpec.constructorBuilder()
            .addParameter(hyperHiveProvider)
            .addParameter(resourceName)
            .addParameter(parameterName)
            .addParameter(isCached)
            .apply {
                val isLocalProp = ParameterSpec.builder(
                    FIELD_DAO_FIELDS,
                    DaoFieldsData::class.asClassName().copy(nullable = true)
                )
                    .defaultValue(NULL_INITIALIZER)
                    .build()
                addParameter(isLocalProp)

                //logger.logging("--------> superTypeGenerics = $superTypeGenerics")

                if (bindData.createTableOnInit) {
                    val members = mutableListOf<Any>()
                    members.add(
                        MemberName(
                            EXTENSIONS_PATH,
                            INIT_CREATE_TABLE
                        )
                    )
                    val initStatement = buildString {
                        if (superTypeGenerics.isNotEmpty()) {
                            append(FUNC_MEMBER_STATEMENT_GENERIC)
                            val className = superTypeGenerics.firstOrNull().orEmpty()
                            append(className)
                            append(FUNC_MEMBER_STATEMENT_GENERIC_CLOSE)
                        } else {
                            append(FUNC_MEMBER_STATEMENT)
                        }
                    }

                    addStatement(
                        initStatement,
                        MemberName(EXTENSIONS_PATH, INIT_CREATE_TABLE)
                    )
                }
            }
            .build()
    }

    private fun createProperties(): List<PropertySpec> {
        val hyperHiveProviderProp =
            PropertySpec.builder(FIELD_PROVIDER, IFmpDatabase::class)
                .initializer(FIELD_PROVIDER)
                .addModifiers(KModifier.PUBLIC, KModifier.OVERRIDE)
                .build()

        val resourceNameProp = PropertySpec.builder(FIELD_RESOURCE_NAME, String::class)
            .initializer(FIELD_RESOURCE_NAME)
            .addModifiers(KModifier.PUBLIC, KModifier.OVERRIDE)
            .build()

        val parameterNameProp = PropertySpec.builder(TABLE_NAME, String::class)
            .initializer(TABLE_NAME)
            .addModifiers(KModifier.PUBLIC, KModifier.OVERRIDE)
            .build()

        val isCachedProp = PropertySpec.builder(IS_DELTA, Boolean::class)
            .initializer(IS_DELTA)
            .addModifiers(KModifier.PUBLIC, KModifier.OVERRIDE)
            .build()

        val isLocalProp = PropertySpec.builder(
            FIELD_DAO_FIELDS,
            DaoFieldsData::class.asTypeName().copy(nullable = true)
        ).mutable()
            .initializer(FIELD_DAO_FIELDS)
            .addModifiers(KModifier.PUBLIC, KModifier.OVERRIDE)
            .build()
        return listOf(
            hyperHiveProviderProp,
            resourceNameProp,
            parameterNameProp,
            isCachedProp,
            isLocalProp
        )
    }

    private fun createRequestFunction(parameters: List<String>): FunSpec.Builder {
        val funcSpec = FunSpec.builder(REQUEST_NAME)
        var mapOfParams = TAG_MEMBER_HALF
        val paramsSize = parameters.size - 1
        val propertyClass = Any::class.asClassName()
        parameters.forEachIndexed { index, paramName ->
            val param = paramName.lowercase()
            val parameterSpec = ParameterSpec.builder(param, propertyClass).build()
            funcSpec.addParameter(parameterSpec)
            mapOfParams += "\"$paramName\" to $param"
            mapOfParams += if (index < paramsSize) ", " else ""
        }
        mapOfParams += ")"

        val replacedParams = FIELD_PARAMS_REPLACE.format(mapOfParams)
        val statement = "$RETURN_STATEMENT ${FUNC_MEMBER_PARAMS_STATEMENT}$replacedParams"
        val returnType = ClassName(MOBRUN_MODEL_PATH, MOBRUN_BASE_NAME)
        val updateFuncName = REQUEST_STATEMENT
        return funcSpec.addStatement(
            statement,
            MemberName(EXTENSIONS_PATH, updateFuncName),
            MemberName(KOTLIN_COLLECTION_PATH, KOTLIN_MAP_OF_NAME)
        ).returns(returnType)
    }

}