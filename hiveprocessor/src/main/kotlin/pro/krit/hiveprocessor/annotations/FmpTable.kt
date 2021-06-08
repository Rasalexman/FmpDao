package pro.krit.hiveprocessor.annotations

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class FmpTable(
    val name: String,
    val fields: Array<String>,
    val isList: Boolean = true
)
