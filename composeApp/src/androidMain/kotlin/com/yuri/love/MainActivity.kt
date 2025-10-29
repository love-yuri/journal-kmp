package com.yuri.love

import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.biometric.BiometricPrompt
import androidx.fragment.app.FragmentActivity
import com.yuri.love.database.DriverFactory
import com.yuri.love.database.SystemConfig
import com.yuri.love.utils.BiometricAuth

@OptIn(ExperimentalMultiplatform::class)
actual object Static {
    actual fun init() {
        System.setProperty("kotlin-logging-to-android-native", "true")
    }
}

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        DriverFactory.context = this
        enableEdgeToEdge()

        super.onCreate(savedInstanceState)

        val biometricAuth = BiometricAuth(this)
        biometricAuth.authenticate(
            title = "登录Journal",
            subtitle = "请使用指纹认证",
            negativeButtonText = "取消",
            onSuccess = {
                setContent {
                    App()
                }
            },
            onError = { errorCode, errString ->
                // 如果不是用户主动取消，就退出程序
                if (errorCode != BiometricPrompt.ERROR_USER_CANCELED &&
                    errorCode != BiometricPrompt.ERROR_NEGATIVE_BUTTON) {
                    Toast.makeText(this@MainActivity, "认证失败，退出程序", Toast.LENGTH_SHORT).show()
                }
                finish()
            },
            onFailed = {
                Toast.makeText(this@MainActivity, "指纹不匹配，退出程序!", Toast.LENGTH_SHORT).show()
                finish()
            }
        )
    }
}