object Config {
    const val min_sdk = 21
    const val target_sdk = 27
    const val compile_sdk = 27
    const val version_code = 1
    const val version_name = "0.1"
}

object Versions {
    //** KOTLIN **//
    const val kotlin = "1.2.51"
    //** SUPPORT LIB **//
    const val supportLib = "27.1.1"
    //** RX **//
    const val rx = "2.1.16"
    //** DAGGER **//
    const val dagger = "2.16"
}

object Deps {

    //** KOTLIN **//
    const val kotlin_stdlib = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:${Versions.kotlin}"

    //** SUPPORT LIB **//
    const val recyclerview = "com.android.support:recyclerview-v7:${Versions.supportLib}"

    //** RX **//
    const val rx = "io.reactivex.rxjava2:rxjava:${Versions.rx}"

    //** DAGGER **//
    const val daggerCompiler = "com.google.dagger:dagger-compiler:${Versions.dagger}"
    const val daggerRuntime = "com.google.dagger:dagger:${Versions.dagger}"
    const val daggerAndroid = "com.google.dagger:dagger-android:${Versions.dagger}"
    const val daggerAndroidSupport = "com.google.dagger:dagger-android-support:${Versions.dagger}"
    const val daggerAndroidSupportCompiler = "com.google.dagger:dagger-android-processor:${Versions.dagger}"
}