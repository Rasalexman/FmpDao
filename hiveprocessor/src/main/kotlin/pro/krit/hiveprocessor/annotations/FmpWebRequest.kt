package pro.krit.hiveprocessor.annotations

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class FmpWebRequest(
    val resourceName: String,
    val parameters: Array<String> = []
)
