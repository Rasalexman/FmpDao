package pro.krit.hiveprocessor.data

import javax.lang.model.element.Element

data class BindData(
    val element: Element,
    val fileName: String,
    val mainData: TypeData,
    val resourceName: String = "",
    val parameterName: String = "",
    val isCached: Boolean = false,
    val isLocal: Boolean = false
)
