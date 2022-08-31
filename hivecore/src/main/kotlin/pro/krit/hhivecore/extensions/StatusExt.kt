package pro.krit.hhivecore.extensions

import pro.krit.hhivecore.base.status.BaseStatus

fun BaseStatus.getErrorMessage(default: String? = null): String {
    val firstError = this.errors?.firstOrNull { !it.description.isNullOrEmpty() || !it.descriptions.isNullOrEmpty() }
    val message = firstError?.run {
        description.takeIf { !it.isNullOrEmpty() }
            ?: descriptions?.firstOrNull { !it.isNullOrEmpty() }
    }
        ?: default
        ?: this.toString()
    return message
}