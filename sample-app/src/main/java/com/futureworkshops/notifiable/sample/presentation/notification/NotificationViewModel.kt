/*
 * Copyright ©  2018 - 2019 FutureWorkshops. All rights reserved.
 */

package com.futureworkshops.notifiable.sample.presentation.notification

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.futureworkshops.notifiable.rx.NotifiableManagerRx
import com.futureworkshops.notifiable.rx.model.NotifiableMessage
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

class NotificationViewModel @Inject constructor(private val notifiableManagerRx: NotifiableManagerRx) :
    ViewModel() {

    val viewState: LiveData<NotificationViewState>
        get() = _viewState

    private val _viewState: MutableLiveData<NotificationViewState> = MutableLiveData()

    private val _notificationLiveData: MutableLiveData<NotifiableMessage> = MutableLiveData()

    private val disposables = CompositeDisposable()


    override fun onCleared() {
        disposables.clear()
        super.onCleared()
    }

    @SuppressLint("CheckResult")
    fun setNotification(notifiableMessage: NotifiableMessage) {
        // save notification locally
        _notificationLiveData.postValue(notifiableMessage)

        // mark notification as opened
        notifiableManagerRx.markNotificationOpened(notifiableMessage.notificationId.toString())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                _viewState.postValue(
                    NotificationViewState(
                        isMarkingNotification = true,
                        notification = notifiableMessage
                    )
                )
            }
            .subscribe(
                {
                    _viewState.postValue(
                        NotificationViewState(
                            isNotificationMarked = true,
                            notification = notifiableMessage
                        )
                    )

                },
                { t ->
                    Timber.e(t)
                    _viewState.postValue(
                        NotificationViewState(
                            hasError = true,
                            errorMessage = t.message
                        )
                    )
                }
            )

    }
}