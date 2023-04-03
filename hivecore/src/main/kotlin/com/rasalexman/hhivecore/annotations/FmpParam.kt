package com.rasalexman.hhivecore.annotations

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class FmpParam(
    val name: String,
    val fields: Array<String>,
    val isList: Boolean = false,
    val isNumeric: Boolean = false
)
