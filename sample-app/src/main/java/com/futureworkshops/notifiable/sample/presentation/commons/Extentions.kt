/*
 * Copyright ©  2018 - 2019 FutureWorkshops. All rights reserved.
 */

package com.futureworkshops.notifiable.sample.presentation.commons

import android.app.Activity
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import com.futureworkshops.notifiable.sample.R
import com.google.android.material.textfield.TextInputEditText

/**
 * TextInputEditText extensions
 */

inline fun TextInputEditText.onTextChanged(
    crossinline after: (s: Editable) -> Unit = {},
    crossinline before: (string: String, start: Int, count: Int, after: Int) -> Unit = { _, _, _, _ -> },
    crossinline onTextChanged: (string: String, start: Int, count: Int, after: Int) -> Unit
) =
    addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable) = after.invoke(s)
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) =
            before.invoke(s.toString(), start, count, after)

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) =
            onTextChanged(s.toString(), start, before, count)
    })

fun TextInputEditText.clear() = text?.clear()


fun Activity.getPrimaryColour(): Int {
    val typedValue = TypedValue()

    val a = obtainStyledAttributes(typedValue.data, intArrayOf(R.attr.colorPrimary))
    val color = a.getColor(0, 0)

    a.recycle()

    return color
}

fun Activity.getSecondaryColour(): Int {
    val typedValue = TypedValue()

    val a = obtainStyledAttributes(typedValue.data, intArrayOf(R.attr.colorSecondary))
    val color = a.getColor(0, 0)

    a.recycle()

    return color
}

fun Activity.getOnSurfaceColour(): Int {
    val typedValue = TypedValue()

    val a = obtainStyledAttributes(typedValue.data, intArrayOf(R.attr.colorOnSurface))
    val color = a.getColor(0, 0)

    a.recycle()

    return color
}