package com.rasalexman.hiveksp

import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.squareup.kotlinpoet.DelicateKotlinPoetApi

@DelicateKotlinPoetApi("This is delicate api. Use it with restrictions")
class FmpSymbolProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor =
        FmpSymbolProcessor(environment)
}