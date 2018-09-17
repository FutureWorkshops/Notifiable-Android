/*
 * Copyright (c) 2018 Future Workshops. All rights reserved.
 */

package com.futureworkshops.notifiable;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.futureworkshops.notifiable.model.NotifiableCallback;
import com.futureworkshops.notifiable.model.NotifiableDevice;
import com.futureworkshops.notifiable.networking.RestManager;

import java.util.HashMap;
import java.util.Locale;

/**
 * Created by stelian on 22/03/2016.
 */
public class NotifiableManager {
    public static final String GOOGLE_CLOUD_MESSAGING_PROVIDER = "gcm";

    private RestManager mRestManager;

    /**
     * Create a new instance of the {@code NotifiableManager}.
     *
     * @param url          server url
     * @param clientId     id of the server app that uses Notifiable-Rails
     * @param clientSecret client secret key
     * @return a new {@code NotifiableManager}
     */
    public static NotifiableManager newInstance(@NonNull final String url,
            @NonNull String clientId, @NonNull String clientSecret) {
        return new NotifiableManager(url, clientId, clientSecret);
    }

    private NotifiableManager(@NonNull final String url,
            @NonNull String clientId, @NonNull String clientSecret) {
        mRestManager = new RestManager(url, clientId, clientSecret);
    }

    /**
     * Register a device to receive notifications from the Notifiable server without associating the device with a user.
     *
     * @param deviceName  the name of the device, can be anything
     * @param deviceToken the GCM token for this device
     * @param locale      locale of the device
     * @param provider    The provider of Cloud Messaging services (typically gcm)
     * @param callback    the callback used to deliver the result of the operation
     */
    public void registerAnonymousDevice(@NonNull final String deviceName,
            @NonNull final String deviceToken,
            @NonNull final Locale locale,
            @NonNull String provider,
            @NonNull final NotifiableCallback<NotifiableDevice> callback) {
        this.registerAnonymousDevice(deviceName, deviceToken, locale, provider, null, callback);
    }

    /**
     * Register a device to receive notifications from the Notifiable server without associating the device with a user.
     *
     * @param deviceName       the name of the device, can be anything
     * @param deviceToken      the GCM token for this device
     * @param locale           locale of the device
     * @param provider         The provider of Cloud Messaging services (typically gcm)
     * @param customProperties Set of properties to be associated to the device
     * @param callback         the callback used to deliver the result of the operation
     */
    public void registerAnonymousDevice(@NonNull final String deviceName,
            @NonNull final String deviceToken,
            @NonNull final Locale locale,
            @NonNull String provider,
            @Nullable HashMap<String, Object> customProperties,
            @NonNull final NotifiableCallback<NotifiableDevice> callback) {
        mRestManager.registerAnonymousDevice(deviceName, deviceToken, locale, provider, customProperties,
                new NotifiableCallback<NotifiableDevice>() {
                    @Override
                    public void onSuccess(@NonNull NotifiableDevice device) {
                        device.setName(deviceName);
                        device.setToken(deviceToken);
                        device.setLocale(locale);
                        callback.onSuccess(device);
                    }

                    @Override
                    public void onError(@NonNull String error) {
                        callback.onError(error);
                    }
                });
    }

    /**
     * Register a device to receive notifications from the Notifiable server and associate the device to the given user.
     *
     * @param deviceName the name of the device, can be anything
     * @param deviceToken the GCM token for this device
     * @param userAlias  the name of the user you want to associate the device to
     * @param locale     locale of the device
     * @param provider   The provider of Cloud Messaging services (typically gcm)
     * @param callback   the callback used to deliver the result of the operation
     */
    public void registerDevice(@NonNull final String deviceName,
            @NonNull final String deviceToken,
            @NonNull final String userAlias,
            @NonNull final Locale locale,
            @NonNull String provider,
            @NonNull final NotifiableCallback<NotifiableDevice> callback) {
        this.registerDevice(deviceName, deviceToken, userAlias, locale, provider, null, callback);
    }

    /**
     * Register a device to receive notifications from the Notifiable server and associate the device to the given user.
     *
     * @param deviceName       the name of the device, can be anything
     * @param deviceToken the GCM token for this device
     * @param userAlias        the name of the user you want to associate the device to
     * @param locale           locale of the device
     * @param provider         The provider of Cloud Messaging services (typically gcm)
     * @param customProperties Set of properties to be associated to the device
     * @param callback         the callback used to deliver the result of the operation
     */
    public void registerDevice(@NonNull final String deviceName,
            @NonNull final String deviceToken,
            @NonNull final String userAlias,
            @NonNull final Locale locale,
            @NonNull String provider,
            @Nullable HashMap<String, Object> customProperties,
            @NonNull final NotifiableCallback<NotifiableDevice> callback) {
        mRestManager.registerDeviceForUser(deviceName, deviceToken, userAlias, locale, provider, customProperties,
                new NotifiableCallback<NotifiableDevice>() {
                    @Override
                    public void onSuccess(@NonNull NotifiableDevice device) {
                        device.setName(deviceName);
                        device.setUser(userAlias);
                        device.setToken(deviceToken);
                        device.setLocale(locale);
                        callback.onSuccess(device);
                    }

                    @Override
                    public void onError(@NonNull String error) {
                        callback.onError(error);
                    }
                });
    }

