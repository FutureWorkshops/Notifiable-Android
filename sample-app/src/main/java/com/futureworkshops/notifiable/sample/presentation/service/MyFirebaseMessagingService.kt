/*
 * Copyright ©  2018 - 2019 FutureWorkshops. All rights reserved.
 */

package com.futureworkshops.notifiable.sample.presentation.service


import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.RingtoneManager
import android.os.Build
import android.os.Build.VERSION_CODES
import android.preference.PreferenceManager
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.futureworkshops.notifiable.rx.NotifiableManagerRx
import com.futureworkshops.notifiable.rx.internal.createNotificationFromMap
import com.futureworkshops.notifiable.rx.model.NotifiableMessage
import com.futureworkshops.notifiable.sample.BuildConfig
import com.futureworkshops.notifiable.sample.Constants
import com.futureworkshops.notifiable.sample.R
import com.futureworkshops.notifiable.sample.presentation.demo.DemoActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class MyFirebaseMessagingService : FirebaseMessagingService() {

    private lateinit var notifiableManagerRx: NotifiableManagerRx

    private var sharedPrefs: SharedPreferences? = null

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }

        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this)


        notifiableManagerRx = NotifiableManagerRx.Builder(this)
            .endpoint(BuildConfig.NOTIFIABLE_SERVER)
            .credentials(
                BuildConfig.NOTIFIABLE_CLIENT_ID,
                BuildConfig.NOTIFIABLE_CLIENT_SECRET
            )
            .debug(BuildConfig.DEBUG)
            .build()

    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        sendNotification(remoteMessage.data)
    }

    override fun onNewToken(token: String?) {
        super.onNewToken(token)

        val registrationComplete = Intent(Constants.FIREBASE_NEW_TOKEN)
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete)
    }


    /**
     * Create and show a simple notification containing the received GCM message
     *
     * @param data GCM message received.
     */
    private fun sendNotification(data: Map<String, String>) {
        val notification = createNotificationFromMap(data)

        markNotificationAsReceived(notification)

        val intent = Intent(this, DemoActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        intent.putExtra(Constants.NOTIFICATION, notification)

        val pendingIntent = PendingIntent.getActivity(
            this, 0 /* Request code */, intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.cast_ic_notification_0)
            .setContentTitle(notification.title)
            .setContentText(notification.message)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.notify(notification.notificationId, notificationBuilder.build())
    }

    @SuppressLint("CheckResult")
    private fun markNotificationAsReceived(notification: NotifiableMessage) {
        val deviceId = sharedPrefs!!.getInt(Constants.NOTIFIABLE_DEVICE_ID, -1)


        notifiableManagerRx.markNotificationReceived(notification.notificationId.toString())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({}, { t -> Timber.e(t) })
    }

    @RequiresApi(api = VERSION_CODES.O)
    private fun createNotificationChannel() {
        val name = "Sample"
        val description = "Sample app general notifications"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(CHANNEL_ID, name, importance)
        channel.description = description

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    companion object {


        private val CHANNEL_ID = "General"
    }
}
