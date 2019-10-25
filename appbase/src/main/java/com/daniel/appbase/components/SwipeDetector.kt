package com.daniel.appbase.components

import android.view.GestureDetector
import android.view.MotionEvent
import kotlin.math.abs

class SwipeDetector(private var onSwipeListener: OnSwipeListener) :
    GestureDetector.SimpleOnGestureListener() {

    override fun onFling(
        e1: MotionEvent,
        e2: MotionEvent,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        // Check movement along the Y-axis. If it exceeds SWIPE_MAX_OFF_PATH,
        // then dismiss the swipe.
        if (abs(e1.y - e2.y) > SWIPE_MAX_OFF_PATH)
            return false

        // Swipe from left to right.
        // The swipe needs to exceed a certain distance (SWIPE_MIN_DISTANCE)
        // and a certain velocity (SWIPE_THRESHOLD_VELOCITY).
        if (e2.x - e1.x > SWIPE_MIN_DISTANCE && abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
            onSwipeListener.onSwipe()
            return true
        }

        return false
    }

    interface OnSwipeListener {
        fun onSwipe()
    }

    companion object {
        const val SWIPE_MIN_DISTANCE = 120
        const val SWIPE_MAX_OFF_PATH = 250
        const val SWIPE_THRESHOLD_VELOCITY = 200
    }
}
