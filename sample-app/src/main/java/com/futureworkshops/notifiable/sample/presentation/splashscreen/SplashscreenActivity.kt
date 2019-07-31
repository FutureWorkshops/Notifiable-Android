/*
 * Copyright ©  2018 - 2019 FutureWorkshops. All rights reserved.
 */

package com.futureworkshops.notifiable.sample.presentation.splashscreen

import android.os.Bundle
import android.os.Handler
import android.view.View.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.futureworkshops.notifiable.sample.NotifiableActivity
import com.futureworkshops.notifiable.sample.R

class SplashscreenActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_splashscreen)

        val rootLayout: ConstraintLayout = findViewById(R.id.rootLayout)
        rootLayout.systemUiVisibility =
            SYSTEM_UI_FLAG_LAYOUT_STABLE or SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION

    }


    override fun onStart() {
        super.onStart()

        Handler().postDelayed({
            startActivity(NotifiableActivity.newIntent(this))
            finish()
        }, 400)
    }

}
