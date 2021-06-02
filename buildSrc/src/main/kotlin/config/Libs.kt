package config

object Libs {
    object Core {
        const val coreKtx = "androidx.core:core-ktx:${Versions.appCoreX}"
        const val navigationUiKtx = "androidx.navigation:navigation-ui-ktx:${Versions.navigation}"
    }

    object Common {
        //--- GSOn
        const val gson = "com.google.code.gson:gson:${Versions.gson}"
        const val sresultpresentation = "com.github.Rasalexman.SResult:sresultpresentation:${Versions.sresult}"
    }

    object Processor {
        const val kotlinPoet = "com.squareup:kotlinpoet:1.8.0"
        const val autoService = "com.google.auto.service:auto-service:1.0"
    }


    object Tests {
        const val junit = "junit:junit:${Versions.junit}"
        const val runner = "com.android.support.test:runner:${Versions.runner}"
        const val espresso = "com.android.support.test.espresso:espresso-core:${Versions.espresso}"
    }

}