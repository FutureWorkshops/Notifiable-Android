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
import android.os.PowerManager
import android.view.HapticFeedbackConstants
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import timber.log.Timber


class IncomingCallActivity : AppCompatActivity() {

    private lateinit var answerBtn: FloatingActionButton
    private lateinit var rejectBtn: FloatingActionButton
    private var activeCallNotificationId: Int = 0

    private lateinit var incomingCallRingtone: Ringtone
    private lateinit var systemNotificationManager: NotificationManager

    // PowerManager
    private lateinit var powerManager: PowerManager
    private var wakeLock: PowerManager.WakeLock? = null
    private var proximityField: Int = 0x00000020


    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.futureworkshops.notifiable.sample.R.layout.activity_incoming_call)

        // ############################################################################################################################################

        // This activity needs to show even if the screen is off or locked
        val window = window
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val km = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
            km.requestDismissKeyguard(this, null)
        } else {
            window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD)
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        // ############################################################################################################################################

        // Initiate PowerManager and WakeLock (turn screen on/off according to distance from face)
        try {
            proximityField =
                PowerManager::class.java.getField("PROXIMITY_SCREEN_OFF_WAKE_LOCK").getInt(null)
        } catch (ignored: Throwable) {
        }

        powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(proximityField, localClassName)

        // ############################################################################################################################################

        systemNotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        answerBtn = findViewById(com.futureworkshops.notifiable.sample.R.id.answer_btn)
        answerBtn.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
            stopRinging()
            startActivity(Intent(this, CallActivity::class.java))
            finish()
        }

        rejectBtn = findViewById(com.futureworkshops.notifiable.sample.R.id.reject_btn)
        rejectBtn.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
            stopRinging()
            releaseWakeLock()
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

    override fun onDestroy() {
        super.onDestroy()
        releaseWakeLock()
    }

    private fun handleIncomingCallIntent(intent: Intent?) {
        if (intent != null && intent.action != null) {
            if (intent.action == ACTION_INCOMING_CALL) {
                Timber.e("Receiving incoming voip call")
                acquireWakeLock()
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

    /**
     * Releases the wake lock
     */
    private fun releaseWakeLock() {

        wakeLock?.let {
            if (it.isHeld) {
                it.release()
            }
        }
    }

    /**
     * Acquires the wake lock
     */
    private fun acquireWakeLock() {
        wakeLock?.let {
            if (!it.isHeld) {
                it.acquire(10 * 60 * 1000L /*10 minutes*/)
            }
        }

    }

    companion object {
        const val INCOMING_CALL_INVITE = "INCOMING_CALL_INVITE"
        const val CANCELLED_CALL_INVITE = "CANCELLED_CALL_INVITE"
        const val INCOMING_CALL_NOTIFICATION_ID = "INCOMING_CALL_NOTIFICATION_ID"
        const val ACTION_INCOMING_CALL = "ACTION_INCOMING_CALL"
        const val ACTION_CANCEL_CALL = "ACTION_CANCEL_CALL"
    }
}
