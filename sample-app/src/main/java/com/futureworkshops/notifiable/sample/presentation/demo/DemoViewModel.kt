/*
 * Copyright ©  2018 - 2019 FutureWorkshops. All rights reserved.
 */

package com.futureworkshops.notifiable.sample.presentation.demo

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.futureworkshops.notifiable.rx.NotifiableManagerRx
import com.futureworkshops.notifiable.rx.internal.GooglePlayServicesException
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class DemoViewModel @Inject constructor(private val notifiableManagerRx: NotifiableManagerRx) :
    ViewModel() {

    val viewState: LiveData<DemoState>
        get() = _viewState

    private val _viewState: MutableLiveData<DemoState> = MutableLiveData()

    private val disposables = CompositeDisposable()

    @SuppressLint("CheckResult")
    fun checkNotifiableStatus() {
        notifiableManagerRx.getRegisteredDevice()
            .doOnSubscribe { _viewState.postValue(DemoState(isCheckingNotifiableState = true)) }
            .subscribe { device, _ ->
                if (device != null) {
                    _viewState.postValue(
                        DemoState(
                            deviceRegistered = true,
                            notifiableDevice = DemoState.RegisteredDevice(device)
                        )
                    )
                } else {
                    _viewState.postValue(DemoState(deviceNotRegistered = true))
                }

            }

    }


    @SuppressLint("CheckResult")
    fun registerNotifiableDevice(user: String?, deviceName: String?) {
        notifiableManagerRx.registerDevice(deviceName, user)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { _viewState.postValue(DemoState(isCheckingNotifiableState = true)) }
            .subscribe { device, error ->


                if (error != null) {
                    val viewStateError: DemoState.Error =
                        if (error is GooglePlayServicesException) {
                            DemoState.Error.GoogleServicesError(error.exceptionCode)
                        } else {
                            DemoState.Error.Generic(error.toString())
                        }

                    _viewState.postValue(DemoState(hasError = true, error = viewStateError))
                } else {
                    _viewState.postValue(
                        DemoState(
                            deviceRegistered = true,
                            notifiableDevice = DemoState.RegisteredDevice(device)
                        )
                    )
                }

            }
    }

    @SuppressLint("CheckResult")
    fun unregisterDevice() {
        notifiableManagerRx.unregisterDevice()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { _viewState.postValue(DemoState(isUnregistering = true)) }
            .subscribe({
                _viewState.postValue(DemoState(deviceNotRegistered = true))

            },
                { t ->
                    Timber.e(t)
                    _viewState.postValue(
                        DemoState(
                            hasError = true,
                            error = DemoState.Error.Generic(t.localizedMessage)
                        )
                    )
                })

    }

    override fun onCleared() {
        disposables.clear()
        super.onCleared()
    }

    @SuppressLint("CheckResult")
    fun updateDeviceInfo(
        deviceName: String? = null,
        userAlias: String? = null,
        locale: Locale? = null
    ) {
        notifiableManagerRx.updateDeviceInformation(
            userAlias = userAlias,
            deviceName = deviceName,
            locale = locale
        )
            .toSingle { }
            .flatMap { notifiableManagerRx.getRegisteredDevice() }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { _viewState.postValue(DemoState(isUpdating = true)) }
            .subscribe({
                _viewState.postValue(
                    DemoState(
                        deviceInfoUpdated = true,
                        notifiableDevice = DemoState.RegisteredDevice(it)
                    )
                )

            },
                { t ->
                    Timber.e(t)
                    _viewState.postValue(DemoState(hasError = true))
                })
    }
}