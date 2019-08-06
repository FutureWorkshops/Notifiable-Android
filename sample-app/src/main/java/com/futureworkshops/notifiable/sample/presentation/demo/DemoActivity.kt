/*
 * Copyright ©  2018 - 2019 FutureWorkshops. All rights reserved.
 */

package com.futureworkshops.notifiable.sample.presentation.demo

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.view.HapticFeedbackConstants
import android.view.View
import android.widget.ProgressBar
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
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
import com.google.android.material.button.MaterialButton
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
        setContentView(R.layout.activity_demo)

        val rootLayout: ConstraintLayout = findViewById(R.id.rootLayout)
        rootLayout.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION


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

    }


    override fun onResume() {
        super.onResume()

        Handler().postDelayed({ viewModel.checkNotifiableStatus() }, 1000)
    }


    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    override fun onClick(v: View) {
        v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)

        when (v.id) {
            R.id.register_btn -> showRegisterDeviceDialog()
            R.id.update_btn -> updateDeviceInfo()
            R.id.unregister_btn -> showUnregisterDeviceDialog()
        }
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
            viewState.deviceInfoUpdated -> {
                updateProgress.visibility = View.INVISIBLE
                updateBtn.visibility = View.VISIBLE
                displayDeviceInfo(viewState)
            }
            viewState.hasError -> {

            }
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

//    private fun showUpdateDeviceDialog() {
//        val view = layoutInflater.inflate(R.layout.dlg_device_info, null)
//
//        val os = view.findViewById<EditText>(R.id.os_et)
//
//        val isEmulator = view.findViewById<CheckBox>(R.id.emulator_cb)
//
//        AlertDialog.Builder(this)
//            .setTitle(getString(R.string.title_update_device))
//            .setView(view)
//            .setCancelable(false)
//            .setPositiveButton(getString(R.string.action_ok)) { dialog1, which ->
//                val osVersion = os.text.toString()
//                val emulator = isEmulator.isChecked.toString()
//
//                updateDeviceInfo(osVersion, emulator)
//            }
//            .setNegativeButton(getString(R.string.action_cancel), null).show()
//
//    }

//    private fun showUpdateDeviceNameDialog() {
//        val view = layoutInflater.inflate(R.layout.dlg_text_input, null)
//
//        val layout = view.findViewById<TextInputLayout>(R.id.input_layout)
//        val inputEt = view.findViewById<EditText>(R.id.input_et)
//        inputEt.hint = getString(R.string.lbl_device_name)
//
//        val dialog = AlertDialog.Builder(this)
//            .setTitle(getString(R.string.title_update_device))
//            .setView(view)
//            .setCancelable(false)
//            .setPositiveButton(getString(R.string.action_ok), null)
//            .setNegativeButton(getString(R.string.action_cancel), null).show()
//
//        //Overriding the handler immediately after showing the dialog in order to prevent it from dismissing on incomplete information
//        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener { v ->
//            val name = inputEt.text.toString()
//
//            if (checkDeviceName(name, layout)) {
//                layout.error = null
//                updateDeviceName(name)
//                dialog.dismiss()
//            }
//        }
//    }

    //    private fun showUnassignDeviceConfirmationDialog() {
//        AlertDialog.Builder(this)
//            .setTitle(getString(R.string.title_unassign_device))
//            .setMessage(getString(R.string.msg_unassign_device_confirmation))
//            .setCancelable(false)
//            .setPositiveButton(getString(R.string.action_ok)) { dialog1, which -> unassignDevice() }
//            .setNegativeButton(getString(R.string.action_cancel), null).show()
//    }
//
//    private fun showAssignDeviceDialog() {
//        val view = layoutInflater.inflate(R.layout.dlg_text_input, null)
//
//        val layout = view.findViewById<TextInputLayout>(R.id.input_layout)
//        val inputEt = view.findViewById<EditText>(R.id.input_et)
//        inputEt.hint = getString(R.string.lbl_user_name)
//
//        val dialog = AlertDialog.Builder(this)
//            .setTitle(getString(R.string.title_update_device))
//            .setView(view)
//            .setCancelable(false)
//            .setPositiveButton(getString(R.string.action_ok), null)
//            .setNegativeButton(getString(R.string.action_cancel), null).show()
//
//        //Overriding the handler immediately after showing the dialog in order to prevent it from dismissing on incomplete information
//        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener { v ->
//            val name = inputEt.text.toString()
//
//            if (checkUser(name, layout)) {
//                layout.error = null
//                assignDeviceToUser(name)
//                dialog.dismiss()
//            }
//        }
//    }
//
    private fun showUnregisterDeviceDialog() {

        AlertDialog.Builder(this)
            .setTitle(getString(R.string.title_unregister_device))
            .setMessage(getString(R.string.msg_unregister_device_confirmation))
            .setCancelable(false)
            .setPositiveButton(getString(R.string.action_ok)) { _, _ -> viewModel.unregisterDevice() }
            .setNegativeButton(getString(R.string.action_cancel), null).show()

    }


//    @SuppressLint("CheckResult")
//    private fun updateDeviceInfo(osVersion: String, emulator: String) {
//        // create map with entered values
//        val customProperties = HashMap<String, String>()
//        customProperties[Constants.OS_PROPERTY] = osVersion
//        customProperties[Constants.IS_EMULATOR_PROPERTY] = emulator
//
//
//        mNotifiableManagerRx.updateDeviceInformation(
//            mDeviceId.toString(),
//            customProperties = customProperties
//        )
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe({
//
//                showSnackbar("Updated device properties")
//            },
//                { t ->
//                    Timber.e(t)
//                    showSnackbar(t.toString())
//                })
//    }

//    @SuppressLint("CheckResult")
//    private fun updateDeviceName(name: String) {
//        mNotifiableManagerRx.updateDeviceInformation(mDeviceId.toString(), deviceName = name)
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe({
//                mDeviceName = name
//                showSnackbar("Updated device name to $name")
//            },
//                { t ->
//                    Timber.e(t)
//                    showSnackbar(t.toString())
//                })
//
//    }
//
//    @SuppressLint("CheckResult")
//    private fun updateDeviceLocale(locale: Locale) {
//        mNotifiableManagerRx.updateDeviceInformation(mDeviceId.toString(), locale = locale)
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe({
//                mCurrentLocale = locale
//
//                showSnackbar("Updated device Locale to " + locale.displayName)
//            },
//                { t ->
//                    Timber.e(t)
//                    showSnackbar(t.toString())
//                })
//    }

//    private fun unassignDevice() {
//    }
//
//    @SuppressLint("CheckResult")
//    private fun assignDeviceToUser(user: String) {
//        mNotifiableManagerRx.updateDeviceInformation(mDeviceId.toString(), userName = user)
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe({
//                mDeviceUser = user
//                mState = NotifiableStates.REGISTERED_WITH_USER
//                updateUi()
//
//                showSnackbar("Device was assigned to " + mDeviceUser!!)
//            },
//                { t ->
//                    Timber.e(t)
//                    showSnackbar(t.toString())
//                })
//    }
//


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

    private fun checkDeviceName(inputValue: String, inputLayout: TextInputLayout): Boolean {
        if (TextUtils.isEmpty(inputValue)) {
            inputLayout.error = getString(R.string.err_device_name_required)
            return false
        } else {
            inputLayout.error = null
            return true
        }
    }

    companion object {

        private val PLAY_SERVICES_RESOLUTION_REQUEST = 9000
        private const val FIREBASE_NEW_TOKEN = "firebase_token"

        fun newIntent(context: Context): Intent {
            return Intent(context, DemoActivity::class.java)
        }

    }


}
