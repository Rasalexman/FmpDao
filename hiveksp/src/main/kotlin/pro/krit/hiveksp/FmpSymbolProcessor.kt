package pro.krit.hiveksp

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.validate
import com.squareup.kotlinpoet.ksp.KotlinPoetKspPreview
import pro.krit.hiveksp.data.KspData
import pro.krit.hiveksp.generators.DaosCodeGenerator
import pro.krit.core.annotations.*
import pro.krit.hiveksp.generators.RequestsCodeGenerator

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
        val daoSymbols = resolver
            // Getting all symbols that are annotated with @FmpDatabase.
            .getSymbolsWithAnnotation(DAO_ANNOTATION_NAME)

        val daoLocalSymbols = resolver.getSymbolsWithAnnotation(DAO_LOCAL_ANNOTATION_NAME)
        val databaseLocalSymbols = resolver.getSymbolsWithAnnotation(DATABASE_ANNOTATION_NAME)
        val restRequestSymbols = resolver.getSymbolsWithAnnotation(REST_ANNOTATION_NAME)

        // Exit from the processor in case nothing is annotated
        /*if (!daoSymbols.iterator().hasNext()) {
            return emptyList()
        }*/

        // validate already generated files
        //val unableDaoToProcess = daoSymbols.filterNot { it.validate() }.toList()
        //val unableLocalDaoToProcess = daoLocalSymbols.filterNot { it.validate() }.toList()
        //val unableDatabaseToProcess = databaseLocalSymbols.filterNot { it.validate() }.toList()

        //val kspDaoDataList = mutableListOf<KspData>()
        val kspRequestsDataList = mutableListOf<KspData>()
        /*daoSymbols.filter { it.validate() }.mapNotNullTo(kspDaoDataList) {
            it.accept(kspAnnotationVisitor, Unit)
        }
        daoLocalSymbols.filter { it.validate() }.mapNotNullTo(kspDaoDataList) {
            it.accept(kspAnnotationVisitor, Unit)
        }
        kspCodeGenerator.processDaos(kspDaoDataList)*/

        restRequestSymbols.filter { it.validate() }.mapNotNullTo(kspRequestsDataList) {
            it.accept(kspAnnotationVisitor, Unit)
        }
        requestsCodeGenerator.processRequest(kspRequestsDataList)

        /*databaseLocalSymbols.filter { it.validate() }.forEach {
            it.accept(DatabaseCodeGenerator(logger, codeGenerator), kspDaoDataList)
        }*/

        logger.warn("----> FmpSymbolProcessor finished in `${System.currentTimeMillis() - startTime}` ms")
        return emptyList()//(unableDaoToProcess + unableLocalDaoToProcess + unableDatabaseToProcess)
    }

}