    /**
     * Updates the Locale information of a specif device
     *
     * @param deviceId id returned by the server after registering the device
     * @param locale   locale of the device
     * @param callback the callback used to deliver the result of the operation
     */
    public void updateDeviceLocale(@NonNull final String deviceId,
            @NonNull Locale locale,
            @NonNull final NotifiableCallback<NotifiableDevice> callback) {
        mRestManager.updateDeviceLocale(deviceId, locale, new NotifiableCallback<NotifiableDevice>() {
            @Override
            public void onSuccess(@NonNull NotifiableDevice device) {
                device.setLocale(locale);
                device.setId(Integer.valueOf(deviceId));
                callback.onSuccess(device);
            }

            @Override
            public void onError(@NonNull String error) {
                callback.onError(error);
            }
        });
    }

    /**
     * Updates the device token of a registered device
     *
     * @param userName    the name of the user you want to associate the device to
     * @param deviceId    id returned by the server after registering the device
     * @param deviceToken the GCM token for this device
     * @param callback    the callback used to deliver the result of the operation
     */
    public void updateDeviceToken(@NonNull final String userName,
            @NonNull final String deviceId,
            @NonNull final String deviceToken,
            @NonNull final NotifiableCallback<NotifiableDevice> callback) {

        mRestManager.updateDeviceInformation(deviceId, deviceToken, userName, null, null, null, new
                NotifiableCallback<NotifiableDevice>() {
                    @Override
                    public void onSuccess(@NonNull NotifiableDevice device) {
                        device.setUser(userName);
                        device.setToken(deviceToken);
                        device.setId(Integer.valueOf(deviceId));
                        callback.onSuccess(device);
                    }

                    @Override
                    public void onError(@NonNull String error) {
                        callback.onError(error);
                    }
                });
    }

    /**
     * Updates the device token of a registered device
     *
     * @param deviceId    id returned by the server after registering the device
     * @param deviceToken the GCM token for this device
     * @param callback    the callback used to deliver the result of the operation
     */
    public void updateAnonymousDeviceToken(@NonNull final String deviceId,
            @NonNull final String deviceToken,
            @NonNull final NotifiableCallback<NotifiableDevice> callback) {

        mRestManager.updateDeviceInformation(deviceId, deviceToken, null, null, null, null, new
                NotifiableCallback<NotifiableDevice>() {
                    @Override
                    public void onSuccess(@NonNull NotifiableDevice device) {
                        device.setToken(deviceToken);
                        device.setId(Integer.valueOf(deviceId));
                        callback.onSuccess(device);
                    }

                    @Override
                    public void onError(@NonNull String error) {
                        callback.onError(error);
                    }
                });
    }

    /**
     * Updates the name of a registered device
     *
     * @param userName   the name of the user you want to associate the device to
     * @param deviceId   id returned by the server after registering the device
     * @param deviceName the name of the device, can be anything
     * @param callback   the callback used to deliver the result of the operation
     */
    public void updateDeviceName(@NonNull final String userName,
            @NonNull final String deviceId,
            @NonNull final String deviceName,
            @NonNull final NotifiableCallback<NotifiableDevice> callback) {

        mRestManager.updateDeviceInformation(deviceId, null, userName, deviceName, null, null, new
                NotifiableCallback<NotifiableDevice>() {
                    @Override
                    public void onSuccess(@NonNull NotifiableDevice device) {
                        device.setUser(userName);
                        device.setName(deviceName);
                        device.setId(Integer.valueOf(deviceId));
                        callback.onSuccess(device);
                    }

                    @Override
                    public void onError(@NonNull String error) {
                        callback.onError(error);
                    }
                });
    }

    /**
     * Updates the name of a registered device
     *
     * @param deviceId   id returned by the server after registering the device
     * @param deviceName the name of the device, can be anything
     * @param callback   the callback used to deliver the result of the operation
     */
    public void updateAnonymousDeviceName(@NonNull final String deviceId, @NonNull final String deviceName,
            @NonNull final NotifiableCallback<NotifiableDevice> callback) {

        mRestManager.updateDeviceInformation(deviceId, null, null, deviceName, null, null, new
                NotifiableCallback<NotifiableDevice>() {
                    @Override
                    public void onSuccess(@NonNull NotifiableDevice device) {
                        device.setName(deviceName);
                        device.setId(Integer.valueOf(deviceId));
                        callback.onSuccess(device);
                    }

                    @Override
                    public void onError(@NonNull String error) {
                        callback.onError(error);
                    }
                });
    }

