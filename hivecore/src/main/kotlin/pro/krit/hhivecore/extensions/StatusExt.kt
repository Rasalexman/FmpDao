package pro.krit.hhivecore.extensions

import com.mobrun.plugin.models.BaseStatus

fun BaseStatus.getErrorMessage(): String {
    val firstError = this.errors.firstOrNull()
    val message = firstError?.run {
        description ?: descriptions.firstOrNull()
    }.orEmpty()
    return message
}