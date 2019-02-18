/*
 * Copyright (c) 2018 Future Workshops. All rights reserved.
 */

package com.futureworkshops.notifiable.networking;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.futureworkshops.notifiable.BuildConfig;
import com.futureworkshops.notifiable.LocaleParser;
import com.futureworkshops.notifiable.Utils;
import com.futureworkshops.notifiable.model.GenericResponse;
import com.futureworkshops.notifiable.model.NotifiableCallback;
import com.futureworkshops.notifiable.model.NotifiableDevice;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.JavaNetCookieJar;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import static com.futureworkshops.notifiable.networking.NotifiableService.CUSTOM_PROPERTIES;
import static com.futureworkshops.notifiable.networking.NotifiableService.DEVICE_ID;
import static com.futureworkshops.notifiable.networking.NotifiableService.DEVICE_NAME;
import static com.futureworkshops.notifiable.networking.NotifiableService.DEVICE_TOKEN;
import static com.futureworkshops.notifiable.networking.NotifiableService.PROVIDER;
import static com.futureworkshops.notifiable.networking.NotifiableService.TOKEN;
import static com.futureworkshops.notifiable.networking.NotifiableService.USER_ALIAS;

/**
 * Created by stelian on 22/03/2016.
 */
public class RestManager implements RestAPI {

    private static final String DATE_HEADER = "Date";
    private static final String CONTENT_TYPE_HEADER = "Content-Type";
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String AUTHORIZATION_HEADER_TEMPLATE = "APIAuth %s:%s";
    private static final String FORM_URL_ENCODED = "application/x-www-form-urlencoded";

    private String mClientId;
    private NotifiableService mService;

    public RestManager(final String endpoint, String clientId, String clientSecret) {
        mClientId = clientId;

        CookieManager cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        JavaNetCookieJar javaNetCookieJar = new JavaNetCookieJar(cookieManager);

        final HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        if (BuildConfig.DEBUG) {
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        } else {
            logging.setLevel(HttpLoggingInterceptor.Level.NONE);
        }

        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .addInterceptor(new RequestInterceptor(clientSecret))
            .addInterceptor(logging)
            .connectTimeout(15000, TimeUnit.MILLISECONDS)
            .readTimeout(20000, TimeUnit.MILLISECONDS)
            .writeTimeout(20000, TimeUnit.MILLISECONDS)
            .cookieJar(javaNetCookieJar)
            .build();


        Retrofit restAdapter = new Retrofit.Builder()
            .baseUrl(endpoint)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build();

        mService = restAdapter.create(NotifiableService.class);
    }

    @Override
    public void registerAnonymousDevice(@NonNull String deviceName, @NonNull String deviceToken,
                                        @Nullable Locale locale,
                                        @Nullable String provider,
                                        @NonNull NotifiableCallback<NotifiableDevice> callback) {
        this.registerAnonymousDevice(deviceName, deviceToken, locale, provider, null, callback);
    }

