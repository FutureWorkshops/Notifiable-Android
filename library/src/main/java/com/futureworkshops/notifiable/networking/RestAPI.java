/*
 * Copyright (c) 2018 Future Workshops. All rights reserved.
 */

package com.futureworkshops.notifiable.networking;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.futureworkshops.notifiable.model.NotifiableCallback;
import com.futureworkshops.notifiable.model.NotifiableDevice;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by stelian on 22/03/2016.
 */
public interface RestAPI {

    void registerAnonymousDevice(@NonNull String deviceName,
                                 @NonNull String deviceToken,
                                 @Nullable Locale locale,
                                 @Nullable String provider,
                                 @NonNull NotifiableCallback<NotifiableDevice> callback);

    void registerAnonymousDevice(@NonNull String deviceName,
                                 @NonNull String deviceToken,
                                 @Nullable Locale locale,
                                 @Nullable String provider,
                                 @Nullable HashMap<String, Object> customProperties,
                                 @NonNull NotifiableCallback<NotifiableDevice> callback);

    void registerDeviceForUser(@NonNull String deviceName,
                               @NonNull String deviceToken,
                               @NonNull String userAlias,
                               @Nullable Locale locale,
                               @Nullable String provider,
                               @NonNull NotifiableCallback<NotifiableDevice> callback);

    void registerDeviceForUser(@NonNull String deviceName,
                               @NonNull String deviceToken,
                               @NonNull String userAlias,
                               @Nullable Locale locale,
                               @Nullable String provider,
                               @Nullable HashMap<String, Object> customProperties,
                               @NonNull NotifiableCallback<NotifiableDevice> callback);

    void updateDeviceInformation(@NonNull String deviceId,
                                 @Nullable String token,
                                 @Nullable String username,
                                 @Nullable String deviceName,
                                 @Nullable Locale locale,
                                 @Nullable Map<String, Object> customProperties,
                                 @NonNull NotifiableCallback<NotifiableDevice> callback);

    void updateDeviceLocale(@NonNull String deviceId,
                            @NonNull Locale locale,
                            @NonNull NotifiableCallback<NotifiableDevice> callback);

    void updateDeviceCustomProperties(@NonNull String deviceId,
                                      @NonNull Map<String, Object> customProperties,
                                      @NonNull NotifiableCallback<NotifiableDevice> callback);

    void unregisterToken(@NonNull String deviceId,
                         @NonNull NotifiableCallback<Object> callback);

    void markNotificationAsReceived(@NonNull String deviceId,
                                    @NonNull String deviceToken,
                                    @NonNull NotifiableCallback<Object> callback);

    void markNotificationAsOpened(@NonNull String deviceId,
                                  @NonNull String deviceToken,
                                  @NonNull NotifiableCallback<Object> callback);
}
