/*
 * Copyright ©  2018 - 2019 FutureWorkshops. All rights reserved.
 */

package com.futureworkshops.notifiable.rx.internal

import android.util.Base64
import com.futureworkshops.notifiable.rx.model.NotifiableMessage
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.format.DateTimeFormatterBuilder
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

private const val HMAC_SHA1 = "HmacSHA1"
private const val httpDateFormat = "EEE, dd MMM yyyy HH:mm:ss"
private const val NOTIFICATION_ID = "n_id"
private const val NOTIFICATION_TITLE = "title"
private const val NOTIFICATION_MESSAGE = "message"


const val LANGUAGE = "language"
const val COUNTRY = "country"

@Throws(NoSuchAlgorithmException::class, InvalidKeyException::class)
fun hmacSha1(value: String, key: String): String {

    val secret = SecretKeySpec(key.toByteArray(), HMAC_SHA1)
    val mac = Mac.getInstance(HMAC_SHA1)
    mac.init(secret)
    val bytes = mac.doFinal(value.toByteArray())

    return Base64.encodeToString(bytes, Base64.NO_WRAP)
}


fun Date.applyNotifiableFormat(): String {
    // create Joda date time formatter that will append GMT when formatting dates
    val formatter = DateTimeFormatterBuilder()
        .appendPattern(httpDateFormat)
        // append GMT if time difference is 0 but don't show hour difference otherwise
        .appendTimeZoneOffset(" GMT", false, 2, 2)
        .toFormatter()
        .withLocale(Locale("en", "US"))


    // get the GMT timezone and use it when creating a new DateTime object
    // time zone adjustments will be done automatically by joda
    val gmtTimeZone = DateTimeZone.forTimeZone(TimeZone.getTimeZone("GMT"))

    // return the formatted GMT date
    return DateTime(gmtTimeZone).withMillis(this.time).toInstant().toString(formatter)
}

fun createNotificationFromMap(data: Map<String, String>): NotifiableMessage {

    var notificationId: Int = -1
    var title: String = ""
    var message: String = ""
    val properties: MutableMap<String, String> = mutableMapOf()

    for (key in data.keys) {
        when {
            key.equals(NOTIFICATION_ID, ignoreCase = true) -> notificationId =
                Integer.valueOf(data.getValue(key))
            key.equals(NOTIFICATION_TITLE, ignoreCase = true) -> title = data.getValue(key)
            key.equals(NOTIFICATION_MESSAGE, ignoreCase = true) -> message = data.getValue(key)
            else -> properties[key] = data.getValue(key)
        }
    }

    return NotifiableMessage(notificationId, title, message, properties)
}

//@Throws(JSONException::class)
//fun populateJSONObject(jsonObject: JSONObject, locale: Locale?): JSONObject {
//    val language: String
//    val country: String
//
//    if (locale == null) {
//        language = DEFAULT_LANGUAGE
//        country = DEFAULT_COUNTRY
//    } else {
//        language = locale.language
//        country = locale.country
//    }
//
//    jsonObject.put(LANGUAGE, language)
//    jsonObject.put(COUNTRY, country)
//
//    return jsonObject
//}