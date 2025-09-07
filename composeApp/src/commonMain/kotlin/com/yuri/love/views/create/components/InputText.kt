package com.yuri.love.views.create.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yuri.love.utils.platformSafeTopPadding
import org.jetbrains.compose.ui.tooling.preview.Preview

@Preview
@Composable
fun InputText() {
    val text = remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    Column (modifier = Modifier
        .fillMaxSize()
        .platformSafeTopPadding()
        .background(Color(0xFFE5CDE5))
        .padding(10.dp)
    ) {

        Text(
            "scroll: ${scrollState.value}",
            color = Color.White,
            fontSize = 30.sp,
        )
        Box(modifier = Modifier
            .fillMaxWidth()
            .fillMaxSize()
            .background(
                color = Color.White,
                shape = RoundedCornerShape(8.dp)
            )
            .border(
                width = 1.dp,
                color = Color.Blue,
                shape = RoundedCornerShape(8.dp)
            )
            .clip(RoundedCornerShape(10.dp))
        ) {
            LaunchedEffect(text.value) {
                scrollState.animateScrollTo(scrollState.maxValue)
            }

            BasicTextField(
                value = text.value,
                onValueChange = {
                    text.value = it
                },
                textStyle = TextStyle(fontSize = 20.sp, color = Color.Black, lineHeight = 18.sp),
                cursorBrush = SolidColor(Color.Red),
                modifier = Modifier.fillMaxSize(),
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier
                            .padding(10.dp)
                            .verticalScroll(scrollState)
                            .fillMaxWidth()
                    ) {
                        if (text.value.isEmpty()) {
                            Text(
                                "请输入内容...",
                                color = Color.Gray,
                                fontSize = 20.sp
                            )
                        }
                        innerTextField()
                    }
                }
            )
        }
    }
}
