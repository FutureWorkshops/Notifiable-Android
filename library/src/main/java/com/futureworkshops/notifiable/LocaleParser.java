/*
 * Copyright ©  2018 - 2019 FutureWorkshops. All rights reserved.
 */

package com.futureworkshops.notifiable;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;

import static com.futureworkshops.notifiable.networking.NotifiableService.COUNTRY;
import static com.futureworkshops.notifiable.networking.NotifiableService.LANGUAGE;

public class LocaleParser {
    private static final String DEFAULT_LANGUAGE = "en";
    private static final String DEFAULT_COUNTRY = "us";

    public static HashMap<String, String> populateHashMap(@NonNull HashMap<String, String> hashMap,
                                                          @NonNull Locale locale) {
        hashMap.put(LANGUAGE, locale.getLanguage());
        hashMap.put(COUNTRY, locale.getCountry());
        return hashMap;
    }

    public static JSONObject populateJSONObject(@NonNull JSONObject jsonObject, @Nullable Locale locale) throws JSONException {
        final String language;
        final String country;

        if (locale == null) {
            language = DEFAULT_LANGUAGE;
            country = DEFAULT_COUNTRY;
        } else {
            language = locale.getLanguage();
            country = locale.getCountry();
        }

        jsonObject.put(LANGUAGE, language);
        jsonObject.put(COUNTRY, country);

        return jsonObject;
    }


}
