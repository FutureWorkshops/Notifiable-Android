/*
 * Copyright ©  2018 - 2019 FutureWorkshops. All rights reserved.
 */

package com.futureworkshops.notifiable.sample.presentation.demo

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.futureworkshops.notifiable.rx.NotifiableManagerRx
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

class DemoViewModel @Inject constructor(private val notifiableManagerRx: NotifiableManagerRx) :
    ViewModel() {

    val viewState: LiveData<DemoState>
        get() = _viewState

    private val _viewState: MutableLiveData<DemoState> = MutableLiveData()

    private val disposables = CompositeDisposable()

    @SuppressLint("CheckResult")
    fun checkNotifiableStatus() {

        _viewState.postValue(DemoState(isCheckingNotifiableState = true))

        notifiableManagerRx.getRegisteredDevice()
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
        _viewState.postValue(DemoState(isCheckingNotifiableState = true))

        notifiableManagerRx.registerDevice(deviceName, user)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { device, error ->

                if (error != null) {
                    // FIXME check error type
                    _viewState.postValue(DemoState(hasError = true))
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
        _viewState.postValue(DemoState(isCheckingNotifiableState = true))
        notifiableManagerRx.unregisterDevice()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                _viewState.postValue(DemoState(deviceNotRegistered = true))

            },
                { t ->
                    Timber.e(t)
                    _viewState.postValue(DemoState(hasError = true))
                })

    }

    override fun onCleared() {
        disposables.clear()
        super.onCleared()
    }
}