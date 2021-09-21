package eu.balzo.authdroid.dependencies

object AndroidX {

    const val appCompat = "androidx.appcompat:appcompat:1.3.1"

    const val activityKtx = "androidx.activity:activity-ktx:1.3.0"

    object Core {

        const val coreKtx = "androidx.core:core-ktx:1.6.0"

    }

    const val constraintLayout = "androidx.constraintlayout:constraintlayout:2.1.0"

    object Lifecycle {

        private const val version = "2.3.1"
        private const val group = "androidx.lifecycle"

        const val runtimeKtx = "$group:lifecycle-runtime-ktx:$version"

        const val commonJava8 = "$group:lifecycle-common-java8:$version"

    }

}