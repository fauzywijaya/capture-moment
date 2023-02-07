package com.example.capturemoment.ui.customview

import android.animation.ObjectAnimator
import android.view.View

fun View.loadingVisible(isVisible: Boolean, duration: Long = 400) {
    ObjectAnimator
        .ofFloat(this, View.ALPHA, if (isVisible) 1f else 0f)
        .setDuration(duration)
        .start()
}