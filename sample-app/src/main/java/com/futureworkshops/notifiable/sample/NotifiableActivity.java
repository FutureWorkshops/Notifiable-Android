/*
 * Copyright (c) 2018 Future Workshops. All rights reserved.
 */

package com.futureworkshops.notifiable.sample;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.futureworkshops.notifiable.NotifiableManager;
import com.futureworkshops.notifiable.model.NotifiableCallback;
import com.futureworkshops.notifiable.model.NotifiableDevice;
import com.futureworkshops.notifiable.model.NotifiableMessage;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NotifiableActivity extends AppCompatActivity {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = NotifiableActivity.class.getSimpleName();

    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private boolean isReceiverRegistered;

    private SharedPreferences mSharedPrefs;

    @BindView(R.id.btn_register_with_name)
    Button mRegisterNotifiableButton;

    @BindView(R.id.btn_register_anonymously)
    Button mRegisterAnonymousNotifiableButton;

    @BindView(R.id.btn_update_device_info)
    Button mUpdateDeviceInfoButton;

    @BindView(R.id.btn_update_device_name)
    Button mUpdateDeviceNameButton;

    @BindView(R.id.btn_update_device_locale)
    Button mUpdateDeviceLocaleButton;

    @BindView(R.id.btn_assign_device_to_user)
    Button mAssignToUserButton;

    @BindView(R.id.btn_unassign_device_from_user)
    Button mUnassignFromUserButton;

    @BindView(R.id.btn_unregister_device)
    Button mUnregisterDeviceButton;

    @BindView(R.id.btn_mark_notification)
    Button mOpenNotificationButton;

    private String mGcmToken;
    private int mDeviceId;
    private String mDeviceUser;
    private String mDeviceName;
    private NotifiableManager mNotifiableManager;
    private NotifiableMessage mLatestNotification;

    private NotifiableStates mState;
    private Locale mCurrentLocale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifiable);

        ButterKnife.bind(this);

        mCurrentLocale = Locale.UK;

        mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        mDeviceId = mSharedPrefs.getInt(Constants.NOTIFIABLE_DEVICE_ID, -1);

        mNotifiableManager = NotifiableManager.newInstance(BuildConfig.NOTIFIABLE_SERVER,
            BuildConfig.NOTIFIABLE_CLIENT_ID, BuildConfig.NOTIFIABLE_CLIENT_SECRET);

        checkPlayServices();

        getTokenAsync();

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                getTokenAsync();
                // If device has already been registered, update the token
                if (mDeviceName != null) {
                    updateDeviceToken();
                }
            }
        };
    }

    private void getTokenAsync() {
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(
            instanceIdResult -> mGcmToken = instanceIdResult.getToken()
        );
    }


    @Override
    protected void onNewIntent(Intent intent) {
        try {
            mLatestNotification = (NotifiableMessage) intent.getSerializableExtra(Constants.NOTIFICATION);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (mLatestNotification != null) {
            // check that notification has id !
            if (mLatestNotification.getNotificationId() != 0) {
                mOpenNotificationButton.setVisibility(View.VISIBLE);
            } else {
                showSnackbar("Received notification without id !");
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver();
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        isReceiverRegistered = false;
        super.onPause();
    }

    @OnClick(R.id.btn_register_with_name)
    void onRegisterDeviceClick(View v) {
        v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
        showRegisterDeviceDialog(false);
    }

    @OnClick(R.id.btn_register_anonymously)
    void onRegisterAnonymousDeviceClick(View v) {
        v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
        showRegisterDeviceDialog(true);
    }

    @OnClick(R.id.btn_update_device_info)
    void onUpdateDeviceInfoClick(View v) {
        v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
        showUpdateDeviceDialog();
    }

    @OnClick(R.id.btn_update_device_name)
    void onUpdateDeviceNameClick(View v) {
        v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
        showUpdateDeviceNameDialog();
    }

    @OnClick(R.id.btn_update_device_locale)
    void onUpdateDeviceLocaleClick(View v) {
        v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
        showUpdateDeviceLocaleDialog();
    }

    @OnClick(R.id.btn_unassign_device_from_user)
    void onUnassignDeviceClick(View v) {
        v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
        showUnassignDeviceConfirmationDialog();
    }

    @OnClick(R.id.btn_assign_device_to_user)
    void onAssignDeviceClick(View v) {
        v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
        showAssignDeviceDialog();
    }

    @OnClick(R.id.btn_unregister_device)
    void onUnregisterDevice(View v) {
        v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
        showUnregisterDeviceDialog();
    }

    @OnClick(R.id.btn_mark_notification)
    void onMarkNotificationClicked(View v) {
        v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);

        mNotifiableManager.markNotificationOpened(String.valueOf(mLatestNotification.getNotificationId()),
            String.valueOf(mDeviceId), new NotifiableCallback<Object>() {
                @Override
                public void onSuccess(@NonNull Object ret) {
                    showSnackbar("Notification marked as open");
                    mLatestNotification = null;
                    mOpenNotificationButton.setVisibility(View.GONE);
                }

                @Override
                public void onError(@NonNull String error) {
                    showSnackbar(error);
                }
            });
    }

    private void updateDeviceToken() {
        final NotifiableCallback<NotifiableDevice> callback = new NotifiableCallback<NotifiableDevice>() {
            @Override
            public void onSuccess(@NonNull NotifiableDevice ret) {
                showSnackbar("GCM token has been refreshed");
            }

            @Override
            public void onError(@NonNull String error) {
                showSnackbar(error);
            }
        };

        if (TextUtils.isEmpty(mDeviceUser)) {
            mNotifiableManager.updateDeviceToken(mDeviceUser, String.valueOf(mDeviceId), mGcmToken, callback);
        } else {
            mNotifiableManager.updateAnonymousDeviceToken(String.valueOf(mDeviceId), mGcmToken, callback);
        }

    }

    private void showUpdateDeviceLocaleDialog() {
        final Locale[] availableLocales = Locale.getAvailableLocales();
        final List<String> localeNames = new ArrayList<>();
        final String currentLocaleDisplayName = mCurrentLocale.getDisplayName();
        int selected = 0;

        for (int i = 0; i < availableLocales.length; i++) {
            String displayCountry = availableLocales[i].getDisplayName();

            if (!TextUtils.isEmpty(displayCountry)) {
                localeNames.add(displayCountry);

                if (displayCountry.equalsIgnoreCase(currentLocaleDisplayName)) {
                    selected = i;
                }
            }
        }

        new AlertDialog.Builder(this)
            .setTitle(getString(R.string.title_user_devices))
            .setSingleChoiceItems(localeNames.toArray(new String[localeNames.size()]), selected, (dialog, which) -> {
                int newSelection = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                updateDeviceLocale(availableLocales[newSelection]);
                dialog.dismiss();
            })
            .setPositiveButton(getString(R.string.action_ok), null)
            .setNegativeButton(getString(R.string.action_cancel), null).show();
    }

    private void showRegisterDeviceDialog(final boolean isAnonymous) {
        final View view = getLayoutInflater().inflate(R.layout.dlg_register, null);

        final TextInputLayout userlayout = view.findViewById(R.id.user_layout);
        final EditText username = view.findViewById(R.id.user_et);
        final TextInputLayout devicelayout = view.findViewById(R.id.device_layout);
        final EditText device = view.findViewById(R.id.device_et);

        if (isAnonymous) {
            userlayout.setVisibility(View.GONE);
        }

        final AlertDialog dialog = new AlertDialog.Builder(this)
            .setTitle(getString(R.string.title_register_device))
            .setView(view)
            .setCancelable(false)
            .setPositiveButton(getString(R.string.action_ok), null)
            .setNegativeButton(getString(R.string.action_cancel), null).show();

        //Overriding the handler immediately after showing the dialog in order to prevent it from dismissing on incomplete information
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String user = null;
            String deviceName = null;
            boolean validContent = true;
            if (isAnonymous) {
                deviceName = device.getText().toString();
                validContent = checkDeviceName(deviceName, devicelayout);
            } else {
                user = username.getText().toString();
                deviceName = device.getText().toString();

                validContent = checkUser(user, userlayout) && checkDeviceName(deviceName, devicelayout);
            }

            if (validContent) {

                userlayout.setError(null);
                devicelayout.setError(null);

                registerNotifiableDevice(user, deviceName);
                dialog.dismiss();
            }
        });

    }

    private void showUpdateDeviceDialog() {
        final View view = getLayoutInflater().inflate(R.layout.dlg_device_info, null);

        final EditText os = view.findViewById(R.id.os_et);

        final CheckBox isEmulator = view.findViewById(R.id.emulator_cb);

        new AlertDialog.Builder(this)
            .setTitle(getString(R.string.title_update_device))
            .setView(view)
            .setCancelable(false)
            .setPositiveButton(getString(R.string.action_ok), (dialog1, which) -> {
                final String osVersion = os.getText().toString();
                final String emulator = String.valueOf(isEmulator.isChecked());

                updateDeviceInfo(osVersion, emulator);
            })
            .setNegativeButton(getString(R.string.action_cancel), null).show();

    }

    private void showUpdateDeviceNameDialog() {
        final View view = getLayoutInflater().inflate(R.layout.dlg_text_input, null);

        final TextInputLayout layout = view.findViewById(R.id.input_layout);
        final EditText inputEt = view.findViewById(R.id.input_et);
        inputEt.setHint(getString(R.string.lbl_device_name));

        final AlertDialog dialog = new AlertDialog.Builder(this)
            .setTitle(getString(R.string.title_update_device))
            .setView(view)
            .setCancelable(false)
            .setPositiveButton(getString(R.string.action_ok), null)
            .setNegativeButton(getString(R.string.action_cancel), null).show();

        //Overriding the handler immediately after showing the dialog in order to prevent it from dismissing on incomplete information
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            final String name = inputEt.getText().toString();

            if (checkDeviceName(name, layout)) {
                layout.setError(null);
                updateDeviceName(name);
                dialog.dismiss();
            }
        });
    }

    private void showUnassignDeviceConfirmationDialog() {
        new AlertDialog.Builder(this)
            .setTitle(getString(R.string.title_unassign_device))
            .setMessage(getString(R.string.msg_unassign_device_confirmation))
            .setCancelable(false)
            .setPositiveButton(getString(R.string.action_ok), (dialog1, which) -> unassignDevice())
            .setNegativeButton(getString(R.string.action_cancel), null).show();
    }

    private void showAssignDeviceDialog() {
        final View view = getLayoutInflater().inflate(R.layout.dlg_text_input, null);

        final TextInputLayout layout = view.findViewById(R.id.input_layout);
        final EditText inputEt = view.findViewById(R.id.input_et);
        inputEt.setHint(getString(R.string.lbl_user_name));

        final AlertDialog dialog = new AlertDialog.Builder(this)
            .setTitle(getString(R.string.title_update_device))
            .setView(view)
            .setCancelable(false)
            .setPositiveButton(getString(R.string.action_ok), null)
            .setNegativeButton(getString(R.string.action_cancel), null).show();

        //Overriding the handler immediately after showing the dialog in order to prevent it from dismissing on incomplete information
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            final String name = inputEt.getText().toString();

            if (checkUser(name, layout)) {
                layout.setError(null);
                assignDeviceToUser(name);
                dialog.dismiss();
            }
        });
    }

    private void showUnregisterDeviceDialog() {

        new AlertDialog.Builder(this)
            .setTitle(getString(R.string.title_unregister_device))
            .setMessage(getString(R.string.msg_unregister_device_confirmation))
            .setCancelable(false)
            .setPositiveButton(getString(R.string.action_ok), (dialog, which) -> unregisterDevice())
            .setNegativeButton(getString(R.string.action_cancel), null).show();

    }

    private void registerNotifiableDevice(final String user, final String deviceName) {
        if (TextUtils.isEmpty(user)) {
            mNotifiableManager.registerAnonymousDevice(deviceName, mGcmToken,
                mCurrentLocale, NotifiableManager.GOOGLE_CLOUD_MESSAGING_PROVIDER,
                new NotifiableCallback<NotifiableDevice>() {
                    @Override
                    public void onSuccess(@NonNull NotifiableDevice ret) {
                        mDeviceName = deviceName;
                        mDeviceUser = user;
                        mState = NotifiableStates.REGISTERED_ANONYMOUSLY;
                        updateUi();

                        mDeviceId = ret.getId();
                        mSharedPrefs.edit().putInt(Constants.NOTIFIABLE_DEVICE_ID, mDeviceId).apply();
                        showSnackbar("Device registered with id " + String.valueOf(ret.getId()));
                    }

                    @Override
                    public void onError(@NonNull String error) {
                        mSharedPrefs.edit().putInt(Constants.NOTIFIABLE_DEVICE_ID, -1).apply();
                        showSnackbar(error);
                    }
                });
        } else {
            mNotifiableManager.registerDevice(deviceName, mGcmToken, user,
                mCurrentLocale, NotifiableManager.GOOGLE_CLOUD_MESSAGING_PROVIDER,
                new NotifiableCallback<NotifiableDevice>() {
                    @Override
                    public void onSuccess(@NonNull NotifiableDevice ret) {
                        mDeviceUser = user;
                        mDeviceName = deviceName;
                        mState = NotifiableStates.REGISTERED_WITH_USER;
                        updateUi();

                        mDeviceId = ret.getId();
                        mSharedPrefs.edit().putInt(Constants.NOTIFIABLE_DEVICE_ID, mDeviceId).apply();
                        showSnackbar("Device registered with id " + String.valueOf(ret.getId()));
                    }

                    @Override
                    public void onError(@NonNull String error) {
                        mSharedPrefs.edit().putInt(Constants.NOTIFIABLE_DEVICE_ID, -1).apply();
                        showSnackbar(error);
                    }
                });
        }

    }

    private void updateDeviceInfo(String osVersion, final String emulator) {
        // create map with entered values
        final HashMap<String, Object> vals = new HashMap<>();
        vals.put(Constants.OS_PROPERTY, osVersion);
        vals.put(Constants.IS_EMULATOR_PROPERTY, emulator);

        NotifiableCallback<NotifiableDevice> notifiableCallback = new NotifiableCallback<NotifiableDevice>() {
            @Override
            public void onSuccess(@NonNull NotifiableDevice ret) {
                showSnackbar("Updated device with id " + String.valueOf(ret.getId()));
            }

            @Override
            public void onError(@NonNull String error) {
                showSnackbar(error);
            }
        };

        mNotifiableManager.updateDeviceCustomProperties(String.valueOf(mDeviceId), vals, notifiableCallback);
    }

    private void updateDeviceName(final String name) {
        NotifiableCallback<NotifiableDevice> callback = new NotifiableCallback<NotifiableDevice>() {
            @Override
            public void onSuccess(@NonNull NotifiableDevice ret) {
                mDeviceName = name;
                showSnackbar("Updated device name to " + name);
            }

            @Override
            public void onError(@NonNull String error) {
                showSnackbar(error);
            }
        };

        if (TextUtils.isEmpty(mDeviceUser)) {
            mNotifiableManager.updateAnonymousDeviceName(String.valueOf(mDeviceId), name, callback);
        } else {
            mNotifiableManager.updateDeviceName(mDeviceUser, String.valueOf(mDeviceId), name, callback);
        }

    }

    private void updateDeviceLocale(final Locale locale) {
        NotifiableCallback<NotifiableDevice> callback = new NotifiableCallback<NotifiableDevice>() {
            @Override
            public void onSuccess(@NonNull NotifiableDevice ret) {
                mCurrentLocale = locale;

                showSnackbar("Updated device Locale to " + locale.getDisplayName());
            }

            @Override
            public void onError(@NonNull String error) {
                showSnackbar(error);
            }
        };

        mNotifiableManager.updateDeviceLocale(String.valueOf(mDeviceId), locale, callback);
    }

    private void unassignDevice() {
        mNotifiableManager.unassignDeviceFromUser(mDeviceName, mGcmToken, new NotifiableCallback<NotifiableDevice>() {
            @Override
            public void onSuccess(@NonNull NotifiableDevice ret) {
                mState = NotifiableStates.REGISTERED_ANONYMOUSLY;
                updateUi();

                showSnackbar("Device was unassigned from " + mDeviceUser);
                mDeviceUser = null;
            }

            @Override
            public void onError(@NonNull String error) {
                showSnackbar(error);
            }
        });
    }

    private void assignDeviceToUser(@NonNull final String userName) {
        mNotifiableManager.assignDeviceToUser(userName, mDeviceName, mGcmToken, new NotifiableCallback<NotifiableDevice>() {
            @Override
            public void onSuccess(@NonNull NotifiableDevice ret) {
                mDeviceUser = userName;
                mState = NotifiableStates.REGISTERED_WITH_USER;
                updateUi();

                showSnackbar("Device was assigned to " + mDeviceUser);
            }

            @Override
            public void onError(@NonNull String error) {
                showSnackbar(error);
            }
        });
    }

    private void unregisterDevice() {
        mNotifiableManager.unregisterDevice(String.valueOf(mDeviceId), new NotifiableCallback<Object>() {
            @Override
            public void onSuccess(@NonNull Object ret) {
                mSharedPrefs.edit().putInt(Constants.NOTIFIABLE_DEVICE_ID, -1).apply();

                // hide buttons
                mState = NotifiableStates.UNREGISTERED;
                updateUi();

                showSnackbar("Device successfully removed ");
            }

            @Override
            public void onError(@NonNull String error) {
                showSnackbar(error);
            }
        });
    }

    private void registerReceiver() {
        if (!isReceiverRegistered) {
            LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Constants.FIREBASE_NEW_TOKEN));
            isReceiverRegistered = true;
        }
    }

    private void updateUi() {
        switch (mState) {
            case UNREGISTERED:
                mRegisterNotifiableButton.setEnabled(true);
                mRegisterAnonymousNotifiableButton.setEnabled(true);

                mUpdateDeviceInfoButton.setVisibility(View.GONE);
                mUnregisterDeviceButton.setVisibility(View.GONE);
                mUpdateDeviceNameButton.setVisibility(View.GONE);
                mUpdateDeviceLocaleButton.setVisibility(View.GONE);
                mUnassignFromUserButton.setVisibility(View.GONE);
                break;
            case REGISTERED_WITH_USER:
                mRegisterNotifiableButton.setEnabled(false);
                mRegisterAnonymousNotifiableButton.setEnabled(false);

                mAssignToUserButton.setVisibility(View.GONE);

                mUpdateDeviceInfoButton.setVisibility(View.VISIBLE);
                mUnregisterDeviceButton.setVisibility(View.VISIBLE);

                mUpdateDeviceNameButton.setVisibility(View.VISIBLE);
                mUpdateDeviceLocaleButton.setVisibility(View.VISIBLE);
                mUnassignFromUserButton.setVisibility(View.VISIBLE);
                break;
            case REGISTERED_ANONYMOUSLY:
                mRegisterNotifiableButton.setEnabled(false);
                mRegisterAnonymousNotifiableButton.setEnabled(false);

                mUnassignFromUserButton.setVisibility(View.GONE);

                mUpdateDeviceInfoButton.setVisibility(View.VISIBLE);
                mUnregisterDeviceButton.setVisibility(View.VISIBLE);

                mUpdateDeviceNameButton.setVisibility(View.VISIBLE);
                mUpdateDeviceLocaleButton.setVisibility(View.VISIBLE);
                mAssignToUserButton.setVisibility(View.VISIBLE);
                break;
        }
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                    .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    private void showSnackbar(String message) {
        final View view = NotifiableActivity.this.getWindow().getDecorView()
            .findViewById(android.R.id.content);
        Snackbar.make(view,
            message, Snackbar.LENGTH_SHORT).show();
    }

    private boolean checkUser(String inputValue, TextInputLayout inputLayout) {
        if (TextUtils.isEmpty(inputValue)) {
            inputLayout.setError(getString(R.string.err_user_required));
            return false;
        } else {
            inputLayout.setError(null);
            return true;
        }
    }

    private boolean checkDeviceName(String inputValue, TextInputLayout inputLayout) {
        if (TextUtils.isEmpty(inputValue)) {
            inputLayout.setError(getString(R.string.err_device_name_required));
            return false;
        } else {
            inputLayout.setError(null);
            return true;
        }
    }
}
