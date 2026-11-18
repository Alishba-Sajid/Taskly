package com.example.taskly.core.extention

import android.view.View

fun View.show() {
    if (visibility != View.VISIBLE) {
        visibility = View.VISIBLE
    }
}

fun View.hide() {
    if (visibility != View.GONE) {
        visibility = View.GONE
    }
}

fun View.enable() {
    if (!isEnabled) {
        isEnabled = true
    }
}

fun View.disable() {
    if (isEnabled) {
        isEnabled = false
    }
}
