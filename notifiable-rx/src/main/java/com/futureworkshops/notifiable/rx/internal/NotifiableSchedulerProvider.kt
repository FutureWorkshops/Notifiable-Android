/*
 * Copyright ©  2018 - 2019 FutureWorkshops. All rights reserved.
 */

package com.futureworkshops.notifiable.rx.internal

import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Abstraction over the threading pool required for Notifiable operations.
 */
interface INotifiablScheduler {
    fun io(): Scheduler

    fun ui(): Scheduler
}


/**
 * Custom implementation of [INotifiablScheduler] that allows SDK user to specify the desired scheduler threads.
 */
class UserDefinedNotifiableScheduler(
    private val ioScheduler: Scheduler,
    private val uiScheduler: Scheduler
) : INotifiablScheduler {
    override fun io(): Scheduler = ioScheduler

    override fun ui(): Scheduler = uiScheduler
}

/**
 * Default implementation of the [INotifiablScheduler]
 */
class DefaultNotifiableSchedulerProvider() : INotifiablScheduler {

    override fun io(): Scheduler {
        return Schedulers.io()
    }

    override fun ui(): Scheduler {
        return AndroidSchedulers.mainThread()
    }

}