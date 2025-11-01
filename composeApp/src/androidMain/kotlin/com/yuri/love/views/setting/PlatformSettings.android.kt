package com.yuri.love.views.setting

import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import com.yuri.love.App
import com.yuri.love.database.SystemConfig
import com.yuri.love.utils.BiometricAuth
import com.yuri.love.utils.notification.Notification

actual class PlatformSettings {
    actual companion object {
        actual fun setFingerprintEnabled(enabled: Boolean, callBack: (Boolean) -> Unit) {
            val biometricAuth = BiometricAuth()
            biometricAuth.authenticate (
                title = "请认证指纹",
                type =  BIOMETRIC_STRONG,
                onSuccess = {
                    callBack(enabled)
                    if (enabled) {
                        Notification.notificationState?.success("指纹认证成功!")
                    } else {
                        Notification.notificationState?.success("指纹认证关闭!")
                    }
                },
                onError = { errorCode, errString ->
                    callBack(!enabled)
                    Notification.notificationState?.error("指纹认证失败: $errString")
                },
                onFailed = {
                    callBack(!enabled)
                }
            )
        }

        actual fun setPinLoginEnabled(enabled: Boolean, callBack: (Boolean) -> Unit) {
            val biometricAuth = BiometricAuth()
            biometricAuth.authenticate (
                title = "请认证Pin密码",
                type =  DEVICE_CREDENTIAL,
                onSuccess = {
                    callBack(enabled)
                    if (enabled) {
                        Notification.notificationState?.success("Pin密码认证成功!")
                    } else {
                        Notification.notificationState?.success("Pin密码认证关闭!")
                    }
                },
                onError = { errorCode, errString ->
                    callBack(!enabled)
                    Notification.notificationState?.error("Pin密码认证失败: $errString")
                },
                onFailed = {
                    callBack(!enabled)
                }
            )
        }
    }

}