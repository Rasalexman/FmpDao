package pro.krit.hiveprocessor.annotations

import pro.krit.hiveprocessor.provider.HyperHiveDatabase

/**
 * Main annotation for generating database implementation and used only with abstract class
 * [HyperHiveDatabase] and his abstract extended children. It's generate an Kotlin Object class
 * with current implementation of abstract functions for provide instance of database DAO
 *
 * Example:
 *
 * <p><pre>
 *  &#64FmpDatabase
 *  abstract class MainDatabase : HyperHiveDatabase() {
 *      abstract fun providePmDao(): IPmDataDao
 *      abstract fun providePmLocalDao(): IPmDataLocalDao
 *  }</pre></p>
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class FmpDatabase()
