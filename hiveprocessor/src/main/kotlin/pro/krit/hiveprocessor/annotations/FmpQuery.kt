package pro.krit.hiveprocessor.annotations

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class FmpQuery(
    val query: String
)
