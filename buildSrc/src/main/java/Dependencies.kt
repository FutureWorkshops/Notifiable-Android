/*
 * Copyright ©  2018 - 2019 FutureWorkshops. All rights reserved.
 */

object Versions {
    const val gradle = "3.4.0"

    const val compileSdk = 28
    const val minSdk = 23
    const val targetSdk = 28


    const val appcompat = "1.0.2"
    const val annotation = "1.1.0"
    const val material = "1.1.0-beta01"
    const val constraintLayout = "2.0.0-beta2"

    const val jodaTime = "2.10.1"

    const val dagger = "2.22.1"


    const val ktx = "1.2.0-alpha01"
    const val kotlin = "1.3.31"

    const val timber = "4.7.1"

    const val firebaseCoreVersion = "16.0.9"
    const val firebaseMessagingVersion = "17.6.0"

    const val securedPreferenceStoreVersion = "0.7.4"
    const val jetpackSecurityVersion = "1.0.0-alpha02"

    const val rxjava = "2.2.8"
    const val rxAndroidVersion = "2.0.2"

    const val gsonVersion = "2.8.0"
    const val retrofit = "2.5.0"
    const val loggingInterceptor = "3.12.1"
    const val okHttpVersion = "3.10.0"

    const val lifecycle = "2.0.0"

    const val playCore = "1.4.1"

    const val junit = "4.12"
    const val espresso = "3.0.2"
    const val testRunner = "1.0.2"
    const val assertjCore = "3.12.2"
    const val mockitoKotlin = "2.1.0"
    const val mockitoInline = "2.27.0"
}

object Libraries {
    const val kotlinStdLib = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:${Versions.kotlin}"
    const val ktx = "androidx.core:core-ktx:${Versions.ktx}"

    const val lifecycleRuntime = "androidx.lifecycle:lifecycle-extensions:${Versions.lifecycle}"
    const val lifecycleExtensions = "androidx.lifecycle:lifecycle-extensions:${Versions.lifecycle}"
    const val lifecycleCompiler = "androidx.lifecycle:lifecycle-compiler:${Versions.lifecycle}"
    const val lifecycleKtx = "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.lifecycle}"

    const val joda = "joda-time:joda-time:${Versions.jodaTime}"

    const val timber = "com.jakewharton.timber:timber:${Versions.timber}"

    const val rxjava = "io.reactivex.rxjava2:rxjava:${Versions.rxjava}"
    const val rxAndroid = "io.reactivex.rxjava2:rxandroid:${Versions.rxAndroidVersion}"


    const val firebaseCore = "com.google.firebase:firebase-core:${Versions.firebaseCoreVersion}"
    const val firebaseMessaging = "com.google.firebase:firebase-messaging:${Versions.firebaseMessagingVersion}"


    const val gson = "com.google.code.gson:gson:${Versions.gsonVersion}"
    const val retrofit = "com.squareup.retrofit2:retrofit:${Versions.retrofit}"
    const val rxjavaAdapter = "com.squareup.retrofit2:adapter-rxjava2:${Versions.retrofit}"
    const val loggingInterceptor = "com.squareup.okhttp3:logging-interceptor:${Versions.loggingInterceptor}"
    const val retrofitGsonConverter = "com.squareup.retrofit2:converter-gson:${Versions.retrofit}"
    const val retrofitScalarConverter = "com.squareup.retrofit2:converter-scalars:${Versions.retrofit}"
    const val okHttpUrlConnection = "com.squareup.okhttp3:okhttp-urlconnection:${Versions.okHttpVersion}"

    const val dagger = "com.google.dagger:dagger:${Versions.dagger}"
    const val daggerCompiler = "com.google.dagger:dagger-compiler:${Versions.dagger}"

    const val daggerAndroid = "com.google.dagger:dagger-android:${Versions.dagger}"
    const val daggerAndroidSupport = "com.google.dagger:dagger-android-support:${Versions.dagger}"
    const val daggerAndroidProcessor = "com.google.dagger:dagger-android-processor:${Versions.dagger}"

    const val securedPreferenceStore =
        "online.devliving:securedpreferencestore:${Versions.securedPreferenceStoreVersion}"
    const val jetpackSecurity =
        "androidx.security:security-crypto:${Versions.jetpackSecurityVersion}"




}

object SupportLibraries {
    const val appCompatX = "androidx.appcompat:appcompat:${Versions.appcompat}"
    const val annotationX = "androidx.annotation:annotation:${Versions.annotation}"
    const val material = "com.google.android.material:material:${Versions.material}"
    const val constraintLayout = "androidx.constraintlayout:constraintlayout:${Versions.constraintLayout}"

}

object GoogleLibraries {
    const val playCore = "com.google.android.play:core:${Versions.playCore}"


}

object TestLibraries {
    const val junit = "junit:junit:${Versions.junit}"
    const val espressoCore = "com.android.support.test.espresso:espresso-core:${Versions.espresso}"
    const val testRunner = "com.android.support.test:runner:${Versions.testRunner}"
    const val assertjCore = "org.assertj:assertj-core:${Versions.assertjCore}"
    const val mockitoKotlin = "com.nhaarman.mockitokotlin2:mockito-kotlin:${Versions.mockitoKotlin}"
    const val mockitoInline = "org.mockito:mockito-inline:${Versions.mockitoInline}"
    const val lifecycleTesting = "androidx.arch.core:core-testing:${Versions.lifecycle}"
}