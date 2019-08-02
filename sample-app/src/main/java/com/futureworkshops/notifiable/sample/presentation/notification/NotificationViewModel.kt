/*
 * Copyright ©  2018 - 2019 FutureWorkshops. All rights reserved.
 */

package com.futureworkshops.notifiable.sample.presentation.notification

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.futureworkshops.notifiable.rx.NotifiableManagerRx
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class NotificationViewModel @Inject constructor(private val notifiableManagerRx: NotifiableManagerRx) :
    ViewModel() {


    val viewState: LiveData<NotificationViewState>
        get() = _viewState

    private val _viewState: MutableLiveData<NotificationViewState> = MutableLiveData()

    private val disposables = CompositeDisposable()


    override fun onCleared() {
        disposables.clear()
        super.onCleared()
    }
}