/*
 * Copyright ©  2018 - 2019 FutureWorkshops. All rights reserved.
 */

package com.futureworkshops.notifiable.sample.presentation.notification


import android.os.Bundle
import android.view.HapticFeedbackConstants
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.futureworkshops.notifiable.rx.model.NotifiableMessage
import com.futureworkshops.notifiable.sample.R
import com.stelianmorariu.antrics.domain.dagger.Injectable
import timber.log.Timber
import javax.inject.Inject

class NotificationActivity : AppCompatActivity(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    lateinit var viewModel: NotificationViewModel

    private lateinit var backBtn: ImageView
    private lateinit var statusProgres: ProgressBar
    private lateinit var notificationStatusTv: TextView
    private lateinit var notificationTitleTv: TextView
    private lateinit var notificationMessageTv: TextView

    private var notifiableMessage: NotifiableMessage? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Timber.e(" notification activity launched")

        setContentView(R.layout.activity_notification)

        val rootLayout: ConstraintLayout = findViewById(R.id.rootLayout)
        rootLayout.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION

        try {
            notifiableMessage = intent.getSerializableExtra(NOTIFICATION) as NotifiableMessage
        } catch (e: Exception) {
            Timber.e(e)
            showNotificationErrorDialog()
        }

        backBtn = findViewById(R.id.back_btn)
        backBtn.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
            finish()
        }

        notificationTitleTv = findViewById(R.id.notification_title_tv)
        statusProgres = findViewById(R.id.progress_circular)
        notificationStatusTv = findViewById(R.id.status_value_tv)
        notificationMessageTv = findViewById(R.id.message_value_tv)

        viewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(NotificationViewModel::class.java)


        viewModel.viewState.observe(this, Observer { viewState ->
            updateUiState(viewState)
        })

    }

    override fun onStart() {
        super.onStart()

        notifiableMessage?.let {
            viewModel.setNotification(it)
        }
    }

    private fun updateUiState(viewState: NotificationViewState) {

        when {
            viewState.displayNotification -> {
                notificationTitleTv.text =
                    viewState.notification?.title ?: getString(R.string.lbl_missing_value)
                notificationMessageTv.text =
                    viewState.notification?.message ?: getString(R.string.lbl_missing_value)

            }
            viewState.isMarkingNotification -> {
                notificationStatusTv.text = ""
                statusProgres.visibility = View.VISIBLE
            }
            viewState.isNotificationMarked -> {
                statusProgres.visibility = View.INVISIBLE
                notificationStatusTv.text = getString(R.string.msg_status_opened)
            }
            viewState.hasError -> {

            }
        }
    }


    private fun showNotificationErrorDialog() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.title_err_notification))
            .setMessage(getString(R.string.err_extracting_notification))
            .setCancelable(false)
            .setPositiveButton(getString(R.string.action_ok)) { _, _ -> finish() }
    }


    companion object {
        const val NOTIFICATION = "notification"
    }

}