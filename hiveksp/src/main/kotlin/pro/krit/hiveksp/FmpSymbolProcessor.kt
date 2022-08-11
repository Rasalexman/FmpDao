package pro.krit.hiveksp

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.validate
import pro.krit.hhivecore.annotations.*
import pro.krit.hiveksp.visitors.DaoAnnotationVisitor
import pro.krit.hiveksp.visitors.DatabaseAnnotationVisitor
import pro.krit.hiveksp.visitors.RequestAnnotationVisitor

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

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val startTime = System.currentTimeMillis()
        logger.logging("----> FmpSymbolProcessor start")

        val restSymbols = resolver.getSymbolsWithAnnotation(REST_ANNOTATION_NAME)
        val webSymbols = resolver.getSymbolsWithAnnotation(WEB_ANNOTATION_NAME)
        val daoSymbols = resolver.getSymbolsWithAnnotation(DAO_ANNOTATION_NAME)
        val daoLocalSymbols = resolver.getSymbolsWithAnnotation(DAO_LOCAL_ANNOTATION_NAME)
        val databaseSymbols = resolver.getSymbolsWithAnnotation(DATABASE_ANNOTATION_NAME)

        // Exit from the processor in case nothing is annotated
        if (!restSymbols.iterator().hasNext() &&
            !webSymbols.iterator().hasNext() &&
            !daoSymbols.iterator().hasNext() &&
            !daoLocalSymbols.iterator().hasNext() &&
            !databaseSymbols.iterator().hasNext()
        ) {
            return emptyList()
        }

        //----- collect BindSingle annotations
        val unableRequests = processRequests(restSymbols, webSymbols)
        val unableDaos = processDaos(daoSymbols, daoLocalSymbols)

        //
        val unableElements = (unableRequests + unableDaos)

        // Getting all symbols that are annotated with @FmpDatabase.
        val databaseSymbolsList = databaseSymbols.toList()
        val unableDatabaseToProcess = databaseSymbolsList.filterNot { it.validate() }.also {
            logger.logging("----> Database unabled: ${it.size}")
        }

        databaseSymbolsList.filter { it.validate() }.forEach {
            it.accept(DatabaseAnnotationVisitor(logger, codeGenerator), Unit)
        }
        val allUnableToProcess = unableElements + unableDatabaseToProcess
        return allUnableToProcess.also {
            showFinishTime(startTime = startTime)
        }
    }

    private fun processDaos(
        daoSymbols: Sequence<KSAnnotated>,
        daoLocalSymbols: Sequence<KSAnnotated>
    ): List<KSAnnotated> {
        //---- FM DAO
        val daoSymbolsList = daoSymbols.toList()
        val unableDaoToProcess = daoSymbolsList.filterNot { it.validate() }.also {
            logger.logging("----> unableDaoToProcess: ${it.size}")
        }
        daoSymbolsList.filter { it.validate() }.forEach {
            it.accept(DaoAnnotationVisitor(logger, codeGenerator), Unit)
        }

        //----- FMP LOCAL DAO
        val daoLocalSymbolsList = daoLocalSymbols.toList()
        val unableLocalDaoToProcess = daoLocalSymbolsList.filterNot { it.validate() }
            .also {
                logger.logging("----> unableLocalDaoToProcess: ${it.size}")
            }
        daoLocalSymbolsList.filter { it.validate() }.forEach {
            it.accept(DaoAnnotationVisitor(logger, codeGenerator), Unit)
        }

        return (unableDaoToProcess + unableLocalDaoToProcess)
    }

    private fun processRequests(
        restSymbols: Sequence<KSAnnotated>,
        webSymbols: Sequence<KSAnnotated>
    ): List<KSAnnotated> {
        //
        val validatedRest = restSymbols.filter { it.validate() }.onEach {
            it.accept(RequestAnnotationVisitor(logger, codeGenerator), Unit)
        }.toSet()
        val unableRestToProcess = restSymbols - validatedRest.toSet()

        ///
        val webValidated = webSymbols.filter { it.validate() }.onEach {
            it.accept(RequestAnnotationVisitor(logger, codeGenerator), Unit)
        }.toSet()

        val unableWebToProcess = webSymbols - webValidated

        return (unableRestToProcess + unableWebToProcess).toList().also {
            logger.logging("----> Request unabled: ${it.size}")
        }
    }

    private fun showFinishTime(key: String = "", startTime: Long) {
        logger.logging("----> FmpSymbolProcessor $key finished in `${System.currentTimeMillis() - startTime}` ms")
    }
}