package pro.krit.hiveksp

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.validate
import com.squareup.kotlinpoet.ksp.KotlinPoetKspPreview
import pro.krit.hiveprocessor.annotations.*

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

    private lateinit var intType: KSType

    override fun process(resolver: Resolver): List<KSAnnotated> {
        intType = resolver.builtIns.intType
        val startTime = System.currentTimeMillis()
        logger.warn("----> FmpSymbolProcessor start")

        //----- collect BindSingle annotations
        val daoSymbols = resolver
            // Getting all symbols that are annotated with @FmpDatabase.
            .getSymbolsWithAnnotation(DAO_ANNOTATION_NAME)

        // Exit from the processor in case nothing is annotated
        if (!daoSymbols.iterator().hasNext()) {
            return emptyList()
        }

        // validate already generated files
        val unableToProcessWithDatabase = daoSymbols.filterNot { it.validate() }.toList()

        daoSymbols.filter { it.validate() }.forEach {
            it.accept(FmpAnnotationVisitor(logger, codeGenerator), Unit)
        }

        logger.warn("----> FmpSymbolProcessor finished in `${System.currentTimeMillis() - startTime}` ms")
        return unableToProcessWithDatabase
    }

}