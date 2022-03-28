package pro.krit.hiveksp

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.validate
import com.squareup.kotlinpoet.DelicateKotlinPoetApi
import com.squareup.kotlinpoet.ksp.KotlinPoetKspPreview
import pro.krit.hhivecore.annotations.*
import pro.krit.hiveksp.visitors.DatabaseAnnotationVisitor
import pro.krit.hiveksp.visitors.DaoAnnotationVisitor
import pro.krit.hiveksp.visitors.RequestAnnotationVisitor

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
    private val daoAnnotationVisitor = DaoAnnotationVisitor(logger, codeGenerator)
    private val requestAnnotationVisitor = RequestAnnotationVisitor(logger, codeGenerator)

    private lateinit var intType: KSType

    override fun process(resolver: Resolver): List<KSAnnotated> {
        intType = resolver.builtIns.intType
        val startTime = System.currentTimeMillis()
        logger.warn("----> FmpSymbolProcessor start")

        //----- collect BindSingle annotations
        val unableRequests = processRequests(resolver)
        val unableDaos = processDaos(resolver)

        //
        val unableElements = (unableRequests + unableDaos)

        // Getting all symbols that are annotated with @FmpDatabase.
        val databaseSymbols = resolver.getSymbolsWithAnnotation(DATABASE_ANNOTATION_NAME).toList()
        // Exit from the processor in case nothing is annotated as FMPDatabase
        if (databaseSymbols.isEmpty()) {
            showFinishTime("database", startTime)
            return unableElements
        }

        val unableDatabaseToProcess = databaseSymbols.filterNot { it.validate() }
        databaseSymbols.filter { it.validate() }.forEach {
            it.accept(DatabaseAnnotationVisitor(logger, codeGenerator), Unit)
        }
        val allUnableToProcess = unableElements + unableDatabaseToProcess
        return allUnableToProcess.also {
            showFinishTime(startTime = startTime)
        }
    }

    private fun processDaos(resolver: Resolver): List<KSAnnotated> {
        //---- FM DAO
        val daoSymbols = resolver.getSymbolsWithAnnotation(DAO_ANNOTATION_NAME).toList()
        val unableDaoToProcess = daoSymbols.filterNot { it.validate() }
        daoSymbols.filter { it.validate() }.forEach {
            it.accept(daoAnnotationVisitor, Unit)
        }

        //----- FMP LOCAL DAO
        val daoLocalSymbols = resolver.getSymbolsWithAnnotation(DAO_LOCAL_ANNOTATION_NAME).toList()
        val unableLocalDaoToProcess = daoLocalSymbols.filterNot { it.validate() }
        daoLocalSymbols.filter { it.validate() }.forEach {
            it.accept(daoAnnotationVisitor, Unit)
        }

        return unableDaoToProcess + unableLocalDaoToProcess
    }

    private fun processRequests(resolver: Resolver): List<KSAnnotated> {
        val restRequestSymbols = resolver.getSymbolsWithAnnotation(REST_ANNOTATION_NAME).toList()
        val unableRestToProcess = restRequestSymbols.filterNot { it.validate() }
        restRequestSymbols.filter { it.validate() }.forEach {
            it.accept(requestAnnotationVisitor, Unit)
        }

        val webRequestSymbols = resolver.getSymbolsWithAnnotation(WEB_ANNOTATION_NAME).toList()
        val unableWebToProcess = webRequestSymbols.filterNot { it.validate() }
        webRequestSymbols.filter { it.validate() }.forEach {
            it.accept(requestAnnotationVisitor, Unit)
        }

        return unableRestToProcess + unableWebToProcess
    }

    private fun showFinishTime(key: String = "", startTime: Long) {
        logger.warn("----> FmpSymbolProcessor $key finished in `${System.currentTimeMillis() - startTime}` ms")
    }
}