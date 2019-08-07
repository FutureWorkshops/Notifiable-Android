/*
 * Copyright ©  2018 - 2019 FutureWorkshops. All rights reserved.
 */

package com.futureworkshops.notifiable.sample.domain.timber

import android.util.Log

import timber.log.Timber

/**
 * Created by stelian on 16/04/2018.
 */

class ReleaseLogTree : Timber.Tree() {

    override fun isLoggable(tag: String?, priority: Int): Boolean {
        return !(priority == Log.VERBOSE || priority == Log.DEBUG || priority == Log.INFO)

    }

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if (isLoggable(tag, priority)) {
            if (message.length < MAX_LOG_LENGTH) {
                when (priority) {
                    Log.ASSERT -> Log.wtf(tag, message)
                    else -> Log.println(priority, tag, message)
                }

                return
            }

            val length = message.length
            var i = 0
            while (i < length) {
                var newLine = message.indexOf('\n', i)
                newLine = if (newLine != -1) newLine else length
                do {
                    val end = Math.min(newLine, i + MAX_LOG_LENGTH)
                    val part = message.substring(i, end)
                    if (priority == Log.ASSERT) {
                        Log.wtf(tag, part)
                    } else {
                        Log.println(priority, tag, part)
                    }
                    i = end
                } while (i < newLine)
                i++
            }
        }
    }

    companion object {

        private val MAX_LOG_LENGTH = 4000
    }
}
