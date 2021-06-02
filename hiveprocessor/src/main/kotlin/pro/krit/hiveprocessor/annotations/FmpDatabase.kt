package pro.krit.hiveprocessor.annotations

/**
 * Main annotation for generating database implementation and used only with abstract class
 * [pro.krit.hiveprocessor.provider.HyperHiveDatabase] and his abstract extended children.
 * It's generate an Kotlin Object class with current implementation of abstract functions f
 * or provide instance of database DAO
 *
 * Example:
 *
 * <p><pre>
 *  &#64FmpDatabase
 *  abstract class MainDatabase : HyperHiveDatabase() {
 *      abstract fun providePmDao(): IPmDataDao
 *      abstract fun providePmLocalDao(): IPmDataLocalDao
 *  }
 *  </pre></p>
 *
 *  or with interface abstraction need to extend [pro.krit.hiveprocessor.provider.IHyperHiveDatabase]
 *  <p><pre>
 *  @FmpDatabase
 *  abstract class MainDatabase : HyperHiveDatabase(), IMainDatabase
 *
 *  interface IMainDatabase : IHyperHiveDatabase {
 *      fun providePmDao(): IPmDataDao
 *      fun providePmLocalDao(): IPmDataLocalDao
 *  }
 *  </pre></p>
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class FmpDatabase()
