package com.yuri.love.views.setting

import com.yuri.love.utils.notification.Notification

actual class PlatformSettings {
    actual companion object {
        actual fun setFingerprintEnabled(enabled: Boolean, callBack: (Boolean) -> Unit) {
            callBack(enabled)
            Notification.notificationState?.error("暂不支持指纹认证")
        }

        actual fun setPinLoginEnabled(enabled: Boolean, callBack: (Boolean) -> Unit) {
            callBack(enabled)
            Notification.notificationState?.error("暂不支持Pin码认证")
        }
    }
}