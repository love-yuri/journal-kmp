package com.yuri.love.views.setting

import com.yuri.love.database.DriverFactory

expect class PlatformSettings {
    companion object {
        // 设置指纹认证
        fun setFingerprintEnabled(enabled: Boolean, callBack: (Boolean) -> Unit)

        // 设置PIN码登录
        fun setPinLoginEnabled(enabled: Boolean, callBack: (Boolean) -> Unit)
    }
}