/*
 * Copyright ©  2018 - 2019 FutureWorkshops. All rights reserved.
 */

package com.futureworkshops.notifiable.sample.presentation.demo

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.HapticFeedbackConstants
import android.view.View
import android.widget.ProgressBar
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.updatePadding
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.transition.TransitionManager
import com.futureworkshops.notifiable.sample.BuildConfig
import com.futureworkshops.notifiable.sample.R
import com.futureworkshops.notifiable.sample.presentation.commons.*
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.stelianmorariu.antrics.domain.dagger.Injectable
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class DemoActivity : AppCompatActivity(), Injectable, View.OnClickListener {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    lateinit var viewModel: DemoViewModel

    private lateinit var rootLayout: MotionLayout
    private lateinit var contentCard: MaterialCardView
    private lateinit var contentLayout: ConstraintLayout
    private lateinit var statusTv: AppCompatTextView
    private lateinit var versionTv: AppCompatTextView
    private lateinit var deviceNameEt: TextInputEditText
    private lateinit var userNameEt: TextInputEditText
    private lateinit var localeEt: TextInputEditText
    private lateinit var registerBtn: MaterialButton
    private lateinit var updateBtn: MaterialButton
    private lateinit var unregisterBtn: MaterialButton
    private lateinit var updateProgress: ProgressBar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // override the default transitions
        overridePendingTransition(R.anim.anim_no_translate, R.anim.anim_no_translate)

        setContentView(R.layout.activity_demo)

        rootLayout = findViewById(R.id.rootLayout)
        rootLayout.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION

        setupMotionLayoutTriggers()

        contentCard = findViewById(R.id.state_card)

        contentLayout = findViewById(R.id.demo_content_motion_layout)

        versionTv = findViewById(R.id.version_tv)
        versionTv.text =
            getString(R.string.lbl_app_version, BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE)

        versionTv.doOnApplyWindowInsets { view, windowInsets, initialPadding ->
            view.updatePadding(
                bottom = initialPadding.bottom + windowInsets.systemWindowInsetBottom
            )
        }

        statusTv = findViewById(R.id.status_tv)

        deviceNameEt = findViewById(R.id.device_name_et)
        deviceNameEt.onTextChanged { _, _, _, _ -> enableUpdateButton() }

        userNameEt = findViewById(R.id.user_name_et)
        userNameEt.onTextChanged { _, _, _, _ -> enableUpdateButton() }

        localeEt = findViewById(R.id.locale_et)
        localeEt.setOnClickListener { v ->

            viewModel.viewState.value?.notifiableDevice?.let {
                v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                showLocaleDialog(it.locale)
            }
        }

        registerBtn = findViewById(R.id.register_btn)
        registerBtn.setOnClickListener(this)

        updateBtn = findViewById(R.id.update_btn)
        updateBtn.setOnClickListener(this)

        updateProgress = findViewById(R.id.device_update_progress)

        unregisterBtn = findViewById(R.id.unregister_btn)
        unregisterBtn.setOnClickListener(this)

        viewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(DemoViewModel::class.java)


        viewModel.viewState.observe(this, Observer { viewState ->
            updateUiState(viewState)
        })

        rootLayout.transitionToEnd()

    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    override fun onClick(v: View) {
        v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
        rootLayout.requestFocus()

        when (v.id) {
            R.id.register_btn -> showRegisterDeviceDialog()
            R.id.update_btn -> updateDeviceInfo()
            R.id.unregister_btn -> showUnregisterDeviceDialog()
        }
    }

    /**
     * Default transition applied to the MotionLayout is the first transition defined in the
     * motion scene file([@id/splash_slide_transition])
     */
    private fun setupMotionLayoutTriggers() {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        rootLayout.setTransitionListener(object : MotionLayout.TransitionListener {
            override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {

            }

            override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {

            }

            override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) {

            }

            /**
             * @param [currentId] - the transition ID currently reached
             */
            override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {
                if (currentId == R.id.cs_splash__slide_in_finish) {
                    viewModel.checkNotifiableStatus()
                }

            }

        })

    }

    private fun updateUiState(viewState: DemoState) {

        when {
            viewState.isCheckingNotifiableState -> {
                updateConstraintSet(R.layout.layout_demo_content, contentLayout)
            }
            viewState.deviceRegistered -> {
                updateConstraintSet(R.layout.layout_demo_content_registered, contentLayout)
                statusTv.text = getString(R.string.lbl_state_registered)
                statusTv.setTextColor(this.getPrimaryColour())
                displayDeviceInfo(viewState)
                unregisterBtn.isEnabled = true
            }
            viewState.deviceNotRegistered -> {
                updateConstraintSet(R.layout.layout_demo_content_not_registered, contentLayout)

                statusTv.text = getString(R.string.lbl_state_not_registered)
                statusTv.setTextColor(this.getOnSurfaceColour())

                deviceNameEt.clear()
                userNameEt.clear()
                localeEt.clear()
            }
            viewState.isUpdating -> {
                updateProgress.visibility = View.VISIBLE
                updateBtn.visibility = View.INVISIBLE
            }
            viewState.isUnregistering -> {
                updateProgress.visibility = View.INVISIBLE
                updateBtn.isEnabled = false
                unregisterBtn.isEnabled = false
            }
            viewState.deviceInfoUpdated -> {
                updateProgress.visibility = View.INVISIBLE
                updateBtn.visibility = View.VISIBLE
                displayDeviceInfo(viewState)
                unregisterBtn.isEnabled = true
            }
            viewState.hasError -> {
                handleError(viewState.error)
            }
        }
    }

    private fun handleError(error: DemoState.Error) {
        when (error) {
            is DemoState.Error.GoogleServicesError -> {
                val apiAvailability = GoogleApiAvailability.getInstance()
                if (apiAvailability.isUserResolvableError(error.errorCode)) {
                    apiAvailability.getErrorDialog(
                        this,
                        error.errorCode,
                        PLAY_SERVICES_RESOLUTION_REQUEST
                    )
                        .show()
                }
            }

            is DemoState.Error.Generic -> showSnackbar(error.toString())
        }
    }

    private fun displayDeviceInfo(viewState: DemoState) {
        deviceNameEt.setText(viewState.notifiableDevice?.name)
        userNameEt.setText(viewState.notifiableDevice?.user)
        localeEt.setText(viewState.notifiableDevice?.locale?.displayName.toString())
        disableUpdateButton()
    }


    private fun updateConstraintSet(@LayoutRes id: Int, target: ConstraintLayout) {
        val newConstraintSet = ConstraintSet()
        newConstraintSet.clone(this, id)
        newConstraintSet.applyTo(target)
        TransitionManager.beginDelayedTransition(target)
    }

    private fun enableUpdateButton() {
        updateBtn.isEnabled = true
    }

    private fun disableUpdateButton() {
        updateBtn.isEnabled = false
    }

    private fun showLocaleDialog(currentLocale: Locale) {
        val availableLocales = Locale.getAvailableLocales()
        val localeNames = ArrayList<String>()
        val currentLocaleDisplayName = currentLocale.displayName
        var selected = 0

        for (i in availableLocales.indices) {
            val displayCountry = availableLocales[i].displayName

            if (displayCountry.isNotBlank()) {
                localeNames.add(displayCountry)

                if (displayCountry.equals(currentLocaleDisplayName, ignoreCase = true)) {
                    selected = i
                }
            }
        }

        AlertDialog.Builder(this)
            .setTitle(getString(R.string.title_choose_locale))
            .setSingleChoiceItems(localeNames.toTypedArray(), selected) { dialog, which ->
                val newSelection = (dialog as AlertDialog).listView.checkedItemPosition
                viewModel.updateDeviceInfo(locale = availableLocales[newSelection])
                dialog.dismiss()
            }
            .setPositiveButton(getString(R.string.action_ok), null)
            .setNegativeButton(getString(R.string.action_cancel), null).show()
    }

    private fun showRegisterDeviceDialog() {
        val view = layoutInflater.inflate(R.layout.dlg_register, null)

        val userlayout = view.findViewById<TextInputLayout>(R.id.user_layout)
        val userAlias = view.findViewById<TextInputEditText>(R.id.user_et)
        val devicelayout = view.findViewById<TextInputLayout>(R.id.device_layout)
        val device = view.findViewById<TextInputEditText>(R.id.device_et)
        device.setText(Build.MODEL.toString())

        val dialog = AlertDialog.Builder(this)
            .setTitle(getString(R.string.title_register_device))
            .setView(view)
            .setCancelable(false)
            .setPositiveButton(getString(R.string.action_ok), null)
            .setNegativeButton(getString(R.string.action_cancel), null).show()

        //Overriding the handler immediately after showing the dialog in order to prevent it from dismissing on incomplete information
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener { v ->
            v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
            val user: String? = userAlias.text.toString()
            val deviceName = device.text.toString()

            if (checkUser(user, userlayout)) {
                viewModel.registerNotifiableDevice(user, deviceName)
                dialog.dismiss()
            }

        }

    }

    private fun updateDeviceInfo() {
        viewModel.updateDeviceInfo(
            deviceNameEt.text.toString(),
            userNameEt.text.toString()
        )
    }

    private fun showUnregisterDeviceDialog() {

        AlertDialog.Builder(this)
            .setTitle(getString(R.string.title_unregister_device))
            .setMessage(getString(R.string.msg_unregister_device_confirmation))
            .setCancelable(false)
            .setPositiveButton(getString(R.string.action_ok)) { _, _ -> viewModel.unregisterDevice() }
            .setNegativeButton(getString(R.string.action_cancel), null).show()

    }

    private fun showSnackbar(message: String) {
        val view = this@DemoActivity.window.decorView
            .findViewById<View>(android.R.id.content)
        Snackbar.make(
            view,
            message, Snackbar.LENGTH_SHORT
        ).show()
    }

    private fun checkUser(inputValue: String?, inputLayout: TextInputLayout): Boolean {
        return if (inputValue.isNullOrBlank()) {
            inputLayout.error = getString(R.string.err_user_required)
            false
        } else {
            inputLayout.error = null
            true
        }
    }

    companion object {
        private const val PLAY_SERVICES_RESOLUTION_REQUEST = 9000

        fun newIntent(context: Context): Intent {
            return Intent(context, DemoActivity::class.java)
        }
    }


}
