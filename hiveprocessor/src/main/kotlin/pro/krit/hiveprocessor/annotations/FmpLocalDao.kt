package pro.krit.hiveprocessor.annotations

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class FmpLocalDao(
    val resourceName: String,
    val parameterName: String = ""
)
