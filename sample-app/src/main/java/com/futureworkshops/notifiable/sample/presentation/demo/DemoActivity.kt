/*
 * Copyright ©  2018 - 2019 FutureWorkshops. All rights reserved.
 */

package com.futureworkshops.notifiable.sample.presentation.demo

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.view.HapticFeedbackConstants
import android.view.View
import android.widget.EditText
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
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.transition.TransitionManager
import com.futureworkshops.notifiable.sample.BuildConfig
import com.futureworkshops.notifiable.sample.R
import com.futureworkshops.notifiable.sample.presentation.commons.doOnApplyWindowInsets
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.stelianmorariu.antrics.domain.dagger.Injectable
import javax.inject.Inject

class DemoActivity : AppCompatActivity(), Injectable, View.OnClickListener {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    lateinit var viewModel: DemoViewModel

    private var mRegistrationBroadcastReceiver: BroadcastReceiver? = null
    private var isReceiverRegistered: Boolean = false

    private lateinit var contentLayout: ConstraintLayout
    private lateinit var statusTv: AppCompatTextView
    private lateinit var versionTv: AppCompatTextView
    private lateinit var deviceNameEt: TextInputEditText
    private lateinit var userNameEt: TextInputEditText
    private lateinit var localeEt: TextInputEditText
    private lateinit var registerBtn: MaterialButton
    private lateinit var unregisterBtn: MaterialButton


//    private var mRegisterNotifiableButton: Button? = null
//    private var mRegisterAnonymousNotifiableButton: Button? = null
//    private var mUpdateDeviceInfoButton: Button? = null
//    private var mUpdateDeviceNameButton: Button? = null
//    private var mUpdateDeviceLocaleButton: Button? = null
//    private var mAssignToUserButton: Button? = null
//    private var mUnassignFromUserButton: Button? = null
//    private var mUnregisterDeviceButton: Button? = null
//    private var mOpenNotificationButton: Button? = null

//    private var mGcmToken: String? = null
//    private var mDeviceId: Int = 0
//    private var mDeviceUser: String? = null
//    private var mDeviceName: String? = null
//    private lateinit var mNotifiableManagerRx: NotifiableManagerRx
//    private var mLatestNotification: NotifiableMessage? = null

//    private var mState: NotifiableStates? = null
//    private var mCurrentLocale: Locale? = null


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
        userNameEt = findViewById(R.id.user_name_et)
        localeEt = findViewById(R.id.locale_et)

        registerBtn = findViewById(R.id.register_btn)
        registerBtn.setOnClickListener(this)

        unregisterBtn = findViewById(R.id.unregister_btn)
        unregisterBtn.setOnClickListener(this)

        viewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(DemoViewModel::class.java)


        viewModel.viewState.observe(this, Observer { viewState ->
            updateUiState(viewState)
        })

//        mRegisterNotifiableButton = findViewById(R.id.btn_register_with_name)
//        mRegisterNotifiableButton!!.setOnClickListener(this)
//
//        mRegisterAnonymousNotifiableButton = findViewById(R.id.btn_register_anonymously)
//        mRegisterAnonymousNotifiableButton!!.setOnClickListener(this)
//
//        mUpdateDeviceInfoButton = findViewById(R.id.btn_update_device_info)
//        mUpdateDeviceInfoButton!!.setOnClickListener(this)
//
//        mUpdateDeviceNameButton = findViewById(R.id.btn_update_device_name)
//        mUpdateDeviceNameButton!!.setOnClickListener(this)
//
//        mUpdateDeviceLocaleButton = findViewById(R.id.btn_update_device_locale)
//        mUpdateDeviceLocaleButton!!.setOnClickListener(this)
//
//        mAssignToUserButton = findViewById(R.id.btn_assign_device_to_user)
//        mAssignToUserButton!!.setOnClickListener(this)
//
//        mUnassignFromUserButton = findViewById(R.id.btn_unassign_device_from_user)
//        mUnassignFromUserButton!!.setOnClickListener(this)
//
//        mUnregisterDeviceButton = findViewById(R.id.btn_unregister_device)
//        mUnregisterDeviceButton!!.setOnClickListener(this)
//
//        mOpenNotificationButton = findViewById(R.id.btn_mark_notification)
//        mOpenNotificationButton!!.setOnClickListener(this)

//        mCurrentLocale = Locale.UK
//        mDeviceId = mSharedPrefs!!.getInt(Constants.NOTIFIABLE_DEVICE_ID, -1)


//        mNotifiableManagerRx = NotifiableManagerRx.Builder(this)
//            .endpoint(BuildConfig.NOTIFIABLE_SERVER)
//            .credentials(
//                BuildConfig.NOTIFIABLE_CLIENT_ID,
//                BuildConfig.NOTIFIABLE_CLIENT_SECRET
//            )
//            .debug(BuildConfig.DEBUG)
//            .build()

