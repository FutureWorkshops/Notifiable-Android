/*
 * Copyright (c) 2018 Future Workshops. All rights reserved.
 */

package com.futureworkshops.notifiable.sample;

import android.support.annotation.NonNull;

import java.util.HashMap;

/**
 * Created by stelian on 30/03/2016.
 */
public class Utils {

    /**
     * Convert a map to a URL query String.
     *
     * @param keyValueMap map of values
     * @return a String that can be used in a URL ({@code key1=val1&key2=val2...})
     */
    public static String mapToString(@NonNull HashMap<String, String> keyValueMap) {
        final StringBuilder sb = new StringBuilder();

        for (String key : keyValueMap.keySet()) {
            String value = keyValueMap.get(key);
            if (value != null) {

                if (sb.length() > 0) {
                    sb.append("&");
                }

                sb.append(key);
                sb.append("=");
                sb.append(value);
            }
        }

        return sb.toString();
    }
}
