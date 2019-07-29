/*
 * Copyright ©  2018 - 2019 FutureWorkshops. All rights reserved.
 */

package com.futureworkshops.notifiable.rx.internal

import android.text.TextUtils
import com.futureworkshops.notifiable.rx.model.GenericResponse
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import okhttp3.*
import java.io.IOException
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import java.util.*

class NotifiableRequestInterceptor(private val secret: String, private val clientId: String) :
    Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        var authRequest: Request? = null

        try {
            val timestamp = Date().applyNotifiableFormat()
            val canonicalString = createCanonicalString(originalRequest, timestamp)
            val encrypted = hmacSha1(canonicalString, secret)

            //  add headers
            val builder = originalRequest.newBuilder()
                .addHeader(
                    CONTENT_TYPE_HEADER,
                    FORM_URL_ENCODED
                )
                .addHeader(DATE_HEADER, timestamp)
                .addHeader(
                    AUTHORIZATION_HEADER,
                    String.format(AUTHORIZATION_HEADER_TEMPLATE, clientId, encrypted)
                )

            authRequest = builder.build()

        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: InvalidKeyException) {
            e.printStackTrace()
        }

//
        // do the request
        return if (authRequest == null) {
            handlePotentialResponseErrors(chain.proceed(originalRequest))
        } else {
            handlePotentialResponseErrors(chain.proceed(authRequest))
        }
    }

    @Throws(IOException::class)
    private fun handlePotentialResponseErrors(response: Response): Response {
        var response = response
        val contentType = response.body()!!.contentType()!!.type()

        if (contentType == "text") {
            val json = response.body()!!.string()

            // check for errors
            if (json.contains("\"success\":false")) {
                val gson = Gson()
                try {
                    val genericResponse = gson.fromJson(json, GenericResponse::class.java)
                    if (!genericResponse.isSuccess) {

                        // create a new response
                        response = response.newBuilder()
                            .code(500)
                            .body(
                                ResponseBody.create(
                                    MediaType.parse(genericResponse.message ?: ""),
                                    genericResponse.message ?: ""
                                )
                            )
                            .message(genericResponse.message ?: "")
                            .build()

                        return response
                    }

                } catch (e: JsonSyntaxException) {
                    e.printStackTrace()
                }

                // simply use the original response, the body has not been "consumed"
                return response
            } else {
                // rebuild the response from the json
                response =
                    response.newBuilder().body(ResponseBody.create(MediaType.parse(json), json))
                        .build()
                return response
            }
        } else {
            // simply use the original response, the body has not been "consumed"
            return response
        }
    }

    /**
     * canonical_string = 'content-type,content-MD5,request URI,timestamp'
     *
     * @param request
     * @return
     */
    private fun createCanonicalString(request: Request, timestamp: String): String {
        val sb = StringBuilder()

        // add method
        sb.append(request.method())
        sb.append(",")

        // add content type
        var contentType =
            FORM_URL_ENCODED
        if (request.body() != null && request.body()!!.contentType() != null) {
            contentType = request.body()!!.contentType()!!.toString()
        }

        sb.append(contentType)
        sb.append(",")

        sb.append(",")

        // add endpoint
        sb.append(request.url().encodedPath())

        // - add query params to canonical string
        val query = request.url().encodedQuery()
        if (!TextUtils.isEmpty(query)) {
            sb.append("?")
            sb.append(query)
        }

        sb.append(",")

        // timestamp
        sb.append(timestamp)

        return sb.toString()
    }

    companion object {
        private const val DATE_HEADER = "Date"
        private const val CONTENT_TYPE_HEADER = "Content-Type"
        private const val AUTHORIZATION_HEADER = "Authorization"
        private const val AUTHORIZATION_HEADER_TEMPLATE = "APIAuth %s:%s"
        private const val FORM_URL_ENCODED = "application/x-www-form-urlencoded"

    }
}