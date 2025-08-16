package com.savana.core.extension

import android.app.Activity
import android.content.Context
import android.view.inputmethod.InputMethodManager

fun Activity.hideKeyboard() {
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
    var currentFocusedView = currentFocus
    if (currentFocusedView == null) {
        currentFocusedView = window.decorView
    }
    currentFocusedView?.let {
        imm?.hideSoftInputFromWindow(it.windowToken, 0)
    }
}