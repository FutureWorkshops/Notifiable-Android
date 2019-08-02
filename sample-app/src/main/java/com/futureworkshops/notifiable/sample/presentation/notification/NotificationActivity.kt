/*
 * Copyright ©  2018 - 2019 FutureWorkshops. All rights reserved.
 */

package com.futureworkshops.notifiable.sample.presentation.notification

import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.futureworkshops.notifiable.sample.R


import com.stelianmorariu.antrics.domain.dagger.Injectable
import javax.inject.Inject

class NotificationActivity : AppCompatActivity(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    lateinit var viewModel: NotificationViewModel

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)

        setContentView(R.layout.activity_notification)

        val rootLayout: ConstraintLayout = findViewById(R.id.rootLayout)
        rootLayout.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION




        viewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(NotificationViewModel::class.java)


        viewModel.viewState.observe(this, Observer { viewState ->
        })

    }

    companion object {
        const val NOTIFICATION = "notification"
    }

}