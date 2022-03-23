package pro.krit.hiveksp

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.validate
import com.squareup.kotlinpoet.DelicateKotlinPoetApi
import com.squareup.kotlinpoet.ksp.KotlinPoetKspPreview
import pro.krit.hiveksp.data.KspData
import pro.krit.hiveksp.generators.DaosCodeGenerator
import pro.krit.processor.annotations.*
import pro.krit.hiveksp.generators.DatabaseCodeGenerator
import pro.krit.hiveksp.generators.RequestsCodeGenerator

@DelicateKotlinPoetApi("Delicate api. Make sure that you know what you did")
@KotlinPoetKspPreview
class FmpSymbolProcessor(
    environment: SymbolProcessorEnvironment
) : SymbolProcessor {

    companion object {
        private val DATABASE_ANNOTATION_NAME = FmpDatabase::class.qualifiedName!!
        private val DAO_ANNOTATION_NAME = FmpDao::class.qualifiedName!!
        private val DAO_LOCAL_ANNOTATION_NAME = FmpLocalDao::class.qualifiedName!!
        private val REST_ANNOTATION_NAME = FmpRestRequest::class.qualifiedName!!
        private val WEB_ANNOTATION_NAME = FmpWebRequest::class.qualifiedName!!
    }

    private val logger = environment.logger
    private val codeGenerator = environment.codeGenerator
    private val daosCodeGenerator = DaosCodeGenerator(logger, codeGenerator)
    private val requestsCodeGenerator = RequestsCodeGenerator(logger, codeGenerator)
    private val kspAnnotationVisitor = FmpAnnotationVisitor(logger)

    private lateinit var intType: KSType

    override fun process(resolver: Resolver): List<KSAnnotated> {
        intType = resolver.builtIns.intType
        val startTime = System.currentTimeMillis()
        logger.warn("----> FmpSymbolProcessor start")

        //----- collect BindSingle annotations
        // Getting all symbols that are annotated with @FmpDatabase.
        val daoSymbols = resolver.getSymbolsWithAnnotation(DAO_ANNOTATION_NAME).toList()
        val daoLocalSymbols = resolver.getSymbolsWithAnnotation(DAO_LOCAL_ANNOTATION_NAME).toList()
        val databaseLocalSymbols = resolver.getSymbolsWithAnnotation(DATABASE_ANNOTATION_NAME).toList()
        val restRequestSymbols = resolver.getSymbolsWithAnnotation(REST_ANNOTATION_NAME).toList()
        val webRequestSymbols = resolver.getSymbolsWithAnnotation(WEB_ANNOTATION_NAME).toList()

        // Exit from the processor in case nothing is annotated
        if (!databaseLocalSymbols.iterator().hasNext()) {
            return emptyList()
        }

        // validate already generated files
        val unableDaoToProcess = daoSymbols.filterNot { it.validate() }
        val unableLocalDaoToProcess = daoLocalSymbols.filterNot { it.validate() }
        val unableDatabaseToProcess = databaseLocalSymbols.filterNot { it.validate() }
        val unableRestToProcess = restRequestSymbols.filterNot { it.validate() }
        val unableWebToProcess = webRequestSymbols.filterNot { it.validate() }

        val kspDaoDataList = mutableListOf<KspData>()
        val kspRequestsDataList = mutableListOf<KspData>()
        daoSymbols.filter { it.validate() }.mapNotNullTo(kspDaoDataList) {
            it.accept(kspAnnotationVisitor, Unit)
        }
        daoLocalSymbols.filter { it.validate() }.mapNotNullTo(kspDaoDataList) {
            it.accept(kspAnnotationVisitor, Unit)
        }
        daosCodeGenerator.processDaos(kspDaoDataList)

        restRequestSymbols.filter { it.validate() }.mapNotNullTo(kspRequestsDataList) {
            it.accept(kspAnnotationVisitor, Unit)
        }
        webRequestSymbols.filter { it.validate() }.mapNotNullTo(kspRequestsDataList) {
            it.accept(kspAnnotationVisitor, Unit)
        }
        requestsCodeGenerator.processRequest(kspRequestsDataList)

        val allKspDataList = kspDaoDataList + kspRequestsDataList
        databaseLocalSymbols.filter { it.validate() }.forEach {
            it.accept(DatabaseCodeGenerator(logger, codeGenerator), allKspDataList)
        }
        val allUnableToProcess = (unableDaoToProcess + unableLocalDaoToProcess + unableDatabaseToProcess + unableRestToProcess + unableWebToProcess)

        logger.warn("----> FmpSymbolProcessor finished in `${System.currentTimeMillis() - startTime}` ms")
        return allUnableToProcess
    }

}