        mRegistrationBroadcastReceiver = object : BroadcastReceiver() {

            override fun onReceive(context: Context, intent: Intent) {
//                getTokenAsync()
                // If device has already been registered, update the token
//                if (mDeviceName != null) {
//                    updateDeviceToken()
//                }
            }
        }
    }


    private fun updateUiState(viewState: DemoState) {

        when {
            viewState.isCheckingNotifiableState -> {
                updateConstraintSet(R.layout.layout_demo_content, contentLayout)
            }
            viewState.deviceRegistered -> {
//                contentLayout.transitionToState(R.id.motion_state_device_registered)
                updateConstraintSet(R.layout.layout_demo_content_registered, contentLayout)
                statusTv.text = getString(R.string.lbl_state_registered)
                deviceNameEt.setText(viewState.notifiableDevice?.name)
                userNameEt.setText(viewState.notifiableDevice?.user)
                localeEt.setText(viewState.notifiableDevice?.locale.toString())
            }
            viewState.deviceNotRegistered -> {
                updateConstraintSet(R.layout.layout_demo_content_not_registered, contentLayout)
//                contentLayout.transitionToState(R.id.motion_state_device_not_registered)

                statusTv.text = getString(R.string.lbl_state_not_registered)
            }
            viewState.isUpdating -> {

            }
            viewState.hasError -> {

            }
        }
    }


    fun updateConstraintSet(@LayoutRes id: Int, target: ConstraintLayout) {
        val newConstraintSet = ConstraintSet()
        newConstraintSet.clone(this, id)
        newConstraintSet.applyTo(target)
        TransitionManager.beginDelayedTransition(target)
    }

//    private fun getTokenAsync() {
//        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener { instanceIdResult ->
//            mGcmToken = instanceIdResult.token
//        }
//    }


//    override fun onNewIntent(intent: Intent) {
//        super.onNewIntent(intent)
//        try {
//            mLatestNotification =
//                intent.getSerializableExtra(Constants.NOTIFICATION) as NotifiableMessage
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//
//        if (mLatestNotification != null) {
//            // check that notification has id !
//            if (mLatestNotification!!.notificationId != 0) {
////                mOpenNotificationButton!!.visibility = View.VISIBLE
//            } else {
//                showSnackbar("Received notification without id !")
//            }
//        }
//
//    }

    override fun onResume() {
        super.onResume()
//        registerReceiver()

        Handler().postDelayed({ viewModel.checkNotifiableStatus() }, 1000)
    }

    override fun onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver!!)
        isReceiverRegistered = false
        super.onPause()
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
//            R.id.btn_register_anonymously -> showRegisterDeviceDialog(true)
//            R.id.btn_update_device_info -> showUpdateDeviceDialog()
//            R.id.btn_update_device_name -> showUpdateDeviceNameDialog()
//            R.id.btn_update_device_locale -> showUpdateDeviceLocaleDialog()
//            R.id.btn_assign_device_to_user -> showAssignDeviceDialog()
//            R.id.btn_unassign_device_from_user -> showUnassignDeviceConfirmationDialog()
            R.id.unregister_btn -> showUnregisterDeviceDialog()
//            R.id.btn_mark_notification -> markNotificationClicked()
        }
    }


