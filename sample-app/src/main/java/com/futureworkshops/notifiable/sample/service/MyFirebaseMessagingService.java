/*
 * Copyright (c) 2018 Future Workshops. All rights reserved.
 */

package com.futureworkshops.notifiable.sample.service;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.futureworkshops.notifiable.NotifiableManager;
import com.futureworkshops.notifiable.Utils;
import com.futureworkshops.notifiable.model.NotifiableCallback;
import com.futureworkshops.notifiable.model.NotifiableMessage;
import com.futureworkshops.notifiable.sample.BuildConfig;
import com.futureworkshops.notifiable.sample.Constants;
import com.futureworkshops.notifiable.sample.NotifiableActivity;
import com.futureworkshops.notifiable.sample.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {


    private static final String CHANNEL_ID = "General";

    private NotifiableManager mNotifiableManager;

    private SharedPreferences mSharedPrefs;

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel();
        }

        mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        mNotifiableManager = NotifiableManager.newInstance(BuildConfig.NOTIFIABLE_SERVER,
            BuildConfig.NOTIFIABLE_CLIENT_ID, BuildConfig.NOTIFIABLE_CLIENT_SECRET);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        sendNotification(remoteMessage.getData());
    }

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);

        Intent registrationComplete = new Intent(Constants.FIREBASE_NEW_TOKEN);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }


    /**
     * Create and show a simple notification containing the received GCM message
     *
     * @param data GCM message received.
     */
    private void sendNotification(Map<String, String> data) {
        final NotifiableMessage notification = Utils.createNotificationFromMap(data);

        markNotificationAsReceived(notification);

        Intent intent = new Intent(this, NotifiableActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(Constants.NOTIFICATION, notification);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
            PendingIntent.FLAG_UPDATE_CURRENT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.cast_ic_notification_0)
            .setContentTitle(notification.getTitle())
            .setContentText(notification.getMessage())
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(notification.getNotificationId(), notificationBuilder.build());
    }

    private void markNotificationAsReceived(NotifiableMessage notification) {
        int deviceId = mSharedPrefs.getInt(Constants.NOTIFIABLE_DEVICE_ID, -1);

        mNotifiableManager.markNotificationReceived(String.valueOf(notification.getNotificationId()),
            String.valueOf(deviceId), new NotifiableCallback<Object>() {
                @Override
                public void onSuccess(@NonNull Object ret) {
                    Log.d("NotificationService", "notification marked as received");
                }

                @Override
                public void onError(@NonNull String error) {
                    Log.e("NotificationService", "Notification delivery error : " + error);

                }
            });
    }

    @RequiresApi(api = VERSION_CODES.O)
    private void createNotificationChannel() {
        String name = "Sample";
        String description = "Sample app general notifications";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
        channel.setDescription(description);

        NotificationManager notificationManager =
            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(channel);
    }
}
