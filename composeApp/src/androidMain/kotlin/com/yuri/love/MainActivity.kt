package com.yuri.love

import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
            // 显示认证背景界面
            setContent {
                AuthenticationScreen(
                    needFingerPrint = needFingerPrintAuth,
                    needPin = needPinAuth
                )
            }

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

@Composable
fun AuthenticationScreen(needFingerPrint: Boolean, needPin: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFFE5EE),  // 极淡粉
                        Color(0xFFFFF0F5)   // 淡粉白
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 120.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 图标
            Icon(
                imageVector = if (needFingerPrint) Icons.Default.Fingerprint else Icons.Default.Lock,
                contentDescription = null,
                modifier = Modifier.size(100.dp),
                tint = Color(0xFFFF69B4).copy(alpha = 0.6f)  // 柔和的粉红色
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 标题
            Text(
                text = "Journal",
                fontSize = 42.sp,
                fontWeight = FontWeight.Light,
                color = Color(0xFFFF1493).copy(alpha = 0.8f),  // 深粉色
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 提示文字
            Text(
                text = when {
                    needFingerPrint && needPin -> "使用指纹或PIN解锁"
                    needFingerPrint -> "使用指纹解锁"
                    needPin -> "使用PIN解锁"
                    else -> "请验证身份"
                },
                fontSize = 15.sp,
                color = Color(0xFFFF69B4).copy(alpha = 0.7f),
                fontWeight = FontWeight.Normal
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 加载动画
            CircularProgressIndicator(
                modifier = Modifier.size(40.dp),
                color = Color(0xFFFF69B4).copy(alpha = 0.6f),
                strokeWidth = 3.dp
            )
        }
    }
}