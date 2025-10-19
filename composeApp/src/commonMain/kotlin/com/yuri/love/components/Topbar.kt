package com.yuri.love.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.yuri.love.share.GlobalColors
import com.yuri.love.views.create.CreateScreen
import com.yuri.love.views.home.components.LocalDrawerController
import journal.composeapp.generated.resources.Res
import journal.composeapp.generated.resources.kiss
import journal.composeapp.generated.resources.more
import org.jetbrains.compose.resources.painterResource

@Composable
fun TopBar() {
    val navigator = LocalNavigator.currentOrThrow
    val drawerController = LocalDrawerController.current
    Box( modifier = Modifier.fillMaxWidth().height(50.dp)) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(GlobalColors.tapBarBackground)
                .blur(radius = 10.dp)
        )
        Box(modifier = Modifier.padding(start = 3.dp, end = 3.dp).fillMaxSize()) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .align(Alignment.CenterStart)
                    .clip(CircleShape)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = LocalIndication.current,
                        onClick = {
                            drawerController.open()
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(Res.drawable.more),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                )
            }
            Text("Journal",
                Modifier.align(Alignment.Center),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFEC4899)
            )
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .align(Alignment.CenterEnd)
                    .clip(CircleShape)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = LocalIndication.current,
                        onClick = {
                            navigator.push(CreateScreen())
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(Res.drawable.kiss),
                    contentDescription = null,
                    modifier = Modifier.size(30.dp)
                )
            }
        }
    }
}