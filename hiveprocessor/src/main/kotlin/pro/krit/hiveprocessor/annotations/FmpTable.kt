package pro.krit.hiveprocessor.annotations

annotation class FmpTable(
    val name: String,
    val fields: Array<String>,
    val isList: Boolean = false
)
