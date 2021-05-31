package pro.krit.hiveprocessor.data

data class BindData(
    val fileName: String,
    val mainData: TypeData,
    val resourceName: String = "",
    val parameterName: String = "",
    val isCached: Boolean = false,
    val isLocal: Boolean = false
)
