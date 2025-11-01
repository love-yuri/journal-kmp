package com.yuri.love.utils

import android.content.Intent
import android.widget.Toast
import com.yuri.love.AppContext
import kotlin.system.exitProcess

actual object PlatformUtils {
    actual fun restart() {
        val activity = AppContext.mainActivity
        val intent = activity.packageManager.getLaunchIntentForPackage(activity.packageName)
        intent?.apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            activity.startActivity(this)
        }
        activity.finishAffinity() // 关闭所有 Activity
        exitProcess(0)
    }

    actual fun toast(msg: String) {
        Toast.makeText(AppContext.mainActivity, msg, Toast.LENGTH_SHORT).show()
    }
}