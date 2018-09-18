/*
 * Copyright (c) 2018 Future Workshops. All rights reserved.
 */

package com.futureworkshops.notifiable.networking;

import com.futureworkshops.notifiable.model.NotifiableDevice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;


/**
 * Created by stelian on 22/03/2016.
 */
public interface NotifiableService {

    String USER_API = "/api";
    String VERSION = "/v1";
    String DEVICE_TOKENS = "/device_tokens";
    String NOTIFICATIONS = "/notifications";
    String ID = "{id}";
    String STATUS_OPENED = "/opened";
    String STATUS_DELIVERED = "/delivered";


    String DEVICE_TOKEN = "device_token";
    String CUSTOM_PROPERTIES = "custom_properties";
    String USER_ALIAS = "user_alias";
    String DEVICE_NAME = "name";
    String PROVIDER = "provider";
    String TOKEN = "token";
    String LANGUAGE = "language";
    String COUNTRY = "country";
    String DEVICE_ID_PATH_PARAM = "{device_token_id}";
    String DEVICE_ID = "device_token_id";

    @Headers("Content-Type: application/json")
    @POST(USER_API + VERSION + DEVICE_TOKENS)
    Call<NotifiableDevice> registerAnonymousDevice(@Body String body);

    @Headers("Content-Type: application/json")
    @POST(USER_API + VERSION + DEVICE_TOKENS)
    Call<NotifiableDevice> registerDeviceWithName(@Body String body);

    @Headers("Content-Type: application/json")
    @PATCH(USER_API + VERSION + DEVICE_TOKENS + "/" + DEVICE_ID_PATH_PARAM)
    Call<NotifiableDevice> updateDeviceInfo(@Path(DEVICE_ID) String notifiableDeviceId,
                                            @Body String deviceInfo);

    @Headers("Content-Type: application/json")
    @PATCH(USER_API + VERSION + DEVICE_TOKENS + "/" + DEVICE_ID_PATH_PARAM)
    Call<ResponseBody> unregisterToken(@Path(DEVICE_ID) String notifiableDeviceId,
                                       @Body String userAlias);

    @Headers("Content-Type: application/json")
    @POST(USER_API + VERSION + NOTIFICATIONS + "/" + ID + STATUS_DELIVERED)
    Call<ResponseBody> markNotificationAsReceived(@Path("id") String notificationId, @Body String body);

    @Headers("Content-Type: application/json")
    @POST(USER_API + VERSION + NOTIFICATIONS + "/" + ID + STATUS_OPENED)
    Call<ResponseBody> markNotificationAsOpened(@Path("id") String notificationId, @Body String body);

}
