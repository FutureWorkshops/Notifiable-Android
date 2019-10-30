/*
 * Copyright ©  2018 - 2019 FutureWorkshops. All rights reserved.
 */

package com.futureworkshops.notifiable.sample.presentation.voip

import android.app.KeyguardManager
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.view.HapticFeedbackConstants
import android.view.WindowManager
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import timber.log.Timber


class IncomingCallActivity : AppCompatActivity() {

    private lateinit var answerBtn: Button
    private lateinit var rejectBtn: Button
    private var activeCallNotificationId: Int = 0

    private lateinit var incomingCallRingtone: Ringtone
    private lateinit var systemNotificationManager: NotificationManager


    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.futureworkshops.notifiable.sample.R.layout.activity_incoming_call)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)

            val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
            keyguardManager.requestDismissKeyguard(this, null)
        } else {
            this.window.addFlags(
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                        or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
            )
        }

        systemNotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        answerBtn = findViewById(com.futureworkshops.notifiable.sample.R.id.btn_answer)
        answerBtn.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
            stopRinging()
            startActivity(Intent(this, CallActivity::class.java))
            finish()
        }

        rejectBtn = findViewById(com.futureworkshops.notifiable.sample.R.id.btn_decline)
        rejectBtn.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
            stopRinging()
            systemNotificationManager.cancel(activeCallNotificationId)
            //  todo : cancel Jitsi call
            finish()
        }

        initRingtone()
        playRinging()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIncomingCallIntent(intent)
    }

    private fun handleIncomingCallIntent(intent: Intent?) {
        if (intent != null && intent.action != null) {
            if (intent.action == ACTION_INCOMING_CALL) {
                Timber.e("Receiving incoming voip call")
                playRinging()
                activeCallNotificationId = intent.getIntExtra(INCOMING_CALL_NOTIFICATION_ID, 0)
            } else if (intent.action == ACTION_CANCEL_CALL) {
                stopRinging()
                systemNotificationManager.cancel(activeCallNotificationId)
            }
        }
    }

    private fun stopRinging() {
        incomingCallRingtone.stop()

    }

    private fun playRinging() {
        if (!incomingCallRingtone.isPlaying) {
            incomingCallRingtone.play()
        }

    }

    private fun initRingtone() {
        val uri = RingtoneManager.getActualDefaultRingtoneUri(this, RingtoneManager.TYPE_RINGTONE)
        Timber.e(" The ringtone uri is: $uri")
        incomingCallRingtone = RingtoneManager.getRingtone(this, uri)

    }

    companion object {
        const val INCOMING_CALL_INVITE = "INCOMING_CALL_INVITE"
        const val CANCELLED_CALL_INVITE = "CANCELLED_CALL_INVITE"
        const val INCOMING_CALL_NOTIFICATION_ID = "INCOMING_CALL_NOTIFICATION_ID"
        const val ACTION_INCOMING_CALL = "ACTION_INCOMING_CALL"
        const val ACTION_CANCEL_CALL = "ACTION_CANCEL_CALL"
    }
}
