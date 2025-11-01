package com.yuri.love

import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.fragment.app.FragmentActivity
import com.yuri.love.database.SystemConfig
import com.yuri.love.utils.BiometricAuth

@OptIn(ExperimentalMultiplatform::class)
actual object Static {
    actual fun init() {
        System.setProperty("kotlin-logging-to-android-native", "true")
    }
}

class MainActivity : FragmentActivity() {
    init {
        AppContext.initialize(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val needFingerPrintAuth = SystemConfig.FingerprintEnabled
        val needPinAuth = SystemConfig.PinLoginEnabled

        val type = when {
            needFingerPrintAuth && needPinAuth -> BIOMETRIC_STRONG or DEVICE_CREDENTIAL
            needFingerPrintAuth -> BIOMETRIC_STRONG
            needPinAuth -> DEVICE_CREDENTIAL
            else -> 0
        }

        if (needFingerPrintAuth || needPinAuth) {
            val biometricAuth = BiometricAuth()
            biometricAuth.authenticate(
                type = type,
                title = "登录Journal",
                onSuccess = {
                    setContent {
                        App()
                    }
                },
                onError = { _, errString ->
                    Toast.makeText(this@MainActivity, "认证失败: $errString", Toast.LENGTH_SHORT).show()
                    finish()
                },
                onFailed = {
                    Toast.makeText(this@MainActivity, "指纹不匹配，退出程序!", Toast.LENGTH_SHORT).show()
                    finish()
                }
            )
        } else {
            setContent {
                App()
            }
        }
    }
}