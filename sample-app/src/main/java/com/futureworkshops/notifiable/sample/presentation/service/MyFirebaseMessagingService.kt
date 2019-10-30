/*
 * Copyright ©  2018 - 2019 FutureWorkshops. All rights reserved.
 */

package com.futureworkshops.notifiable.sample.presentation.service


import android.R
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.os.Build.VERSION_CODES
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import com.futureworkshops.notifiable.rx.NotifiableManagerRx
import com.futureworkshops.notifiable.rx.internal.createNotificationFromMap
import com.futureworkshops.notifiable.rx.model.NotifiableMessage
import com.futureworkshops.notifiable.sample.presentation.notification.NotificationActivity
import com.futureworkshops.notifiable.sample.presentation.notification.NotificationActivity.Companion.NOTIFICATION
import com.futureworkshops.notifiable.sample.presentation.voip.IncomingCallActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.android.AndroidInjection
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject


class MyFirebaseMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var notifiableManagerRx: NotifiableManagerRx

    override fun onCreate() {
        AndroidInjection.inject(this)
        super.onCreate()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }

    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val notification = createNotificationFromMap(remoteMessage.data)


        if (notification.deviceProperties.containsKey("voipCall")) {
            showVoipNotification(notification)
            sendCallInviteToActivity(notification.notificationId)
        } else {
            showNotification(notification)
        }

    }

    override fun onNewToken(token: String?) {
        super.onNewToken(token)

        token?.let { newToken ->
            // update token if device is registered
            notifiableManagerRx.getRegisteredDevice()
                .subscribeOn(Schedulers.io())
                .subscribe { device, _ ->

                    device?.let {
                        if (it.token != newToken) {
                            notifiableManagerRx.updateDeviceInformation(token = newToken)
                                .subscribeOn(Schedulers.io())
                        }
                    }
                }
        }
    }


    /**
     * Create and show a simple notification containing the received GCM message
     *
     * @param data FCM message received.
     */
    private fun showNotification(notification: NotifiableMessage) {
        markNotificationAsReceived(notification)

        val intent = Intent(this, NotificationActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        intent.putExtra(NOTIFICATION, notification)


        // Create the TaskStackBuilder
        val resultPendingIntent: PendingIntent? = TaskStackBuilder.create(this).run {
            // Add the intent, which inflates the back stack
            addNextIntentWithParentStack(intent)
            // Get the PendingIntent containing the entire back stack
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(com.futureworkshops.notifiable.sample.R.drawable.ic_logo_light)
            .setContentTitle(notification.title)
            .setContentText(notification.message)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(resultPendingIntent)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.notify(notification.notificationId, notificationBuilder.build())
    }


    private fun showVoipNotification(notification: NotifiableMessage) {
        markNotificationAsReceived(notification)

        val intent = Intent(this, IncomingCallActivity::class.java)
        intent.action = IncomingCallActivity.ACTION_INCOMING_CALL
        intent.putExtra(
            IncomingCallActivity.INCOMING_CALL_NOTIFICATION_ID,
            notification.notificationId
        )
//        intent.putExtra(IncomingCallActivity.INCOMING_CALL_INVITE, callInvite)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this,
            notification.notificationId,
            intent,
            PendingIntent.FLAG_CANCEL_CURRENT
        )

        val extras = Bundle()
        extras.putInt(NOTIFICATION_ID_KEY, notification.notificationId)

        val notificationBuilder = NotificationCompat.Builder(this)
            .setSmallIcon(R.drawable.ic_menu_call)
            .setContentTitle("Notificable VoIP")
            .setContentText("The future is calling.")
            .setAutoCancel(true)
            .setExtras(extras)
            .setContentIntent(pendingIntent)
            .setColor(Color.rgb(214, 10, 37))

        //########################################################################################

//        val intent = Intent(this, NotificationActivity::class.java)
//        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//        intent.putExtra(NOTIFICATION, notification)


        // Create the TaskStackBuilder
//        val resultPendingIntent: PendingIntent? = TaskStackBuilder.create(this).run {
//            // Add the intent, which inflates the back stack
//            addNextIntentWithParentStack(intent)
//            // Get the PendingIntent containing the entire back stack
//            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
//        }


//        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
//            .setSmallIcon(com.futureworkshops.notifiable.sample.R.drawable.ic_logo_light)
//            .setContentTitle(notification.title)
//            .setContentText(notification.message)
//            .setAutoCancel(true)
//
//            .setContentIntent(resultPendingIntent)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.notify(notification.notificationId, notificationBuilder.build())
    }


    @SuppressLint("CheckResult")
    private fun markNotificationAsReceived(notification: NotifiableMessage) {
        notifiableManagerRx.markNotificationReceived(notification.notificationId.toString())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({}, { t -> Timber.e(t) })
    }

    @RequiresApi(api = VERSION_CODES.O)
    private fun createNotificationChannel() {
        val name = "Notifiable Sample"
        val description = "Notifiable sample app general notifications"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(CHANNEL_ID, name, importance)
        channel.description = description

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }


    /*
     * Send the CallInvite to the VoiceActivity. Start the activity if it is not running already.
     */
    private fun sendCallInviteToActivity(notificationId: Int) {
        val intent = Intent(this, IncomingCallActivity::class.java)
        intent.action = IncomingCallActivity.ACTION_INCOMING_CALL
        intent.putExtra(IncomingCallActivity.INCOMING_CALL_NOTIFICATION_ID, notificationId)
//        intent.putExtra(VoiceActivity.INCOMING_CALL_INVITE, callInvite)
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        this.startActivity(intent)
    }

    companion object {


        private const val CHANNEL_ID = "NotifiableSample"


        private const val NOTIFICATION_ID_KEY = "NOTIFICATION_ID"

    }
}
