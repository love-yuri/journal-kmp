package com.yuri.love

import android.content.Context

object AppContext {
    private var _mainActivity: MainActivity? = null

    val mainActivity: MainActivity
        get() = _mainActivity ?: throw IllegalStateException("mainActivity not initialized")

    fun initialize(activity: MainActivity) {
        _mainActivity = activity
    }
}