    @Override
    public void registerAnonymousDevice(@NonNull String deviceName, @NonNull String deviceToken,
                                        @Nullable Locale locale,
                                        @Nullable String provider,
                                        @Nullable HashMap<String, Object> customProperties,
                                        @NonNull NotifiableCallback<NotifiableDevice> callback) {

        try {
            final JSONObject paramObject = new JSONObject();
            final JSONObject deviceTokenObject = new JSONObject();

            deviceTokenObject.put(DEVICE_NAME, deviceName);
            deviceTokenObject.put(TOKEN, deviceToken);
            if (customProperties != null && customProperties.size() > 0) {
                deviceTokenObject.put(CUSTOM_PROPERTIES, new JSONObject(customProperties).toString());
            }
            if (provider != null) {
                deviceTokenObject.put(PROVIDER, provider);
            }

            paramObject.put(DEVICE_TOKEN, LocaleParser.populateJSONObject(deviceTokenObject, locale));

            final Call<NotifiableDevice> notifiableDeviceCall = mService.registerAnonymousDevice(paramObject.toString());
            notifiableDeviceCall.enqueue(new DefaultResponseHandler<>(callback));

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void registerDeviceForUser(@NonNull String deviceName, @NonNull String deviceToken,
                                      @NonNull String userAlias, @Nullable Locale locale,
                                      @Nullable String provider,
                                      @NonNull NotifiableCallback<NotifiableDevice> callback) {
        this.registerDeviceForUser(deviceName, deviceToken, userAlias, locale, provider, null, callback);
    }

    @Override
    public void registerDeviceForUser(@NonNull String deviceName, @NonNull String deviceToken,
                                      @NonNull String userAlias, @Nullable Locale locale,
                                      @Nullable String provider,
                                      @Nullable HashMap<String, Object> customProperties,
                                      @NonNull NotifiableCallback<NotifiableDevice> callback) {

        try {
            final JSONObject paramObject = new JSONObject();
            final JSONObject deviceTokenObject = new JSONObject();

            deviceTokenObject.put(DEVICE_NAME, deviceName);
            deviceTokenObject.put(TOKEN, deviceToken);
            deviceTokenObject.put(USER_ALIAS, userAlias);
            if (customProperties != null && customProperties.size() > 0) {
                deviceTokenObject.put(CUSTOM_PROPERTIES, new JSONObject(customProperties).toString());
            }
            if (provider != null) {
                deviceTokenObject.put(PROVIDER, provider);
            }

            paramObject.put(DEVICE_TOKEN, LocaleParser.populateJSONObject(deviceTokenObject, locale));

            final Call<NotifiableDevice> notifiableDeviceCall = mService.registerDeviceWithName(paramObject.toString());
            notifiableDeviceCall.enqueue(new DefaultResponseHandler<>(callback));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateDeviceCustomProperties(@NonNull String deviceId,
                                             @NonNull Map<String, Object> customProperties,
                                             @NonNull NotifiableCallback<NotifiableDevice> callback) {
        this.updateDeviceInformation(deviceId, null, null, null, null, customProperties, callback);
    }

    @Override
    public void updateDeviceLocale(@NonNull String deviceId,
                                   @NonNull Locale locale,
                                   @NonNull NotifiableCallback<NotifiableDevice> callback) {
        this.updateDeviceInformation(deviceId, null, null, null, locale, null, callback);
    }

    @Override
    public void updateDeviceInformation(@NonNull String deviceId,
                                        @Nullable String token,
                                        @Nullable String username,
                                        @Nullable String deviceName,
                                        @Nullable Locale locale,
                                        @Nullable Map<String, Object> customProperties,
                                        @NonNull NotifiableCallback<NotifiableDevice> callback) {

        try {
            final JSONObject paramObject = new JSONObject();
            final JSONObject deviceTokenObject = new JSONObject();

            if (!TextUtils.isEmpty(username)) {
                deviceTokenObject.put(NotifiableService.USER_ALIAS, username);
            }

            if (!TextUtils.isEmpty(deviceName)) {
                deviceTokenObject.put(DEVICE_NAME, deviceName);
            }

            if (customProperties != null && customProperties.size() > 0) {
                deviceTokenObject.put(CUSTOM_PROPERTIES, new JSONObject(customProperties).toString());
            }

            if (!TextUtils.isEmpty(token)) {
                deviceTokenObject.put(TOKEN, token);
            }

            if (locale != null) {
                paramObject.put(DEVICE_TOKEN, LocaleParser.populateJSONObject(deviceTokenObject, locale));
            } else {
                paramObject.put(DEVICE_TOKEN, deviceTokenObject);
            }

            final Call<NotifiableDevice> deviceCall = mService.updateDeviceInfo(deviceId, paramObject.toString());
            deviceCall.enqueue(new DefaultResponseHandler<>(callback));

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void unregisterToken(@NonNull String deviceId,
                                @NonNull final NotifiableCallback<Object> callback) {

        try {
            final JSONObject paramObject = new JSONObject();
            final JSONObject deviceTokenObject = new JSONObject();

            deviceTokenObject.put(USER_ALIAS, "");

            paramObject.put(DEVICE_TOKEN, deviceTokenObject);

            final Call<ResponseBody> call = mService.unregisterToken(deviceId, paramObject.toString());
            call.enqueue(new DefaultResponseHandler<>(new NotifiableCallback<ResponseBody>() {
                @Override
                public void onSuccess(ResponseBody ret) {
                    callback.onSuccess(null);
                }

                @Override
                public void onError(String error) {
                    callback.onError(error);
                }
            }));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void markNotificationAsOpened(@NonNull String notificationId,
                                         @NonNull String deviceId,
                                         @NonNull final NotifiableCallback<Object> callback) {

        try {
            JSONObject paramObject = new JSONObject();
            paramObject.put(DEVICE_ID, deviceId);

            final Call<ResponseBody> call = mService.markNotificationAsOpened(notificationId, paramObject.toString());
            call.enqueue(new DefaultResponseHandler<>(new NotifiableCallback<ResponseBody>() {
                @Override
                public void onSuccess(ResponseBody ret) {
                    callback.onSuccess(null);
                }

                @Override
                public void onError(String error) {
                    callback.onError(error);
                }
            }));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void markNotificationAsReceived(@NonNull String notificationId,
                                           @NonNull String deviceId,
                                           @NonNull final NotifiableCallback<Object> callback) {

        try {
            JSONObject paramObject = new JSONObject();
            paramObject.put(DEVICE_ID, deviceId);

            final Call<ResponseBody> call = mService.markNotificationAsReceived(notificationId, paramObject.toString());
            call.enqueue(new DefaultResponseHandler<>(new NotifiableCallback<ResponseBody>() {
                @Override
                public void onSuccess(ResponseBody ret) {
                    callback.onSuccess(null);
                }

                @Override
                public void onError(String error) {
                    callback.onError(error);
                }
            }));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class DefaultResponseHandler<T> implements retrofit2.Callback<T> {

        private NotifiableCallback<T> mCallback;

        public DefaultResponseHandler(NotifiableCallback<T> callback) {
            mCallback = callback;
        }

        @Override
        public void onResponse(Call<T> call, Response<T> response) {
            // check response status
            if (response.isSuccessful()) {
                /* we either have the desired object, or a {@link GenericResponse} */

                mCallback.onSuccess(response.body());
            } else {
                mCallback.onError(response.message());
            }

        }

        @Override
        public void onFailure(Call<T> call, Throwable t) {
            mCallback.onError(t.getMessage());
        }
    }

    private class RequestInterceptor implements Interceptor {

        private String mSecret;

        public RequestInterceptor(String secret) {
            this.mSecret = secret;
        }

        @Override
        public okhttp3.Response intercept(Chain chain) throws IOException {
            final Request originalRequest = chain.request();
            Request authRequest = null;

            try {
                final String timestamp = Utils.formatDate(new Date());
                final String canonicalString = createCanonicalString(originalRequest, timestamp);
                final String encrypted = Utils.hmacSha1(canonicalString, mSecret);

                //  add headers
                Request.Builder builder = originalRequest.newBuilder()
                    .addHeader(CONTENT_TYPE_HEADER, FORM_URL_ENCODED)
                    .addHeader(DATE_HEADER, timestamp)
                    .addHeader(AUTHORIZATION_HEADER,
                        String.format(AUTHORIZATION_HEADER_TEMPLATE, mClientId, encrypted));

                authRequest = builder.build();

            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            }
//
            // do the request
            if (authRequest == null) {
                return handlePotentialResponseErrors(chain.proceed(originalRequest));
            } else {
                return handlePotentialResponseErrors(chain.proceed(authRequest));
            }

        }

        private okhttp3.Response handlePotentialResponseErrors(okhttp3.Response response) throws IOException {
            String contentType = response.body().contentType().type();

            if (contentType.equals("text")) {
                String json = response.body().string();

                // check for errors
                if (json.contains("\"success\":false")) {
                    Gson gson = new Gson();
                    try {
                        GenericResponse generic = gson.fromJson(json, GenericResponse.class);
                        if (!generic.isSuccess()) {

                            // create a new response
                            response = response.newBuilder()
                                .code(500)
                                .body(ResponseBody.create(MediaType.parse(generic.getMessage()), generic.getMessage()))
                                .message(generic.getMessage())
                                .build();

                            return response;
                        }

                    } catch (JsonSyntaxException e) {
                        e.printStackTrace();
                    }

                    // simply use the original response, the body has not been "consumed"
                    return response;
                } else {
                    // rebuild the response from the json
                    response = response.newBuilder().body(ResponseBody.create(MediaType.parse(json), json)).build();
                    return response;
                }
            } else {
                // simply use the original response, the body has not been "consumed"
                return response;
            }
        }

        /**
         * canonical_string = 'content-type,content-MD5,request URI,timestamp'
         *
         * @param request
         * @return
         */
        private String createCanonicalString(Request request, String timestamp) {
            StringBuilder sb = new StringBuilder();

            // add method
            sb.append(request.method());
            sb.append(",");

            // add content type
            String contentType = FORM_URL_ENCODED;
            if (request.body() != null && request.body().contentType() != null) {
                contentType = request.body().contentType().toString();
            }

            sb.append(contentType);
            sb.append(",");

            sb.append(",");

            // add endpoint
            sb.append(request.url().encodedPath());

            // - add query params to canonical string
            String query = request.url().encodedQuery();
            if (!TextUtils.isEmpty(query)) {
                sb.append("?");
                sb.append(query);
            }

            sb.append(",");

            // timestamp
            sb.append(timestamp);

            return sb.toString();
        }

    }
}