//    @SuppressLint("CheckResult")
//    private fun markNotificationClicked() {
//        mNotifiableManagerRx.markNotificationOpened(
//            mLatestNotification!!.notificationId.toString()
//        )
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe({
//                showSnackbar("Notification marked as open")
//                mLatestNotification = null
////                mOpenNotificationButton!!.visibility = View.GONE
//            }, { t ->
//                Timber.e(t)
//                showSnackbar(t.toString())
//            })
//
//    }

//    @SuppressLint("CheckResult")
//    private fun updateDeviceToken() {
//        mNotifiableManagerRx.updateDeviceInformation(token = mGcmToken)
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe({
//                showSnackbar("FCM token has been refreshed")
//            },
//                { t ->
//                    Timber.e(t)
//                    showSnackbar(t.toString())
//                })
//
//    }

//    private fun showUpdateDeviceLocaleDialog() {
//        val availableLocales = Locale.getAvailableLocales()
//        val localeNames = ArrayList<String>()
//        val currentLocaleDisplayName = mCurrentLocale!!.displayName
//        var selected = 0
//
//        for (i in availableLocales.indices) {
//            val displayCountry = availableLocales[i].displayName
//
//            if (!TextUtils.isEmpty(displayCountry)) {
//                localeNames.add(displayCountry)
//
//                if (displayCountry.equals(currentLocaleDisplayName, ignoreCase = true)) {
//                    selected = i
//                }
//            }
//        }
//
//        AlertDialog.Builder(this)
//            .setTitle(getString(R.string.title_user_devices))
//            .setSingleChoiceItems(localeNames.toTypedArray<String>(), selected) { dialog, which ->
//                val newSelection = (dialog as AlertDialog).listView.checkedItemPosition
//                updateDeviceLocale(availableLocales[newSelection])
//                dialog.dismiss()
//            }
//            .setPositiveButton(getString(R.string.action_ok), null)
//            .setNegativeButton(getString(R.string.action_cancel), null).show()
//    }

    private fun showRegisterDeviceDialog() {
        val view = layoutInflater.inflate(R.layout.dlg_register, null)

        val userlayout = view.findViewById<TextInputLayout>(R.id.user_layout)
        val username = view.findViewById<EditText>(R.id.user_et)
        val devicelayout = view.findViewById<TextInputLayout>(R.id.device_layout)
        val device = view.findViewById<EditText>(R.id.device_et)


        val dialog = AlertDialog.Builder(this)
            .setTitle(getString(R.string.title_register_device))
            .setView(view)
            .setCancelable(false)
            .setPositiveButton(getString(R.string.action_ok), null)
            .setNegativeButton(getString(R.string.action_cancel), null).show()

        //Overriding the handler immediately after showing the dialog in order to prevent it from dismissing on incomplete information
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener { v ->
            v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
            val user: String? = username.text.toString()
            val deviceName = device.text.toString()

            viewModel.registerNotifiableDevice(user, deviceName)
            dialog.dismiss()
        }

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

//
//    private fun registerReceiver() {
//        if (!isReceiverRegistered) {
//            LocalBroadcastManager.getInstance(this).registerReceiver(
//                mRegistrationBroadcastReceiver!!,
//                IntentFilter(Constants.FIREBASE_NEW_TOKEN)
//            )
//            isReceiverRegistered = true
//        }
//    }

    private fun updateUi() {
//        when (mState) {
//            NotifiableStates.UNREGISTERED -> {
//                mRegisterNotifiableButton!!.isEnabled = true
//                mRegisterAnonymousNotifiableButton!!.isEnabled = true
//
//                mUpdateDeviceInfoButton!!.visibility = View.GONE
//                mUnregisterDeviceButton!!.visibility = View.GONE
//                mUpdateDeviceNameButton!!.visibility = View.GONE
//                mUpdateDeviceLocaleButton!!.visibility = View.GONE
//                mUnassignFromUserButton!!.visibility = View.GONE
//            }
//            NotifiableStates.REGISTERED_WITH_USER -> {
//                mRegisterNotifiableButton!!.isEnabled = false
//                mRegisterAnonymousNotifiableButton!!.isEnabled = false
//
//                mAssignToUserButton!!.visibility = View.GONE
//
//                mUpdateDeviceInfoButton!!.visibility = View.VISIBLE
//                mUnregisterDeviceButton!!.visibility = View.VISIBLE
//
//                mUpdateDeviceNameButton!!.visibility = View.VISIBLE
//                mUpdateDeviceLocaleButton!!.visibility = View.VISIBLE
//                mUnassignFromUserButton!!.visibility = View.VISIBLE
//            }
//            NotifiableStates.REGISTERED_ANONYMOUSLY -> {
//                mRegisterNotifiableButton!!.isEnabled = false
//                mRegisterAnonymousNotifiableButton!!.isEnabled = false
//
//                mUnassignFromUserButton!!.visibility = View.GONE
//
//                mUpdateDeviceInfoButton!!.visibility = View.VISIBLE
//                mUnregisterDeviceButton!!.visibility = View.VISIBLE
//
//                mUpdateDeviceNameButton!!.visibility = View.VISIBLE
//                mUpdateDeviceLocaleButton!!.visibility = View.VISIBLE
//                mAssignToUserButton!!.visibility = View.VISIBLE
//            }
//        }
    }


    private fun showSnackbar(message: String) {
        val view = this@DemoActivity.window.decorView
            .findViewById<View>(android.R.id.content)
        Snackbar.make(
            view,
            message, Snackbar.LENGTH_SHORT
        ).show()
    }

    private fun checkUser(inputValue: String, inputLayout: TextInputLayout): Boolean {
        if (TextUtils.isEmpty(inputValue)) {
            inputLayout.error = getString(R.string.err_user_required)
            return false
        } else {
            inputLayout.error = null
            return true
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
        private val TAG = DemoActivity::class.java!!.getSimpleName()

        fun newIntent(context: Context): Intent {
            return Intent(context, DemoActivity::class.java)
        }

    }


}
