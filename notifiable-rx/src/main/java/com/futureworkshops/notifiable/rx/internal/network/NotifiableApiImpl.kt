/*
 * Copyright ©  2018 - 2019 FutureWorkshops. All rights reserved.
 */

package com.futureworkshops.notifiable.rx.internal.network

import com.futureworkshops.notifiable.rx.internal.network.NotifiableServiceRx.Companion.DEVICE_ID
import com.futureworkshops.notifiable.rx.model.NotifiableDevice
import io.reactivex.Completable
import io.reactivex.Single
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.JavaNetCookieJar
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import timber.log.Timber
import java.net.CookieManager
import java.net.CookiePolicy
import java.util.*
import java.util.concurrent.TimeUnit

class NotifiableApiImpl(
    private var endpoint: String,
    private val clientId: String,
    private val clientSecret: String,
    private var debug: Boolean = false
) : NotifiableApi {

    private var httpClient: OkHttpClient


    private lateinit var restService: NotifiableServiceRx

    init {
        httpClient = buildClient()

        initRetrofit()
    }

    override fun registerDevice(
        deviceName: String,
        deviceToken: String,
        locale: Locale?,
        userAlias: String?,
        provider: String,
        customProperties: Map<String, String>?
    ): Single<NotifiableDevice> {
        try {

            val requestBody = NotifiableRegisterRequesBody(
                NotifiableDeviceRequestBody(
                    deviceName,
                    deviceToken,
                    provider,
                    userAlias,
                    locale?.language ?: DEFAULT_LANGUAGE,
                    locale?.country ?: DEFAULT_COUNTRY,
                    properties = customProperties
                )
            )

            return restService.registerDevice(requestBody)

        } catch (e: JSONException) {
            Timber.e(e)
            return Single.error(e)
        }

    }

    override fun updateDeviceInformation(
        deviceId: String,
        token: String?,
        userName: String?,
        deviceName: String?,
        locale: Locale?,
        customProperties: Map<String, String>?
    ): Completable {
        return try {
            val requestBody = NotifiableRegisterRequesBody(
                NotifiableDeviceRequestBody(
                    deviceName,
                    token,
                    user = userName,
                    language = locale?.language ?: DEFAULT_LANGUAGE,
                    country = locale?.country ?: DEFAULT_COUNTRY,
                    properties = customProperties
                )
            )

            restService.updateDeviceInfo(deviceId, requestBody)

        } catch (e: JSONException) {
            Timber.e(e)
            Completable.error(e)
        }

    }

    override fun unregisterToken(deviceId: String): Completable {
        return try {
            val paramObject = JSONObject()
            val deviceTokenObject = JSONObject()

            deviceTokenObject.put(USER_ALIAS, "")

            paramObject.put(DEVICE_TOKEN, deviceTokenObject)

            restService.unregisterToken(deviceId, paramObject.toString())


        } catch (e: JSONException) {
            Timber.e(e)
            Completable.error(e)
        }

    }

    override fun markNotificationAsReceived(deviceId: String, notificationId: String): Completable {
        return try {
            val paramObject = JSONObject()
            paramObject.put(DEVICE_ID, deviceId)

            return restService.markNotificationAsReceived(notificationId, paramObject.toString())


        } catch (e: JSONException) {
            Timber.e(e)
            Completable.error(e)
        }
    }

    override fun markNotificationAsOpened(deviceId: String, notificationId: String): Completable {
        return try {
            val paramObject = JSONObject()
            paramObject.put(DEVICE_ID, deviceId)

            return restService.markNotificationAsOpened(notificationId, paramObject.toString())


        } catch (e: JSONException) {
            Timber.e(e)
            Completable.error(e)
        }
    }

    private fun buildClient(): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(
                NotifiableRequestInterceptor(
                    clientSecret,
                    clientId
                )
            )
            .addInterceptor(getLoggingInterceptor())
            .connectTimeout(15000, TimeUnit.MILLISECONDS)
            .readTimeout(20000, TimeUnit.MILLISECONDS)
            .writeTimeout(20000, TimeUnit.MILLISECONDS)
            .cookieJar(getCookieJar())
            .build()


    private fun getCookieJar(): JavaNetCookieJar {
        val cookieManager = CookieManager()
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL)
        return JavaNetCookieJar(cookieManager)
    }


    private fun initRetrofit() {

        // check that URL is valid
        checkValidUrl()

        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(endpoint)
            .client(httpClient)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()

        restService = retrofit.create(NotifiableServiceRx::class.java)

//        secureKeyStore.updateServer(endpoint)

    }

    private fun checkValidUrl() {
        if (!endpoint.endsWith("/")) {
            endpoint += "/"
        }

        HttpUrl.parse(endpoint) ?: throw IllegalArgumentException("Illegal URL: $endpoint")
    }

    private fun getLoggingInterceptor(): Interceptor {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        if (debug) {
            httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        } else {
            httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.NONE
        }

        return httpLoggingInterceptor
    }

    companion object {
        const val DEVICE_TOKEN = "device_token"
        const val CUSTOM_PROPERTIES = "custom_properties"
        const val USER_ALIAS = "user_alias"
        const val DEVICE_NAME = "name"
        const val PROVIDER = "provider"
        const val TOKEN = "token"


        private const val DEFAULT_LANGUAGE = "en"
        private const val DEFAULT_COUNTRY = "us"

    }

}