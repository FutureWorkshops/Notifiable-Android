/*
 * Copyright ©  2018 - 2019 FutureWorkshops. All rights reserved.
 */

package com.futureworkshops.notifiable;


import android.util.Base64;

import androidx.annotation.NonNull;

import com.futureworkshops.notifiable.model.NotifiableMessage;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class Utils {

    private static final String httpDateFormat = "EEE, dd MMM yyyy HH:mm:ss";
    private static final String HMAC_SHA1 = "HmacSHA1";
    private static final String NOTIFICATION_ID = "n_id";
    private static final String NOTIFICATION_TITLE = "title";
    private static final String NOTIFICATION_MESSAGE = "message";

    public static String formatDate(@NonNull Date date) {

        // create Joda date time formatter that will append GMT when formatting dates
        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .appendPattern(httpDateFormat)
                // append GMT if time difference is 0 but don't show hour difference otherwise
                .appendTimeZoneOffset(" GMT", false, 2, 2)
                .toFormatter()
                .withLocale(new Locale("en", "US"));


        // get the GMT timezone and use it when creating a new DateTime object
        // time zone adjustments will be done automatically by joda
        final DateTimeZone gmtTimeZone = DateTimeZone.forTimeZone(TimeZone.getTimeZone("GMT"));

        // return the formatted GMT date
        return new DateTime(gmtTimeZone).withMillis(date.getTime()).toInstant().toString(formatter);
    }

    public static String hmacSha1(@NonNull String value, @NonNull String key)
            throws NoSuchAlgorithmException,
            InvalidKeyException {

        SecretKeySpec secret = new SecretKeySpec(key.getBytes(), HMAC_SHA1);
        Mac mac = Mac.getInstance(HMAC_SHA1);
        mac.init(secret);
        byte[] bytes = mac.doFinal(value.getBytes());

        return Base64.encodeToString(bytes, Base64.NO_WRAP);
    }


    public static NotifiableMessage createNotificationFromMap(@NonNull Map<String, String> data) {
        final NotifiableMessage notification = new NotifiableMessage();
        notification.setDeviceProperties(new HashMap<>());

        Set<String> keys = data.keySet();
        for (String key : keys) {
            if (key.equalsIgnoreCase(NOTIFICATION_ID)) {
                notification.setNotificationId(Integer.valueOf(data.get(key)));
            } else if (key.equalsIgnoreCase(NOTIFICATION_TITLE)) {
                notification.setTitle(data.get(key));
            } else if (key.equalsIgnoreCase(NOTIFICATION_MESSAGE)) {
                notification.setMessage(data.get(key));
            } else {
                notification.getDeviceProperties().put(key, String.valueOf(data.get(key)));
            }
        }

        return notification;
    }
}
