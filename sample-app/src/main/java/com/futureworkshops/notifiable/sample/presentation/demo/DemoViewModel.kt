/*
 * Copyright ©  2018 - 2019 FutureWorkshops. All rights reserved.
 */

package com.futureworkshops.notifiable.sample.presentation.demo

import androidx.lifecycle.ViewModel
import com.futureworkshops.notifiable.rx.NotifiableManagerRx
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class DemoViewModel @Inject constructor(private val notificaationManager: NotifiableManagerRx) :
    ViewModel() {
    private val disposables = CompositeDisposable()


    override fun onCleared() {
        disposables.clear()
        super.onCleared()
    }
}