    /**
     * Update device information. Valid properties are defined in the server app.
     *
     * @param deviceId         id returned by the server after registering the device
     * @param customProperties map containing device properties
     * @param callback         the callback used to deliver the result of the operation
     */
    public void updateDeviceCustomProperties(@NonNull final String deviceId,
            @NonNull final HashMap<String, Object> customProperties,
            @NonNull final NotifiableCallback<NotifiableDevice> callback) {
        mRestManager.updateDeviceCustomProperties(deviceId, customProperties, new
                NotifiableCallback<NotifiableDevice>() {
                    @Override
                    public void onSuccess(@NonNull NotifiableDevice device) {
                        device.setCustomProperties(customProperties);
                        callback.onSuccess(device);
                    }

                    @Override
                    public void onError(@NonNull String error) {
                        callback.onError(error);
                    }
                });
    }

    /**
     * Unregister the given device.
     *
     * @param deviceId id returned by the server after registering the device
     * @param callback the callback used to deliver the result of the operation
     */
    public void unregisterDevice(@NonNull String deviceId,
            @NonNull final NotifiableCallback<Object> callback) {
        mRestManager.unregisterToken(deviceId, new NotifiableCallback<Object>() {
            @Override
            public void onSuccess(@NonNull Object o) {
                callback.onSuccess(o);
            }

            @Override
            public void onError(@NonNull String error) {
                callback.onError(error);
            }
        });
    }

    /**
     * This will notify the {@code Notifiable} application that a notification has been received on a device.
     *
     * @param notificationId id of the received notification
     * @param deviceId       id returned by the server after registering the device
     * @param callback       the callback used to deliver the result of the operation
     */
    public void markNotificationReceived(@NonNull String notificationId, @NonNull String deviceId, @NonNull final
    NotifiableCallback<Object> callback) {
        mRestManager.markNotificationAsReceived(notificationId, deviceId, new NotifiableCallback<Object>() {

            @Override
            public void onSuccess(@NonNull Object o) {
                callback.onSuccess(o);
            }

            @Override
            public void onError(@NonNull String error) {
                callback.onError(error);
            }
        });
    }

    /**
     * This will notify the {@code Notifiable} application that a notification has been opened from a device.
     *
     * @param notificationId id of the received notification
     * @param deviceId       id returned by the server after registering the device
     * @param callback       the callback used to deliver the result of the operation
     */
    public void markNotificationOpened(@NonNull String notificationId, @NonNull String deviceId, @NonNull final
    NotifiableCallback<Object> callback) {
        mRestManager.markNotificationAsOpened(notificationId, deviceId, new NotifiableCallback<Object>() {

            @Override
            public void onSuccess(@NonNull Object o) {
                callback.onSuccess(o);
            }

            @Override
            public void onError(@NonNull String error) {
                callback.onError(error);
            }
        });
    }

    /**
     * Unassign a device from it's current user. This is basically registering the device as anonymous.
     * The device will still be able to receive notifications.
     *
     * @param deviceName  the name of the device we want to anonymise
     * @param deviceToken the GCM token for the device
     * @param callback    the callback used to deliver the result of the operation
     */
    public void unassignDeviceFromUser(@NonNull final String deviceName, @NonNull final String deviceToken,
            @NonNull final NotifiableCallback<NotifiableDevice> callback) {
        mRestManager.registerAnonymousDevice(deviceName, deviceToken, null, null,
                new NotifiableCallback<NotifiableDevice>() {
                    @Override
                    public void onSuccess(@NonNull NotifiableDevice device) {
                        device.setName(deviceName);
                        device.setToken(deviceToken);
                        callback.onSuccess(device);
                    }

                    @Override
                    public void onError(@NonNull String error) {
                        callback.onError(error);
                    }
                });
    }

    /**
     * Assign an anonymously registered device to a user.
     * <p>The device must have been registered before this method is called !</p>
     *
     * @param userName    the name of the user we want to assign the device to
     * @param deviceName  the name of the device we want to assign
     * @param deviceToken the GCM token for the device
     * @param callback    the callback used to deliver the result of the operation
     */
    public void assignDeviceToUser(@NonNull final String userName, @NonNull final String deviceName,
            @NonNull final String deviceToken,
            @NonNull final NotifiableCallback<NotifiableDevice> callback) {
        mRestManager.registerDeviceForUser(deviceName, deviceToken, userName, null, null,
                new NotifiableCallback<NotifiableDevice>() {
                    @Override
                    public void onSuccess(@NonNull NotifiableDevice device) {
                        device.setName(deviceName);
                        device.setUser(userName);
                        device.setToken(deviceToken);
                        callback.onSuccess(device);
                    }

                    @Override
                    public void onError(@NonNull String error) {
                        callback.onError(error);
                    }
                });
    }
}
