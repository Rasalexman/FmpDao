package pro.krit.hiveprocessor.annotations

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class FmpDao(
    val resourceName: String,
    val parameterName: String = "",
    val isCached: Boolean = false
)
